#!/data/data/com.termux/files/usr/bin/bash
# ================================================================
# KDF-Tools · run.sh — Lanzador Principal Mewtwo Edition
# Spark Core + Gemini 3.8 Flash + llama-server :8080 + Panel Web :5000
# ================================================================

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

CYAN='\033[0;36m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
PURPLE='\033[0;35m'
NC='\033[0m'

echo -e "${PURPLE}"
echo "  =========================================================="
echo "  ⚡  KDF-Tools · UNIVERSAL SUITE (MEWTWO BIMETALLIC)"
echo "  Spark Core + Google Gemini 3.8 Flash + ADB/Fastboot/EDL"
echo "  =========================================================="
echo -e "${NC}"

# ── Activar entorno virtual ──────────────────────────────────
if [ -d "$DIR/.venv" ]; then
    source "$DIR/.venv/bin/activate"
    echo -e "${GREEN}[✓] Entorno virtual activado${NC}"
fi

export PORT=${PORT:-5000}
export GEMINI_API_KEY=${GEMINI_API_KEY:-"AIzaSyAwk5I9UzFXinqQO4UAgo2-m3Xt-Sa9DLY"}

# ── Verificar/Iniciar IA Local ────────────────────────────────
echo ""
echo -e "${CYAN}[1/2] Verificando IA Local KDF-STUDIO (Gemma 2B en :4250)...${NC}"

if curl -s --max-time 2 "http://127.0.0.1:4250/health" >/dev/null 2>&1; then
    echo -e "${GREEN}[✓] IA Local ACTIVA en http://127.0.0.1:4250 (IA-LuX Gemma 2B)${NC}"
else
    echo -e "${YELLOW}[⚠] IA Local no detectada en :4250. Iniciando demonio KDF-STUDIO...${NC}"
    kdf-studio wake >/dev/null 2>&1 || nohup python3 "$HOME/.kdf-studio/bin/kdf_studio_server.py" >/dev/null 2>&1 &
    sleep 2
    if curl -s --max-time 2 "http://127.0.0.1:4250/health" >/dev/null 2>&1; then
        echo -e "${GREEN}[✓] Demonio KDF-STUDIO iniciado correctamente.${NC}"
    else
        echo -e "${YELLOW}[i] Continuando con Gemini Cloud como motor de respaldo.${NC}"
    fi
fi

# ── Mostrar IP local ─────────────────────────────────────────
echo ""
echo -e "${CYAN}[2/2] Iniciando Panel Web KDF-Tools en :$PORT ...${NC}"
IP=$(ip route get 1.1.1.5 2>/dev/null | awk '{print $7}' | head -1)

echo ""
echo "  =========================================================="
echo -e "  ${GREEN}Panel Web:${NC}  http://localhost:$PORT"
if [ -n "$IP" ]; then
    echo -e "  ${GREEN}Red local:${NC}  http://$IP:$PORT"
fi
echo -e "  ${GREEN}IA Local:${NC}   http://127.0.0.1:8080"
echo -e "  ${GREEN}AI Status:${NC}  http://localhost:$PORT/api/ai_status"
echo "  =========================================================="
echo ""

# ── Lanzar Flask ──────────────────────────────────────────────
python3 app.py
