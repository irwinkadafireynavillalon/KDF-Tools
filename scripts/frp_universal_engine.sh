#!/usr/bin/env bash
# ==============================================================================
# KDF-TOOLS · FRP UNIVERSAL RESET ENGINE
# Módulo de remoción y bypass de protección Factory Reset Protection (FRP)
# Métodos: Fastboot OEM / ADB Settings Ingestion / Browser Intent
# ==============================================================================

CYAN='\033[0;36m'
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${CYAN}======================================================${NC}"
echo -e "${CYAN}    ⚡ KDF-TOOLS · FRP UNIVERSAL BYPASS ENGINE ⚡      ${NC}"
echo -e "${CYAN}======================================================${NC}"

# 1. Comprobar si hay dispositivo en ADB
ADB_DEV=$(adb devices | grep -v "List" | grep "device" | awk '{print $1}' | head -n 1)
# 2. Comprobar si hay dispositivo en Fastboot
FB_DEV=$(fastboot devices 2>/dev/null | awk '{print $1}' | head -n 1)

if [ -n "$ADB_DEV" ]; then
    echo -e "${GREEN}[+] Dispositivo detectado vía ADB: $ADB_DEV${NC}"
    echo -e "[*] Desactivando Setup Wizard y Google Play Services Setup..."
    
    adb -s "$ADB_DEV" shell pm disable-user --user 0 com.google.android.setupwizard 2>/dev/null
    adb -s "$ADB_DEV" shell pm disable-user --user 0 com.sec.android.app.SecSetupWizard 2>/dev/null
    adb -s "$ADB_DEV" shell pm disable-user --user 0 com.motorola.setup 2>/dev/null
    adb -s "$ADB_DEV" shell pm disable-user --user 0 com.miui.setupwizard 2>/dev/null

    echo -e "[*] Sobrescribiendo flags de provisión de cuenta..."
    adb -s "$ADB_DEV" shell settings put secure user_setup_complete 1
    adb -s "$ADB_DEV" shell settings put global device_provisioned 1
    adb -s "$ADB_DEV" shell content insert --uri content://settings/secure --bind name:s:user_setup_complete --bind value:s:1 2>/dev/null

    echo -e "${GREEN}[✓] Inyección de Bypass completada con éxito.${NC}"
    exit 0
fi

if [ -n "$FB_DEV" ]; then
    echo -e "${YELLOW}[+] Dispositivo detectado vía Fastboot: $FB_DEV${NC}"
    echo -e "[*] Ejecutando borrado de partición FRP / persist..."
    fastboot erase frp 2>/dev/null || fastboot erase config 2>/dev/null || fastboot erase persistent 2>/dev/null
    echo -e "${GREEN}[✓] Comando de borrado enviado a Fastboot.${NC}"
    exit 0
fi

echo -e "${RED}[-] No se detectó dispositivo en ADB ni Fastboot.${NC}"
echo -e "${YELLOW}[!] Conecta el teléfono con depuración USB activa o en modo Fastboot.${NC}"
exit 1
