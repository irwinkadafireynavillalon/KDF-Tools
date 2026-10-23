#!/usr/bin/env bash
# scripts/backup_partitions.sh - Respaldo de Particiones Críticas
set -e
echo "=========================================================="
echo " [KDF-LuX] RESPALDO DE SEGURIDAD DE PARTICIONES           "
echo "=========================================================="
BACKUP_DIR="/storage/emulated/0/Download/KDF_Backups"
mkdir -p "$BACKUP_DIR"
echo "[*] Carpeta de respaldos: $BACKUP_DIR"
echo "[*] Extrayendo propiedades de hardware locales del equipo..."
getprop ro.product.model > "$BACKUP_DIR/dispositivo_info.txt" 2>&1 || true
getprop ro.build.display.id >> "$BACKUP_DIR/dispositivo_info.txt" 2>&1 || true
getprop ro.carrier >> "$BACKUP_DIR/dispositivo_info.txt" 2>&1 || true
echo "[OK] Respaldo de información de dispositivo guardado en $BACKUP_DIR/dispositivo_info.txt"
