# Integración de generación y edición de imágenes / video en la IA local

## 1️⃣ Requisitos de software
| Herramienta | Instalación en Termux / Linux | Uso |
|------------|-------------------------------|-----|
| **Stable Diffusion (Automatic1111)** | ```bash
# Instalar dependencias
pkg install -y python python-pip git wget
# Clonar repositorio UI
git clone https://github.com/AUTOMATIC1111/stable-diffusion-webui.git $HOME/sd-webui
cd $HOME/sd-webui
# Instalar requerimientos (conda no disponible en Termux, usar pip)
pip install -r requirements.txt
# Descargar modelo (ej. sd-v1-5.ckpt) y mover al folder `models/Stable-diffusion/`
``` | Genera imágenes a partir de prompt. |
| **FFmpeg** | ```bash
pkg install -y ffmpeg
``` | Conversión, montaje y edición de video. |
| **ImageMagick** | ```bash
pkg install -y imagemagick
``` | Edición rápida de imágenes (crop, resize, overlay). |
| **OpenCV (Python)** | ```bash
pip install opencv-python-headless
``` | Manipulación avanzada de video/imágenes en scripts Python. |

## 2️⃣ Configuración del servidor de generación de imágenes
1. Ejecuta el UI de Stable Diffusion en modo *API*:
   ```bash
cd $HOME/sd-webui
python launch.py --api --listen --port 7860
```
   - El flag `--api` habilita `/sdapi/v1/txt2img` y `/sdapi/v1/img2img`.
   - `--listen` permite acceso desde `127.0.0.1`.
2. Verifica que funcione:
   ```bash
curl -X POST http://127.0.0.1:7860/sdapi/v1/txt2img \
    -H "Content-Type: application/json" \
    -d '{"prompt":"un robot futurista en un bosque", "steps":20, "cfg_scale":7, "width":512, "height":512}'
```
   La respuesta contiene `images` codificadas en base64.

## 3️⃣ Scripts auxiliares (bash) para la IA local
### 3.1 generar_imagen.sh
```bash
#!/usr/bin/env bash
# $1 = prompt
# $2 = nombre de salida (sin extensión)
PROMPT="$1"
OUTFILE="$2.png"
PAYLOAD=$(cat <<EOF
{
  "prompt": "$PROMPT",
  "steps": 25,
  "cfg_scale": 7,
  "width": 512,
  "height": 512,
  "sampler_name": "Euler a"
}
EOF
)
RESP=$(curl -s -X POST http://127.0.0.1:7860/sdapi/v1/txt2img \
    -H "Content-Type: application/json" -d "$PAYLOAD")
# Extraer la primera imagen base64 y guardarla
echo "$RESP" | python - <<PY
import sys, json, base64, pathlib
resp=json.load(sys.stdin)
img_b64=resp['images'][0]
data=base64.b64decode(img_b64)
path=pathlib.Path('$OUTFILE')
path.write_bytes(data)
PY
```
chmod +x generar_imagen.sh
```

### 3.2 editar_imagen.sh (usa ImageMagick)
```bash
#!/usr/bin/env bash
# $1 = archivo origen
# $2 = operación (resize, crop, overlay, rotate)
# $3 = parámetro de la operación
INFILE="$1"
OP="$2"
PARAM="$3"
OUTFILE="edited_${INFILE}"
case "$OP" in
  resize) convert "$INFILE" -resize "$PARAM" "$OUTFILE" ;;
  crop)   convert "$INFILE" -crop "$PARAM" "$OUTFILE" ;;
  rotate) convert "$INFILE" -rotate "$PARAM" "$OUTFILE" ;;
  overlay) # $PARAM debe ser la ruta de la imagen overlay
            convert "$INFILE" "$PARAM" -gravity center -composite "$OUTFILE" ;;
  *) echo "Operación no soportada"; exit 1;;
esac
```
chmod +x editar_imagen.sh
```

### 3.3 crear_video.sh (monta imágenes en video con FFmpeg)
```bash
#!/usr/bin/env bash
# $1 = carpeta con imágenes numeradas (img001.png, img002.png, ...)
# $2 = nombre de salida .mp4
IMGDIR="$1"
OUTFILE="$2"
ffmpeg -y -framerate 24 -i "$IMGDIR/img%03d.png" -c:v libx264 -pix_fmt yuv420p "$OUTFILE"
```
chmod +x crear_video.sh
```

## 4️⃣ Extender `app.py` de KDF‑LuX
Añade un nuevo endpoint `/media` que delegue en los scripts anteriores.
```python
@app.route("/media", methods=["POST"])
def media():
    data = request.get_json() or {}
    action = data.get("action")   # "generate_image", "edit_image", "make_video"
    if action == "generate_image":
        prompt = data.get("prompt", "")
        filename = f"generated_{int(time.time())}.png"
        script = os.path.join(BASE_DIR, "scripts/generar_imagen.sh")
        run_cmd(["bash", script, prompt, filename])
        return jsonify({"status": "ok", "file": filename})
    elif action == "edit_image":
        src = data.get("src")
        op = data.get("operation")
        param = data.get("param")
        script = os.path.join(BASE_DIR, "scripts/editar_imagen.sh")
        run_cmd(["bash", script, src, op, param])
        return jsonify({"status": "ok", "file": f"edited_{src}"})
    elif action == "make_video":
        folder = data.get("folder")
        out = data.get("output", "output.mp4")
        script = os.path.join(BASE_DIR, "scripts/crear_video.sh")
        run_cmd(["bash", script, folder, out])
        return jsonify({"status": "ok", "file": out})
    else:
        return jsonify({"error": "acción no reconocida"}), 400
```
- **BASE_DIR** se define al inicio de `app.py` como `os.path.dirname(__file__)`.
- El endpoint devuelve el nombre del archivo generado; el frontend puede descargarlo con un simple GET a `/static/<file>` (agrega una ruta estática si es necesario).

## 5️⃣ Integración en la UI (JavaScript) – ejemplo rápido
```javascript
function sendMedia(action, payload){
  $.ajax({
    url:'/media',
    method:'POST',
    contentType:'application/json',
    data: JSON.stringify(Object.assign({action}, payload)),
    success:function(resp){
      if(resp.file){
        // Mostrar imagen/vídeo en la página
        if(action === 'generate_image' || action === 'edit_image'){
          $('#media-output').html('<img src="/static/'+resp.file+'" style="max-width:100%"/>');
        }else if(action === 'make_video'){
          $('#media-output').html('<video controls src="/static/'+resp.file+'" style="max-width:100%"></video>');
        }
      }
    }
  });
}
// Uso:
// sendMedia('generate_image', {prompt:'un dragón cyberpunk'});
// sendMedia('edit_image', {src:'generated_...png', operation:'resize', param:'800x600'});
// sendMedia('make_video', {folder:'/path/to/imgs', output:'myclip.mp4'});
```
Asegúrate de servir la carpeta `static/` con los archivos creados (Flask lo hace automáticamente si están dentro de `static/`).

## 6️⃣ Pasos finales
1. **Instala los paquetes** listados en la sección 1️⃣.
2. **Ejecuta Stable Diffusion** en modo API (puedes ejecutarlo en segundo plano con `nohup` o `tmux`).
3. **Copia los scripts** a `KDF-LuX/scripts/` y otórgales permiso de ejecución.
4. **Modifica `app.py`** añadiendo el endpoint `/media` como se muestra.
5. **Reinicia** la aplicación Flask.
6. Desde la UI, llama a `/media` mediante los ejemplos de JavaScript o a través del chat (puedes crear comandos como `/image <prompt>` que internamente envían la petición). 

Con esto la IA local disponible en `http://127.0.0.1:8080` podrá **generar imágenes, editarlas y componer videos** directamente desde KDF‑LuX, sin necesidad de servicios externos.
