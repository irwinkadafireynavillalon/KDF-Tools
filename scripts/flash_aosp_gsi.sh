#!/usr/bin/env bash
# scripts/flash_aosp_gsi.sh - Flasheador de Imágenes GSI AOSP (Treble arm64_ab)
set -e
echo "=========================================================="
echo " [KDF-LuX] FLASHEADOR GSI TREBLE AOSP (Android 15/16/17)  "
echo "=========================================================="
TARGET_IMG="${1:-system.img}"

echo "[*] Comprobando entorno FastbootD..."
if command -v fastboot >/dev/null 2>&1; then
    fastboot getvar is-userspace 2>&1 || true
    echo "[*] Para instalar AOSP GSI en equipos con particiones dinámicas A/B:"
    echo " 1. fastboot reboot fastboot"
    echo " 2. fastboot --disable-verity --disable-verification flash vbmeta vbmeta.img"
    echo " 3. fastboot flash system $TARGET_IMG"
    echo " 4. fastboot reboot"
else
    echo "Fastboot no encontrado en el PATH."
fi
