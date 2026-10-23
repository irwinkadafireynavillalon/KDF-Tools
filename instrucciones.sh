#!/data/data/com.termux/files/usr/bin/bash
# ==============================================================================
# KDF-LuX UNIVERSAL SUITE - GUÍA TÉCNICA Y SCRIPT DE CONFIGURACIÓN AUTOMÁTICA
# ==============================================================
# Este script automatiza la instalación de dependencias del sistema,
# creación del entorno virtual Python, instalación de paquetes Python,
# preparación de scripts de utilidad, y arranque del backend (Flask/FastAPI).
# Opcionalmente soporta Docker‑Compose y un dashboard Node/React.
# ==============================================================================
# Directorio del proyecto: /data/data/com.termux/files/home/KDF-LuX
# ==============================================================================

set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

echo "======================================================================"
echo "⚡ KDF-LuX UNIVERSAL REPAIR SUITE - ESTADO Y CONFIGURACIÓN"
echo "======================================================================"

echo ""
echo "📋 1. RESUMEN DE LA ARQUITECTURA"
echo "----------------------------------------------------------------------"
echo "• Backend: Python 3 + Flask (app.py) con soporte de Server-Sent Events (SSE)."
echo "• Inteligencia Artificial Doble Núcleo:"
echo "   1) Cloud: Google Gemini 3.8 Flash (Proyecto kdf-studio)."
echo "   2) Local: LLaMA 3.2 1B / Gemma 3 en servidor local (127.0.0.1:8080)."
echo "• Operaciones de Reparación:"
echo "   - Modo seguro (enter_safe_mode.sh)"
echo "   - Limpieza de memoria y wipe (clean_memory.sh)"
echo "   - Flasheo Motorola Moto G24 (flash_moto_g24.sh)"
echo "   - Desbrickeo y rescate bootloop (unbrick_recovery.sh)"
echo "   - Creador USB Booteable UEFI/BIOS (create_usb_boot.sh)"
echo "   - Respaldo de particiones críticas (backup_partitions.sh)"
echo "   - Salida segura y regreso al home (return_home.sh)"

echo ""
echo "🔍 2. COMPROBACIÓN DE DEPENDENCIAS DEL SISTEMA"
echo "----------------------------------------------------------------------"

# Permisos de ejecución en todos los scripts
chmod +x "$DIR"/scripts/*.sh 2>/dev/null || true
chmod +x "$DIR"/run.sh 2>/dev/null || true

# Verificación de paquetes
echo -n "[*] Python 3: "
if command -v python3 >/dev/null 2>&1; then
    echo "INSTALADO ($(python3 --version))"
else
    echo "FALTA -> Ejecutar: pkg install python -y"
fi

echo -n "[*] Android Tools (ADB & Fastboot): "
if command -v adb >/dev/null 2>&1 && command -v fastboot >/dev/null 2>&1; then
    echo "INSTALADO"
else
    echo "FALTA -> Ejecutar: pkg install android-tools -y"
fi

echo -n "[*] Motor de IA Local (llama-server): "
if command -v llama-server >/dev/null 2>&1; then
    echo "INSTALADO"
else
    echo "NO ENCONTRADO EN PATH"
fi

echo -n "[*] Librería PyYAML y Requests: "
if python3 -c "import yaml, requests, flask" >/dev/null 2>&1; then
    echo "OK (flask, pyyaml, requests listos)"
else
    echo "Instalando dependencias de Python..."
    pip install flask pyyaml requests -q || true
fi

echo ""
echo "🦙 3. MODELOS DE INTELIGENCIA ARTIFICIAL DISPONIBLES EN TU DISPOSITIVO"
echo "----------------------------------------------------------------------"
LLAMA_MODEL="/storage/emulated/0/Download/KDF_Models/Llama-3.2-1B-Instruct-Q4_K_M.gguf"
GEMMA_MODEL="/storage/emulated/0/Download/Archivos_Recuperados_USB/gemma-3-270m.Q8_0.gguf"

if [ -f "$LLAMA_MODEL" ]; then
    echo "  [✓] Llama 3.2 1B Instruct: $LLAMA_MODEL"
fi
if [ -f "$GEMMA_MODEL" ]; then
    echo "  [✓] Gemma 3 270M (Ultra ligero): $GEMMA_MODEL"
fi

echo ""
echo "🚀 4. CÓMO INICIAR TODO EL SISTEMA"
echo "----------------------------------------------------------------------"
echo "• Opción A (Arrancar Panel Web KDF-LuX):"
echo "    cd $DIR"
echo "    ./run.sh"
echo "    -> Luego abre en tu navegador: http://localhost:5000"
echo ""
echo "• Opción B (Arrancar el Servidor de IA Local Llama 3.2):"
echo "    llama-server -m $LLAMA_MODEL --host 127.0.0.1 --port 8080 -c 2048 -t 4"
echo ""
echo "⚙️ 5. LO QUE DEBES CONFIGURAR TÚ (SI APLICA):"
echo "----------------------------------------------------------------------"
echo "1. En el teléfono objetivo que conectes por cable OTG:"
echo "   - Activar 'Opciones de desarrollador' y 'Depuración USB'."
echo "   - Aceptar la huella RSA en la pantalla del celular objetivo."
echo "2. Para cambiar tu clave de Google Gemini (opcional):"
echo "   - export GEMINI_API_KEY='TU_CLAVE_AQUI'"
echo "======================================================================"
