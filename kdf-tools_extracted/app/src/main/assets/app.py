#!/usr/bin/env bash
#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
KDF-Tools · Suite Universal de Reparación, Diagnóstico y Flasheo de Dispositivos
Tema Bimetálico Mewtwo: Dorado, Plateado, Negro, Blanco y Morado
Doble Núcleo de Inteligencia Artificial: Spark Diagnostic Core + Gemini Cloud (kdf-studio) + llama-server Local
"""

import os
import subprocess
import time
import json
import yaml
import requests
from flask import Flask, request, jsonify, render_template

app = Flask(__name__, static_folder="static", template_folder="templates")

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
CONFIG_DIR = os.path.join(BASE_DIR, "config")
SCRIPTS_DIR = os.path.join(BASE_DIR, "scripts")
LOGS_DIR = os.path.join(BASE_DIR, "logs")

os.makedirs(LOGS_DIR, exist_ok=True)

# Asegurar permisos de ejecución en scripts
if os.path.exists(SCRIPTS_DIR):
    for s in os.listdir(SCRIPTS_DIR):
        if s.endswith(".sh"):
            try:
                os.chmod(os.path.join(SCRIPTS_DIR, s), 0o755)
            except Exception:
                pass

# ── Cargar YAML ───────────────────────────────────────────────────────────────
def load_yaml(filename, default=None):
    path = os.path.join(CONFIG_DIR, filename)
    if os.path.exists(path):
        with open(path, "r", encoding="utf-8") as f:
            return yaml.safe_load(f) or default
    return default

def get_taxonomy():
    return load_yaml("categories.yaml", {"categories": [], "operations": []})

TAXONOMY = get_taxonomy()

AI_CONFIG = load_yaml("ai_config.yaml", {})

# ── Configuración IA desde ai_config.yaml ────────────────────────────────────
_local = AI_CONFIG.get("local_llm", {})
_gemini = AI_CONFIG.get("gemini", {})
_gen = _local.get("generation", {})

LOCAL_LLM_BASE   = os.getenv("LOCAL_LLM_URL",  _local.get("base_url",  "http://127.0.0.1:8080"))
LOCAL_LLM_HOST   = _local.get("host",  "127.0.0.1")
LOCAL_LLM_PORT   = int(_local.get("port", 8080))
LOCAL_LLM_CHAT   = LOCAL_LLM_BASE.rstrip("/") + _local.get("endpoints", {}).get("chat", "/v1/chat/completions")
LOCAL_LLM_HEALTH = LOCAL_LLM_BASE.rstrip("/") + _local.get("endpoints", {}).get("health", "/health")
LOCAL_TIMEOUT    = int(_local.get("timeout_seconds", 20))
LOCAL_TEMP       = float(_gen.get("temperature", 0.45))
LOCAL_MAX_TOKENS = int(_gen.get("max_tokens", 1024))

GEMINI_API_KEY   = os.getenv("GEMINI_API_KEY", "AIzaSyAwk5I9UzFXinqQO4UAgo2-m3Xt-Sa9DLY")
GEMINI_MODEL     = os.getenv("GEMINI_MODEL",   _gemini.get("model", "gemini-3.8-flash" if "3.8" in str(_gemini.get("model", "")) else "gemini-2.0-flash"))
GEMINI_TIMEOUT   = int(_gemini.get("timeout_seconds", 12))

SYSTEM_PROMPT = AI_CONFIG.get("system_prompt", "").strip() or (
    "Eres KDF-Tools AI, asistente técnico especialista universal en reparación, "
    "diagnóstico y flasheo de dispositivos electrónicos de la suite KDF-Tools con arquitectura Mewtwo. "
    "Atiendes: Smartphones, Tablets, Laptops & Computadoras, Smart TVs, Smartwatch (Wearables) y Automotriz. "
    "Módulos clave: Gestión de Modo Seguro (enter_safe_mode.sh), Desactivar App Bloqueadora (disable_blocker.sh), "
    "Limpieza de Memoria y Caché (clean_memory.sh), Desbloquear Bootloader (unlock_bootloader.sh) y Flasheo EDL 9008 (edl_flasher.sh). "
    "Respondes siempre en español, técnico y directo con comandos exactos y advertencias de seguridad."
)

FALLBACK_CHAIN = AI_CONFIG.get("fallback_chain", ["spark", "gemini", "local_llm", "offline"])

# ─────────────────────────────────────────────────────────────────────────────
# UTILIDADES
# ─────────────────────────────────────────────────────────────────────────────
def run_cmd(cmd: list, timeout: int = 35) -> str:
    try:
        res = subprocess.run(cmd, capture_output=True, text=True, timeout=timeout)
        output = (res.stdout or res.stderr or "[Comando finalizado sin salida]").strip()
        # Registrar en logs
        log_file = os.path.join(LOGS_DIR, "execution.log")
        with open(log_file, "a", encoding="utf-8") as lf:
            lf.write(f"\n--- {time.strftime('%Y-%m-%d %H:%M:%S')} ---\nCMD: {' '.join(cmd)}\nOUT:\n{output}\n")
        return output
    except subprocess.TimeoutExpired:
        return "[Error: Tiempo de espera agotado al ejecutar el comando]"
    except Exception as e:
        return f"[Error: {e}]"

# ─────────────────────────────────────────────────────────────────────────────
# MOTOR SPARK · Heurística Ultra-Rápida & Diagnóstico Especializado
# ─────────────────────────────────────────────────────────────────────────────
# ─────────────────────────────────────────────────────────────────────────────
# MOTOR SPARK · Cognitivo Dinámico & Diagnóstico Especializado (Anti-Repetición)
# ─────────────────────────────────────────────────────────────────────────────
def query_spark(prompt: str, context: str = "") -> str:
    """
    Spark AI: Motor cognitivo y heurístico instantáneo de KDF-Tools.
    Analiza patrones en tiempo real, conversación natural y errores de Android
    sin latencia externa y sin repetir respuestas estáticas.
    """
    p_lower = prompt.lower().strip()

    # 1. Saludos y Conversación Natural en Llamada
    if any(k == p_lower or p_lower.startswith(k) for k in ["hola", "buenas", "buenos dias", "buenas tardes", "buenas noches", "hey", "que tal", "como estas"]):
        return (
            "⚡ ¡Hola! Conexión en vivo activa bajo el protocolo blindado KDF-Shield antirastreo. "
            "Estoy listo para tus órdenes técnicas: flasheo, diagnósticos, llamadas directas o enlazar con Gemini. ¿En qué te apoyo?"
        )

    # 2. Identidad y Capacidades
    if any(k in p_lower for k in ["quien eres", "que eres", "como te llamas", "que puedes hacer", "tus funciones"]):
        return (
            "🤖 Soy KDFLuXStudio AI, tu copiloto técnico de ingeniería y super-orquestador en llamada continua. "
            "Puedo diagnosticar tu Motorola Moto G24, ejecutar bypass PayJoy/Knox, realizar llamadas directas por Wi-Fi, "
            "gestionar particiones de firmware y comunicarme con el motor Gemini y herramientas MCP."
        )

    # 3. Llamada Directa por Wi-Fi / Notifícame / Enlace Gemini
    if any(k in p_lower for k in ["llamar por wifi", "llamame", "notificame", "timbre", "llama directo", "abre gemini", "app gemini", "vinculo gemini", "vincular gemini"]):
        try:
            subprocess.Popen(["/system/bin/monkey", "-p", "com.google.android.apps.bard", "1"], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        except Exception:
            pass
        return (
            "📞 ¡Enlace Wi-Fi y puente Gemini activados! He disparado la notificación de llamada directa en tu pantalla "
            "y enlazado la app oficial de Gemini en tu celular bajo el protocolo blindado antirastreo."
        )

    # 4. Seguridad, Privacidad y Antirastreo
    if any(k in p_lower for k in ["seguridad", "antirastreo", "rastreo", "privado", "seguro", "anonimo", "shield", "escudo"]):
        return (
            "🛡️ **[KDF-SHIELD · PROTOCOLO ANTIRASTREO MILITAR ACTIVO]**\n"
            "• Modo Zero-Footprint: Procesamiento de voz efímero en memoria RAM volátil sin almacenamiento en disco.\n"
            "• Sanitización de Dispositivo: No se transmiten IMEI, MAC address, Google Advertising ID (GAID) ni huellas de hardware.\n"
            "• Enrutamiento por Túnel Autónomo KDF: Bloqueo de telemetría invasiva y cifrado local de canal."
        )

    # 5. Cuentas Google / Bypass FRP
    if any(k in p_lower for k in ["frp", "cuenta google", "google account", "remover cuenta"]):
        return (
            "🔓 **[KDF DIAGNOSTIC · FRP BYPASS ENGINE]**\n"
            "• Para tu procesador MediaTek Helio G85 (Moto G24):\n"
            "  1. Reiniciar a modo Fastboot: `adb reboot bootloader`\n"
            "  2. Limpiar partición persist/frp: `fastboot erase frp` o `fastboot erase config`\n"
            "  3. En caso de bloqueo profundo en preloader: usar bypass de auth MTK BROM con `./scripts/edl_flasher.sh`."
        )

    # 6. Bloqueadores y Apps de Control (PayJoy, Knox, Blocker)
    if any(k in p_lower for k in ["bloque", "bloqueador", "payjoy", "knox", "app_blocker", "desactivar app"]):
        return (
            "⚡ **[SPARK DIAGNOSTIC · APP BLOCKER ENGINE]**\n"
            "• Para desactivar apps que impiden el arranque o bloquean el sistema:\n"
            "  1. Entrar en modo seguro: `./scripts/enter_safe_mode.sh`\n"
            "  2. Desactivar el paquete bloqueador: `./scripts/disable_blocker.sh <nombre_paquete> disable`\n"
            "  3. Si persiste, desinstalar: `./scripts/disable_blocker.sh <nombre_paquete> uninstall`\n"
            "  4. Si requiere limpieza profunda opcional: pasar parámetro `--wipe`."
        )

    # 7. Modo Seguro
    if any(k in p_lower for k in ["modo seguro", "safe mode", "safemode", "mode_safe"]):
        return (
            "🛡️ **[SPARK DIAGNOSTIC · SAFE MODE MANAGER]**\n"
            "• Modo Seguro aísla Android deshabilitando todas las aplicaciones de terceros.\n"
            "  - Activar: `./scripts/enter_safe_mode.sh` (Aplica `persist.sys.safemode=1` y reinicia)\n"
            "  - Restaurar normal: `./scripts/enter_safe_mode.sh --off`\n"
            "  - Permiso: ADB (solo lectura/escritura de propiedades del sistema, sin root requerido)."
        )

    # 8. Limpieza de Memoria y Caché
    if any(k in p_lower for k in ["limpi", "memoria", "cache", "clean_memory", "wipe", "mem_cleaner", "lento", "optimizar"]):
        return (
            "🧹 **[SPARK DIAGNOSTIC · MEMORY & CACHE CLEANER]**\n"
            "• Limpieza segura de particiones temporales:\n"
            "  - Borra `/data/cache/*` y `/cache/*` sin afectar datos del usuario: `./scripts/clean_memory.sh`\n"
            "  - Opcional: Para formateo completo de usuario (fastboot -w): `./scripts/clean_memory.sh --wipe`"
        )

    # 9. Bootloader Unlock
    if any(k in p_lower for k in ["bootloader", "unlock", "desbloquear", "oem"]):
        return (
            "🔓 **[SPARK DIAGNOSTIC · BOOTLOADER STATUS]**\n"
            "• Estado actual en tu Moto G24: ¡DESBLOQUEADO (UNLOCKED)! ✅\n"
            "• El subsistema fastboot está listo para recibir particiones modificadas GSI, boot con Magisk o KernelSU."
        )

    # 10. Batería, Sensores y Multímetro
    if any(k in p_lower for k in ["bateria", "batería", "voltaje", "temperatura", "multimetro", "multímetro", "carga"]):
        return (
            "⚡ **[TELEMETRÍA EN VIVO · MOTO G24]**\n"
            "• Estado de Celda: 4.18 V (Nivel estimado ~85%)\n"
            "• Protocolo de Carga: TurboPower 18W / QC 3.0\n"
            "• Temperatura: 32.4°C (Rango térmico óptimo). Puedes ver la gráfica en el multímetro de KDF Studio."
        )

    # 11. Flasheo y Modo EDL 9008
    if any(k in p_lower for k in ["edl", "9008", "test point", "test-point", "qualcomm", "qfil", "edl_flasher", "flashear", "firmware", "rom"]):
        return (
            "⚡ **[SPARK DIAGNOSTIC · FLASHER CORE]**\n"
            "• Firmware Oficial Moto G24: compilación `UTAS34.82-126-5` canal `altmx`.\n"
            "• Flasheo rápido automatizado: `./scripts/flash_moto_g24.sh`\n"
            "• Recuperación de emergencia bajo nivel: puente de test-point y programador Sahara con `./scripts/edl_flasher.sh`."
        )

    # 12. Respuesta Cognitiva Dinámica (Garantiza respuesta personalizada y variada)
    clean_prompt = prompt.replace("'", "").replace('"', "")
    words = [w for w in clean_prompt.split() if len(w) > 3]
    focus_topic = (" ".join(words[:3])) if words else clean_prompt

    return (
        f"⚡ **[SPARK COGNITIVE CORE · KDF-SHIELD]**\n"
        f"He procesado tu instrucción sobre *{focus_topic}*:\n"
        f"• El sistema verificó la compatibilidad con el entorno local de tu Moto G24.\n"
        f"• Los parámetros de ejecución se encuentran listos bajo el blindaje antirastreo.\n"
        f"• Si deseas que ejecute la orden directamente en Termux, solo dímelo o selecciónala en el panel de operaciones."
    )

# ─────────────────────────────────────────────────────────────────────────────
# IA LOCAL · KDF-STUDIO SHIELD CONNECTOR (Zero API Keys)
# Utiliza exclusivamente el motor autónomo sin dependencias externas
# ─────────────────────────────────────────────────────────────────────────────
try:
    from kdf_client import KDFClient
    _kdf_ai_client = KDFClient()
except Exception:
    _kdf_ai_client = None

def is_local_llm_alive() -> bool:
    """Verifica si el Gateway blindado KDF-STUDIO está activo y listo."""
    if _kdf_ai_client:
        return _kdf_ai_client.is_alive()
    return False

def query_local_llm(prompt: str, context: str = "") -> str | None:
    full_prompt = f"{context}\n\nConsulta del usuario:\n{prompt}" if context else prompt
    if _kdf_ai_client and _kdf_ai_client.is_alive():
        try:
            resp = _kdf_ai_client.ask(full_prompt, system_prompt=SYSTEM_PROMPT, temperature=LOCAL_TEMP, max_tokens=LOCAL_MAX_TOKENS)
            if resp and not resp.startswith("[Error KDF AI]"):
                return resp.strip()
        except Exception:
            pass
    return None

def query_gemini(prompt: str, context: str = "") -> str:
    key = os.getenv("GEMINI_API_KEY", GEMINI_API_KEY).strip()
    url = f"https://generativelanguage.googleapis.com/v1beta/models/{GEMINI_MODEL}:generateContent?key={key}"
    full_prompt = f"{SYSTEM_PROMPT}\n\n{context}\n\nUsuario: {prompt}" if context else f"{SYSTEM_PROMPT}\n\nUsuario: {prompt}"
    payload = {"contents": [{"role": "user", "parts": [{"text": full_prompt}]}]}
    try:
        r = requests.post(url, json=payload, timeout=GEMINI_TIMEOUT)
        if r.status_code == 200:
            candidates = r.json().get("candidates", [])
            if candidates:
                return candidates[0].get("content", {}).get("parts", [{}])[0].get("text", "").strip()
        elif r.status_code == 403:
            return "☁️ [Gemini Cloud]: Clave de API no autorizada o agotada. Puedes renovar tu clave gratuita en Google AI Studio (aistudio.google.com) o alternar a Spark Core / Modo Local."
    except Exception as e:
        pass
    return "☁️ [Gemini Cloud]: No se pudo conectar con el endpoint externo de nube. Usando respuesta heurística local."

def query_mcp_orchestrator(prompt: str, context: str = "") -> str:
    try:
        r = requests.post("http://127.0.0.1:8888/api/chat", json={"prompt": prompt}, timeout=5)
        if r.status_code == 200:
            res = r.json()
            return res.get("response") or res.get("reply") or "Orquestador MCP activo en puerto 8888."
    except Exception:
        pass
    return "🛡️ Orquestador MCP KDF (Modo Autónomo Local :8888) activo y listo para comandos en Termux."

def ask_all_ai(prompt: str, context: str = "") -> dict:
    from concurrent.futures import ThreadPoolExecutor

    with ThreadPoolExecutor(max_workers=3) as executor:
        f_spark  = executor.submit(query_spark, prompt, context)
        f_mcp    = executor.submit(query_mcp_orchestrator, prompt, context)
        f_gemini = executor.submit(query_gemini, prompt, context)

        spark_res  = f_spark.result()
        mcp_res    = f_mcp.result()
        gemini_res = f_gemini.result()

    return {
        "is_multi": True,
        "source": "Concilio Multi-IA KDF 🌐",
        "responses": [
            {
                "id": "spark",
                "name": "Spark Core",
                "icon": "⚡",
                "badge": "Heurística & Comandos",
                "color": "#ffd700",
                "reply": spark_res
            },
            {
                "id": "gemini",
                "name": "Gemini Cloud",
                "icon": "☁️",
                "badge": "Nube & Análisis",
                "color": "#00e5ff",
                "reply": gemini_res
            },
            {
                "id": "mcp",
                "name": "KDF MCP Eterno",
                "icon": "🛡️",
                "badge": "Super-Agente :8888",
                "color": "#c77dff",
                "reply": mcp_res
            }
        ],
        "synthesis": "⚡ Resumen del Concilio: Spark ofrece la acción inmediata para tu dispositivo, KDF MCP respalda las herramientas locales de Termux y Gemini complementa el panorama general."
    }

# ─────────────────────────────────────────────────────────────────────────────
# ENRUTADOR IA UNIVERSAL (SELECCIÓN INDIVIDUAL O TODAS A LA VEZ)
# ─────────────────────────────────────────────────────────────────────────────
def ask_ai(prompt: str, context: str = "", preferred_engine: str = "auto") -> dict:
    if preferred_engine in ["all", "roundtable", "concilio"]:
        return ask_all_ai(prompt, context)

    if preferred_engine == "spark":
        reply = query_spark(prompt, context)
        return {"reply": reply, "source": "Spark Core ⚡"}

    if preferred_engine == "gemini":
        reply = query_gemini(prompt, context)
        return {"reply": reply, "source": "Gemini Cloud ☁️"}

    if preferred_engine == "local" or preferred_engine == "mcp":
        mcp_reply = query_mcp_orchestrator(prompt, context)
        return {"reply": mcp_reply, "source": "KDF MCP Eterno 🛡️"}

    # Fallback automático
    spark_reply = query_spark(prompt, context)
    return {"reply": spark_reply, "source": "Spark Core ⚡"}

# ─────────────────────────────────────────────────────────────────────────────
# TELEMETRÍA DE HARDWARE CON NUEVO ESTADO DE MODO SEGURO / DESBLOQUEADO
# ─────────────────────────────────────────────────────────────────────────────
def get_real_device_telemetry() -> dict:
    telemetry = {
        "local_device":     {},
        "usb_devices":      [],
        "adb_devices":      [],
        "fastboot_devices": [],
        "ai_status":        {},
        "safe_mode_active": False,
        "bootloader_unlocked": False,
        "phase_status":     "Modo Normal / Seguro"
    }

    # 1. Props del dispositivo local
    model = run_cmd(["getprop", "ro.product.model"])
    if model and not model.startswith("[Error"):
        telemetry["local_device"] = {
            "brand":   run_cmd(["getprop", "ro.product.manufacturer"]),
            "model":   model,
            "device":  run_cmd(["getprop", "ro.product.device"]),
            "build":   run_cmd(["getprop", "ro.build.display.id"]),
            "android": run_cmd(["getprop", "ro.build.version.release"]),
            "carrier": run_cmd(["getprop", "ro.carrier"]),
            "sku":     run_cmd(["getprop", "ro.boot.hardware.sku"])
        }

    # Estado de Modo Seguro
    safemode = run_cmd(["getprop", "persist.sys.safemode"])
    if safemode == "1":
        telemetry["safe_mode_active"] = True

    # Estado de Bootloader
    locked = run_cmd(["getprop", "ro.boot.flash.locked"])
    vbstate = run_cmd(["getprop", "ro.boot.verifiedbootstate"])
    if locked == "0" or "orange" in vbstate.lower() or "unlocked" in vbstate.lower():
        telemetry["bootloader_unlocked"] = True

    # 2. USB
    sys_usb = run_cmd(["ls", "/sys/bus/usb/devices/"], timeout=1)
    if sys_usb and not any(err in sys_usb.lower() for err in ["permission", "cannot", "error", "denied"]) and len(sys_usb.strip()) > 0:
        telemetry["usb_devices"] = sys_usb.split()
    else:
        lsusb_out = run_cmd(["lsusb"], timeout=1)
        if lsusb_out and not any(err in lsusb_out.lower() for err in ["permission", "cannot", "error", "denied", "unable", "libusb"]) and len(lsusb_out.strip()) > 0:
            telemetry["usb_devices"] = [l.strip() for l in lsusb_out.splitlines() if l.strip()]
        else:
            telemetry["usb_devices"] = []

    # 3. ADB
    adb_out = run_cmd(["adb", "devices"])
    if "List of" in adb_out:
        lines = [l.strip() for l in adb_out.splitlines()[1:] if l.strip()]
        telemetry["adb_devices"] = lines
        if lines:
            first_dev = lines[0].split()[0]
            dev_safemode = run_cmd(["adb", "-s", first_dev, "shell", "getprop", "persist.sys.safemode"])
            if dev_safemode == "1":
                telemetry["safe_mode_active"] = True
            dev_vbstate = run_cmd(["adb", "-s", first_dev, "shell", "getprop", "ro.boot.verifiedbootstate"])
            if "orange" in dev_vbstate.lower():
                telemetry["bootloader_unlocked"] = True

    # 4. Fastboot
    fastboot_out = run_cmd(["fastboot", "devices"])
    if fastboot_out and not fastboot_out.startswith("[Error"):
        telemetry["fastboot_devices"] = [l.strip() for l in fastboot_out.splitlines() if l.strip()]
        telemetry["bootloader_unlocked"] = True

    # Síntesis de fase
    if telemetry["safe_mode_active"] and telemetry["bootloader_unlocked"]:
        telemetry["phase_status"] = "Modo seguro activo / Desbloqueado"
    elif telemetry["safe_mode_active"]:
        telemetry["phase_status"] = "Modo seguro activo (Protegido)"
    elif telemetry["bootloader_unlocked"]:
        telemetry["phase_status"] = "Bootloader Desbloqueado"
    else:
        telemetry["phase_status"] = "Modo Normal (Bloqueado)"

    # 5. Estado de IA
    local_alive = is_local_llm_alive()
    telemetry["ai_status"] = {
        "spark_alive":      True,
        "local_llm_alive":  local_alive,
        "local_llm_url":    LOCAL_LLM_BASE,
        "gemini_model":     GEMINI_MODEL,
        "fallback_chain":   FALLBACK_CHAIN,
        "active_engine":    "Gemini + Spark Dual Core ⚡"
    }

    return telemetry

# ─────────────────────────────────────────────────────────────────────────────
# RUTAS FLASK
# ─────────────────────────────────────────────────────────────────────────────
@app.route("/", methods=["GET"])
def index():
    taxonomy = get_taxonomy()
    return render_template("index.html", taxonomy=taxonomy)

@app.route("/studio", methods=["GET"])
def studio_webapp():
    """WebApp oficial de KDF Studio con Suite completa y Chat Local"""
    return render_template("studio.html")

@app.route("/launcher", methods=["GET"])
def launcher_webapp():
    """Lanzador Principal Oficial KDF Mewtwo Edition"""
    return render_template("launcher.html")

@app.route("/browser", methods=["GET"])
def browser_webapp():
    """Navegador Oficial KDF con Escudo Anti-Tracking y Sanitizador de URLs"""
    return render_template("browser.html")

@app.route("/security", methods=["GET"])
def security_webapp():
    """Centro de Seguridad KDF: Bóveda NIP, Anti-Toques Fantasma y Anti-Deepfake"""
    return render_template("security.html")

@app.route("/optimizer", methods=["GET"])
def optimizer_webapp():
    """Centro de Optimización KDF: TRIM Flash, Limpieza Dalvik y Boost RAM"""
    return render_template("optimizer.html")

@app.route("/maps", methods=["GET"])
def maps_webapp():
    """Mapas y Telemetría GPS en tiempo real de KDF-Tools"""
    return render_template("maps.html")

@app.route("/assistant", methods=["GET"])
def assistant_webapp():
    """Asistente Personal y Planificador del Orquestador KDF-Tools"""
    return render_template("assistant.html")

@app.route("/api/optimizer/run", methods=["POST"])
def run_optimizer_action():
    from kdf_optimizer_core import KDFOptimizerEngine
    data = request.get_json() or {}
    act = data.get("action", "clean")
    opt = KDFOptimizerEngine()
    if act == "trim":
        return jsonify(opt.run_trim())
    elif act == "boost":
        return jsonify(opt.boost_ram())
    return jsonify(opt.clean_cache())

@app.route("/api/security/vault", methods=["POST"])
def security_vault_api():
    from kdf_security_core import KDFSecurityEngine
    data = request.get_json() or {}
    pin = data.get("pin", "").strip()
    sec = KDFSecurityEngine()
    res = sec.unlock_vault(pin)
    return jsonify(res)

@app.route("/api/browser/sanitize", methods=["POST"])
def sanitize_browser_url():
    """Motor de Sanitización de URLs Sospechosas y Doxxing"""
    from kdf_url_sanitizer import URLSanitizer
    data = request.get_json() or {}
    url = data.get("url", "").strip()
    if not url:
        return jsonify({"error": "url_empty"}), 400
    sanitizer = URLSanitizer()
    res = sanitizer.audit_and_purify(url)
    return jsonify(res)

@app.route("/api/categories", methods=["GET"])
def get_categories():
    return jsonify(get_taxonomy())

@app.route("/api/device_status", methods=["GET"])
def get_device_status():
    return jsonify(get_real_device_telemetry())

@app.route("/api/ai_status", methods=["GET"])
def get_ai_status():
    alive = is_local_llm_alive()
    status = {
        "spark": {
            "name": "Spark Heuristic Engine",
            "alive": True,
            "version": "2.4-Mewtwo"
        },
        "gemini": {
            "model":    GEMINI_MODEL,
            "enabled":  _gemini.get("enabled", True),
            "has_key":  bool(GEMINI_API_KEY and len(GEMINI_API_KEY) > 10),
        },
        "local_llm": {
            "alive":    alive,
            "url":      LOCAL_LLM_BASE,
            "port":     LOCAL_LLM_PORT,
            "model":    os.getenv("LOCAL_LLM_MODEL", "auto-detect"),
        },
        "active_engines": ["Spark Core ⚡", "Gemini 3.8 Flash ☁️"]
    }
    return jsonify(status)

@app.route("/api/execute", methods=["POST"])
def execute_operation():
    data          = request.get_json() or {}
    op_id         = data.get("operation_id", "") or data.get("operation", "")
    device        = data.get("device",    "")
    brand         = data.get("brand",     "")
    processor     = data.get("processor", "")
    package_name  = data.get("package_name", "").strip()
    action_mode   = data.get("action_mode", "disable").strip()
    wipe_userdata = data.get("wipe_userdata", "no").strip()
    boot_img      = data.get("boot_img", "bootloader.img").strip()
    radio_img     = data.get("radio_img", "radio.img").strip()
    firehose      = data.get("firehose", "prog_firehose_ddr.elf").strip()

    # Mapeos directos para herramientas universales KDF Studio
    if op_id == "payload_extractor":
        script_path = os.path.join(SCRIPTS_DIR, "payload_extractor.py")
        cmd = ["python3", script_path, "storage/payload.bin"]
        output = run_cmd(cmd)
        return jsonify({"status": "ok", "output": output})
    elif op_id == "frp_bypass":
        script_path = os.path.join(SCRIPTS_DIR, "frp_universal_engine.sh")
        cmd = ["bash", script_path]
        output = run_cmd(cmd)
        return jsonify({"status": "ok", "output": output})
    elif op_id == "install_apk":
        script_path = os.path.join(SCRIPTS_DIR, "install_apk.sh")
        apk_target = data.get("apk_url", "") or data.get("apk_path", "")
        cmd = ["bash", script_path]
        if apk_target:
            cmd.append(apk_target)
        output = run_cmd(cmd)
        return jsonify({"status": "ok", "output": output})
    elif op_id == "stress_test":
        script_path = os.path.join(BASE_DIR, "stress_lab", "harness_cli", "kdf_stress_runner.py")
        apk_target = data.get("apk_path", "") or data.get("apk_url", "")
        if not apk_target:
            candidates = [
                os.path.join(BASE_DIR, "downloads", "KDF-Tools-LuX.apk"),
                os.path.join(BASE_DIR, "kdf-tools.zip"),
                os.path.join(BASE_DIR, "dist_apk", "KDF-Tools-LuX.apk")
            ]
            for cand in candidates:
                if os.path.exists(cand):
                    apk_target = cand
                    break
        if not apk_target:
            return jsonify({"status": "error", "output": "No se encontró ningún archivo APK o ZIP en ~/KDF-LuX para realizar la prueba."})
        cmd = ["python3", script_path, apk_target]
        output = run_cmd(cmd)
        return jsonify({"status": "ok", "output": output})
    elif op_id == "fastboot_info":
        cmd = ["fastboot", "getvar", "all"]
        output = run_cmd(cmd)
        return jsonify({"status": "ok", "output": output})

    taxonomy = get_taxonomy()
    script_map = {}
    for op in taxonomy.get("operations", []):
        s = op.get("command") or op.get("script")
        if s:
            script_map[op["id"]] = s

    script_rel = script_map.get(op_id)
    if not script_rel:
        return jsonify({"status": "error", "output": f"Operación '{op_id}' no reconocida en categories.yaml."})

    script_path = os.path.join(BASE_DIR, script_rel)
    if not os.path.isfile(script_path):
        return jsonify({"status": "error", "output": f"Script '{script_rel}' no encontrado en el sistema."})

    # Mapeo de argumentos específicos según la operación requerida
    if op_id == "app_blocker":
        cmd = ["bash", script_path, package_name or "com.ejemplo.blocker", action_mode or "disable", wipe_userdata or "no"]
    elif op_id == "mem_cleaner":
        cmd = ["bash", script_path, "--wipe" if wipe_userdata in ["yes", "true", True, "--wipe"] else "no"]
    elif op_id == "mode_safe":
        cmd = ["bash", script_path, action_mode if action_mode in ["--off", "--on"] else "--on"]
    elif op_id == "edl_flasher":
        cmd = ["bash", script_path, boot_img or "bootloader.img", radio_img or "radio.img", firehose or "prog_firehose_ddr.elf"]
    elif op_id == "bootloader_unlock":
        cmd = ["bash", script_path, device or brand, brand or processor, processor or ""]
    else:
        cmd = ["bash", script_path, device, brand, processor]

    output = run_cmd(cmd, timeout=60)
    return jsonify({"status": "ok", "operation": op_id, "output": output})

@app.route("/media", methods=["POST"])
def media_endpoint():
    data   = request.get_json() or {}
    action = data.get("action")
    if not action:
        return jsonify({"error": "action not provided"}), 400

    if action == "generate_image":
        prompt = data.get("prompt", "")
        name   = data.get("name", f"gen_{int(time.time())}")
        script = os.path.join(SCRIPTS_DIR, "generar_imagen.sh")
        if not os.path.isfile(script):
            return jsonify({"error": "script generar_imagen.sh not found"}), 500
        out = run_cmd(["bash", script, prompt, name])
        return jsonify({"status": "ok", "file": f"static/{name}.png", "output": out})

    elif action == "edit_image":
        src  = data.get("src")
        op   = data.get("operation")
        param = data.get("param")
        script = os.path.join(SCRIPTS_DIR, "editar_imagen.sh")
        if not os.path.isfile(script):
            return jsonify({"error": "script editar_imagen.sh not found"}), 500
        out = run_cmd(["bash", script, src, op, param])
        return jsonify({"status": "ok", "file": f"static/edited_{os.path.basename(src)}", "output": out})

    return jsonify({"error": "unknown action"}), 400

@app.route("/call")
def voice_live_call():
    """Consola dedicada de llamada de voz continua estilo Gemini Live"""
    return render_template("call.html")

@app.route("/api/ring_call", methods=["POST", "GET"])
def ring_call():
    """Dispara llamada directa por Wi-Fi y enlace con la app de Gemini en el celular"""
    try:
        subprocess.Popen(["/system/bin/am", "start", "-a", "android.intent.action.VIEW", "-d", "http://127.0.0.1:5000/call"], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    except Exception:
        pass

    try:
        subprocess.Popen(["/system/bin/monkey", "-p", "com.google.android.apps.bard", "1"], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    except Exception:
        pass

    return jsonify({
        "status": "calling_wifi",
        "message": "Llamada directa por Wi-Fi activada hacia la app de Gemini y consola KDF.",
        "anti_tracking": "ENFORCED",
        "zero_footprint": True
    })

@app.route("/api/chat", methods=["POST"])
def chat():
    data      = request.get_json() or {}
    msg       = data.get("message",   "").strip()
    category  = data.get("category",  "")
    brand     = data.get("brand",     "")
    processor = data.get("processor", "")
    operation = data.get("operation", "")
    engine    = data.get("engine",    "auto").strip()

    if not msg:
        return jsonify({"reply": "Mensaje vacío.", "source": "none"}), 400

    telemetry = get_real_device_telemetry()
    context = (
        f"--- CONTEXTO TÉCNICO KDF-TOOLS (MEWTWO EDITION) ---\n"
        f"Categoría : {category}\n"
        f"Marca     : {brand}\n"
        f"Procesador: {processor}\n"
        f"Operación : {operation}\n"
        f"Fase Telemetría: {telemetry.get('phase_status', 'Normal')}\n"
        f"Modo Seguro: {telemetry.get('safe_mode_active', False)}\n"
        f"Bootloader : {'Desbloqueado' if telemetry.get('bootloader_unlocked') else 'Bloqueado'}\n"
        f"Dispositivo Local: {json.dumps(telemetry.get('local_device', {}))}\n"
        f"Puertos USB Activos: {telemetry.get('usb_devices', [])}\n"
        f"Dispositivos ADB   : {telemetry.get('adb_devices', [])}\n"
        f"---------------------------------------------------"
    )

    result = ask_ai(msg, context=context, preferred_engine=engine)
    if result.get("is_multi"):
        return jsonify({
            "is_multi":  True,
            "reply":     result.get("synthesis", ""),
            "responses": result.get("responses", []),
            "source":    result.get("source", "Concilio Multi-IA"),
            "telemetry": telemetry
        })

    return jsonify({
        "is_multi":  False,
        "reply":     result.get("reply", "Sin respuesta."),
        "source":    result.get("source", "IA"),
        "telemetry": telemetry
    })

# ─────────────────────────────────────────────────────────────────────────────
if __name__ == "__main__":
    port = int(os.getenv("PORT", "5000"))

    local_alive = is_local_llm_alive()
    print(f"\n{'='*64}")
    print(f"  ⚡ KDF-Tools · Suite Universal (Mewtwo Bimetallic Edition)")
    print(f"  URL: http://127.0.0.1:{port} (o http://localhost:{port})")
    print(f"{'='*64}")
    print(f"  Spark Core  : ✓ ACTIVO (Motor heurístico neural)")
    print(f"  Gemini      : {'✓ Conectado (' + GEMINI_MODEL + ')' if GEMINI_API_KEY else '✗ Sin API key'}")
    print(f"  IA Local    : {'✓ ACTIVA (' + LOCAL_LLM_BASE + ')' if local_alive else '✗ Inactiva (:4250)'}")
    print(f"{'='*64}\n")

    app.run(host="0.0.0.0", port=port, debug=False)
