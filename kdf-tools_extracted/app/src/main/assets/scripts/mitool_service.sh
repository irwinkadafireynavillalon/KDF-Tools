#!/usr/bin/env bash
# ==============================================================================
# KDF-LuX - MiTool Service Engine (Xiaomi / Redmi / POCO)
# ==============================================================================
# Protocolo de servicio, verificación de Anti-Rollback (ARB), comprobación de
# bootloader y gestión de flasheo Fastboot / EDL para dispositivos Xiaomi.
# ==============================================================================

set -euo pipefail

echo "=========================================================="
echo "🦊 [MiTool Service Engine] Diagnóstico Xiaomi/Redmi/POCO"
echo "=========================================================="

echo "[1/4] Verificando comunicación en modo Fastboot..."
FASTBOOT_DEV=$(fastboot devices 2>/dev/null || true)

if [ -n "$FASTBOOT_DEV" ]; then
    echo "[✓] Dispositivo detectado en Fastboot:"
    echo "$FASTBOOT_DEV"
    
    echo ""
    echo "[2/4] Verificando índice Anti-Rollback (ARB) y Seguridad..."
    ARB_INDEX=$(fastboot getvar anti 2>&1 | grep -i "anti:" | awk '{print $2}' || echo "No reportado")
    echo " -> Índice Anti-Rollback (anti): $ARB_INDEX"
    
    echo ""
    echo "[3/4] Comprobando estado de Bootloader Xiaomi..."
    BL_UNLOCKED=$(fastboot getvar unlocked 2>&1 | grep -i "unlocked:" | awk '{print $2}' || echo "Desconocido")
    SECURE_BOOT=$(fastboot getvar secure 2>&1 | grep -i "secure:" | awk '{print $2}' || echo "Desconocido")
    PRODUCT_NAME=$(fastboot getvar product 2>&1 | grep -i "product:" | awk '{print $2}' || echo "Desconocido")
    
    echo " -> Nombre de Producto / Codename: $PRODUCT_NAME"
    echo " -> Bootloader Desbloqueado: $BL_UNLOCKED"
    echo " -> Secure Boot Activo: $SECURE_BOOT"

    echo ""
    echo "[4/4] Comprobando particiones críticas del sistema..."
    fastboot getvar partition-type:boot 2>&1 | head -n 2 || true
    fastboot getvar current-slot 2>&1 | head -n 2 || true
else
    echo "[!] No se detectó dispositivo en Fastboot. Comprobando modo ADB / Normal..."
    ADB_DEV=$(adb devices 2>/dev/null | grep -w "device" || true)
    if [ -n "$ADB_DEV" ]; then
        echo "[✓] Dispositivo activo en ADB:"
        echo "$ADB_DEV"
        echo " -> Versión MIUI / HyperOS:"
        adb shell getprop ro.miui.ui.version.name 2>/dev/null || echo "No es MIUI/HyperOS nativo"
        adb shell getprop ro.build.version.incremental 2>/dev/null || true
    else
        echo "[!] Comprobando puertos EDL (Emergency Download Mode 9008)..."
        if command -v lsusb >/dev/null 2>&1; then
            lsusb | grep -E -i "05c6:9008|xiaomi|qualcomm" || echo "Sin puerto EDL detectado."
        else
            echo "Verifica la conexión física del cable USB/OTG."
        fi
    fi
fi

echo ""
echo "[✓] Diagnóstico MiTool completado con éxito."
