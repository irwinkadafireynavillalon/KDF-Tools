#!/usr/bin/env bash
# scripts/diagnose_connectivity.sh - Diagnóstico de puertos de comunicación
set -e
echo "=========================================================="
echo " [KDF-LuX] DIAGNÓSTICO DE CONECTIVIDAD UNIVERSAL          "
echo "=========================================================="
echo "[1] Dispositivos USB OTG conectados:"
termux-usb -l 2>/dev/null || echo "Ninguno detectado."

echo "[2] Dispositivos ADB (Depuración USB / Wi-Fi):"
if command -v adb >/dev/null 2>&1; then
    adb devices -l 2>/dev/null || true
else
    echo "ADB no está instalado en este entorno."
fi

echo "[3] Dispositivos Fastboot / FastbootD:"
if command -v fastboot >/dev/null 2>&1; then
    fastboot devices 2>/dev/null || true
else
    echo "Fastboot no está instalado en este entorno."
fi

echo "[4] Conexiones de red locales activas:"
ip -br addr 2>/dev/null || true
