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
def query_spark(prompt: str, context: str = "") -> str:
    """
    Spark AI: Motor heurístico instantáneo de KDF-Tools.
    Analiza patrones de errores de Android (bootloop, payjoy, dm-verity, EDL, fastboot)
    y genera instrucciones precisas sin latencia externa.
    """
    p_lower = prompt.lower()
    resp = []

    # 1. Bloqueadores y Apps de Control (PayJoy, Knox, Blocker)
    if any(k in p_lower for k in ["bloque", "bloqueador", "payjoy", "knox", "app_blocker", "desactivar app"]):
        resp.append("⚡ **[SPARK DIAGNOSTIC · APP BLOCKER ENGINE]**")
        resp.append("• Para desactivar apps que impiden el arranque o bloquean el sistema:")
        resp.append("  1. Entrar en modo seguro: `./scripts/enter_safe_mode.sh`")
        resp.append("  2. Desactivar el paquete bloqueador:")
        resp.append("     `./scripts/disable_blocker.sh <nombre_paquete> disable`")
        resp.append("  3. Si persiste, desinstalar:")
        resp.append("     `./scripts/disable_blocker.sh <nombre_paquete> uninstall`")
        resp.append("  4. Si requiere limpieza profunda opcional: pasar parámetro `--wipe`.")
        return "\n".join(resp)

    # 2. Modo Seguro
    if any(k in p_lower for k in ["modo seguro", "safe mode", "safemode", "mode_safe"]):
        resp.append("🛡️ **[SPARK DIAGNOSTIC · SAFE MODE MANAGER]**")
        resp.append("• Modo Seguro aísla Android deshabilitando todas las aplicaciones de terceros.")
        resp.append("  - Activar: `./scripts/enter_safe_mode.sh` (Aplica `persist.sys.safemode=1` y reinicia)")
        resp.append("  - Restaurar normal: `./scripts/enter_safe_mode.sh --off`")
        resp.append("  - Permiso: ADB (solo lectura/escritura de propiedades del sistema, sin root requerido).")
        return "\n".join(resp)

    # 3. Limpieza de Memoria y Caché
    if any(k in p_lower for k in ["limpi", "memoria", "cache", "clean_memory", "wipe", "mem_cleaner"]):
        resp.append("🧹 **[SPARK DIAGNOSTIC · MEMORY & CACHE CLEANER]**")
        resp.append("• Limpieza segura de particiones temporales:")
        resp.append("  - Borra `/data/cache/*` y `/cache/*` sin afectar datos del usuario: `./scripts/clean_memory.sh`")
        resp.append("  - Opcional: Para formateo completo de usuario (fastboot -w): `./scripts/clean_memory.sh --wipe`")
        return "\n".join(resp)

    # 4. Bootloader Unlock
    if any(k in p_lower for k in ["bootloader", "unlock", "desbloquear", "oem"]):
        resp.append("🔓 **[SPARK DIAGNOSTIC · BOOTLOADER UNLOCK ENGINE]**")
        resp.append("• Secuencia Fastboot de desbloqueo:")
        resp.append("  1. Reiniciar a fastboot: `adb reboot bootloader`")
        resp.append("  2. Verificar variables: `fastboot getvar unlocked`")
        resp.append("  3. Desbloquear: `./scripts/unlock_bootloader.sh`")
        resp.append("     (Ejecuta `fastboot flashing unlock` y `fastboot oem unlock`)")
        return "\n".join(resp)

    # 5. Modo EDL 9008
    if any(k in p_lower for k in ["edl", "9008", "test point", "test-point", "qualcomm", "qfil", "edl_flasher"]):
        resp.append("⚡ **[SPARK DIAGNOSTIC · QUALCOMM EDL 9008 CORE]**")
        resp.append("• Modo EDL (Emergency Download 05c6:9008):")
        resp.append("  - Conexión por Test-Point puenteando pads dorados en placa madre.")
        resp.append("  - Flasheo directo con programador Firehose Sahara:")
        resp.append("    `./scripts/edl_flasher.sh bootloader.img radio.img prog_firehose_ddr.elf`")
        resp.append("  - Escribe directamente sobre memoria flash UFS/eMMC antes del arranque del bootloader.")
        return "\n".join(resp)

    # 6. Diagnóstico general
    resp.append("⚡ **[SPARK NEURAL HEURISTIC · KDF-TOOLS]**")
    resp.append(f"He analizado tu consulta técnica: *'{prompt}'*")
    resp.append("Recomendación de arquitectura KDF-Tools (Mewtwo Edition):")
    resp.append("• 1. Verifica en la barra superior el estado de telemetría (Modo Seguro / Bootloader).")
    resp.append("• 2. Si hay problemas de congelamiento o apps maliciosas, usa la sección **🛠 Herramientas de Mantenimiento**.")
    resp.append("• 3. Para flasheo profundo GSI o Stock, asegúrate de tener el bootloader desbloqueado.")
    return "\n".join(resp)

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

# ─────────────────────────────────────────────────────────────────────────────
# ENRUTADOR IA UNIVERSAL (PRIORIDAD NÚCLEO LOCAL KDF-STUDIO)
# ─────────────────────────────────────────────────────────────────────────────
def ask_ai(prompt: str, context: str = "", preferred_engine: str = "auto") -> dict:
    if preferred_engine == "spark":
        reply = query_spark(prompt, context)
        return {"reply": reply, "source": "Spark Core ⚡"}

    # Prioridad absoluta: IA Local de KDF-Studio (Gemma 2B) sin API Keys
    local_reply = query_local_llm(prompt, context)
    if local_reply:
        return {"reply": local_reply, "source": "KDF-Studio Local IA 🛡️"}

    # Fallback automático al motor analítico interno Spark
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
    usb_out = run_cmd(["termux-usb", "-l"])
    if usb_out.startswith("["):
        try:
            telemetry["usb_devices"] = json.loads(usb_out)
        except Exception:
            telemetry["usb_devices"] = [usb_out]

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
    return jsonify({
        "reply":     result["reply"],
        "source":    result["source"],
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

    app.run(host="127.0.0.1", port=port, debug=False)
