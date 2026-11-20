# Scripts del usuario y del orquestador

El usuario mantiene sus scripts. Esta carpeta define cómo encontrarlos y conservarlos; no contiene código ejecutable generado para un dispositivo real. Los scripts creados durante el uso se guardan en el almacenamiento privado de la app, por ejemplo `KDF-LuX/generated/`, separados del repositorio.

## Buscar herramienta/script con el diagnóstico

El botón previsto «Buscar herramienta» consume el informe disponible y busca coincidencias en `../catalogo/herramientas.json` y en el índice local de scripts del usuario. Debe usar tipo de dispositivo, fabricante, modelo y revisión de placa cuando estén confirmados, chipset, VID/PID, protocolo USB/ADB/Fastboot y el estado del adaptador de medición. La tarjeta de resultado muestra fuente, versión/hash cuando existan, requisitos, por qué coincide, incertidumbre y alternativas manuales.

Si la pantalla está rota, el modo USB puede exponer fabricante/modelo, pero no se garantiza: VID/PID/chipset pueden ser compartidos. Marcar los campos como observados/no disponibles/no confirmados; permitir completar modelo y variante manualmente. Sin identidad suficiente, mostrar únicamente diagnóstico de solo lectura y no sugerir acciones de escritura o firmware específico.

## Reutilizar scripts del usuario

Conservar scripts por fabricante/modelo/revisión y etiqueta de síntoma, protocolo, versión, fecha, fuente y resultado del técnico (hipótesis confirmada/descartada/no concluyente). Al buscar un caso parecido, presentar el script previo y las diferencias a revisar; no modificar ni borrar el original automáticamente. El usuario puede clonar una copia, ajustar variables y guardar una nueva revisión. El aprendizaje local debe guardar solo datos autorizados y permitir exportar/eliminar registros.

Antes de ejecutar un script, mostrar contenido/diff, origen, objetivo, argumentos, compatibilidad y efectos previstos. Lectura/diagnóstico se distingue de escritura, borrado, desbloqueo y flasheo; para las acciones modificadoras se requiere confirmación explícita. No ejecutar automáticamente código de IA, GitHub o foros. Si una herramienta no es compatible, explicar la razón y dejar la ruta manual.

No se incluyen bypass de FRP/iCloud, MDM/financiamiento, protecciones de acceso, modificación de IMEI ni evasión de autorización OEM.
