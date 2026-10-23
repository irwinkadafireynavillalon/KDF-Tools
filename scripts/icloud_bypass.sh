#!/usr/bin/env bash
# scripts/icloud_bypass.sh - Asistente de Bypass iCloud Activation Lock
# KDF-LuX · Módulo Apple DFU + Checkm8 + Bypass Tools
echo "=========================================================="
echo "  [KDF-LuX] BYPASS ICLOUD ACTIVATION LOCK - APPLE        "
echo "=========================================================="

DEVICE="$1"
BRAND="$2"
PROCESSOR="$3"

echo "[*] Modo: $BRAND $PROCESSOR"
echo ""

# ─── 1. Detectar dispositivo Apple en DFU ────────────────────────────────────
echo "[1/6] Buscando dispositivo Apple (DFU / Recovery)..."
USB_LIST=$(termux-usb -l 2>/dev/null || echo "[]")
echo "  USB activos: $USB_LIST"

# VID Apple DFU = 05ac:1227 | Recovery = 05ac:1281
echo ""
echo "  Identificadores Apple:"
echo "  • DFU Mode    → VID: 05ac  PID: 1227"
echo "  • Recovery    → VID: 05ac  PID: 1281"
echo "  • Normal iOS  → VID: 05ac  PID: 12a8"

# ─── 2. Método por chipset ───────────────────────────────────────────────────
echo ""
echo "[2/6] Método de bypass según chip Apple..."

echo ""
echo "  ┌─────────────────────────────────────────────────────────────────┐"
echo "  │  CHECKM8 (Vulnerable: A5 → A11, iPhone 4S → X)                │"
echo "  │  HERRAMIENTA: checkra1n / palera1n / checkm8 (open source)     │"
echo "  │  BYPASS: ra1nUSB + iActivate / KFD Exploit (iOS 16+)           │"
echo "  ├─────────────────────────────────────────────────────────────────┤"
echo "  │  CHIP A12 → A17 (iPhone XS/XR → 15) → NO checkm8             │"
echo "  │  MÉTODO: Software bypass con MEID/FMI-OFF (limpieza IMEI)      │"
echo "  │  Herramientas: iRemove Tools, DoulCi Bypass, iCloud DNS Bypass │"
echo "  └─────────────────────────────────────────────────────────────────┘"

# ─── 3. Pasos checkm8 (A5-A11) ───────────────────────────────────────────────
echo ""
echo "[3/6] PASOS - Checkra1n / Palera1n Bypass (chips A5–A11)..."
echo "  Paso 1: Instalar checkra1n en PC/Mac o palera1n en Linux ARM"
echo "          git clone https://github.com/palera1n/palera1n"
echo "          cd palera1n && ./palera1n -l  (modo rootless iOS 15/16)"
echo ""
echo "  Paso 2: Entrar en modo DFU:"
echo "    iPhone X y anterior:"
echo "      • Mantén Encendido + Vol Bajo 8s → suelta Encendido → mantén Vol Bajo 5s"
echo "    iPhone 8:"
echo "      • Vol Subir → Vol Bajar → Encendido + Vol Bajo hasta DFU"
echo ""
echo "  Paso 3: Ejecutar jailbreak + Bypass:"
echo "          ./palera1n --tweaks -B  # modo bypass activación"
echo ""
echo "  Paso 4: Instalar iActivate desde Cydia/Sileo:"
echo "          apt install com.naut.iactivate"
echo "          iactivate bypass"
echo ""
echo "  Paso 5: Para bypass permanente FMI-OFF:"
echo "          Necesitas IMEI del dispositivo → Servicio IMEI FMI-OFF"
echo "          (servicios: imei24.com, doctorsim.com)"

# ─── 4. DNS Bypass (cualquier iPhone) ────────────────────────────────────────
echo ""
echo "[4/6] Bypass DNS iCloud (método sin PC, temporal)..."
echo "  Paso 1: En la pantalla de activación → conectar a Wi-Fi"
echo "  Paso 2: Tap en 'i' → DNS Manual → cambiar a:"
echo "          • 104.154.51.7 (iCloud Bypass DNS)"
echo "          • o 78.100.17.60"
echo "  Paso 3: Conectar → pantalla de activación debería saltar"
echo "  Nota: Temporal, llama, WhatsApp, App Store limitados"

# ─── 5. MDM / Activation Lock Policy ────────────────────────────────────────
echo ""
echo "[5/6] MDM Profile Bypass..."
echo "  Si el dispositivo tiene MDM corporativo:"
echo "  Herramienta: iRemove MDM Bypass Tool"
echo "  Paso 1: Conectar en DFU"
echo "  Paso 2: iRemove → MDM Bypass → seleccionar modelo"
echo "  Paso 3: Proceso automático ~5 minutos"

# ─── 6. Herramientas disponibles ─────────────────────────────────────────────
echo ""
echo "[6/6] Estado de herramientas..."
for tool in python3 ideviceinfo idevicerestore libimobiledevice; do
    if command -v "$tool" >/dev/null 2>&1; then
        echo "  ✓ $tool"
    else
        echo "  ✗ $tool (instalar: pkg install $tool)"
    fi
done

if command -v python3 >/dev/null 2>&1; then
    echo ""
    echo "  Instalando dependencias iCloud bypass..."
    pip3 install pymobiledevice3 2>/dev/null || true
fi

echo ""
echo "=========================================================="
echo "  KDF-LuX iCloud Bypass - Guía completada"
echo "  AVISO LEGAL: Solo en dispositivos de tu propiedad"
echo "=========================================================="
