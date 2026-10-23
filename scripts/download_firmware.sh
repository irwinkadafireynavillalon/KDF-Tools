# download_firmware.sh – Descarga genérica usando aria2c
# Uso: ./download_firmware.sh <url> <dest_dir>
# Crea el directorio si no existe y descarga el archivo con reintentos.

#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 2 ]]; then
  echo "Uso: $0 <url> <dest_dir>"
  exit 1
fi

URL="$1"
DEST_DIR="$2"

mkdir -p "$DEST_DIR"
# aria2c con 4 conexiones, reintentos y barra de progreso
aria2c -x4 -s4 -k1M --retry-wait=5 --max-tries=5 -d "$DEST_DIR" "$URL"
