#!/usr/bin/env bash
# scripts/imei_repair.sh - Reparación / Escritura de IMEI (MediaTek, Qualcomm, Samsung)
# KDF-LuX · Módulo IMEI Repair & NV Restore
echo "=========================================================="
echo "  [KDF-LuX] REPARACIÓN DE IMEI / NV DATA                "
echo "=========================================================="

DEVICE="$1"
BRAND="$2"
PROCESSOR="$3"

echo "[*] Dispositivo objetivo : $DEVICE | Marca: $BRAND | Proc: $PROCESSOR"
echo ""

# ─── 1. Detectar modo de conexión ───────────────────────────────────────────
echo "[1/5] Escaneando conexión USB..."
ADB_DEV=$(adb devices 2>/dev/null | grep -v "List" | grep -v "^$" | head -1 || true)
FB_DEV=$(fastboot devices 2>/dev/null | head -1 || true)
USB_DEV=$(termux-usb -l 2>/dev/null || true)

if [ -n "$ADB_DEV" ]; then
    echo "  → Dispositivo ADB detectado: $ADB_DEV"
    MODE="adb"
elif [ -n "$FB_DEV" ]; then
    echo "  → Dispositivo Fastboot detectado: $FB_DEV"
    MODE="fastboot"
else
    echo "  → Buscando modo EDL/BROM..."
    echo "  USB activo: $USB_DEV"
    MODE="edl"
fi

# ─── 2. Leer IMEI actual ─────────────────────────────────────────────────────
echo ""
echo "[2/5] Leyendo IMEI actual del dispositivo..."

if [ "$MODE" = "adb" ]; then
    IMEI1=$(adb shell service call iphonesubinfo 1 2>/dev/null | \
        grep -oE "'[0-9]+'" | tr -d "'" | tr -d '\n' || echo "No disponible")
    IMEI2=$(adb shell service call iphonesubinfo 3 2>/dev/null | \
        grep -oE "'[0-9]+'" | tr -d "'" | tr -d '\n' || echo "No disponible")
    echo "  → IMEI 1 (ADB): $IMEI1"
    echo "  → IMEI 2 (ADB): $IMEI2"

    # Propiedades del dispositivo
    echo ""
    echo "  → Info adicional:"
    adb shell getprop ro.product.brand 2>/dev/null && \
    adb shell getprop ro.product.model 2>/dev/null && \
    adb shell getprop ro.serialno 2>/dev/null || true

elif [ "$MODE" = "fastboot" ]; then
    echo "  → Dispositivo en Fastboot. Para leer IMEI reinicia a Android primero."
    fastboot getvar product 2>&1 || true
fi

# ─── 3. Guía por procesador ──────────────────────────────────────────────────
echo ""
echo "[3/5] Método de reparación de IMEI según procesador..."

case "$PROCESSOR" in
    *MediaTek*|*MTK*|*Helio*|*Dimensity*|*Unisoc*|*Spreadtrum*)
        echo "  ── MEDIATEK / UNISOC ──"
        echo "  Herramienta: SP Flash Tool + Maui META"
        echo "  Paso 1: Descarga SP Flash Tool v5.x o MTK Client (open-source)"
        echo "  Paso 2: Carga el scatter file del dispositivo"
        echo "  Paso 3: En Maui META: Conecta en modo BROM (Vol- + USB)"
        echo "          → RF → Write_IMEI → Ingresa IMEI válido de 15 dígitos"
        echo "  Paso 4: Alternativamente usa MTK Engineering Mode:"
        echo "          adb shell am start -n com.mediatek.engineermode/.EngineerMode"
        echo "          → Telephony → CDS Information → Radio Information → AT+EGMR=1,7,\"IMEI\""
        echo "  Paso 5: Verificar: adb shell dumpsys iphonesubinfo | grep IMEI"
        echo ""
        echo "  Comando AT directo (si tiene acceso engineering mode):"
        echo "  AT+EGMR=1,7,\"IMEI_SLOT1\""
        echo "  AT+EGMR=1,10,\"IMEI_SLOT2\""
        ;;
    *Qualcomm*|*Snapdragon*)
        echo "  ── QUALCOMM / SNAPDRAGON ──"
        echo "  Herramienta: QPST / QFIL + Hydra Tool / MRT Key"
        echo "  Paso 1: Entra en modo diagnóstico: adb reboot edl"
        echo "  Paso 2: Verifica PID: 9006 (diagnóstico) o 9008 (EDL)"
        echo "  Paso 3: Con QPST → EFS Explorer → NV items → NV#550 (IMEI slot1), NV#4198 (IMEI slot2)"
        echo "  Paso 4: Exporta backup NV antes de escribir"
        echo "  Paso 5: Con Chimera Tool: Device Info → Write IMEI"
        echo "  Paso 6: Reinicia y verifica con *#06#"
        echo ""
        echo "  Intentando modo diagnóstico QDLoader..."
        if command -v adb >/dev/null 2>&1 && [ -n "$ADB_DEV" ]; then
            echo "  → Ejecutando: adb reboot edl"
            echo "  [!] Para continuar conecta QPST / Chimera / Hydra en PC"
        fi
        ;;
    *Exynos*|*Samsung*)
        echo "  ── SAMSUNG / EXYNOS ──"
        echo "  Herramienta: Chimera Tool / Z3X Samsung Tool Pro / UFI Box"
        echo "  Paso 1: Entra en Download Mode (Vol- + Bixby + USB o Vol- + USB en nuevos)"
        echo "  Paso 2: Con Z3X: Samsung → Unlock → Write IMEI"
        echo "  Paso 3: Con Chimera: Device → Write IMEI (requiere créditos/licencia)"
        echo "  Paso 4: Reinicia: adb reboot && verificar *#06#"
        ;;
    *Apple*|*Bionic*)
        echo "  ── APPLE ──"
        echo "  IMEI Apple se gestiona vía servidor Apple. No se puede escribir en hardware sin jailbreak."
        echo "  Solución: Solicitar cambio de IMEI en Apple Store (SOLO dispositivos robados con factura)"
        echo "  Herramienta: Checkm8 + iPwnder para acceso DFU avanzado"
        ;;
    *)
        echo "  ── GENÉRICO ──"
        echo "  Paso 1: Verifica procesador exacto"
        echo "  Paso 2: Usa la guía correspondiente arriba"
        echo "  Herramientas universales: Chimera Tool, Hydra Tool, UFI Box, MRT Key"
        ;;
esac

# ─── 4. Verificar IMEI con patrón Luhn ──────────────────────────────────────
echo ""
echo "[4/5] Validador de IMEI (algoritmo Luhn)..."
cat << 'LUHN_SCRIPT'
  Para validar un IMEI ejecuta en Termux:
  python3 -c "
  imei='AQUI_TU_IMEI_15_DIGITOS'
  s=sum(int(d)*2-9 if (i%2==0 and int(d)*2>9) else int(d)*2 if i%2==0 else int(d) for i,d in enumerate(imei[-2::-1]))
  print('IMEI VÁLIDO' if (s+int(imei[-1]))%10==0 else 'IMEI INVÁLIDO')
  "
LUHN_SCRIPT

# ─── 5. Herramientas disponibles ─────────────────────────────────────────────
echo ""
echo "[5/5] Herramientas GSM disponibles en el sistema..."
for tool in adb fastboot python3 mtkclient edl qpst chimera hydra; do
    if command -v "$tool" >/dev/null 2>&1; then
        echo "  ✓ $tool disponible"
    else
        echo "  ✗ $tool no instalado"
    fi
done

echo ""
echo "=========================================================="
echo "  KDF-LuX IMEI Repair - Diagnóstico completado"
echo "  ADVERTENCIA: Solo repara IMEI en dispositivos propios"
echo "=========================================================="
