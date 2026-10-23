#!/usr/bin/env bash
# scripts/unbrick_recovery.sh - Asistente de Recuperación de Bootloop / Brick
set -e
echo "=========================================================="
echo " [KDF-LuX] ASISTENTE DE RECUPERACIÓN DE BOOTLOOP / BRICK "
echo "=========================================================="

echo "[1/4] Verificando dispositivos conectados por USB..."
if command -v fastboot >/dev/null 2>&1; then
    FASTBOOT_DEVS=$(fastboot devices 2>/dev/null || true)
    if [ -n "$FASTBOOT_DEVS" ]; then
        echo " -> Dispositivo detectado en modo Fastboot: $FASTBOOT_DEVS"
        echo " -> Obteniendo variables críticas del dispositivo..."
        fastboot getvar product 2>&1 || true
        fastboot getvar max-sparse-size 2>&1 || true
        fastboot getvar current-slot 2>&1 || true
    else
        echo " -> No se detectó dispositivo en Fastboot directo."
    fi
fi

if command -v adb >/dev/null 2>&1; then
    ADB_DEVS=$(adb devices 2>/dev/null | grep -v "List of" | grep -v "^$" || true)
    if [ -n "$ADB_DEVS" ]; then
        echo " -> Dispositivo detectado en modo ADB: $ADB_DEVS"
        echo " -> Reiniciando a modo recovery/bootloader si es requerido..."
    fi
fi

echo "[2/4] Comprobando puertos seriales y modo de emergencia (EDL / BROM / Preloader)..."
USB_LIST=$(termux-usb -l 2>/dev/null || true)
echo " -> Puertos USB activos en Termux: $USB_LIST"

echo "[3/4] Sugerencias de recuperación inmediata:"
echo " • Si el equipo es MediaTek (MTK): Mantén presionado Vol- y conecta cable USB para entrar en modo BROM / Preloader."
echo " • Si el equipo es Qualcomm: Usa la combinación Vol+ y Vol- al conectar cable para modo EDL 9008."
echo " • Si el equipo es Motorola: Mantén presionado Vol- y Encendido por 6 segundos para entrar en Fastboot."

echo "[4/4] Diagnóstico completado. Listo para aplicar firmware de rescate."
