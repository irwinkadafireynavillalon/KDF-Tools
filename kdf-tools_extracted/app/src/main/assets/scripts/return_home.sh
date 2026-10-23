#!/usr/bin/env bash
# return_home.sh – Cambia al directorio HOME del usuario
# Uso: source return_home.sh  (para que el cd afecte al shell actual)
# Si se ejecuta directamente, solo muestra la ruta.

if [[ "${BASH_SOURCE[0]}" != "$0" ]]; then
  # El script está siendo sourced
  cd "$HOME"
  echo "[INFO] Cambiado al directorio HOME: $HOME"
else
  echo "$HOME"
fi
