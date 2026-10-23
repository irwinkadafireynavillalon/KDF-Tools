#!/usr/bin/env bash
# scripts/nh_root_tool.sh - NH Root / Magisk / KernelSU / APatch Root Engine
# KDF-LuX · Módulo Root Automation
echo "=========================================================="
echo "  [KDF-LuX] NH ROOT / MAGISK / KERNELSU ENGINE            "
echo "=========================================================="

DEVICE="$1"
BRAND="$2"
PROCESSOR="$3"

echo "[*] $BRAND | $PROCESSOR"
echo ""

# ─── 1. Estado del dispositivo ───────────────────────────────────────────────
echo "[1/6] Verificando estado actual del dispositivo..."
ADB_DEV=$(adb devices 2>/dev/null | grep -v "List" | grep -v "^$" | head -1 || true)
FB_DEV=$(fastboot devices 2>/dev/null | head -1 || true)

if [ -n "$ADB_DEV" ]; then
    echo "  → ADB: $ADB_DEV"
    ROOT=$(adb shell su -c id 2>/dev/null || echo "No root")
    echo "  → Root actual: $ROOT"
    SELINUX=$(adb shell getenforce 2>/dev/null || echo "desconocido")
    echo "  → SELinux: $SELINUX"
    ANDROID_VER=$(adb shell getprop ro.build.version.release 2>/dev/null || echo "?")
    BOOT_STATE=$(adb shell getprop ro.boot.verifiedbootstate 2>/dev/null || echo "?")
    echo "  → Android: $ANDROID_VER | Boot: $BOOT_STATE"
fi

# ─── 2. Métodos de root por tipo ─────────────────────────────────────────────
echo ""
echo "[2/6] Herramientas y métodos de root disponibles..."
echo ""
echo "  ┌────────────────────────────────────────────────────────────────┐"
echo "  │  MAGISK (Universal, Android 6-15)                             │"
echo "  │  • Instala en boot.img parchado → no toca /system             │"
echo "  │  • MagiskHide / Zygisk para bypasear detección root           │"
echo "  ├────────────────────────────────────────────────────────────────┤"
echo "  │  KERNELSU (Android 12+, requiere kernel GKI)                  │"
echo "  │  • Root a nivel kernel, más sigiloso que Magisk               │"
echo "  │  • Compatible con APatch                                       │"
echo "  ├────────────────────────────────────────────────────────────────┤"
echo "  │  APATCH (Android 8+, kernel patcher)                          │"
echo "  │  • Parcha el kernel directamente, sin módulos zona             │"
echo "  ├────────────────────────────────────────────────────────────────┤"
echo "  │  NH ROOT (Motorola/MediaTek específico)                       │"
echo "  │  • Script automatizado para Moto G series con MTK             │"
echo "  ├────────────────────────────────────────────────────────────────┤"
echo "  │  EXPLOIT ROOT (Temporal, sin desbloquear bootloader)          │"
echo "  │  • Dirty Cow, DirtyPipe, CVE-2022-2586 (kernel exploit)      │"
echo "  └────────────────────────────────────────────────────────────────┘"

# ─── 3. Proceso Magisk ───────────────────────────────────────────────────────
echo ""
echo "[3/6] PROCESO MAGISK (Método recomendado)..."
echo ""
echo "  REQUISITO: Bootloader desbloqueado"
echo ""
echo "  Paso 1: Extraer boot.img del firmware oficial"
if [ -n "$ADB_DEV" ]; then
    echo "    Extrayendo boot.img del dispositivo..."
    adb shell dd if=/dev/block/by-name/boot of=/sdcard/boot.img bs=4096 2>/dev/null || \
    adb shell dd if=/dev/block/bootdevice/by-name/boot of=/sdcard/boot.img bs=4096 2>/dev/null || \
    echo "    → Usar 'adb shell' y ejecutar: dd if=/dev/block/by-name/boot of=/sdcard/boot.img"
    adb pull /sdcard/boot.img /tmp/boot_original.img 2>/dev/null || true
    if [ -f "/tmp/boot_original.img" ]; then
        echo "    ✓ boot.img extraído: /tmp/boot_original.img"
    fi
fi
echo ""
echo "  Paso 2: Parchear boot.img con Magisk:"
echo "    • Copia boot.img a /sdcard/Download/"
echo "    • Abre app Magisk → Install → Select a file → boot.img"
echo "    • Espera al archivo magisk_patched_*.img"
echo ""
echo "  Paso 3: Flashear boot parchado:"
echo "    adb pull /sdcard/Download/magisk_patched_*.img ."
echo "    adb reboot bootloader"
echo "    fastboot flash boot magisk_patched_*.img"
echo "    fastboot reboot"

# ─── 4. KernelSU ─────────────────────────────────────────────────────────────
echo ""
echo "[4/6] KERNELSU (Android 12+ con kernel GKI)..."
echo "  Paso 1: Verificar kernel GKI:"
if [ -n "$ADB_DEV" ]; then
    KERNEL=$(adb shell uname -r 2>/dev/null || echo "no disponible")
    echo "    Kernel: $KERNEL"
fi
echo "  Paso 2: Descargar KSU Manager: github.com/tiann/KernelSU"
echo "  Paso 3: Instalar KSU módulo en boot parchado o usar AnyKernel3"
echo "  Paso 4: Flashear con TWRP / fastboot boot ksu_boot.img"

# ─── 5. NH Root específico ───────────────────────────────────────────────────
echo ""
echo "[5/6] NH ROOT para Motorola/MTK..."
echo "  NH Root Tool funciona con:"
echo "  • Moto G Play (2021/2022), Moto G Power, Moto E series"
echo "  • Requiere: ADB activado + app instalada"
echo ""
echo "  Pasos:"
echo "  1. Instalar NH Root App en el dispositivo"
echo "  2. Activar depuración USB"
echo "  3. Ejecutar: adb shell 'sh /sdcard/NHroot.sh'"
echo "  4. Esperar reinicio con root"
echo ""
echo "  Para Xiaomi/Redmi (MTK): usar Magisk + SP Flash Tool parchado"

# ─── 6. Verificar root ───────────────────────────────────────────────────────
echo ""
echo "[6/6] Verificación post-root..."
if [ -n "$ADB_DEV" ]; then
    ROOT_CHECK=$(adb shell which su 2>/dev/null || echo "su no encontrado")
    echo "  → su: $ROOT_CHECK"
    MAGISK=$(adb shell pm list packages 2>/dev/null | grep magisk || echo "Magisk no instalado")
    echo "  → Magisk: $MAGISK"
fi

echo ""
echo "=========================================================="
echo "  KDF-LuX NH Root Engine - Guía completada"
echo "  Módulos extra: Magisk, KernelSU, APatch, NH Root"
echo "=========================================================="
