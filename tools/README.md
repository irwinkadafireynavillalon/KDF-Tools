# Catálogo y flujo de herramientas KDF

## Ejecutar búsqueda local

```sh
python3 -m tools.catalogo.buscar_herramientas --protocol fastboot
python3 -m tools.catalogo.buscar_herramientas --device smartphones --brand Apple --json
python3 -m tools.catalogo.buscar_herramientas --device other-usb --json
```

El catálogo es una lista de referencias/capacidades, no instala ni ejecuta binarios. Nix-on-Droid puede proporcionar una dependencia individual cuando exista una build compatible; debe detectarse host/ABI. Las descargas opcionales se hacen visibles y a petición, nunca silenciosas.

## Funciones modeladas

- Categorías de celulares, tabletas, wearables, computadoras, pantallas/TV, automotriz y otros accesorios conectados.
- Referencias ADB/Fastboot, Qualcomm EDL, MediaTek, soporte Apple, USB UVC/microscopio, USB inventory y medios de arranque.
- Casos de reparación locales, referencia a fotos privadas, varios dispositivos por caso, planes de diagnóstico por lotes, notificaciones locales y reportes agregados.
- Transportes locales sin descubrimiento por IP/WEP.

Las funciones de flujo Python solo crean registros y planes: no acceden a cámara/USB ni ejecutan diagnósticos. La aplicación Android todavía requiere su propia integración de permisos, UI y hardware.

## Pruebas

```sh
python3 -m unittest discover -s tools/tests -v
```

Las pruebas son simuladas y no conectan equipos. No se pudo ejecutar una compilación Android desde esta carpeta.
