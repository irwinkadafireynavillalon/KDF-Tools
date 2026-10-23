#!/usr/bin/env bash
# scripts/payjoy_unlock.sh - Bypass / Desbloqueo de Payjoy, Peyo, FRP y Admin Lock
# KDF-LuX · Módulo Finance Lock Bypass
echo "=========================================================="
echo "  [KDF-LuX] DESBLOQUEO PAYJOY / PEYO / FINANCE LOCK      "
echo "=========================================================="

DEVICE="$1"
BRAND="$2"
PROCESSOR="$3"

echo "[*] Objetivo: $BRAND | Procesador: $PROCESSOR"
echo ""

# ─── 1. Detectar dispositivo ─────────────────────────────────────────────────
echo "[1/7] Detectando dispositivo conectado..."
ADB_DEV=$(adb devices 2>/dev/null | grep -v "List" | grep -v "^$" | head -1 || true)
FB_DEV=$(fastboot devices 2>/dev/null | head -1 || true)

if [ -n "$ADB_DEV" ]; then
    echo "  → ADB: $ADB_DEV"
    PAYJOY_PKG=$(adb shell pm list packages 2>/dev/null | grep -i "payjoy\|peyo\|devicelock\|device_lock\|paygo\|sievert\|tecno.lock\|kikuu\|jumia.lock" || true)
    if [ -n "$PAYJOY_PKG" ]; then
        echo ""
        echo "  ⚠️  PAQUETES DE BLOQUEO DETECTADOS:"
        echo "$PAYJOY_PKG"
    else
        echo "  → No se detectaron paquetes Payjoy/Peyo visibles desde ADB"
        echo "  → Puede estar oculto como app de sistema"
    fi
fi

# ─── 2. Análisis del tipo de bloqueo ────────────────────────────────────────
echo ""
echo "[2/7] Tipo de bloqueo Finance Lock..."
echo ""
echo "  ┌────────────────────────────────────────────────────────┐"
echo "  │  PAYJOY  → App sistema: com.payjoy.devicelocker       │"
echo "  │  PEYO    → App sistema: com.peyo.lock / cr.creditea   │"
echo "  │  TECNO   → App sistema: com.transsion.devicelock      │"
echo "  │  KIKUU   → App sistema: com.kikuumart.lock            │"
echo "  │  JUMIA   → App sistema: com.jumia.devicelock          │"
echo "  │  KFD/Generic → Device admin + overlay blocker        │"
echo "  └────────────────────────────────────────────────────────┘"

# ─── 3. Método 1: ADB Disable + Uninstall ────────────────────────────────────
echo ""
echo "[3/7] MÉTODO 1 - Desinstalar vía ADB (requiere depuración USB activa)..."
if [ -n "$ADB_DEV" ]; then
    echo "  → Dispositivo ADB disponible. Ejecutando..."
    echo ""

    # Lista de paquetes Payjoy/Finance conocidos
    LOCK_PACKAGES=(
        "com.payjoy.devicelocker"
        "com.payjoy.loanagent"
        "com.peyo.lock"
        "com.creditea.devicelock"
        "com.transsion.devicelock"
        "com.sievert.devicelock"
        "com.kikuumart.lock"
        "com.jumia.devicelock"
        "com.paygo.lock"
        "com.devicelockcontroller"
        "com.android.devicelockcontroller"
    )

    for pkg in "${LOCK_PACKAGES[@]}"; do
        FOUND=$(adb shell pm list packages 2>/dev/null | grep "$pkg" || true)
        if [ -n "$FOUND" ]; then
            echo "  ✓ Encontrado: $pkg"
            echo "    Deshabilitando admin..."
            adb shell dpm remove-active-admin "$pkg/.DeviceLockAdmin" 2>/dev/null || true
            echo "    Desinstalando para usuario 0..."
            adb shell pm uninstall --user 0 "$pkg" 2>/dev/null || true
            echo "    Deshabilitando permanente..."
            adb shell pm disable-user --user 0 "$pkg" 2>/dev/null || true
            echo "    ✓ Procesado: $pkg"
        fi
    done

    echo ""
    echo "  → Limpiando Device Admin / Device Owner..."
    adb shell dpm list-owners 2>/dev/null || true
    adb shell settings put global device_provisioned 1 2>/dev/null || true
    adb shell settings put secure user_setup_complete 1 2>/dev/null || true

else
    echo "  → ADB no disponible. Prueba el método 2 (Fastboot)"
fi

# ─── 4. Método 2: Fastboot Wipe + Factory ────────────────────────────────────
echo ""
echo "[4/7] MÉTODO 2 - Fastboot Wipe (borra datos, desbloquea Payjoy)..."
if [ -n "$FB_DEV" ]; then
    echo "  ⚠️  ADVERTENCIA: Esto borrará todos los datos del dispositivo"
    echo "  → Comandos para ejecutar manualmente:"
    echo ""
    echo "    fastboot erase userdata"
    echo "    fastboot erase cache"
    echo "    fastboot -w"
    echo "    fastboot reboot"
    echo ""
    echo "  Si Payjoy está en /system: necesitas ROM limpia sin Payjoy preinstalado"
else
    echo "  → No hay dispositivo en Fastboot"
fi

# ─── 5. Método 3: Recovery + Wipe ────────────────────────────────────────────
echo ""
echo "[5/7] MÉTODO 3 - Recovery Mode + Factory Reset..."
echo "  Si el dispositivo tiene recovery accesible:"
if [ -n "$ADB_DEV" ]; then
    echo "  → Reiniciando a recovery..."
    adb reboot recovery 2>/dev/null || true
fi
echo "  En pantalla: Wipe Data / Factory Reset → Confirmar → Reboot System"
echo ""
echo "  ⚠️  Si Payjoy está en ROM del fabricante, el reset no elimina el bloqueo."
echo "  → Solución: Flashear ROM limpia sin Payjoy (ver módulo Flash Stock/GSI)"

# ─── 6. Método 4: EDL/BROM + ROM limpia ──────────────────────────────────────
echo ""
echo "[6/7] MÉTODO 4 - Flasheo completo ROM limpia (elimina Payjoy definitivamente)..."
echo "  Paso 1: Entra en modo EDL (Qualcomm) o BROM (MediaTek)"
echo "  Paso 2: Flashea ROM oficial SIN Payjoy preinstalado"
echo ""
echo "  Fuentes de ROM limpia:"
echo "  • Motorola: mirrors.lolinet.com/firmware/motorola/"
echo "  • Xiaomi:   miuirom.org / xiaomifirmwareupdater.com"
echo "  • Samsung:  samfw.com (firmware oficial por CSC)"
echo "  • Tecno:    tecnoflash.com"
echo ""
echo "  Comandos generales EDL (Qualcomm con edl tool):"
echo "    edl --loader=prog_firehose.elf rf          # leer particiones"
echo "    edl --loader=prog_firehose.elf w userdata emptyfile.img"
echo "    edl --loader=prog_firehose.elf reset"

# ─── 7. FRP Bypass adicional ─────────────────────────────────────────────────
echo ""
echo "[7/7] FRP (Factory Reset Protection) Bypass..."
if [ -n "$ADB_DEV" ]; then
    echo "  → Limpiando cuenta FRP vía ADB..."
    adb shell content delete --uri content://settings/secure --where "name='android_id'" 2>/dev/null || true
    adb shell am start -n "com.google.android.gms/.provision.GoogleAccountDataBrokerActivity" 2>/dev/null || true
    adb shell settings put global setup_wizard_has_run 1 2>/dev/null || true
    adb shell pm clear com.google.android.gms 2>/dev/null || true
    echo "  → FRP limpieza ejecutada"
fi
echo "  Alternativa: Odin FRP Reset (Samsung) / Mi PC Suite FRP (Xiaomi)"

echo ""
echo "=========================================================="
echo "  KDF-LuX Payjoy/Peyo Unlock - Completado"
echo "  Para bloqueo en ROM sistema: Usar módulo Flash ROM Limpia"
echo "=========================================================="
