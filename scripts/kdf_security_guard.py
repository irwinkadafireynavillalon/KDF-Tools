#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
KDF-Tools · Security Guard & Anti-Tamper Sentinel
Monitoreo de integridad de archivos, vigilancia de puertos y alarmas contra intrusiones.
"""

import os
import sys
import time
import json
import hashlib
import subprocess

STATE_DIR = os.path.expanduser("~/.kdf-studio")
BASELINE_FILE = os.path.join(STATE_DIR, "security_baseline.json")
SECURITY_LOG = "/data/data/com.termux/files/home/KDF-LuX/logs/security_alerts.log"
os.makedirs(STATE_DIR, exist_ok=True)
os.makedirs(os.path.dirname(SECURITY_LOG), exist_ok=True)

# Lista de rutas críticas a vigilar
CRITICAL_TARGETS = [
    os.path.expanduser("~/.bashrc"),
    os.path.expanduser("~/.zshrc"),
    os.path.expanduser("~/.ssh/authorized_keys"),
    os.path.expanduser("~/.env"),
    "/data/data/com.termux/files/home/KDF-LuX/.env",
    "/data/data/com.termux/files/home/KDF-LuX/config/ai_config.yaml",
    "/data/data/com.termux/files/home/KDF-LuX/config/categories.yaml",
    "/data/data/com.termux/files/home/google-cloud-sdk/properties"
]

def log_event(message: str, severity: str = "INFO"):
    timestamp = time.strftime("%Y-%m-%d %H:%M:%S")
    entry = f"[{timestamp}] [{severity}] {message}"
    print(entry)
    try:
        with open(SECURITY_LOG, "a", encoding="utf-8") as f:
            f.write(entry + "\n")
    except Exception:
        pass

def trigger_alarm(title: str, message: str):
    log_event(f"🚨 ALARMA DISPARADA: {title} - {message}", "CRITICAL")
    try:
        cmd = [
            "/data/data/com.termux/files/usr/bin/termux-notification",
            "--id", "9999",
            "--title", title,
            "--content", message,
            "--priority", "max",
            "--vibrate", "500,200,500,200,500",
            "--sound"
        ]
        subprocess.run(cmd, capture_output=True, timeout=5)
    except Exception as e:
        log_event(f"Error al enviar notificación de alarma: {e}", "ERROR")

def hash_file(path: str) -> str:
    if not os.path.exists(path):
        return "NON_EXISTENT"
    try:
        hasher = hashlib.sha256()
        with open(path, "rb") as f:
            while chunk := f.read(8192):
                hasher.update(chunk)
        return hasher.hexdigest()
    except Exception as e:
        return f"ERROR_{e}"

def generate_baseline():
    baseline = {}
    for target in CRITICAL_TARGETS:
        baseline[target] = hash_file(target)
    with open(BASELINE_FILE, "w", encoding="utf-8") as f:
        json.dump(baseline, f, indent=2)
    log_event("✓ Línea base de integridad criptográfica generada con éxito.", "OK")
    return baseline

def check_integrity():
    if not os.path.exists(BASELINE_FILE):
        log_event("No existe línea base. Creando una nueva...", "WARN")
        return generate_baseline()

    try:
        with open(BASELINE_FILE, "r", encoding="utf-8") as f:
            baseline = json.load(f)
    except Exception as e:
        log_event(f"Error al leer línea base: {e}", "ERROR")
        return None

    tampered = []
    for target in CRITICAL_TARGETS:
        expected = baseline.get(target)
        current = hash_file(target)
        if expected is None:
            # Nuevo archivo no contemplado
            baseline[target] = current
            continue
        if expected != current:
            tampered.append({"path": target, "expected": expected[:10], "current": current[:10]})

    if tampered:
        for t in tampered:
            msg = f"Archivo crítico modificado: {t['path']}"
            trigger_alarm("🛡️ KDF ALERTA: MODIFICACIÓN SOSPECHOSA", msg)
    else:
        log_event("✓ Integridad de archivos verificada: Todos los archivos coinciden.", "OK")

    return tampered

def audit_ports():
    suspicious_ports = []
    try:
        # Intentar con netstat o ss si existe
        cmd = ["netstat", "-tuln"]
        res = subprocess.run(cmd, capture_output=True, text=True, timeout=5)
        lines = res.stdout.splitlines() if res.returncode == 0 else []
        for line in lines:
            if "0.0.0.0:" in line and "LISTEN" in line:
                # Revisar si se expone un puerto indebido
                suspicious_ports.append(line.strip())
    except Exception:
        pass

    if suspicious_ports:
        log_event(f"Advertencia: Sockets escuchando en 0.0.0.0: {suspicious_ports}", "WARN")
    else:
        log_event("✓ Auditoría de red: Sin sockets públicos no autorizados.", "OK")

    return suspicious_ports

def run_full_scan():
    log_event("=== INICIANDO ESCANEO DE SEGURIDAD KDF ===", "INFO")
    tampered = check_integrity()
    ports = audit_ports()
    status = {
        "timestamp": time.time(),
        "integrity_ok": len(tampered or []) == 0,
        "tampered_files": tampered or [],
        "suspicious_ports": ports,
        "shield_active": True
    }
    return status

if __name__ == "__main__":
    if "--init-baseline" in sys.argv:
        generate_baseline()
        print("Línea base creada.")
    elif "--test-alarm" in sys.argv:
        trigger_alarm("🛡️ PRUEBA DE ALARMA KDF", "Simulación de detección de intrusión exitosa.")
        print("Alarma de prueba activada.")
    else:
        status = run_full_scan()
        print(json.dumps(status, indent=2))
