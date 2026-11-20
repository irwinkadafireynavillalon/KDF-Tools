"""Validación estructural de muestras del adaptador; no realiza diagnóstico eléctrico."""
from __future__ import annotations

from typing import Any


def validate_measurement(payload: dict[str, Any]) -> tuple[bool, list[str]]:
    errors: list[str] = []
    if payload.get("protocol") != "kdf-measurement-json-v1":
        errors.append("protocol debe ser kdf-measurement-json-v1")
    measurement = payload.get("measurement")
    if not isinstance(measurement, dict):
        return False, errors + ["measurement debe ser un objeto"]
    samples = measurement.get("samples")
    if not isinstance(samples, list) or not samples:
        return False, errors + ["samples debe ser una lista no vacía"]
    previous_time = -1
    for index, sample in enumerate(samples):
        if not isinstance(sample, dict):
            errors.append(f"samples[{index}] debe ser un objeto")
            continue
        timestamp = sample.get("t_ns")
        if not isinstance(timestamp, int) or timestamp < 0:
            errors.append(f"samples[{index}].t_ns debe ser entero no negativo")
        elif timestamp < previous_time:
            errors.append(f"samples[{index}].t_ns no puede retroceder")
        else:
            previous_time = timestamp
        for key in ("vbus_mv", "input_current_ma"):
            value = sample.get(key)
            if value is not None and (not isinstance(value, (int, float)) or isinstance(value, bool) or value < 0):
                errors.append(f"samples[{index}].{key} debe ser numérico no negativo o null")
    return not errors, errors


def measurement_readiness(payload: dict[str, Any]) -> dict[str, Any]:
    valid, errors = validate_measurement(payload)
    measurement = payload.get("measurement", {}) if isinstance(payload, dict) else {}
    samples = measurement.get("samples", []) if isinstance(measurement, dict) else []
    calibrated = bool(measurement.get("calibration_id")) if isinstance(measurement, dict) else False
    ready = valid and calibrated and len(samples) >= 2
    return {
        "status": "ready_for_technical_review" if ready else "insufficient_data",
        "valid_structure": valid,
        "calibrated": calibrated,
        "sample_count": len(samples) if isinstance(samples, list) else 0,
        "errors": errors,
        "diagnosis": None,
        "note": "La validación de datos no determina ni descarta un corto ni identifica una zona de placa."
    }
