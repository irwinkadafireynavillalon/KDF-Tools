# Plataformas adicionales y medios de arranque

El catálogo complementario `expansiones_plataformas.json` añade referencias para iPhone/iPad/Mac y para crear medios USB de instalación/recuperación de computadoras. Se carga junto con `herramientas.json` por `buscar_herramientas.py`.

## Apple

Se listan flujos oficiales de Apple Devices/Finder y guías de recuperación. Las opciones dependen del modelo y sistema operativo anfitrión; una restauración puede borrar datos. No se ofrece elusión de Activation Lock ni acceso a datos protegidos.

## USB booteables y sistemas de computadora

Se registran páginas oficiales de Rufus, Ventoy, Microsoft, Apple y distribuciones Linux. No se incluyen imágenes de sistema ni instaladores en Git. La creación de medios puede borrar la unidad USB; antes de cualquier integración de escritura se debe identificar explícitamente el dispositivo destino, mostrar aviso y requerir confirmación.

Los paquetes Nix pueden ayudar a obtener utilidades individuales en un host compatible, pero no garantizan acceso USB de bajo nivel ni convierten automáticamente Android en un creador de medios compatible. La aplicación debe detectar el host/ABI y mostrar “no compatible” o una ruta manual cuando sea necesario.

## Marcas y modelos recurrentes

La lista de marcas es un filtro inicial, no una afirmación de compatibilidad ni de frecuencia de fallos. Para recomendar modelos con problemas recurrentes, usar datos de casos del taller (modelo, revisión, síntoma, diagnóstico confirmado) y mostrar cantidad de casos, periodo y fuente. No inferir una falla común solo por la marca.
