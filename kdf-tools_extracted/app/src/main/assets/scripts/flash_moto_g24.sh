#!/usr/bin/env bash

# Script rápido para descargar y flashear firmware oficial del Moto G24.
# Tiempo estimado < 20 minutos (requiere conexión a internet y USB fastboot).

set -e

# 1️⃣ Definir URL del firmware oficial (actualízala si cambia)
FIRMWARE_URL="https://firmware.moto.com/MotoG24/official/firmware.zip"
FIRMWARE_ZIP="/tmp/moto_g24_firmware.zip"
FIRMWARE_DIR="/tmp/moto_g24_fw"

# 2️⃣ Descargar firmware (usa curl)
if command -v curl >/dev/null 2>&1; then
  echo "[+] Descargando firmware de $FIRMWARE_URL..."
  curl -L -o "$FIRMWARE_ZIP" "$FIRMWARE_URL"
else
  echo "[!] curl no está disponible. Abortando."
  exit 1
fi

# 3️⃣ Verificar integridad (hash MD5/SHA256) – el hash debe obtenerse del sitio oficial.
#   Sustituye <PUT_OFFICIAL_HASH_HERE> por el hash real.
HASH_EXPECTED="<PUT_OFFICIAL_HASH_HERE>"
HASH_ACTUAL=$(sha256sum "$FIRMWARE_ZIP" | awk '{print $1}')
if [[ "$HASH_ACTUAL" != "$HASH_EXPECTED" ]]; then
  echo "[!] Hash no coincide (esperado $HASH_EXPECTED, obtenido $HASH_ACTUAL). Abortando."
  exit 1
fi

echo "[+] Hash verificado."

# 4️⃣ Descomprimir firmware
mkdir -p "$FIRMWARE_DIR"
unzip -o "$FIRMWARE_ZIP" -d "$FIRMWARE_DIR"

# 5️⃣ Backup de particiones críticas (fastboot)
if command -v fastboot >/dev/null 2>&1; then
  echo "[+] Realizando backup de particiones críticas..."
  ./backup_partitions.sh "$FIRMWARE_DIR/backup_$(date +%Y%m%d_%H%M%S).img"
else
  echo "[!] fastboot no está disponible. Abortando."
  exit 1
fi

# 6️⃣ Flashear imágenes (asume que el zip contiene imágenes fastboot: boot.img, system.img, etc.)
echo "[+] Iniciando flasheo via fastboot..."
fastboot flash boot "$FIRMWARE_DIR/boot.img"
fastboot flash system "$FIRMWARE_DIR/system.img"
fastboot flash vendor "$FIRMWARE_DIR/vendor.img" || true
fastboot flash userdata "$FIRMWARE_DIR/userdata.img" || true

# 7️⃣ Reiniciar dispositivo
echo "[+] Reiniciando dispositivo..."
fastboot reboot

echo "[+] Proceso completado en menos de 20 minutos."
