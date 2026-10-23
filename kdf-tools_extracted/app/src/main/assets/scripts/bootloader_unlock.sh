#!/usr/bin/env bash
# scripts/bootloader_unlock.sh - Desbloqueo de Bootloader Universal
# KDF-LuX · Módulo Bootloader Unlock + Anti-Rollback Check
echo "=========================================================="
echo "  [KDF-LuX] DESBLOQUEO DE BOOTLOADER UNIVERSAL            "
echo "=========================================================="

DEVICE="$1"
BRAND="$2"
PROCESSOR="$3"

echo "[*] $BRAND | $PROCESSOR"
echo ""

# ─── 1. Verificar estado actual ──────────────────────────────────────────────
echo "[1/5] Verificando estado del bootloader..."

FB_DEV=$(fastboot devices 2>/dev/null | head -1 || true)
ADB_DEV=$(adb devices 2>/dev/null | grep -v "List" | grep -v "^$" | head -1 || true)

if [ -n "$FB_DEV" ]; then
    echo "  → Dispositivo en Fastboot: $FB_DEV"
    UNLOCK_STATUS=$(fastboot getvar unlocked 2>&1 | grep "unlocked" | head -1 || true)
    SECURE=$(fastboot getvar secure 2>&1 | grep "secure" | head -1 || true)
    OEM=$(fastboot getvar device-locked 2>&1 | grep "device" | head -1 || true)
    echo "  → $UNLOCK_STATUS"
    echo "  → $SECURE"
    echo "  → $OEM"
elif [ -n "$ADB_DEV" ]; then
    echo "  → Dispositivo en ADB: $ADB_DEV"
    BL_STATE=$(adb shell getprop ro.boot.verifiedbootstate 2>/dev/null || echo "desconocido")
    echo "  → Verified Boot State: $BL_STATE"
    echo "  → Reiniciando a Fastboot para desbloqueo..."
    adb reboot bootloader 2>/dev/null || true
    sleep 5
fi

# ─── 2. Método por marca ─────────────────────────────────────────────────────
echo ""
echo "[2/5] Método de desbloqueo por marca..."

case "$BRAND" in
    *Motorola*|*Moto*)
        echo "  ── MOTOROLA ──"
        echo "  Paso 1: Obtén tu unlock code en bootloader.lenovo.com/BootloaderUnlockRequest"
        echo "  Paso 2: Activa OEM Unlock: Ajustes → Acerca del teléfono → Número de compilación (7 veces)"
        echo "          Ajustes → Sistema → Opciones de desarrollador → Desbloqueo OEM"
        echo "  Paso 3: Reiniciar a Fastboot: adb reboot bootloader"
        echo "  Paso 4: Obtener código de dispositivo:"
        fastboot oem get_unlock_data 2>&1 || echo "    (conectar en fastboot primero)"
        echo "  Paso 5: Pegar código en la web de Motorola → obtener unlock code"
        echo "  Paso 6: fastboot oem unlock UNLOCK_CODE_AQUI"
        echo "  Paso 7: fastboot reboot"
        ;;
    *Xiaomi*|*Redmi*|*POCO*|*Mi*)
        echo "  ── XIAOMI / REDMI / POCO ──"
        echo "  Paso 1: Vincula cuenta Mi en Ajustes → Mi Account"
        echo "  Paso 2: Descarga Mi Unlock Tool (Windows): en.miui.com/unlock"
        echo "  Paso 3: Espera período de gracia (7-30 días según cuenta)"
        echo "  Paso 4: Conecta en Fastboot + Mi Unlock → Unlock"
        echo ""
        echo "  Método alternativo (sin espera) - A/B devices:"
        echo "    fastboot flashing unlock"
        echo "    fastboot flashing unlock_critical"
        echo ""
        echo "  Anti-Rollback Check (IMPORTANTE para Xiaomi):"
        fastboot getvar anti 2>&1 || echo "  (conectar en fastboot)"
        ;;
    *Samsung*)
        echo "  ── SAMSUNG ──"
        echo "  Paso 1: Activa OEM Unlock en Opciones Desarrollador"
        echo "  Paso 2: Reinicia en Download Mode (Vol- + USB)"
        echo "  Paso 3: OEM Unlock con Odin o con:"
        echo "    fastboot flashing unlock  (solo algunos modelos)"
        echo "  Paso 4: Para root total: Magisk + AP en Odin"
        echo "  NOTA: Knox se activa (0x1) y garantía se anula permanentemente"
        ;;
    *OnePlus*)
        echo "  ── ONEPLUS ──"
        echo "  Paso 1: Activar OEM Unlock"
        echo "  Paso 2: fastboot oem unlock"
        echo "  Paso 3: Confirmar en pantalla del dispositivo"
        ;;
    *Realme*|*OPPO*|*Vivo*)
        echo "  ── REALME / OPPO / VIVO ──"
        echo "  Paso 1: Solicitar deep test en community.realme.com o forum.oppo.com"
        echo "  Paso 2: Activar Deep Test Mode (esperar aprobación)"
        echo "  Paso 3: fastboot oem unlock"
        echo "  Alternativa: ColorOS Deep Test / Realme Unlock Tool"
        ;;
    *)
        echo "  ── GENÉRICO / AOSP ──"
        echo "  Intento universal..."
        echo "  fastboot flashing unlock"
        echo "  fastboot oem unlock"
        echo "  fastboot oem unlock-go"
        if [ -n "$FB_DEV" ]; then
            fastboot flashing unlock 2>&1 || true
        fi
        ;;
esac

# ─── 3. Anti-Rollback Protection ─────────────────────────────────────────────
echo ""
echo "[3/5] ⚠️  Verificación Anti-Rollback (ARB)..."
if [ -n "$FB_DEV" ]; then
    ARB=$(fastboot getvar anti 2>&1 | grep "anti" | head -1 || echo "  No detectado")
    echo "  → ARB Level: $ARB"
    echo "  ⚠️  ADVERTENCIA: No flashear ROM con ARB inferior al actual"
    echo "     Esto puede bloquear el dispositivo permanentemente (bootloop forzado)"
fi

# ─── 4. Comandos finales ──────────────────────────────────────────────────────
echo ""
echo "[4/5] Comandos de desbloqueo disponibles..."
echo "  fastboot flashing unlock          # Estándar Android 8+"
echo "  fastboot flashing unlock_critical # Particiones críticas"
echo "  fastboot oem unlock               # OEM legacy"
echo "  fastboot oem unlock-go            # Algunos MediaTek"
echo "  fastboot -w                       # Wipe datos + unlock"

# ─── 5. Post-unlock ───────────────────────────────────────────────────────────
echo ""
echo "[5/5] Post-desbloqueo..."
echo "  → El dispositivo se reiniciará y borrará todos los datos (normal)"
echo "  → Verified Boot = ORANGE (desactivado, esperado)"
echo "  → Ahora puedes: instalar TWRP, Magisk, ROMs custom"
echo ""
echo "  Instalar Magisk (root):"
echo "    adb push Magisk-*.apk /sdcard/"
echo "    adb reboot recovery"
echo "    En TWRP: Install → Magisk → Flash"

echo ""
echo "=========================================================="
echo "  KDF-LuX Bootloader Unlock - Completado"
echo "=========================================================="
