#!/usr/bin/env bash
# scripts/clean_memory.sh - Limpieza de Memoria y Cachés Android
# KDF-Tools · Módulo de Utilidad Universal
set -euo pipefail

echo "=========================================================="
echo "  [KDF-Tools] LIMPIEZA DE MEMORIA Y CACHÉS                "
echo "=========================================================="

# 1. Comprobar ADB
DEV=$(adb devices 2>/dev/null | grep -v "List" | grep -v "^$" | head -1 || true)
if [ -z "$DEV" ]; then
    echo "[!] No se detectó dispositivo en ADB."
    echo "    Intentando comprobar modo Fastboot..."
    FB_DEV=$(fastboot devices 2>/dev/null | head -1 || true)
    if [ -n "$FB_DEV" ] && [[ "${1-}" == "--wipe" ]]; then
        echo "[+] Dispositivo en Fastboot: $FB_DEV"
        echo "[*] Ejecutando fastboot -w (Wipe Userdata)..."
        fastboot -w || echo "[!] Error en fastboot -w"
        fastboot reboot
        exit 0
    fi
    echo "[!] Conecta el dispositivo con depuración USB."
    exit 1
fi

echo "[+] Dispositivo detectado: $DEV"
echo "[1/3] Limpiando particiones de caché..."

adb shell "rm -rf /data/cache/* /cache/* /data/local/tmp/*" 2>/dev/null || true
echo "  [✓] Cachés de /data/cache, /cache y /data/local/tmp purgados."

echo "[2/3] Optimizando almacenamiento y TRIM..."
adb shell "sm fstrim" 2>/dev/null || adb shell "fstrim -v /data" 2>/dev/null || echo "  [i] fstrim no soportado sin root (ignorado)."

# Liberar RAM del kernel si hay root
echo "[3/3] Liberando búferes y cachés de RAM..."
adb shell "su -c 'echo 3 > /proc/sys/vm/drop_caches'" 2>/dev/null || true

if [[ "${1-}" == "--wipe" ]]; then
    echo "[*] Reiniciando a Fastboot para wipe de userdata..."
    adb reboot bootloader
    sleep 5
    fastboot -w || echo "[!] Error en fastboot -w"
    fastboot reboot
fi

echo "=========================================================="
echo "  [✓] Limpieza de memoria completada exitosamente.        "
echo "=========================================================="
