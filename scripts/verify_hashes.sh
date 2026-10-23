# verify_hashes.sh – Verifica SHA256 de un archivo descargado
# Uso: ./verify_hashes.sh <archivo> <sha256_expected>

#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 2 ]]; then
  echo "Uso: $0 <archivo> <sha256_expected>"
  exit 1
fi

FILE="$1"
EXPECTED="$2"

if [[ ! -f "$FILE" ]]; then
  echo "Error: archivo no encontrado: $FILE"
  exit 1
fi

CALC=$(sha256sum "$FILE" | awk '{print $1}')
if [[ "$CALC" == "$EXPECTED" ]]; then
  echo "Checksum OK"
  exit 0
else
  echo "Checksum FAIL (expected $EXPECTED, got $CALC)"
  exit 1
fi
