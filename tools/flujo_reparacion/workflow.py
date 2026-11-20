#!/usr/bin/env python3
"""Ayudantes locales de flujo de reparación; no acceden a dispositivos.

Este módulo crea registros, planes de lote, notificaciones locales y resúmenes.
La UI Android debe manejar cámara, USB, permisos y ejecución de diagnósticos.
"""
from __future__ import annotations

import json
import uuid
from collections import Counter
from datetime import datetime, timezone
from pathlib import Path
from typing import Any

ALLOWED_MODES = {"manual", "automatic", "assistant"}
ALLOWED_TRANSPORTS = {"usb", "usb_otg", "adb_usb", "fastboot_usb", "usb_serial", "uvc", "bluetooth_accessory", "other_local"}
ALLOWED_STATES = {"received", "diagnosing", "awaiting_approval", "in_repair", "ready", "delivered", "closed"}


def _now() -> str:
    return datetime.now(timezone.utc).isoformat()


def _no_personal_keys(value: Any, path: str = "record") -> None:
    forbidden = {"password", "token", "access_token", "customer_name", "phone_number", "email", "imei", "serial_number"}
    if isinstance(value, dict):
        for key, child in value.items():
            if key.casefold() in forbidden:
                raise ValueError(f"No guardar el campo personal/credencial: {path}.{key}")
            _no_personal_keys(child, f"{path}.{key}")
    elif isinstance(value, list):
        for index, child in enumerate(value):
            _no_personal_keys(child, f"{path}[{index}]")


def new_case(*, category: str, mode: str = "manual", brand: str | None = None,
             model: str | None = None, symptoms: list[str] | None = None,
             observations: list[str] | None = None) -> dict[str, Any]:
    if mode not in ALLOWED_MODES:
        raise ValueError(f"mode debe ser uno de {sorted(ALLOWED_MODES)}")
    record = {
        "case_id": str(uuid.uuid4()), "created_at": _now(), "updated_at": _now(),
        "category": category, "brand": brand, "model": model, "mode": mode,
        "state": "received", "symptoms": symptoms or [], "observations": observations or [],
        "devices": [], "photo_attachments": [], "diagnostic_results": [],
        "script_references": [], "notifications": [], "consents": {}, "notes": ""
    }
    _no_personal_keys(record)
    return record


def add_device(case: dict[str, Any], *, label: str, transport: str,
               vendor_id: str | None = None, product_id: str | None = None,
               brand: str | None = None, model: str | None = None,
               connection_state: str = "detected") -> dict[str, Any]:
    if transport not in ALLOWED_TRANSPORTS:
        raise ValueError(f"Transporte local no soportado: {transport}")
    device = {
        "device_id": str(uuid.uuid4()), "label": label, "transport": transport,
        "vendor_id": vendor_id, "product_id": product_id, "brand": brand, "model": model,
        "connection_state": connection_state, "detected_at": _now()
    }
    _no_personal_keys(device)
    case.setdefault("devices", []).append(device)
    case["updated_at"] = _now()
    return device


def add_photo_reference(case: dict[str, Any], *, app_private_relative_path: str,
                        caption: str = "", captured_at: str | None = None) -> dict[str, Any]:
    """Registra metadatos de una foto; la UI debe capturarla con permiso y guardarla privada."""
    path = Path(app_private_relative_path)
    if path.is_absolute() or ".." in path.parts:
        raise ValueError("La referencia de imagen debe ser relativa al almacenamiento privado de la app")
    if not path.as_posix().startswith("cases/"):
        raise ValueError("Las fotos deben estar bajo el directorio privado cases/")
    attachment = {"attachment_id": str(uuid.uuid4()), "path": path.as_posix(),
                  "caption": caption, "captured_at": captured_at or _now(), "storage": "app_private"}
    case.setdefault("photo_attachments", []).append(attachment)
    case["updated_at"] = _now()
    return attachment


def create_batch_plan(devices: list[dict[str, Any]], *, checks: list[str]) -> dict[str, Any]:
    """Crea un plan, no ejecuta checks ni comandos."""
    if not devices:
        raise ValueError("El lote requiere al menos un dispositivo")
    if not checks:
        raise ValueError("El lote requiere al menos una comprobación")
    return {
        "batch_id": str(uuid.uuid4()), "created_at": _now(), "status": "planned",
        "device_count": len(devices), "checks": list(dict.fromkeys(checks)),
        "devices": [{"device_id": d.get("device_id"), "label": d.get("label"),
                     "transport": d.get("transport"), "status": "pending"} for d in devices],
        "execution_started": False,
        "note": "Plan local solamente: cada dispositivo/permisos se revisa antes de ejecutar."
    }


def add_local_notification(case: dict[str, Any], *, event: str, message: str) -> dict[str, Any]:
    notification = {"notification_id": str(uuid.uuid4()), "created_at": _now(),
                    "channel": "local", "event": event, "message": message, "read": False}
    case.setdefault("notifications", []).append(notification)
    return notification


def save_case(path: Path, case: dict[str, Any]) -> None:
    _no_personal_keys(case)
    path = Path(path)
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("a", encoding="utf-8") as stream:
        stream.write(json.dumps(case, ensure_ascii=False) + "\n")


def build_summary(cases: list[dict[str, Any]]) -> dict[str, Any]:
    """Resumen agregado sin nombres, IMEI, seriales ni contenido de fotos."""
    states = Counter(str(case.get("state", "unknown")) for case in cases)
    outcomes = Counter(
        str(result.get("outcome", "pending"))
        for case in cases for result in case.get("diagnostic_results", [])
    )
    return {
        "generated_at": _now(), "case_count": len(cases),
        "states": dict(sorted(states.items())), "diagnostic_outcomes": dict(sorted(outcomes.items())),
        "categories": dict(sorted(Counter(str(c.get("category", "unknown")) for c in cases).items())),
        "privacy_note": "Resumen agregado; no incluye imágenes ni identificadores personales."
    }
