#!/usr/bin/env bash
# scripts/disable_blocker.sh - Desactivar / Eliminar App Bloqueadora
# KDF-Tools · Módulo de Utilidad Universal
set -e

TARGET_PKG="${1:-}"
WIPE_FLAG="${2:-}"

echo "=========================================================="
echo "  [KDF-Tools] DESACTIVADOR DE APPS BLOQUEADORAS           "
echo "  PayJoy · MDM · Knox Guard · Bloqueo de Operador         "
echo "=========================================================="

echo "[1/4] Verificando conexión de dispositivo..."
DEV=$(adb devices 2>/dev/null | grep -v "List" | grep -v "^$" | head -1 || true)
if [ -z "$DEV" ]; then
    echo "[!] No se detectó dispositivo en modo ADB."
    echo "    Intentando verificar modo Fastboot..."
    FB_DEV=$(fastboot devices 2>/dev/null | head -1 || true)
    if [ -n "$FB_DEV" ]; then
        echo "[+] Dispositivo en modo Fastboot: $FB_DEV"
        if [ "$TARGET_PKG" == "--wipe" ] || [ "$WIPE_FLAG" == "--wipe" ]; then
            echo "[*] Ejecutando fastboot -w (Wipe de fábrica para remover bloqueos)..."
            fastboot -w
            echo "[+] Reiniciando dispositivo..."
            fastboot reboot
            exit 0
        else
            echo "[i] Dispositivo en Fastboot. Para wipe forzado usa: ./disable_blocker.sh --wipe"
            exit 0
        fi
    fi
    echo "[!] Error: Conecta el dispositivo con Depuración USB o en Fastboot."
    exit 1
fi

echo "[+] Dispositivo ADB activo: $DEV"

# Lista de paquetes bloqueadores conocidos
BLOCKER_LIST=(
    "com.payjoy.access"
    "com.payjoy.status"
    "com.payjoy.companion"
    "com.claro.seguridad"
    "com.telcel.contenedor"
    "com.samsung.ucs.agent.boot"
    "com.sec.knox.kccagent"
    "com.sec.knox.knoxsetupwizardclient"
    "com.google.android.apps.work.clouddpc"
)

echo ""
echo "[2/4] Escaneando aplicaciones bloqueadoras en el sistema..."
FOUND=()

if [ -n "$TARGET_PKG" ] && [ "$TARGET_PKG" != "--wipe" ]; then
    FOUND+=("$TARGET_PKG")
else
    for pkg in "${BLOCKER_LIST[@]}"; do
        if adb shell pm list packages 2>/dev/null | grep -q "$pkg"; then
            FOUND+=("$pkg")
            echo "  [!] Detectada app bloqueadora: $pkg"
        fi
    done
fi

if [ ${#FOUND[@]} -eq 0 ]; then
    echo "  [✓] No se encontraron paquetes bloqueadores comunes automáticos."
    echo "      Si conoces el nombre del paquete, ejecútalo como: ./disable_blocker.sh <nombre_paquete>"
else
    echo ""
    echo "[3/4] Desactivando y purgando apps bloqueadoras..."
    for pkg in "${FOUND[@]}"; do
        echo "  → Procesando: $pkg"
        adb shell pm disable-user --user 0 "$pkg" 2>/dev/null || true
        adb shell pm hide --user 0 "$pkg" 2>/dev/null || true
        adb shell pm suspend "$pkg" 2>/dev/null || true
        adb shell pm uninstall -k --user 0 "$pkg" 2>/dev/null || true
        echo "    [✓] Paquete deshabilitado/desinstalado para usuario 0."
    done
fi

# Opción wipe
if [ "$TARGET_PKG" == "--wipe" ] || [ "$WIPE_FLAG" == "--wipe" ]; then
    echo ""
    echo "[4/4] Reiniciando a Fastboot para wipe de datos..."
    adb reboot bootloader 2>/dev/null || true
    sleep 5
    fastboot -w 2>/dev/null || true
    fastboot reboot 2>/dev/null || true
fi

echo ""
echo "=========================================================="
echo "  [✓] Operación de desbloqueo completada.                 "
echo "=========================================================="
