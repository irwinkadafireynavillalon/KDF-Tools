#!/usr/bin/env bash
# ============================================================
# KDF-LuX · setup.sh
# Instalador completo: librerías sistema + Python + IA Local
# ============================================================
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

CYAN='\033[0;36m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

log()  { echo -e "${CYAN}[KDF]${NC} $1"; }
ok()   { echo -e "${GREEN}[✓]${NC} $1"; }
warn() { echo -e "${YELLOW}[⚠]${NC} $1"; }
err()  { echo -e "${RED}[✗]${NC} $1"; }

echo ""
echo -e "${CYAN}=========================================================="
echo -e "  ⚡ KDF-LuX · INSTALADOR DE DEPENDENCIAS COMPLETO"
echo -e "==========================================================${NC}"
echo ""

# ════════════════════════════════════════════════════════════
# 1. PAQUETES DE SISTEMA (Termux)
# ════════════════════════════════════════════════════════════
log "PASO 1/6 · Actualizando repositorios Termux..."
pkg update -y 2>/dev/null || warn "No se pudo actualizar repos (sin internet?)"

log "Instalando paquetes de sistema..."
SYSTEM_PKGS=(
    python          # Python 3
    android-tools   # ADB + Fastboot
    libusb          # USB nativo
    curl wget       # Descarga de firmware
    unzip zip       # Descompresión
    git             # Control de versiones
    openssl         # Criptografía
    termux-api      # APIs nativas Termux (USB, sensores, etc.)
    libimobiledevice # Herramientas iOS (ideviceinfo, idevicerestore)
    ideviceinstaller # Instalar IPA en iPhone
    nmap            # Diagnóstico de red
    openssh         # ADB over Wi-Fi / SSH
)

for pkg in "${SYSTEM_PKGS[@]}"; do
    if pkg install -y "$pkg" >/dev/null 2>&1; then
        ok "$pkg instalado"
    else
        warn "$pkg: no disponible o ya instalado"
    fi
done

# ════════════════════════════════════════════════════════════
# 2. LLAMA-SERVER (IA Local)
# ════════════════════════════════════════════════════════════
log ""
log "PASO 2/6 · Verificando llama-server (IA Local)..."

if command -v llama-server >/dev/null 2>&1; then
    ok "llama-server ya disponible: $(which llama-server)"
else
    warn "llama-server no encontrado. Intentando instalar llama.cpp..."
    # Intentar instalar desde pkg
    if pkg install -y llama.cpp >/dev/null 2>&1; then
        ok "llama.cpp instalado desde pkg"
    else
        warn "No disponible en pkg. Compilando desde fuente..."
        if [ -d "$HOME/llama.cpp" ]; then
            cd "$HOME/llama.cpp"
            log "Compilando llama-server (puede tardar 5-10 min)..."
            cmake -B build -DLLAMA_NATIVE=ON -DLLAMA_BUILD_SERVER=ON \
                  -DCMAKE_BUILD_TYPE=Release . 2>/dev/null && \
            cmake --build build --target llama-server -j4 2>/dev/null && \
            cp build/bin/llama-server "$PREFIX/bin/" && \
            ok "llama-server compilado e instalado" || \
            err "Error compilando llama-server"
            cd "$DIR"
        else
            err "llama.cpp no encontrado en ~/llama.cpp"
            warn "Instala manualmente: pkg install llama.cpp"
        fi
    fi
fi

# ════════════════════════════════════════════════════════════
# 3. ENTORNO VIRTUAL PYTHON
# ════════════════════════════════════════════════════════════
log ""
log "PASO 3/6 · Configurando entorno virtual Python..."

if [ ! -d "$DIR/.venv" ]; then
    python3 -m venv "$DIR/.venv" 2>/dev/null || {
        warn "venv no disponible, usando pip global"
        GLOBAL_PIP=true
    }
fi

if [ -d "$DIR/.venv" ]; then
    source "$DIR/.venv/bin/activate"
    ok "Entorno virtual activado: $DIR/.venv"
    PIP="$DIR/.venv/bin/pip"
else
    PIP="pip3"
fi

# ════════════════════════════════════════════════════════════
# 4. PAQUETES PYTHON
# ════════════════════════════════════════════════════════════
log ""
log "PASO 4/6 · Instalando paquetes Python..."

# Core primero (sin fallar todo si alguno no compila)
CORE_PKGS="flask werkzeug requests httpx pyyaml numpy rich psutil websockets"
log "  Instalando core: $CORE_PKGS"
$PIP install --quiet --upgrade $CORE_PKGS && ok "Core packages instalados" || err "Error en core packages"

# OpenAI-compatible client (para llamar llama-server)
log "  Instalando cliente OpenAI (compatible llama-server)..."
$PIP install --quiet openai>=1.30.0 && ok "openai client instalado" || warn "openai no instalado"

# Google Gemini SDK
log "  Instalando Google Generative AI SDK..."
$PIP install --quiet google-generativeai && ok "google-generativeai instalado" || warn "google-generativeai no instalado"

# iOS tools
log "  Instalando pymobiledevice3 (iOS/iCloud tools)..."
$PIP install --quiet pymobiledevice3 && ok "pymobiledevice3 instalado" || warn "pymobiledevice3 no instalado"

# USB / Serial
log "  Instalando pyusb y pyserial..."
$PIP install --quiet pyusb pyserial aiohttp && ok "pyusb/pyserial/aiohttp instalados" || warn "Error USB libs"

echo ""
ok "Todos los paquetes Python procesados"
$PIP list --format=columns 2>/dev/null | grep -E "Flask|requests|openai|google|yaml|numpy|websockets|pymobile" || true

# ════════════════════════════════════════════════════════════
# 5. MODELOS IA LOCAL
# ════════════════════════════════════════════════════════════
log ""
log "PASO 5/6 · Verificando modelos de IA local..."

MODEL_LLAMA="$HOME/modelos_kdf/Llama-3.2-1B-Instruct-Q4_K_M.gguf"
MODEL_GEMMA="/storage/emulated/0/Download/Archivos_Recuperados_USB/gemma-3-270m.Q8_0.gguf"
MODEL_LLAMA_EXT="/storage/emulated/0/Download/KDF_Models/Llama-3.2-1B-Instruct-Q4_K_M.gguf"

if [ -f "$MODEL_LLAMA" ]; then
    SIZE=$(du -h "$MODEL_LLAMA" | cut -f1)
    ok "Llama 3.2 1B: $MODEL_LLAMA ($SIZE)"
elif [ -f "$MODEL_LLAMA_EXT" ]; then
    warn "Llama encontrado en storage externo. Copiando a modelos_kdf/..."
    mkdir -p "$HOME/modelos_kdf"
    cp "$MODEL_LLAMA_EXT" "$MODEL_LLAMA" && ok "Llama 3.2 1B copiado a $MODEL_LLAMA"
else
    warn "Llama 3.2 1B no encontrado. Descargando modelo ligero (~680MB)..."
    mkdir -p "$HOME/modelos_kdf"
    curl -L --progress-bar \
        "https://huggingface.co/bartowski/Llama-3.2-1B-Instruct-GGUF/resolve/main/Llama-3.2-1B-Instruct-Q4_K_M.gguf" \
        -o "$MODEL_LLAMA" && ok "Llama 3.2 1B descargado" || err "Error descargando modelo"
fi

if [ -f "$MODEL_GEMMA" ]; then
    SIZE=$(du -h "$MODEL_GEMMA" | cut -f1)
    ok "Gemma 3 270M: $MODEL_GEMMA ($SIZE)"
else
    warn "Gemma 3 270M no encontrado (opcional)"
fi

# ════════════════════════════════════════════════════════════
# 6. PERMISOS Y SCRIPTS
# ════════════════════════════════════════════════════════════
log ""
log "PASO 6/6 · Configurando permisos de scripts..."
chmod +x "$DIR"/scripts/*.sh "$DIR/run.sh" "$DIR/setup.sh" "$DIR/start_ai.sh" 2>/dev/null || true
ok "Permisos configurados"

# ════════════════════════════════════════════════════════════
# RESUMEN FINAL
# ════════════════════════════════════════════════════════════
echo ""
echo -e "${CYAN}=========================================================="
echo -e "  ✅ KDF-LuX · INSTALACIÓN COMPLETADA"
echo -e "==========================================================${NC}"
echo ""
echo -e "  ${GREEN}▶ Iniciar IA Local (llama-server):${NC}"
echo -e "    ./start_ai.sh"
echo ""
echo -e "  ${GREEN}▶ Iniciar panel web KDF-LuX:${NC}"
echo -e "    ./run.sh"
echo ""
echo -e "  ${GREEN}▶ Acceder al panel:${NC}"
echo -e "    http://localhost:5000"
echo ""
echo -e "  ${YELLOW}▶ Variables de entorno opcionales:${NC}"
echo -e "    export GEMINI_API_KEY='tu_clave_aqui'"
echo -e "    export PORT=5000"
echo ""
