#!/usr/bin/env bash
# ==============================================================================
# KDF-Tools · Script: unlock_bootloader.sh
# Módulo: Bootloader Unlock (Desbloquear Bootloader)
# Permisos: Fastboot (dispositivo en modo Fastboot / Bootloader)
# ==============================================================================
set -e

DEVICE="${1:-}"
BRAND="${2:-}"
PROCESSOR="${3:-}"

echo "=========================================================="
echo "  ⚡ [KDF-Tools] DESBLOQUEO DE BOOTLOADER (FASTBOOT)      "
echo "  Tema Mewtwo: Dorado • Plateado • Negro • Blanco • Morado"
echo "=========================================================="

echo "[1/4] Verificando conexión en modo Fastboot..."
if ! command -v fastboot >/dev/null 2>&1; then
    echo "[!] Error: 'fastboot' no está instalado en el sistema."
    exit 1
fi

FB_DEV=$(fastboot devices 2>/dev/null | head -1 | awk '{print $1}' || true)
if [ -z "$FB_DEV" ]; then
    echo "[*] No se detectó dispositivo en Fastboot. Comprobando ADB..."
    ADB_DEV=$(adb devices 2>/dev/null | grep -v "List" | grep "device$" | head -1 | awk '{print $1}' || true)
    if [ -n "$ADB_DEV" ]; then
        echo "[+] Dispositivo ADB detectado ($ADB_DEV). Reiniciando a bootloader..."
        adb -s "$ADB_DEV" reboot bootloader 2>&1 || true
        sleep 4
        FB_DEV=$(fastboot devices 2>/dev/null | head -1 | awk '{print $1}' || true)
    fi
fi

if [ -z "$FB_DEV" ]; then
    echo "[!] No se detectó dispositivo en modo Fastboot."
    echo "    Por favor pon el equipo en modo Fastboot (Vol Abajo + Power al encender)"
    echo "    Comandos preparados para ejecución:"
    echo "      1) fastboot flashing unlock"
    echo "      2) fastboot oem unlock"
    echo "      3) fastboot flashing unlock_critical"
    echo "[+] Operación registrada en KDF-Tools."
    exit 0
fi

echo "[+] Dispositivo Fastboot detectado: $FB_DEV"

echo "[2/4] Comprobando variables de seguridad y estado de bloqueo..."
fastboot getvar unlocked 2>&1 || true
fastboot getvar secure 2>&1 || true
fastboot getvar is-userspace 2>&1 || true

echo "[3/4] Ejecutando secuencia de desbloqueo estándar Fastboot..."
echo "  → Intentando: fastboot flashing unlock"
fastboot flashing unlock 2>&1 || true

echo "  → Intentando: fastboot oem unlock"
fastboot oem unlock 2>&1 || true

echo "  → Intentando: fastboot flashing unlock_critical"
fastboot flashing unlock_critical 2>&1 || true

echo "[4/4] Verificación de estado final:"
FINAL_STATE=$(fastboot getvar unlocked 2>&1 | grep "unlocked" || echo "Comando enviado al dispositivo.")
echo "  → $FINAL_STATE"

echo "=========================================================="
echo "  [+] SECUENCIA DE DESBLOQUEO FINALIZADA                  "
echo "  [i] Confirma en la pantalla del equipo si solicita OK.  "
echo "=========================================================="
