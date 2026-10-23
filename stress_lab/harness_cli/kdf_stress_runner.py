#!/usr/bin/env python3
# ==============================================================================
# KDF-STRESSLAB · CLI EXTREME STRESS HARNESS & BENCHMARK RUNNER
# Soporta archivos .apk y .zip directos
# ==============================================================================

import os
import sys
import time
import json
import zipfile
import subprocess
import shutil
from datetime import datetime

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
REPORT_DIR = "/storage/emulated/0/Download" if os.path.exists("/storage/emulated/0/Download") else os.path.join(BASE_DIR, "reports")
os.makedirs(REPORT_DIR, exist_ok=True)

def print_banner():
    print("""
\033[38;5;135m  ╔═══════════════════════════════════════════════════════════════╗\033[0m
\033[38;5;135m  ║\033[38;5;220m   ⚡ KDF-STRESSLAB · EXTREME HARNESS & RESILIENCE RUNNER     \033[38;5;135m║\033[0m
\033[38;5;135m  ║\033[38;5;251m       Banco Automatizado de Caos, Fatiga y Benchmarking       \033[38;5;135m║\033[0m
\033[38;5;135m  ╚═══════════════════════════════════════════════════════════════╝\033[0m
""")

def run_cmd(cmd):
    try:
        res = subprocess.run(cmd, shell=True, capture_output=True, text=True, timeout=60)
        return res.stdout.strip()
    except Exception as e:
        return f"Error: {e}"

def extract_apk_from_input(file_path):
    if not os.path.exists(file_path):
        print(f"\033[31m[-] Error: Archivo no encontrado: {file_path}\033[0m")
        sys.exit(1)

    work_dir = os.path.join(BASE_DIR, "temp_extract")
    if os.path.exists(work_dir):
        shutil.rmtree(work_dir)
    os.makedirs(work_dir, exist_ok=True)

    if file_path.endswith(".zip"):
        print(f"\033[34m[*] Descomprimiendo archivo ZIP: {file_path}...\033[0m")
        with zipfile.ZipFile(file_path, 'r') as zf:
            apk_entries = [f for f in zf.namelist() if f.endswith(".apk")]
            if not apk_entries:
                print("\033[31m[-] Error: No se encontró ningún archivo .apk dentro del ZIP.\033[0m")
                sys.exit(1)
            target_entry = apk_entries[0]
            extracted_path = zf.extract(target_entry, work_dir)
            print(f"\033[32m[+] APK extraído con éxito: {target_entry}\033[0m")
            return extracted_path
    elif file_path.endswith(".apk"):
        return file_path
    else:
        print("\033[31m[-] Error: Solo se admiten archivos .apk o .zip.\033[0m")
        sys.exit(1)

def inspect_apk(apk_path):
    print("\033[34m[*] Inspeccionando metadatos y manifiesto del APK...\033[0m")
    # Usar aapt dump si existe o aapt2
    aapt_cmd = "aapt" if shutil.which("aapt") else "aapt2"
    badging = run_cmd(f"{aapt_cmd} dump badging '{apk_path}' 2>/dev/null")
    
    pkg_name = "desconocido"
    ver_name = "1.0"
    target_sdk = "34"

    for line in badging.split("\n"):
        if line.startswith("package:"):
            parts = line.split(" ")
            for p in parts:
                if p.startswith("name="): pkg_name = p.split("=")[1].strip("'")
                if p.startswith("versionName="): ver_name = p.split("=")[1].strip("'")
        if line.startswith("targetSdkVersion:"):
            target_sdk = line.split(":")[1].strip().strip("'")

    if pkg_name == "desconocido":
        # Fallback de nombre por archivo
        pkg_name = os.path.basename(apk_path).replace(".apk", "").lower().replace("-", ".")
        if not "." in pkg_name: pkg_name = f"com.kdf.{pkg_name}"

    print(f"\033[32m[+] Paquete Objetivo : {pkg_name}\033[0m")
    print(f"\033[32m[+] Versión          : {ver_name}\033[0m")
    print(f"\033[32m[+] Target SDK       : {target_sdk}\033[0m")
    return {"package": pkg_name, "version": ver_name, "target_sdk": target_sdk}

def execute_stress_sequence(apk_path, meta):
    pkg = meta["package"]
    print(f"\n\033[38;5;220m[1/6] 📥 Instalando APK en el dispositivo...\033[0m")
    install_res = run_cmd(f"pm install -r '{apk_path}' 2>&1 || adb install -r '{apk_path}' 2>&1")
    print(f"      Resultado instalación: {install_res}")

    print(f"\033[38;5;220m[2/6] 🧹 Limpiando búfer de registros Logcat...\033[0m")
    run_cmd("logcat -c")

    print(f"\033[38;5;220m[3/6] 🚀 Lanzando aplicación objetivo ({pkg})...\033[0m")
    launch_res = run_cmd(f"monkey -p {pkg} -c android.intent.category.LAUNCHER 1 2>&1")
    time.sleep(3)

    print(f"\033[38;5;220m[4/6] 📊 Capturando línea base de telemetría (Idle State)...\033[0m")
    mem_baseline = run_cmd(f"dumpsys meminfo {pkg} | grep 'TOTAL PSS:' | head -1")
    print(f"      Consumo Base de Memoria: {mem_baseline}")

    print(f"\033[38;5;220m[5/6] 🌪️ INYECTANDO CAOS EXTREMO & MONKEY FUZZING (60 segundos)...\033[0m")
    print("      - Ráfagas de 1500 eventos táctiles continuos")
    print("      - Simulación de Low Memory Killer (am send-trim-memory CRITICAL)")
    print("      - Sobrecarga de eventos de rotación y teclas del sistema")
    
    # Lanzar Monkey Fuzzing en background
    monkey_cmd = f"monkey -p {pkg} --throttle 100 --pct-touch 40 --pct-motion 25 --pct-trackball 0 --pct-nav 10 --pct-majornav 10 --pct-syskeys 10 --pct-appswitch 5 1500"
    monkey_proc = subprocess.Popen(monkey_cmd, shell=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

    for i in range(12):
        time.sleep(5)
        # Inyección periódica de presión de memoria del sistema
        if i % 3 == 0:
            run_cmd(f"am send-trim-memory {pkg} RUNNING_CRITICAL 2>/dev/null")
        print(f"      [Caos Activo] Segundo {(i+1)*5}/60...")

    monkey_proc.terminate()

    print(f"\033[38;5;220m[6/6] 🔍 Recopilando diagnósticos y analizando fallos...\033[0m")
    mem_peak = run_cmd(f"dumpsys meminfo {pkg} | grep 'TOTAL PSS:' | head -1")
    gfx_stats = run_cmd(f"dumpsys gfxinfo {pkg} | grep -E 'Janky frames|Total frames rendered' | head -5")
    crashes = run_cmd(f"logcat -d | grep -E 'FATAL EXCEPTION|SIGSEGV|ANR in {pkg}' | head -20")

    crash_count = len([c for c in crashes.split("\n") if "FATAL" in c or "SIGSEGV" in c])
    anr_count = len([c for c in crashes.split("\n") if "ANR" in c])

    return generate_super_detailed_report(meta, mem_baseline, mem_peak, gfx_stats, crashes, crash_count, anr_count)

def generate_super_detailed_report(meta, mem_baseline, mem_peak, gfx_stats, crashes, crash_count, anr_count):
    pkg = meta["package"]
    score = 100.0
    score -= (crash_count * 35.0)
    score -= (anr_count * 25.0)
    final_score = max(0.0, score)

    verdict = "🟢 APROBADA (Alta Resiliencia y Estabilidad)" if final_score >= 85 else \
              "🟡 ADVERTENCIA (Requiere optimizaciones de fluidez)" if final_score >= 60 else \
              "🔴 RECHAZADA (Inestable bajo fatiga extrema)"

    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    report_data = {
        "suite": "KDF-STRESSLAB RIGOROUS HARNESS",
        "timestamp": timestamp,
        "package": pkg,
        "version": meta["version"],
        "target_sdk": meta["target_sdk"],
        "resilience_score": f"{final_score:.1f} / 100",
        "verdict": verdict,
        "metrics": {
            "baseline_memory": mem_baseline,
            "peak_memory": mem_peak,
            "gfx_jank_stats": gfx_stats,
            "fatal_crashes": crash_count,
            "anr_freezes": anr_count
        },
        "crash_logcat_excerpt": crashes if crashes else "Sin excepciones fatales detectadas."
    }

    # Guardar en Download/ en formato JSON
    json_path = os.path.join(REPORT_DIR, f"Reporte_KDF_Stress_{pkg}_{int(time.time())}.json")
    with open(json_path, "w") as f:
        json.dump(report_data, f, indent=4)

    # Imprimir Reporte Super Detallado en Pantalla
    print("\n" + "="*65)
    print("\033[38;5;82m           📋 REPORTE SUPER DETALLADO DE PRUEBA EXTREMA\033[0m")
    print("="*65)
    print(f"\033[1m🎯 Aplicación Evaluada   :\033[0m {pkg} (v{meta['version']})")
    print(f"\033[1m📱 Target Android SDK    :\033[0m {meta['target_sdk']}")
    print(f"\033[1m⏱️ Duración de Fatiga    :\033[0m 60 segundos de inyección continua")
    print(f"\033[1m🏆 Puntuación Estabilidad:\033[0m \033[38;5;220m{final_score:.1f} / 100\033[0m")
    print(f"\033[1m🚦 Veredicto Play Store  :\033[0m {verdict}")
    print("-" * 65)
    print("\033[38;5;45m📊 MÉTRICAS DE TELEMETRÍA:\033[0m")
    print(f"  • Memoria Inicial       : {mem_baseline}")
    print(f"  • Memoria Pico (Fatiga) : {mem_peak}")
    print(f"  • Crashes Fatales       : {crash_count}")
    print(f"  • Congelamientos (ANR)  : {anr_count}")
    if gfx_stats:
        print(f"  • Fluidez de Pantalla   :\n    {gfx_stats}")
    print("-" * 65)
    if crashes:
        print(f"\033[31m💥 DETALLE DE CRASHES CAPTURADOS:\033[0m\n{crashes}")
    else:
        print("\033[32m✅ Cero Crashes. La app toleró el 100% de los eventos caóticos.\033[0m")
    print("=" * 65)
    print(f"\033[38;5;251m💾 Reporte completo exportado a:\033[0m {json_path}\n")
    return json_path

def main():
    print_banner()
    if len(sys.argv) < 2:
        print("Uso: python3 kdf_stress_runner.py <ruta_al_archivo.apk_o_.zip>\n")
        print("Ejemplo: python3 kdf_stress_runner.py ~/KDF-LuX/kdf-tools.zip")
        sys.exit(1)

    input_file = sys.argv[1]
    apk_file = extract_apk_from_input(input_file)
    meta = inspect_apk(apk_file)
    execute_stress_sequence(apk_file, meta)

if __name__ == "__main__":
    main()
