# Catálogo y almacenamiento de herramientas

Esta carpeta contiene el catálogo, las reglas de descarga y la documentación de herramientas externas. **No se deben guardar aquí ROMs, firmware ni paquetes descargados.** Las descargas de la aplicación deben almacenarse en su directorio privado en el dispositivo y quedar fuera de Git.

## Estructura

- `catalogo/herramientas.json`: catálogo organizado por tipo de dispositivo, marca y herramientas universales.
- `descargas/README.md`: contrato para descargas opcionales y almacenamiento privado.
- `generadas/README.md`: reglas para scripts creados por el orquestador.
- `gsm_modules/`: módulos GSM existentes; no se reemplazan en este cambio.

## Integración propuesta

La aplicación Android puede mostrar una opción de configuración para descargar herramientas compatibles. No recomiendo descargarlas sin avisar durante la instalación: ofrecer un selector inicial evita consumo inesperado de datos y almacenamiento. El catálogo fija un límite de **100 MiB en total** para herramientas externas opcionales; no es una promesa de que cualquier conjunto de herramientas quepa ni que cada herramienta funcione en todas las arquitecturas.

Antes de implementar el descargador, debe verificar las limitaciones de Android/Termux, ABI, licencia, origen oficial, tamaño y SHA-256. Los elementos con `status: "planned"` no son descargables ni ejecutables todavía. El catálogo actual es una especificación, no un instalador.

## Seguridad y alcance

- Usar únicamente herramientas de diagnóstico y reparación autorizada, procedentes de fuentes oficiales.
- No descargar ni distribuir ROMs, firmware, firehose/programadores, imágenes de partición o archivos de desbloqueo.
- No ejecutar archivos de GitHub solo por estar alojados allí; revisar proyecto, licencia, versión, firma/hash y permisos requeridos.
- No incluir bypass de FRP/iCloud, eliminación de MDM/financiamiento, modificación de IMEI, evasión de controles de seguridad ni flasheo automático.
- Operaciones que escriben, borran o desbloquean requieren revisión del modelo exacto, copia de seguridad y confirmación explícita.
- El diagnóstico eléctrico de cortocircuitos no puede inferirse con certeza usando solo el cable USB o software. Hace falta equipo de medición adecuado; presentar telemetría como indicio, no como medición de resistencia.

Consulta `catalogo/herramientas.json` para la matriz inicial.
