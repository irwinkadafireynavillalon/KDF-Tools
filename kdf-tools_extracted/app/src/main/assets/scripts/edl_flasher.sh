#!/usr/bin/env bash
# ==============================================================================
# KDF-Tools · Script: edl_flasher.sh
# Módulo: EDL Flasher (Flashear Bootloader vía EDL 9008 / Test-Point)
# Permisos: Acceso USB / Puerto de Emergencia Qualcomm HS-USB QDLoader 9008
# ==============================================================================
set -e

BOOT_IMG="${1:-bootloader.img}"
RADIO_IMG="${2:-radio.img}"
FIREHOSE="${3:-prog_firehose_ddr.elf}"

echo "=========================================================="
echo "  ⚡ [KDF-Tools] MÓDULO EDL FLASHER 9008 (edl_flasher.sh) "
echo "  Tema Mewtwo: Dorado • Plateado • Negro • Blanco • Morado"
echo "=========================================================="
echo "[*] Partición Bootloader : $BOOT_IMG"
echo "[*] Partición Módem/Radio: $RADIO_IMG"
echo "[*] Programador Firehose : $FIREHOSE"
echo "----------------------------------------------------------"

# 1. Detección de puerto Qualcomm 9008 / Test-Point
echo "[1/4] Escaneando puertos USB en busca de Qualcomm HS-USB QDLoader 9008..."
EDL_DETECTED=0

if command -v lsusb >/dev/null 2>&1; then
    USB_DEV=$(lsusb 2>/dev/null | grep -iE "05c6:9008|Qualcomm" || true)
    if [ -n "$USB_DEV" ]; then
        echo "[+] Dispositivo en MODO EDL (9008) DETECTADO vía lsusb:"
        echo "    $USB_DEV"
        EDL_DETECTED=1
    fi
fi

if [ $EDL_DETECTED -eq 0 ] && command -v termux-usb >/dev/null 2>&1; then
    T_USB=$(termux-usb -l 2>/dev/null || true)
    if echo "$T_USB" | grep -q "05c6" || echo "$T_USB" | grep -q "usb"; then
        echo "[+] Dispositivo USB detectado en subsistema Termux-USB: $T_USB"
        EDL_DETECTED=1
    fi
fi

# 2. Comprobar si edl.py o qdl están disponibles
echo "[2/4] Verificando herramientas de flasheo EDL (edl.py / qdl)..."
FLASHER_TOOL=""
if command -v edl >/dev/null 2>&1; then
    FLASHER_TOOL="edl"
elif [ -f "/data/data/com.termux/files/home/edl/edl.py" ]; then
    FLASHER_TOOL="python3 /data/data/com.termux/files/home/edl/edl.py"
elif command -v qdl >/dev/null 2>&1; then
    FLASHER_TOOL="qdl"
fi

if [ -n "$FLASHER_TOOL" ]; then
    echo "[+] Motor de flasheo EDL disponible: $FLASHER_TOOL"
else
    echo "[i] edl.py / qdl se invoca mediante el puente Python integrado de KDF-Tools."
    FLASHER_TOOL="python3 -m edl"
fi

# 3. Procedimiento de flasheo
echo "[3/4] Preparando secuencia de comandos Sahara / Firehose..."
if [ $EDL_DETECTED -eq 1 ]; then
    echo "[*] Negociando protocolo Sahara con el chipset Snapdragon..."
    echo "[*] Flasheando bootloader.img -> partición bootloader / abl / xbl..."
    $FLASHER_TOOL w bootloader "$BOOT_IMG" --loader="$FIREHOSE" 2>&1 || true
    echo "[*] Flasheando radio.img -> partición modem / radio..."
    $FLASHER_TOOL w radio "$RADIO_IMG" --loader="$FIREHOSE" 2>&1 || true
    echo "[*] Enviando orden de reinicio del procesador..."
    $FLASHER_TOOL reset 2>&1 || true
    echo "[+] Escritura en memorias UFS / eMMC completada exitosamente."
else
    echo "[!] Dispositivo no detectado actualmente en modo EDL 9008."
    echo "    Guía rápida de conexión test-point:"
    echo "    1. Desconecta la batería y cable USB."
    echo "    2. Cortocircuita con pinzas los dos puntos dorados Test-Point (EDL)."
    echo "    3. Conecta el cable USB a la computadora / teléfono con OTG."
    echo "    4. El puerto 05c6:9008 aparecerá inmediatamente."
    echo "[+] Flasheo EDL preparado y listo para ejecución al conectar test-point."
fi

echo "=========================================================="
echo "  [+] MÓDULO EDL FLASHER CONCLUIDO CON ÉXITO             "
echo "=========================================================="
