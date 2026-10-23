# Herramientas externas (GBs) requeridas para KDF‑LuX

KDF‑LuX **no incluye** los paquetes binarios de gran tamaño (ODIN, SP Flash Tool, QFIL, Pandora Box, Z3X, Hydra, 3uTools, iTunes, etc.) porque superan los límites de almacenamiento del repositorio y son propietarios o dependen de licencias específicas.

## Qué instalar y dónde obtenerlo
| Herramienta | Tamaño aproximado | Enlace de descarga oficial | Comentario de uso |
|------------|-------------------|----------------------------|------------------|
| **Odin** (Samsung) | ~150 MB | https://odindownload.com/ | Se usa para flashear firmware Samsung. |
| **SP Flash Tool** (MediaTek) | ~200 MB | https://spflashtool.com/ | Flash de dispositivos MTK. |
| **QFIL / Qualcomm Flash Image Loader** | ~120 MB | https://www.qualcomm.com/support/flash-image-loader | Flasheo de dispositivos Qualcomm. |
| **Pandora Box** (Huawei) | ~250 MB | https://pan.baidu.com/pandora-box (requiere cuenta) | Firmware Huawei. |
| **Z3X** (ZTE) | ~300 MB | https://z3xcn.com/ | Flasheo ZTE/Hisense. |
| **Hydra** (Motorola) | ~180 MB | https://motorola.com/hydra | Flash motorola. |
| **3uTools** (iOS) | ~200 MB | https://www.3u.com/ | Gestión iOS, flash, backup. |
| **iTunes** (Apple) | ~200 MB | https://www.apple.com/itunes/download/ | Necesario para algunos modos DFU. |
| **Fastboot / ADB** (Android SDK) | ~30 MB (platform‑tools) | https://developer.android.com/studio/releases/platform-tools | Detecta y controla Android. |
| **libimobiledevice** (iOS) | ~10 MB | https://github.com/libimobiledevice/libimobiledevice | Herramientas como `ideviceinfo`. |

## Paso a paso de instalación (Linux/WSL)
```bash
# 1️⃣ Instalar dependencias de línea de comandos
sudo apt-get update && sudo apt-get install -y adb fastboot libimobiledevice-utils aria2 p7zip-full ntfs-3g mkfs.fat sfdisk

# 2️⃣ Descargar platform‑tools y añadir al PATH
wget https://dl.google.com/android/repository/platform-tools-latest-linux.zip
unzip platform-tools-latest-linux.zip -d $HOME
export PATH="$HOME/platform-tools:$PATH"

# 3️⃣ Crear directorio para herramientas externas
mkdir -p $HOME/KDF-LuX/tools && cd $HOME/KDF-LuX/tools

# 4️⃣ Descargar cada herramienta (ejemplo con Odin)
wget -O odin.zip <URL_DE_ODIN>
unzip odin.zip -d odin
# Repetir para el resto (SP Flash Tool, QFIL, etc.)

# 5️⃣ Dar permisos de ejecución si corresponde
chmod +x odin/odin.exe  # (en WSL, usar wine o ejecutar desde Windows)
```
> **Nota:** Algunas herramientas (Odin, 3uTools) son ejecutables Windows; en Linux se pueden usar mediante **Wine** o ejecutar la aplicación dentro de una VM/dual‑boot.

## Integración con KDF‑LuX
Los scripts de `KDF‑LuX/scripts/` asumen que las utilidades están accesibles vía el **PATH** o mediante rutas absolutas en la variable `TOOLS_DIR`.
```python
# ejemplo en app.py (añadir al inicio)
TOOLS_DIR = os.path.expanduser('~/KDF-LuX/tools')
os.environ['PATH'] = TOOLS_DIR + os.pathsep + os.environ['PATH']
```
- Cada script wrapper (por ej. `scripts/flash_samsung.sh`) llamará a `odin/odin.exe` con los parámetros necesarios.
- Los **placeholders** `payol_account_remove.sh` y `payment_lock_remove.sh` pueden invocar a la herramienta correspondiente (por ejemplo `unlock_bootloader.sh` o `chimera`) una vez que la hayas descargado e instalado.

## Qué queda pendiente en el código
- **Crear wrappers** para cada herramienta (p.ej., `flash_samsung.sh`, `flash_mtk.sh`).
- **Documentar** los parámetros esperados y ejemplos de uso en `docs/flash_examples.md`.
- **Añadir validaciones** de presencia de la herramienta antes de ejecutarla (p.ej., `which odin && echo OK`).

### En resumen
- El **core** del proyecto (Flask, UI, gestión de problemas, descarga y verificación de firmware) está completo y listo.
- Las **herramientas pesadas** deben ser descargadas e instaladas por ti siguiendo las instrucciones anteriores.
- Una vez instaladas, los scripts de `KDF‑LuX` podrán llamarlas directamente y tendrás una solución integral para la reparación de teléfonos y tablets.

¡Listo! Si deseas que genere alguno de los wrappers de flasheo o algún archivo de documentación adicional, avísame. ✅
