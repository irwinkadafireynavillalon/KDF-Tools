# Funciones previstas para el módulo Android

`tools/` define contratos y helpers locales; no modifica el proyecto Android ni añade pantallas/permisos al manifiesto.

## Recepción y registros

- Crear caso con categoría, marca/modelo si se conocen, síntoma y modo Manual/Automático/Asistente.
- Adjuntar fotos tomadas solo cuando el usuario active la cámara. Guardar archivos en almacenamiento privado de la aplicación y registrar referencias relativas; no incluir imágenes de clientes en Git.
- Mantener estado, conexiones, observaciones, pruebas, resultado confirmado/descartado/inconcluso y referencias a scripts.

## Conectividad y varios dispositivos

Inventariar conexiones locales separadas: USB Host/OTG, ADB USB, Fastboot USB, serial USB, cámaras/microscopios USB UVC, Bluetooth accesorio y otros accesorios locales. El permiso USB se solicita para el dispositivo elegido. Mostrar todos los nodos detectados con su propia identidad/estado; no asumir que accesorios (como un microscopio) son el dispositivo reparado. No se requiere escaneo de IP ni WEP.

ADB/Fastboot se detectan como capacidades separadas. EDL Qualcomm y MediaTek se muestran como modos específicos cuyo soporte depende del modelo, host, herramienta y autorización del fabricante. iPhone/iPad se reconocen como categoría Apple y usan flujos de soporte oficiales; las operaciones disponibles dependen del sistema anfitrión y modelo.

## Diagnóstico por lotes, notificaciones e informes

El helper Python crea planes revisables por dispositivo, pero no ejecuta comandos. La futura app debe ejecutar solo verificaciones compatibles, identificar resultados por dispositivo y detener un lote si cambia/desaparece la conexión. Las notificaciones de esta fase son locales: reconexión, permiso requerido, prueba finalizada y aprobación pendiente. El informe agrega categorías y resultados, separando hechos e hipótesis.

## Microscopio/cámara

La captura puede usar cámara integrada o una cámara USB compatible. Microscopios USB suelen exponerse como UVC, pero el soporte depende del dispositivo Android, OTG, permisos, ancho de banda, energía y formato de vídeo. La app debe probar enumeración/vista previa y mostrar incompatibilidad si falla; no prometer soporte universal.

## Privacidad por defecto

Para diagnosticar funciones básicas no se necesitan contraseñas, tokens, nombres de clientes, IMEI o números de serie. Registrar solo lo necesario; fotos e informes se quedan en almacenamiento privado, con controles de exportar y borrar. Solicitar permisos de cámara, USB, Bluetooth o notificaciones únicamente cuando se active la función correspondiente.

## Alcance de implementación en este cambio

Se añadieron contratos JSON y helpers Python para recepción, referencias de fotos, varios dispositivos, planes de lote, notificaciones locales y resumen agregado. No se modificó AndroidManifest, UI, cámara, capa USB ni ejecutor de ADB/Fastboot: esas integraciones requieren cambios en `kdf-tools_extracted/`, fuera del alcance acordado para esta carpeta.
