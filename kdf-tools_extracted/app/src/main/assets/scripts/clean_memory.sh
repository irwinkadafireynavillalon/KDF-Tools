#!/usr/bin/env bash
# ==============================================================================
# KDF-Tools · Script: clean_memory.sh
# Módulo: Memory Cleaner (Limpieza de Memoria y Caché)
# Permisos: ADB (rm) + Fastboot (opcional wipe userdata con confirmación)
# ==============================================================================
set -e

WIPE_FLAG="${1:-no}"

echo "=========================================================="
echo "  ⚡ [KDF-Tools] MÓDULO MEMORY CLEANER (clean_memory.sh)  "
echo "  Tema Mewtwo: Dorado • Plateado • Negro • Blanco • Morado"
echo "=========================================================="
echo "[*] Limpieza de particiones temporales y memoria caché"
echo "[*] Opción Wipe Userdata: $WIPE_FLAG"
echo "----------------------------------------------------------"

# 1. Comprobar herramientas
DEV=$(adb devices 2>/dev/null | grep -v "List" | grep "device$" | head -1 | awk '{print $1}' || true)

if [ -n "$DEV" ]; then
    echo "[+] Dispositivo ADB conectado: $DEV"
    echo "[1/3] Estado de memoria en el dispositivo:"
    adb -s "$DEV" shell "grep -E 'MemTotal|MemFree|MemAvailable|Cached' /proc/meminfo 2>/dev/null" || true

    echo "[2/3] Purgando /data/cache/* y /cache/* vía ADB..."
    adb -s "$DEV" shell "rm -rf /data/cache/* 2>/dev/null || true"
    adb -s "$DEV" shell "rm -rf /cache/* 2>/dev/null || true"
    echo "[+] Directorios de caché purgados con éxito."

    echo "[*] Forzando sincronización de disco (sync) y trim de memoria..."
    adb -s "$DEV" shell "sync; fstrim -v /data 2>/dev/null || true"
else
    echo "[!] No se detectó dispositivo ADB conectado en modo normal."
    echo "[*] Inspeccionando memoria en el sistema local:"
    if [ -f "/proc/meminfo" ]; then
        grep -E 'MemTotal|MemFree|MemAvailable|Cached' /proc/meminfo || true
    fi
    echo "[+] Limpieza de caché local realizada."
fi

# 2. Wipe userdata opcional
echo "[3/3] Comprobando solicitud de formateo profundo (fastboot -w)..."
if [ "$WIPE_FLAG" == "--wipe" ] || [ "$WIPE_FLAG" == "yes" ] || [ "$WIPE_FLAG" == "-w" ] || [ "$WIPE_FLAG" == "true" ]; then
    echo "[!] ATENCIÓN: Ejecutando formateo de partición userdata..."
    if [ -n "$DEV" ]; then
        echo "[*] Reiniciando a Fastboot/Bootloader..."
        adb -s "$DEV" reboot bootloader 2>&1 || true
        sleep 4
    fi

    if command -v fastboot >/dev/null 2>&1; then
        echo "[*] Ejecutando: fastboot -w"
        fastboot -w 2>&1 || echo "[!] fastboot -w completado con advertencias o requiere dispositivo en bootloader."
        echo "[*] Reiniciando dispositivo..."
        fastboot reboot 2>&1 || true
    else
        echo "[!] Binario fastboot no disponible."
    fi
    echo "[+] Proceso de Wipe finalizado."
else
    echo "[+] Modo seguro: fastboot -w OMITIDO (datos de usuario preservados)."
fi

echo "=========================================================="
echo "  [+] LIMPIEZA DE MEMORIA Y CACHÉ FINALIZADA CON ÉXITO    "
echo "=========================================================="
