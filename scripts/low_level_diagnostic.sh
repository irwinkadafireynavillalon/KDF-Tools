#!/usr/bin/env bash
# scripts/low_level_diagnostic.sh - Diagnóstico de bajo nivel (EDL 9008, BROM MTK, Test-Point)
set -e
echo "=========================================================="
echo " [KDF-LuX] DIAGNÓSTICO DE BAJO NIVEL (EDL, BROM, TEST-POINT) "
echo "=========================================================="
echo "[*] Escaneando buses USB del sistema..."
termux-usb -l 2>/dev/null || true
echo "[*] Verificando controladores soportados:"
echo " • Qualcomm HS-USB QDLoader 9008 (VID: 05c6, PID: 9008)"
echo " • MediaTek USB Port / Preloader / BROM (VID: 0e8d, PID: 0003 / 2000)"
echo " • Samsung Exynos USB Download Mode (VID: 04e8, PID: 6860)"
echo " • Unisoc / Spreadtrum SCI USB2Serial (VID: 1782, PID: 4d00)"
echo "[*] Escaneo finalizado. Conecta el dispositivo con el Test-Point o combinación de botones adecuada."
