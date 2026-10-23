#!/usr/bin/env bash
# generar_imagen.sh - Genera una imagen usando Stable Diffusion API
# Uso: ./generar_imagen.sh "<prompt>" <output_filename_without_ext>
# Requiere que el API de SD esté corriendo en http://127.0.0.1:7860

set -euo pipefail

if [[ $# -lt 2 ]]; then
  echo "Uso: $0 <prompt> <output_name>"
  exit 1
fi

PROMPT="$1"
OUT_NAME="$2"
STATIC_DIR="$(dirname "${BASH_SOURCE[0]}")/../static"
mkdir -p "$STATIC_DIR"
OUTFILE="$STATIC_DIR/${OUT_NAME}.png"

PAYLOAD=$(cat <<EOF
{
  "prompt": "$PROMPT",
  "steps": 25,
  "cfg_scale": 7,
  "width": 512,
  "height": 512,
  "sampler_name": "Euler a"
}
EOF
)

RESP=$(curl -s -X POST http://127.0.0.1:7860/sdapi/v1/txt2img \
    -H "Content-Type: application/json" -d "$PAYLOAD")

# Extraer la primera imagen base64 y guardarla
python3 - <<PY
import sys, json, base64, pathlib
resp = json.load(sys.stdin)
img_b64 = resp['images'][0]
data = base64.b64decode(img_b64)
path = pathlib.Path('$OUTFILE')
path.write_bytes(data)
PY <<< "$RESP"

echo "Imagen guardada en $OUTFILE"
