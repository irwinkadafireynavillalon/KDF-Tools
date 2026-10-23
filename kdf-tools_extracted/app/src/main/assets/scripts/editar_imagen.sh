#!/usr/bin/env bash
# editar_imagen.sh - Usa ImageMagick para editar imágenes
# Uso: ./editar_imagen.sh <input_file> <operation> <parameter>
# Operaciones soportadas:
#   resize <WIDTHxHEIGHT>    -> redimensionar
#   crop   <WIDTHxHEIGHT+X+Y> -> recortar
#   rotate <DEGREES>          -> rotar
#   overlay <overlay_png>      -> superponer una imagen (centrada)

set -euo pipefail

if [[ $# -lt 3 ]]; then
  echo "Uso: $0 <input> <operacion> <parametro>"
  exit 1
fi

INFILE="$1"
OP="$2"
PARAM="$3"
OUTFILE="$(dirname "$INFILE")/edited_$(basename "$INFILE")"

case "$OP" in
  resize)
    convert "$INFILE" -resize "$PARAM" "$OUTFILE"
    ;;
  crop)
    convert "$INFILE" -crop "$PARAM" "$OUTFILE"
    ;;
  rotate)
    convert "$INFILE" -rotate "$PARAM" "$OUTFILE"
    ;;
  overlay)
    # $PARAM debe ser la ruta al PNG a superponer
    convert "$INFILE" "$PARAM" -gravity center -composite "$OUTFILE"
    ;;
  *)
    echo "Operación no soportada: $OP"
    exit 1
    ;;
esac

echo "Imagen editada guardada en $OUTFILE"
