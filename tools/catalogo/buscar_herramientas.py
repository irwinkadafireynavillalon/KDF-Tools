#!/usr/bin/env python3
"""Busca herramientas compatibles con un perfil de dispositivo.

Solo lee el catálogo y comprueba dependencias locales; no instala ni ejecuta
herramientas o scripts. Ejemplo:
  python3 -m tools.catalogo.buscar_herramientas --protocol fastboot --available
"""
from __future__ import annotations

import argparse
import importlib.util
import json
import shutil
from pathlib import Path
from typing import Any

CATALOG_PATH = Path(__file__).with_name("herramientas.json")


def load_catalog(path: Path = CATALOG_PATH) -> dict[str, Any]:
    with path.open(encoding="utf-8") as source:
        data = json.load(source)
    if not isinstance(data.get("tools"), list):
        raise ValueError("El catálogo debe contener una lista 'tools'.")
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
    del model  # Se conserva como dato de búsqueda/UI, no asumir compatibilidad por texto libre.
    results = []
    for item in catalog["tools"]:
        if not _contains(item.get("brands", []), brand):
            continue
        if not _contains(item.get("protocols", []), protocol):
            continue
        if not _contains(item.get("chipsets", []), chipset):
            continue
        if device and device.casefold() not in {"universal", "other-usb"}:
            # Las herramientas universales sirven como candidatos; el catálogo no
            # afirma compatibilidad exacta con una marca/modelo no enumerados.
            if "Universal" not in item.get("brands", []) and brand:
                if brand.casefold() not in [b.casefold() for b in item.get("brands", [])]:
                    continue
        availability = check_availability(item)
        if available_only and availability["available"] is not True:
            continue
        results.append({**item, "availability": availability})
    return results


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--device", help="Categoría (phones, tablets, wearables, computers, screens, automotive)")
    parser.add_argument("--brand")
    parser.add_argument("--model", help="Se presenta como contexto; no prueba compatibilidad exacta")
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
            state = "dependencias disponibles" if availability["available"] else "referencia/manual"
            if availability["missing"]:
                state += "; faltan: " + ", ".join(availability["missing"])
            if availability["suggested_nix_package"]:
                state += "; paquete Nix sugerido: " + availability["suggested_nix_package"]
            print(f"{item['name']} [{item['id']}] — {state}")
            print(f"  {item.get('notes', '')}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
