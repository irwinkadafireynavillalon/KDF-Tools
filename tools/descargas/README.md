# Descargas opcionales

Las herramientas descargadas deben permanecer en el almacenamiento privado de la aplicación Android, no en esta carpeta del repositorio. La app ya define un directorio de datos bajo `filesDir/KDF-LuX`; la implementación puede usar un subdirectorio `tools/` para paquetes validados.

## Flujo requerido

1. El usuario elige categoría de dispositivo, marca y herramienta.
2. La aplicación muestra tamaño, versión, compatibilidad de host/ABI, licencia, origen y permisos.
3. Antes de descargar, comprueba el espacio disponible y la cuota de 100 MiB del catálogo. La cuota es configurable y no debe traducirse en una descarga silenciosa.
4. Descarga por HTTPS desde un proveedor permitido; valida tamaño y SHA-256 contra un manifiesto de confianza.
5. Mantiene el archivo en cuarentena hasta terminar todas las comprobaciones; solo entonces lo habilita.
6. No ejecuta herramientas recién descargadas automáticamente. Solicita confirmación justo antes de una operación y registra el resultado sin secretos.
7. Permite cancelar, eliminar y consultar versión/licencia de cada herramienta.

No ofrecer un paquete universal de 100 MiB como garantía: el tamaño cambia según sistema operativo y arquitectura. En Android, muchas herramientas de escritorio no son ejecutables directamente. No empaquetar ni descargar firmware, ROMs o imágenes de partición mediante este catálogo.

Por ahora este documento define requisitos; no hay descargador implementado ni herramientas habilitadas en `herramientas.json` (`status: planned`).
