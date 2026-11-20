#!/usr/bin/env python3
"""Almacena y busca casos de diagnóstico en JSONL fuera del repositorio."""
from __future__ import annotations

import json
import os
import uuid
from datetime import datetime, timezone
from pathlib import Path
from typing import Any

DEFAULT_CASES_PATH = Path(os.environ.get("KDF_TOOLS_DATA_DIR", Path.home() / ".local" / "share" / "kdf-tools")) / "cases.jsonl"
ALLOWED_OUTCOMES = {"confirmed", "ruled_out", "inconclusive", "pending"}


class CaseStore:
    def __init__(self, path: Path = DEFAULT_CASES_PATH) -> None:
        self.path = Path(path)

    def _read_all(self) -> list[dict[str, Any]]:
        if not self.path.exists():
            return []
        records = []
        with self.path.open(encoding="utf-8") as stream:
            for line_number, line in enumerate(stream, start=1):
                if not line.strip():
                    continue
                try:
                    item = json.loads(line)
                except json.JSONDecodeError as exc:
                    raise ValueError(f"Registro JSONL inválido en línea {line_number}") from exc
                if isinstance(item, dict):
                    records.append(item)
        return records

    def add(self, case: dict[str, Any]) -> dict[str, Any]:
        forbidden = {"imei", "serial_number", "password", "token", "customer_name", "phone_number"}
        if forbidden.intersection(key.casefold() for key in case):
            raise ValueError("No guardar identificadores personales o credenciales en casos técnicos.")
        outcome = case.get("outcome", "pending")
        if outcome not in ALLOWED_OUTCOMES:
            raise ValueError(f"outcome debe ser uno de: {', '.join(sorted(ALLOWED_OUTCOMES))}")
        record = {
            "case_id": str(uuid.uuid4()),
            "created_at": datetime.now(timezone.utc).isoformat(),
            "category": case.get("category", "other-usb"),
            "brand": case.get("brand"),
            "model": case.get("model"),
            "board_revision": case.get("board_revision"),
            "symptoms": case.get("symptoms", []),
            "observations": case.get("observations", []),
            "protocol": case.get("protocol"),
            "hypotheses": case.get("hypotheses", []),
            "steps": case.get("steps", []),
            "outcome": outcome,
            "notes": case.get("notes", "")
        }
        self.path.parent.mkdir(parents=True, exist_ok=True)
        with self.path.open("a", encoding="utf-8") as stream:
            stream.write(json.dumps(record, ensure_ascii=False) + "\n")
        return record

    def search(self, *, brand: str | None = None, model: str | None = None, symptom: str | None = None) -> list[dict[str, Any]]:
        result = []
        for item in self._read_all():
            if brand and brand.casefold() not in str(item.get("brand") or "").casefold():
                continue
            if model and model.casefold() not in str(item.get("model") or "").casefold():
                continue
            symptom_text = " ".join(str(x) for x in item.get("symptoms", []))
            if symptom and symptom.casefold() not in symptom_text.casefold():
                continue
            result.append(item)
        return result

    def set_outcome(self, case_id: str, outcome: str) -> bool:
        if outcome not in ALLOWED_OUTCOMES:
            raise ValueError(f"outcome debe ser uno de: {', '.join(sorted(ALLOWED_OUTCOMES))}")
        records = self._read_all()
        found = False
        for item in records:
            if item.get("case_id") == case_id:
                item["outcome"] = outcome
                item["updated_at"] = datetime.now(timezone.utc).isoformat()
                found = True
        if found:
            self.path.write_text("".join(json.dumps(item, ensure_ascii=False) + "\n" for item in records), encoding="utf-8")
        return found

    def delete_all(self) -> None:
        if self.path.exists():
            self.path.unlink()
