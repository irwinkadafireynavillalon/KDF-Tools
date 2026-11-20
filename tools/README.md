# Alcance y verificación de `tools/`

Esta carpeta contiene los manifiestos y helpers portables para el módulo de reparación. Los cambios al catálogo son aditivos: se preservan los módulos existentes de `gsm_modules/` y las entradas de catálogos heredados.

## Estructura

- `catalogo/`: capacidades por dispositivo/marca, búsqueda local y administración Android especificada.
- `casos/`: registro JSONL local fuera del repositorio.
- `diagnostico/`: formato común de resultados.
- `diagnostico_usb/`: contrato/validador de telemetría y límites de inferencia eléctrica.
- `flujo_reparacion/`: alta local de casos, referencias de fotos privadas, varios dispositivos, planes de lote, notificaciones locales y resumen.
- `descargas/`: política de descargas opcionales y firmware elegido por el usuario.
- `generadas/`: contrato para indexar/revisar scripts del usuario, sin ejecutar código generado.
- `gsm_modules/`: herramientas GSM existentes, preservadas.
- `tests/`: pruebas simuladas, sin hardware real.

## Ejecutar en Termux

Primero asegúrate de estar en la raíz del checkout (la carpeta que contiene `tools/`). Si el código está en otra ruta, cambia el ejemplo por esa ruta:

```sh
cd /ruta/al/KDF-Tools
test -f tools/__init__.py && test -f tools/tests/__init__.py
python3 -m tools.catalogo.buscar_herramientas --validate
python3 -m unittest discover -s tools/tests -t . -v
```

`-t .` indica a `unittest` que el directorio actual es el paquete raíz; `tools/tests/__init__.py` permite descubrir esa carpeta como paquete. Ejecuta los comandos desde la raíz, no desde `tools/` ni `tools/tests/`.

Si `tools/` no existe, el checkout local no contiene esta rama/commit todavía. Confirma la rama `add-tooling-generated-scripts-scaffold` y actualízala antes de correr las pruebas. No instales nada para resolver `ModuleNotFoundError: tools`: normalmente significa que Python se lanzó desde otro directorio.

## Búsqueda del catálogo

Desde la raíz del repositorio:

```sh
python3 -m tools.catalogo.buscar_herramientas --protocol fastboot
python3 -m tools.catalogo.buscar_herramientas --device iphones --brand Apple --json
python3 -m tools.catalogo.buscar_herramientas --validate
```

El buscador y catálogo no descargan, instalan ni ejecutan programas. La ausencia de coincidencia exacta devuelve opciones informativas/manuales; no genera comandos por conjetura. Nix-on-Droid puede proporcionar una dependencia concreta si existe una build compatible con el host/ABI.

## Alcance y límites

Los contratos de fotos, Android en segundo plano, cámara UVC, lotes y notificaciones no añaden permisos, UI ni servicios al APK. Los helpers de lote crean planes; no ejecutan chequeos. iPhone/iPad, EDL, MediaTek y medios booteables son referencias o candidatos sujetos a host/modelo; no se afirma integración universal. Medios booteables y firmware no se escriben ni se flashean automáticamente.

No se incluyen ROMs/firmware, acceso IP/WEP requerido, bypass de seguridad ni redistribución no autorizada. No registrar identificadores personales/credenciales. Valores de telemetría del Android anfitrión no son mediciones eléctricas del dispositivo reparado.

Las pruebas son simuladas y no conectan equipos. Solo se debe declarar que pasaron después de ejecutar los comandos en un entorno real y observar el resultado.
