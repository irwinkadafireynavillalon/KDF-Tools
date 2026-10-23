#!/usr/bin/env bash
# ==============================================================================
# KDF-TOOLS · INSTALADOR DE APK (UNIVERSAL ANDROID / TERMUX)
# ==============================================================================
set -e

REPO_URL="https://github.com/irwinkadafireynavillalon/KDF-Tools"
RELEASE_APK_URL="${REPO_URL}/releases/latest/download/KDF-Tools-LuX.apk"
LOCAL_DIR="$HOME/KDF-LuX/downloads"
APK_FILE="${1:-$LOCAL_DIR/KDF-Tools-LuX.apk}"

mkdir -p "$LOCAL_DIR"

echo -e "\033[1;35m⚡ =====================================================\033[0m"
echo -e "\033[1;36m       KDF-TOOLS · APK DOWNLOADER & INSTALLER\033[0m"
echo -e "\033[1;35m⚡ =====================================================\033[0m"

# Si el archivo no existe o se pasa una URL
if [[ "$1" == http* ]]; then
    echo -e "\033[1;33m[*] Descargando APK desde: $1\033[0m"
    curl -L "$1" -o "$LOCAL_DIR/KDF-Tools-LuX.apk"
    APK_FILE="$LOCAL_DIR/KDF-Tools-LuX.apk"
elif [ ! -f "$APK_FILE" ]; then
    echo -e "\033[1;33m[*] APK local no encontrado. Intentando descargar la última versión de GitHub Releases...\033[0m"
    if curl -fL "$RELEASE_APK_URL" -o "$APK_FILE" 2>/dev/null; then
        echo -e "\033[1;32m[✓] APK descargado exitosamente en: $APK_FILE\033[0m"
    else
        echo -e "\033[1;31m[!] No se pudo descargar automáticamente el release.\033[0m"
        echo -e "\033[1;37m    Asegúrate de haber activado el Workflow en GitHub Actions o pasa la ruta del archivo APK.\033[0m"
        echo -e "\033[1;37m    Uso: $0 [ruta_al_apk o url_del_apk]\033[0m"
        exit 1
    fi
fi

echo -e "\033[1;32m[✓] Archivo APK localizado:\033[0m $APK_FILE"

# Intentar instalación vía ADB si hay dispositivo conectado
if command -v adb >/dev/null 2>&1; then
    DEVICES=$(adb devices | grep -w "device" | awk '{print $1}')
    if [ -n "$DEVICES" ]; then
        echo -e "\033[1;34m[*] Instalando mediante ADB en dispositivo(s) conectado(s)...\033[0m"
        for DEV in $DEVICES; do
            echo -e "\033[1;36m    -> Instalando en $DEV...\033[0m"
            adb -s "$DEV" install -r "$APK_FILE" && echo -e "\033[1;32m    [✓] ¡Instalado con éxito en $DEV!\033[0m"
        done
        exit 0
    fi
fi

# Si no hay ADB conectado, usar el instalador nativo del sistema de Android vía Termux
if command -v termux-open >/dev/null 2>&1; then
    echo -e "\033[1;34m[*] Abriendo instalador de paquetes nativo de Android...\033[0m"
    termux-open "$APK_FILE"
    echo -e "\033[1;32m[✓] Instalador lanzado. Confirma la instalación en la ventana emergente de tu pantalla.\033[0m"
elif [ -f "/data/data/com.termux/files/usr/bin/termux-open" ]; then
    echo -e "\033[1;34m[*] Abriendo instalador de paquetes nativo de Android...\033[0m"
    /data/data/com.termux/files/usr/bin/termux-open "$APK_FILE"
    echo -e "\033[1;32m[✓] Instalador lanzado. Confirma la instalación en la ventana emergente de tu pantalla.\033[0m"
else
    echo -e "\033[1;33m[!] No se detectó termux-open. El archivo APK está listo en:\033[0m"
    echo "    $APK_FILE"
    echo "    Puedes abrirlo con tu gestor de archivos favorito en Android para instalarlo."
fi
