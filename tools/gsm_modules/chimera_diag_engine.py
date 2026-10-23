#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
KDF-LuX - Chimera Diag Engine (Módem AT, Diag Port & Samsung ODIN Check)
Módulo independiente para interactuar con módems celulares vía comandos AT serie
y comprobación de protocolos de servicio Samsung / MediaTek.
"""

import sys
import time

try:
    import serial
    SERIAL_AVAILABLE = True
except ImportError:
    SERIAL_AVAILABLE = False

def send_at_command(port, command="AT", timeout=3):
    """Envía un comando AT estándar a un puerto serie de módem/diagnóstico."""
    if not SERIAL_AVAILABLE:
        return "[Error: pyserial no disponible]"
    try:
        ser = serial.Serial(port, baudrate=115200, timeout=timeout)
        cmd_bytes = (command.strip() + "\r\n").encode("utf-8")
        ser.write(cmd_bytes)
        time.sleep(0.5)
        response = ser.read_all().decode("utf-8", errors="replace")
        ser.close()
        return response.strip() or "[Sin respuesta]"
    except Exception as e:
        return f"[Error puerto serie {port}: {e}]"

def get_modem_info(port):
    """Obtiene información básica de radio/módem vía comandos AT."""
    info = {
        "ping": send_at_command(port, "AT"),
        "model": send_at_command(port, "AT+GMM"),
        "firmware": send_at_command(port, "AT+CGMR"),
        "imei_status": send_at_command(port, "AT+CGSN")
    }
    return info

if __name__ == "__main__":
    print("=== Chimera Diag Engine (Serial AT & Modem Service) ===")
    print(f"[*] Driver Serial PySerial disponible: {SERIAL_AVAILABLE}")
    print("[*] Listo para escuchar en puertos serie OTG (/dev/ttyACM*, /dev/ttyUSB*).")
