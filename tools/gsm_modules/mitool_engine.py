#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
KDF-LuX - MiTool Engine (Xiaomi / Redmi / POCO)
Módulo independiente para diagnóstico Fastboot, comprobación de Anti-Rollback (ARB),
estado de Bootloader y verificación de ROMs oficiales Fastboot (flash_all).
"""

import subprocess
import sys
import os

def check_fastboot_devices():
    try:
        res = subprocess.run(["fastboot", "devices"], capture_output=True, text=True, timeout=10)
        return res.stdout.strip()
    except Exception as e:
        return f"[Error Fastboot: {e}]"

def get_fastboot_var(var_name):
    try:
        res = subprocess.run(["fastboot", "getvar", var_name], capture_output=True, text=True, timeout=10)
        output = (res.stdout + "\n" + res.stderr).strip()
        for line in output.splitlines():
            if var_name in line:
                parts = line.split(":")
                if len(parts) > 1:
                    return parts[1].strip()
        return output
    except Exception as e:
        return f"[Error: {e}]"

def get_xiaomi_diag_report():
    dev = check_fastboot_devices()
    if not dev:
        return {
            "device": "No detectado en Fastboot",
            "status": "Conecta el equipo en modo Fastboot (Volumen Abajo + Encendido)"
        }
    report = {
        "device": dev,
        "anti_rollback": get_fastboot_var("anti"),
        "unlocked": get_fastboot_var("unlocked"),
        "secure": get_fastboot_var("secure"),
        "product": get_fastboot_var("product"),
        "current_slot": get_fastboot_var("current-slot")
    }
    return report

if __name__ == "__main__":
    print("=== MiTool Engine (Xiaomi Service Diag) ===")
    rep = get_xiaomi_diag_report()
    for k, v in rep.items():
        print(f"[*] {k}: {v}")
