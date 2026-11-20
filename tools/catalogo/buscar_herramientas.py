#!/usr/bin/env python3
"""Busca herramientas candidatas en manifiestos locales sin instalarlas ni ejecutarlas."""
from __future__ import annotations

import argparse
import importlib.util
import json
import shutil
from pathlib import Path
from typing import Any

CATALOG_PATH = Path(__file__).with_name("herramientas.json")
EXPANSIONS_PATH = Path(__file__).with_name("expansiones_plataformas.json")


def _merge_by_id(primary: list[dict[str, Any]], additional: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Merge catalog entries by id, preserving existing entries and their order."""
    merged = list(primary)
    positions = {item.get("id"): index for index, item in enumerate(merged) if item.get("id")}
    for item in additional:
        item_id = item.get("id")
        if item_id and item_id in positions:
            # Keep canonical entry; enrich missing metadata without overwriting it.
            current = merged[positions[item_id]]
            merged[positions[item_id]] = {**item, **current}
        else:
            if item_id:
                positions[item_id] = len(merged)
            merged.append(item)
    return merged


def load_catalog(path: Path = CATALOG_PATH, expansion_path: Path | None = None) -> dict[str, Any]:
    """Load current and legacy catalogs additively for backward compatibility."""
    with Path(path).open(encoding="utf-8") as source:
        data = json.load(source)
    if not isinstance(data, dict) or not isinstance(data.get("tools"), list):
        raise ValueError("El catálogo debe ser un objeto JSON con lista 'tools'.")
    selected_expansion = Path(expansion_path) if expansion_path else EXPANSIONS_PATH
    if selected_expansion.exists():
        with selected_expansion.open(encoding="utf-8") as source:
            expansion = json.load(source)
        if not isinstance(expansion, dict):
            raise ValueError("La expansión debe ser un objeto JSON.")
        data["tools"] = _merge_by_id(data["tools"], expansion.get("tools", []))
        data["device_categories"] = _merge_by_id(
            data.get("device_categories", []), expansion.get("device_categories", [])
        )
    return data


def _contains(values: list[str], query: str | None) -> bool:
    if not query:
        return True
    q = query.casefold()
    return any(q in str(value).casefold() for value in values)


def check_availability(item: dict[str, Any]) -> dict[str, Any]:
    commands = item.get("required_commands", [])
    found = {name: shutil.which(name) for name in commands}
    modules = item.get("python_modules", [])
    module_found = {name: importlib.util.find_spec(name) is not None for name in modules}
    missing = [name for name, location in found.items() if location is None]
    missing.extend(name for name, present in module_found.items() if not present)
    return {
        "available": not missing if (commands or modules) else None,
        "commands": found,
        "python_modules": module_found,
        "missing": missing,
        "suggested_nix_package": item.get("nix_package"),
    }


def _device_matches(item: dict[str, Any], category: str | None, catalog: dict[str, Any]) -> bool:
    if not category:
        return True
    category = category.casefold()
    declared = item.get("device_categories")
    if declared:
        return _contains(declared, category)
    # Older catalog schema: category lists identify tools by tool_ids.
    matching_groups = [group for group in catalog.get("device_categories", [])
                       if str(group.get("id", "")).casefold() == category]
    if matching_groups:
        tool_ids = matching_groups[0].get("tool_ids", [])
        return not tool_ids or item.get("id") in tool_ids
    # Preserve generic legacy entries when the requested category is unknown.
    return True


def _brand_matches(item: dict[str, Any], brand: str | None) -> bool:
    if not brand:
        return True
    brands = item.get("brands", [])
    if not brands:
        return True
    if _contains(brands, brand):
        return True
    return any(str(value).casefold() in {"universal", "any"} for value in brands)


def search_tools(
    catalog: dict[str, Any], *, device: str | None = None, brand: str | None = None,
    model: str | None = None, protocol: str | None = None, chipset: str | None = None,
    available_only: bool = False,
) -> list[dict[str, Any]]:
    """Return candidates; model text alone never establishes exact compatibility."""
    results = []
    for item in catalog.get("tools", []):
        if not _device_matches(item, device, catalog):
            continue
        if not _brand_matches(item, brand):
            continue
        if not _contains(item.get("protocols", []), protocol):
            continue
        if not _contains(item.get("chipsets", []), chipset):
            continue
        supported_models = item.get("supported_models", [])
        if model and supported_models and not _contains(supported_models, model):
            continue
        availability = check_availability(item)
        if available_only and availability["available"] is not True:
            continue
        result = {**item, "availability": availability}
        if model and not supported_models:
            result["model_match"] = "not_verified"
        results.append(result)
    return results


def validate_catalog(catalog: dict[str, Any]) -> list[str]:
    errors: list[str] = []
    for list_name in ("tools", "device_categories"):
        entries = catalog.get(list_name, [])
        if not isinstance(entries, list):
            errors.append(f"{list_name} debe ser una lista")
            continue
        seen: set[str] = set()
        for index, entry in enumerate(entries):
            if not isinstance(entry, dict):
                errors.append(f"{list_name}[{index}] debe ser un objeto")
                continue
            entry_id = entry.get("id")
            if not isinstance(entry_id, str) or not entry_id.strip():
                errors.append(f"{list_name}[{index}] requiere id no vacío")
            elif entry_id in seen:
                errors.append(f"id duplicado en {list_name}: {entry_id}")
            else:
                seen.add(entry_id)
    tool_ids = {item.get("id") for item in catalog.get("tools", []) if isinstance(item, dict)}
    for group in catalog.get("device_categories", []):
        if isinstance(group, dict):
            for tool_id in group.get("tool_ids", []):
                if tool_id not in tool_ids:
                    errors.append(f"{group.get('id')} referencia herramienta inexistente: {tool_id}")
    return errors


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--device", help="Categoría (phones, tablets, iphones, computers, smart-tvs, automotive, bootable-media)")
    parser.add_argument("--brand")
    parser.add_argument("--model", help="Contexto; no prueba compatibilidad exacta")
    parser.add_argument("--protocol")
    parser.add_argument("--chipset")
    parser.add_argument("--available", action="store_true", help="Mostrar solo dependencias detectadas localmente")
    parser.add_argument("--validate", action="store_true", help="Validar identificadores y referencias del catálogo")
    parser.add_argument("--json", action="store_true", help="Emitir JSON")
    args = parser.parse_args()
    try:
        catalog = load_catalog()
        errors = validate_catalog(catalog)
        if args.validate:
            if errors:
                print("\n".join(errors))
                return 1
            print("Catálogo válido.")
            return 0
        if errors:
            parser.error("Catálogo inválido: " + "; ".join(errors))
        matches = search_tools(catalog, device=args.device, brand=args.brand, model=args.model,
                              protocol=args.protocol, chipset=args.chipset, available_only=args.available)
    except (OSError, json.JSONDecodeError, ValueError) as exc:
        parser.error(str(exc))
    if args.json:
        print(json.dumps(matches, ensure_ascii=False, indent=2))
    else:
        for item in matches:
            availability = item["availability"]
            if availability["available"] is True:
                state = "dependencias disponibles"
            elif availability["available"] is False:
                state = "faltan: " + ", ".join(availability["missing"])
            else:
                state = item.get("status", "revisión manual")
            if availability["suggested_nix_package"]:
                state += "; paquete Nix sugerido: " + availability["suggested_nix_package"]
            if item.get("model_match") == "not_verified":
                state += "; modelo exacto no verificado"
            print(f"{item['name']} [{item['id']}] — {state}")
            print(f"  {item.get('notes', '')}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
