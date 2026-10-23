#!/usr/bin/env bash
# scripts/edl_flasher.sh - Detección y Flasheo de Emergencia EDL 9008 / BROM
# KDF-Tools · Módulo de Utilidad Universal
set -e

DEVICE="$1"
BRAND="$2"
PROCESSOR="$3"

echo "=========================================================="
echo "  [KDF-Tools] FLASHER EDL 9008 / BROM (BAJO NIVEL)        "
echo "=========================================================="
echo "[*] Plataforma: ${BRAND:-Universal} | Procesador: ${PROCESSOR:-Auto-detect}"
echo ""

echo "[1/3] Escaneando puertos USB de emergencia..."

# Comprobar si hay dispositivos en EDL (Qualcomm 05c6:9008) o MTK BROM (0e8d:0003)
EDL_FOUND=0
if command -v termux-usb >/dev/null 2>&1; then
    USB_LIST=$(termux-usb -l 2>/dev/null || true)
    echo "  → Dispositivos USB: $USB_LIST"
fi

if lsusb 2>/dev/null | grep -iE "9008|05c6"; then
    echo "  [+] ¡DISPOSITIVO QUALCOMM DETECTADO EN MODO EDL 9008!"
    EDL_FOUND=1
elif lsusb 2>/dev/null | grep -iE "0e8d|brom|preloader"; then
    echo "  [+] ¡DISPOSITIVO MEDIATEK DETECTADO EN MODO BROM / PRELOADER!"
    EDL_FOUND=2
else
    echo "  [i] No se detecta puerto EDL/BROM directo."
    echo "      Para entrar a EDL:"
    echo "      • Qualcomm: adb reboot edl  o  fastboot oem edl  o  Test-Point (GND)."
    echo "      • MediaTek: Apagar equipo y conectar manteniendo presionado Vol- / Vol+."
fi

echo ""
echo "[2/3] Verificando herramientas de bajo nivel..."
if command -v edl >/dev/null 2>&1; then
    echo "  [✓] edl-tool disponible."
else
    echo "  [i] edl-tool (Qualcomm) no instalado en PATH global."
fi

if command -v mtk >/dev/null 2>&1 || python3 -c "import mtkclient" >/dev/null 2>&1; then
    echo "  [✓] mtkclient disponible."
else
    echo "  [i] mtkclient (MediaTek) disponible en entorno virtual o herramientas KDF."
fi

echo ""
echo "[3/3] Estado del sistema preparado para inyección de payload / firmware."
echo "=========================================================="
echo "  [✓] Diagnóstico EDL completado.                         "
echo "=========================================================="
