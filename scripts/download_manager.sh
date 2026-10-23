#!/usr/bin/env bash
# download_manager.sh – Gestor de descargas de firmware/ROM/ISO
# Uso: ./download_manager.sh <brand> <model> [country]
# Busca en config/firmware_index.yaml la entrada que coincida y descarga el archivo
# usando download_firmware.sh y luego verifica el checksum con verify_hashes.sh.

set -euo pipefail

if [[ $# -lt 2 ]]; then
  echo "Uso: $0 <brand> <model> [country]"
  exit 1
fi

BRAND="$1"
MODEL="$2"
COUNTRY="${3:-}"  # opcional

# Ruta del índice
INDEX_FILE="$(dirname "${BASH_SOURCE[0]}")/../config/firmware_index.yaml"

# Función para extraer valores con grep/sed (asumiendo formato simple)
extract_entry() {
  awk "BEGIN{RS=\"-\";FS=\"\n\"} \
    /brand: $BRAND/ && /model: $MODEL/ && (\"$COUNTRY\"==\"\" || /country: $COUNTRY/) {print}" "$INDEX_FILE"
}

ENTRY=$(extract_entry)
if [[ -z "$ENTRY" ]]; then
  echo "No se encontró firmware para $BRAND $MODEL $COUNTRY"
  exit 1
fi

# Obtener url y sha256
URL=$(echo "$ENTRY" | grep '^ *url:' | head -n1 | awk -F': ' '{print $2}')
SHA=$(echo "$ENTRY" | grep '^ *sha256:' | head -n1 | awk -F': ' '{print $2}')

if [[ -z "$URL" || -z "$SHA" ]]; then
  echo "Entrada incompleta (url o sha256 faltante)."
  exit 1
fi

DEST_DIR="downloads/${BRAND}/${MODEL}"
mkdir -p "$DEST_DIR"

# Descargar
$(dirname "${BASH_SOURCE[0]}")/download_firmware.sh "$URL" "$DEST_DIR"

# Verificar checksum (asume que el archivo descargado tiene el mismo nombre que en la URL)
FILE_NAME=$(basename "$URL")
$(dirname "${BASH_SOURCE[0]}")/verify_hashes.sh "$DEST_DIR/$FILE_NAME" "$SHA"

echo "Firmware descargado y verificado en $DEST_DIR/$FILE_NAME"
