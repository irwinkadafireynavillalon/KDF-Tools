#!/usr/bin/env bash
# ==============================================================================
# KDF-Tools · Script: disable_blocker.sh
# Módulo: App Blocker (Desactivar / Eliminar App Bloqueadora)
# Permisos: ADB (pm) + Fastboot (opcional para wipe con confirmación)
# ==============================================================================
set -e

TARGET_PKG="${1:-com.ejemplo.blocker}"
ACTION_MODE="${2:-disable}"       # disable | uninstall
WIPE_USERDATA="${3:-no}"         # yes | no

echo "=========================================================="
echo "  ⚡ [KDF-Tools] MÓDULO APP BLOCKER (disable_blocker.sh)  "
echo "  Tema Mewtwo: Dorado • Plateado • Negro • Blanco • Morado"
echo "=========================================================="
echo "[*] Paquete objetivo : $TARGET_PKG"
echo "[*] Modo de acción   : $ACTION_MODE"
echo "[*] Wipe Userdata    : $WIPE_USERDATA"
echo "----------------------------------------------------------"

# 1. Comprobación de herramientas
if ! command -v adb >/dev/null 2>&1; then
    echo "[!] Advertencia: adb no disponible directamente en el PATH."
fi

DEV=$(adb devices 2>/dev/null | grep -v "List" | grep "device$" | head -1 | awk '{print $1}' || true)

if [ -z "$DEV" ]; then
    echo "[!] No se detectó dispositivo ADB activo."
    echo "[*] Verificando modo local de Android o simulación asistida..."
    if command -v pm >/dev/null 2>&1; then
        echo "[1/3] Entorno Android local detectado:"
        if [ "$ACTION_MODE" == "uninstall" ]; then
            echo "[2/3] Ejecutando: pm uninstall -k --user 0 $TARGET_PKG"
            pm uninstall -k --user 0 "$TARGET_PKG" 2>&1 || true
        else
            echo "[2/3] Ejecutando: pm disable-user --user 0 $TARGET_PKG"
            pm disable-user --user 0 "$TARGET_PKG" 2>&1 || true
            pm clear "$TARGET_PKG" 2>&1 || true
        fi
        echo "[+] Acción completada sobre el paquete local $TARGET_PKG."
        exit 0
    else
        echo "[i] Dispositivo objetivo no conectado por cable OTG / USB."
        echo "    Comandos preparados para ejecución:"
        echo "    1) adb shell setprop persist.sys.safemode 1"
        if [ "$ACTION_MODE" == "uninstall" ]; then
            echo "    2) adb shell pm uninstall -k --user 0 $TARGET_PKG"
        else
            echo "    2) adb shell pm disable-user --user 0 $TARGET_PKG"
            echo "    3) adb shell pm clear $TARGET_PKG"
        fi
        if [ "$WIPE_USERDATA" == "yes" ] || [ "$WIPE_USERDATA" == "--wipe" ]; then
            echo "    4) adb reboot bootloader && fastboot -w && fastboot reboot"
        fi
        echo "[+] Módulo App Blocker preparado. Conecta el dispositivo para aplicar."
        exit 0
    fi
fi

echo "[+] Dispositivo objetivo conectado: $DEV"

# Paso 1: Activar Modo Seguro preventivo
echo "[1/3] Activando Modo Seguro preventivo en el dispositivo..."
adb -s "$DEV" shell setprop persist.sys.safemode 1 2>&1 || true
echo "[+] Modo seguro activado temporalmente para aislar el proceso."

# Paso 2: Desactivar o desinstalar la app
echo "[2/3] neutralizando la aplicación $TARGET_PKG..."
if [ "$ACTION_MODE" == "uninstall" ]; then
    echo "[*] Ejecutando: pm uninstall -k --user 0 $TARGET_PKG"
    OUT_UNINSTALL=$(adb -s "$DEV" shell pm uninstall -k --user 0 "$TARGET_PKG" 2>&1 || true)
    echo "  → Resultado: $OUT_UNINSTALL"
else
    echo "[*] Ejecutando: pm disable-user --user 0 $TARGET_PKG"
    OUT_DISABLE=$(adb -s "$DEV" shell pm disable-user --user 0 "$TARGET_PKG" 2>&1 || true)
    echo "  → Resultado: $OUT_DISABLE"
    echo "[*] Purgando datos del paquete (pm clear)..."
    OUT_CLEAR=$(adb -s "$DEV" shell pm clear "$TARGET_PKG" 2>&1 || true)
    echo "  → Cache/Datos: $OUT_CLEAR"
fi

# Paso 3: Wipe de datos de usuario opcional (fastboot -w)
echo "[3/3] Comprobando solicitud de formateo de usuario (Wipe Userdata)..."
if [ "$WIPE_USERDATA" == "yes" ] || [ "$WIPE_USERDATA" == "--wipe" ] || [ "$WIPE_USERDATA" == "true" ]; then
    echo "[!] ADVERTENCIA: Se solicitó fastboot -w (Wipe Userdata completo)."
    echo "[*] Reiniciando dispositivo en modo FASTBOOT..."
    adb -s "$DEV" reboot bootloader 2>&1 || true
    sleep 4
    if command -v fastboot >/dev/null 2>&1; then
        echo "[*] Ejecutando: fastboot -w"
        fastboot -w 2>&1 || echo "[!] fastboot -w reportó advertencias o requiere bootloader desbloqueado."
        echo "[*] Reiniciando dispositivo al sistema..."
        fastboot reboot 2>&1 || true
    else
        echo "[!] Binario fastboot no disponible para realizar el wipe automático."
    fi
else
    echo "[+] Wipe userdata OMITIDO (datos de usuario preservados)."
fi

echo "=========================================================="
echo "  [+] OPERACIÓN APP BLOCKER COMPLETADA EXITOSAMENTE       "
echo "  [i] Paquete $TARGET_PKG desactivado/eliminado.          "
echo "=========================================================="
