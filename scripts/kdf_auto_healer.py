#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
KDF-Tools · Auto-Healer & Service Sentinel
Monitorea la disponibilidad del servidor KDF-LuX, diagnostica fallos y auto-repara.
"""

import os
import sys
import time
import socket
import subprocess
import urllib.request
import py_compile

BASE_DIR = "/data/data/com.termux/files/home/KDF-LuX"
APP_PY = os.path.join(BASE_DIR, "app.py")
LOG_FILE = os.path.join(BASE_DIR, "logs", "app.log")
HEALER_LOG = os.path.join(BASE_DIR, "logs", "auto_healer.log")
PORT = 5000

def log(msg: str):
    timestamp = time.strftime("%Y-%m-%d %H:%M:%S")
    formatted = f"[{timestamp}] [HEALER] {msg}"
    print(formatted)
    try:
        os.makedirs(os.path.dirname(HEALER_LOG), exist_ok=True)
        with open(HEALER_LOG, "a", encoding="utf-8") as f:
            f.write(formatted + "\n")
    except Exception:
        pass

def send_notification(title: str, content: str, priority: str = "high"):
    try:
        cmd = [
            "/data/data/com.termux/files/usr/bin/termux-notification",
            "--id", "7701",
            "--title", title,
            "--content", content,
            "--priority", priority,
            "--vibrate", "300,150,300"
        ]
        subprocess.run(cmd, capture_output=True, timeout=5)
    except Exception as e:
        log(f"No se pudo enviar notificación: {e}")

def is_port_open(port: int = PORT, host: str = "127.0.0.1") -> bool:
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.settimeout(2.0)
            return s.connect_ex((host, port)) == 0
    except Exception:
        return False

def check_http_status() -> bool:
    try:
        req = urllib.request.Request(f"http://127.0.0.1:{PORT}/api/status", headers={"User-Agent": "KDFHealer/1.0"})
        with urllib.request.urlopen(req, timeout=3.0) as res:
            return res.status == 200
    except Exception:
        return False

def compile_check() -> bool:
    try:
        py_compile.compile(APP_PY, doraise=True)
        return True
    except Exception as e:
        log(f"Error de sintaxis en {APP_PY}: {e}")
        send_notification("🚨 Error Crítico en Código", f"Sintaxis inválida en app.py: {e}", "max")
        return False

def kill_process_on_port(port: int = PORT):
    try:
        out = subprocess.check_output(["lsof", "-t", f"-i:{port}"], text=True).strip()
        if out:
            pids = out.splitlines()
            for pid in pids:
                log(f"Terminando proceso zombi en puerto {port} (PID: {pid})")
                subprocess.run(["kill", "-9", pid], check=False)
            time.sleep(1)
    except Exception:
        pass

def restart_service():
    log("Iniciando procedimiento de auto-recuperación de KDF-LuX...")
    if not compile_check():
        log("Cancelando reinicio debido a errores de sintaxis.")
        return False

    kill_process_on_port(PORT)
    os.makedirs(os.path.dirname(LOG_FILE), exist_ok=True)
    
    with open(LOG_FILE, "a") as f_out:
        proc = subprocess.Popen(
            [sys.executable, APP_PY],
            cwd=BASE_DIR,
            stdout=f_out,
            stderr=subprocess.STDOUT,
            start_new_session=True
        )
    log(f"Servidor KDF-LuX lanzado con PID {proc.pid}")
    
    # Esperar hasta 5 segundos para verificar que subió
    for _ in range(5):
        time.sleep(1)
        if is_port_open(PORT):
            log("✓ Servidor recuperado y escuchando correctamente en el puerto 5000.")
            send_notification(
                "🛡️ KDF-LuX Auto-Healed",
                "El servidor web se recuperó automáticamente y está en línea.",
                "default"
            )
            return True
            
    log("⚠️ El servidor no respondió dentro del tiempo límite tras reinicio.")
    return False

def audit_and_heal():
    log("Ejecutando auditoría de salud del ecosistema KDF-LuX...")
    if is_port_open(PORT):
        log(f"✓ Puerto {PORT} activo y respondiendo.")
        return {"status": "healthy", "port": PORT}
    else:
        log(f"⚠️ Puerto {PORT} no responde. Activando protocolo de auto-sanación.")
        healed = restart_service()
        return {"status": "recovered" if healed else "failed", "port": PORT}

if __name__ == "__main__":
    if "--notify" in sys.argv:
        send_notification("🛡️ Prueba KDF Sentinel", "Centinela activo en Termux y listo para proteger.", "default")
        print("Notificación de prueba enviada.")
    else:
        result = audit_and_heal()
        print(f"Resultado de auditoría: {result}")
