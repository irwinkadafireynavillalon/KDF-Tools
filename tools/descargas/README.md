# Descargas visibles y bajo demanda

Este directorio contiene la política del descargador, no binarios. La app debe pedir selección y confirmación; también puede ofrecer herramientas opcionales en la configuración inicial. El presupuesto inicial sugerido para herramientas es 100 MiB. No se aplica al firmware escogido por el usuario: mostrar su tamaño y pedir confirmación independiente.

## Verificación antes de habilitar una herramienta

- Permitir únicamente HTTPS y fuentes revisadas; mostrar propietario, repositorio/página, versión/release, licencia y fecha de revisión.
- Resolver compatibilidad de sistema operativo, ABI/arquitectura, Android/Termux y modelo de dispositivo. No asumir que herramientas de escritorio funcionan en Android.
- Comprobar tamaño contra presupuesto/espacio disponible; permitir cancelar y reanudar de forma segura.
- Validar SHA-256 o firma si el proveedor publica un valor de confianza. Si no hay verificación, indicarlo y no marcar el archivo como verificado.
- Descargar a almacenamiento privado y temporal, validar, y luego habilitar como herramienta instalada. No ejecutar automáticamente.
- Permitir ver, borrar y actualizar herramientas, conservando el sistema de archivos del usuario fuera del alcance.

## Firmware bajo demanda

No se guarda firmware, ROM, imagen de partición ni programador en el repositorio. Cuando haya una página oficial para un modelo exacto, la app puede mostrar el enlace y dejar que el usuario inicie la descarga. Antes, pedir confirmación del modelo, variante, región, versión y tamaño, explicar riesgos de pérdida de datos/incompatibilidad y mostrar la firma/hash oficial si se publica. No hacer flasheo automático ni convertir publicaciones de foros en fuentes de firmware confiables.

## Repositorios GitHub y scripts

Una cuenta de GitHub conectada es opcional; debe usar autorización oficial, permisos mínimos y revocables. Los repositorios públicos pueden servir como referencia, pero no se ejecuta código de un repositorio automáticamente. Mostrar dueño, URL, licencia, release/tag y cambios; revisar dependencias y hash. Los tokens no se guardan en el catálogo, scripts ni logs.

La revisión de licencia es necesaria para decidir si se puede incluir, redistribuir o empaquetar una herramienta. Si no se conoce, se puede mostrar un enlace al proveedor para evaluación manual, sin copiar el software.
