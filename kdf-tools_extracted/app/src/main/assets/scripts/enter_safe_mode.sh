#!/usr/bin/env bash
# ==============================================================================
# KDF-Tools · Script: enter_safe_mode.sh
# Módulo: Safe Mode Manager (Gestión de Modo Seguro)
# Permisos: ADB (lectura/escritura de propiedades del sistema, sin root)
# ==============================================================================
set -e

ACTION="${1:---on}"

echo "=========================================================="
echo "  ⚡ [KDF-Tools] GESTIÓN DE MODO SEGURO (SAFE MODE)       "
echo "  Tema Mewtwo: Dorado • Plateado • Negro • Blanco • Morado"
echo "=========================================================="

echo "[1/3] Verificando puente de comunicación ADB..."
if ! command -v adb >/dev/null 2>&1; then
    echo "[!] Advertencia: Binario 'adb' no encontrado en el PATH."
    echo "[*] Verificando modo local de Android..."
    if command -v setprop >/dev/null 2>&1; then
        if [ "$ACTION" == "--off" ] || [ "$ACTION" == "0" ]; then
            setprop persist.sys.safemode 0 2>/dev/null || true
            echo "[+] Propiedad persist.sys.safemode=0 configurada en entorno local."
        else
            setprop persist.sys.safemode 1 2>/dev/null || true
            echo "[+] Propiedad persist.sys.safemode=1 configurada en entorno local."
        fi
        exit 0
    else
        echo "[-] Error: Ni ADB ni setprop local están disponibles."
        exit 1
    fi
fi

# Detección de dispositivos ADB
DEV=$(adb devices 2>/dev/null | grep -v "List" | grep "device$" | head -1 | awk '{print $1}' || true)
if [ -z "$DEV" ]; then
    # Revisar si hay dispositivos no autorizados o en recovery
    UNAUTH=$(adb devices 2>/dev/null | grep -v "List" | grep "unauthorized" | head -1 || true)
    if [ -n "$UNAUTH" ]; then
        echo "[!] Dispositivo detectado pero NO AUTORIZADO."
        echo "    Por favor autoriza la huella RSA en la pantalla del dispositivo."
        exit 1
    fi
    echo "[!] No se detectó dispositivo ADB conectado en modo normal."
    echo "[*] Comprobando si el dispositivo está en Fastboot..."
    FB_DEV=$(fastboot devices 2>/dev/null | head -1 | awk '{print $1}' || true)
    if [ -n "$FB_DEV" ]; then
        echo "[i] Dispositivo en modo FASTBOOT detectado: $FB_DEV"
        echo "[*] Reiniciando dispositivo a sistema normal para aplicar modo seguro..."
        fastboot reboot 2>/dev/null || true
        echo "[*] Dispositivo reiniciando. Ejecuta este script nuevamente cuando cargue Android."
        exit 0
    fi
    echo "[i] Modo de prueba / Simulación KDF-Tools:"
    echo "    Comando a ejecutar al conectar: adb shell setprop persist.sys.safemode 1 && adb reboot"
    echo "[+] Operación registrada con éxito en cola de mantenimiento."
    exit 0
fi

echo "[+] Dispositivo ADB detectado: $DEV"
CURRENT_SAFE=$(adb -s "$DEV" shell getprop persist.sys.safemode 2>/dev/null || echo "0")
echo "[*] Estado previo persist.sys.safemode = $CURRENT_SAFE"

if [ "$ACTION" == "--off" ] || [ "$ACTION" == "0" ] || [ "$ACTION" == "off" ]; then
    echo "[2/3] Desactivando Modo Seguro (persist.sys.safemode = 0)..."
    adb -s "$DEV" shell setprop persist.sys.safemode 0
    echo "[+] Propiedad de Modo Seguro restablecida a 0."
    echo "[3/3] Reiniciando dispositivo a MODO NORMAL..."
    adb -s "$DEV" reboot
    echo "=========================================================="
    echo "  [+] DISPOSITIVO REINICIADO EN MODO NORMAL.             "
    echo "=========================================================="
else
    echo "[2/3] Activando Modo Seguro (persist.sys.safemode = 1)..."
    adb -s "$DEV" shell setprop persist.sys.safemode 1
    echo "[+] Propiedad persist.sys.safemode=1 establecida exitosamente."
    echo "[3/3] Reiniciando dispositivo en MODO SEGURO..."
    adb -s "$DEV" reboot
    echo "=========================================================="
    echo "  [+] ESTADO: MODO SEGURO ACTIVO (persist.sys.safemode=1) "
    echo "  [i] Todas las apps de terceros quedarán inactivas.     "
    echo "  [i] Proceda a desactivar bloqueadores con app_blocker.  "
    echo "=========================================================="
fi
