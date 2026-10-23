#!/usr/bin/env bash
# scripts/create_usb_boot.sh - Creador de Medios USB Booteables
set -e
echo "=========================================================="
echo " [KDF-LuX] CREADOR DE MEDIOS USB / SD BOOTEABLES          "
echo "=========================================================="
ISO_FILE="${1:-}"
TARGET_USB="${2:-}"

if [ -z "$ISO_FILE" ]; then
    echo "Uso: $0 <archivo.iso> [dispositivo]"
    echo "Ejemplo: $0 ubuntu-24.04-desktop-amd64.iso /dev/block/sda"
    exit 0
fi

if [ ! -f "$ISO_FILE" ]; then
    echo "[ERROR] El archivo ISO '$ISO_FILE' no existe."
    exit 1
fi

echo "[*] Analizando ISO: $ISO_FILE"
echo "[*] Calculando suma de verificación SHA-256..."
sha256sum "$ISO_FILE" 2>/dev/null || true
echo "[*] Medio listo para escribir en unidad USB mediante sector raw o partición FAT32/NTFS."
