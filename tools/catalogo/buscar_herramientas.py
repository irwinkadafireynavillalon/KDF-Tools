#!/usr/bin/env python3
"""Busca herramientas compatibles con un perfil de dispositivo.

Lee solo manifiestos locales y comprueba dependencias disponibles. No descarga,
instala ni ejecuta herramientas o scripts.
"""
from __future__ import annotations

import argparse
import importlib.util
import json
import shutil
from pathlib import Path
from typing import Any

CATALOG_PATH = Path(__file__).with_name("herramientas.json")
EXPANSIONS_PATH = Path(__file__).with_name("expansiones_plataformas.json")


def load_catalog(path: Path = CATALOG_PATH) -> dict[str, Any]:
    with path.open(encoding="utf-8") as source:
        data = json.load(source)
    if not isinstance(data.get("tools"), list):
        raise ValueError("El catálogo debe contener una lista 'tools'.")
    if EXPANSIONS_PATH.exists():
        with EXPANSIONS_PATH.open(encoding="utf-8") as source:
            expansion = json.load(source)
        data["tools"].extend(expansion.get("tools", []))
        data["device_categories"].extend(expansion.get("device_categories", []))
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


def search_tools(
    catalog: dict[str, Any],
    *,
    device: str | None = None,
    brand: str | None = None,
    model: str | None = None,
    protocol: str | None = None,
    chipset: str | None = None,
    available_only: bool = False,
) -> list[dict[str, Any]]:
    results = []
    for item in catalog["tools"]:
        categories = item.get("device_categories", [])
        if device and categories and not _contains(categories, device):
            continue
        brands = item.get("brands", [])
        if brand and brands and not (
            _contains(brands, brand)
            or any(value.casefold() in {"universal", "android compatible", "any"} for value in brands)
        ):
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


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--device", help="Categoría: phones, tablets, iphones, computers, screens, automotive, bootable-media, etc.")
    parser.add_argument("--brand")
    parser.add_argument("--model", help="Contexto de búsqueda; no prueba compatibilidad exacta")
    parser.add_argument("--protocol")
    parser.add_argument("--chipset")
    parser.add_argument("--available", action="store_true", help="Mostrar solo dependencias detectadas localmente")
    parser.add_argument("--json", action="store_true", help="Emitir JSON")
    args = parser.parse_args()
    try:
        matches = search_tools(
            load_catalog(), device=args.device, brand=args.brand, model=args.model,
            protocol=args.protocol, chipset=args.chipset, available_only=args.available,
        )
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
