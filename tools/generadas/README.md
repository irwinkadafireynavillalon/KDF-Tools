# Scripts del usuario y del orquestador

El catálogo puede enlazar scripts mantenidos por el usuario o por el proyecto, pero los scripts de ejecución y su integración de interfaz están fuera del alcance de esta carpeta hasta que se autorice su incorporación. Los scripts generados durante el uso no se escriben en Git: la app debe guardarlos en su espacio privado `KDF-LuX/generated/`.

## Botón «Buscar herramienta» (contrato para la app

1. Recibir el informe de diagnóstico disponible: descriptores USB, permiso USB concedido, estado ADB/Fastboot, fabricante/modelo/variante/chipset solo si el dispositivo los expone, y nivel de confianza.
2. Buscar en `../catalogo/herramientas.json` por protocolo y coincidencias de fabricante/modelo/chipset.
3. Mostrar coincidencias ordenadas, sus limitaciones, compatibilidad y fuente. Marcar claramente cuando una coincidencia sea solo aproximada.
4. Permitir abrir la herramienta local existente o consultar su fuente oficial; una descarga requiere aceptación visible.
5. Si no hay coincidencia fiable, ofrecer selección manual y ruta de trabajo manual. No generar comandos de escritura ni escoger firmware por conjetura.

## Scripts creados por el usuario

La aplicación puede mostrar o indexar scripts que el usuario coloque en su ubicación de scripts, pero debe distinguirlos de herramientas verificadas. Antes de ejecutar: mostrar ruta/origen, contenido o diff, permisos, argumentos, dispositivo objetivo y efectos previstos; detectar operaciones de escritura/borrado y pedir confirmación. No ejecutar código generado por IA solo por haberlo creado el orquestador.

No se implementan aquí mecanismos de evasión de autenticación, FRP/iCloud, gestión MDM/financiación, IMEI ni controles OEM. Para operaciones no compatibles, presentar el motivo y permitir que el técnico continúe manualmente fuera de la app.
