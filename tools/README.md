# Alcance y verificación de `tools/`

Esta carpeta contiene los manifiestos y helpers portables para el módulo de reparación. Preserva los módulos existentes en `gsm_modules/`; este conjunto de cambios es aditivo y mantiene también las entradas de catálogos heredados.

## Estructura

- `catalogo/`: capacidades por dispositivo/marca, búsqueda local y administración Android especificada.
- `casos/`: registro JSONL local fuera del repositorio.
- `diagnostico/`: formato común de resultados.
- `diagnostico_usb/`: contrato/validador de telemetría y límites de inferencia eléctrica.
- `flujo_reparacion/`: alta local de caso, referencias de fotos privadas, varios dispositivos, planes de lote, notificaciones locales y resumen.
- `descargas/`: política de descargas opcionales y firmware elegido por el usuario.
- `generadas/`: contrato para indexar/revisar scripts del usuario, sin ejecutar código generado.
- `gsm_modules/`: herramientas GSM existentes, preservadas.
- `tests/`: pruebas simuladas, sin hardware real.

## Uso

```sh
python3 -m tools.catalogo.buscar_herramientas --validate
python3 -m tools.catalogo.buscar_herramientas --protocol fastboot
python3 -m tools.catalogo.buscar_herramientas --device iphones --brand Apple --json
python3 -m unittest discover -s tools/tests -v
```

El buscador y el catálogo no descargan, instalan ni ejecutan programas. La ausencia de una coincidencia exacta devuelve opciones informativas/manuales; no debe generar comandos por conjetura. Nix-on-Droid puede proporcionar una dependencia concreta si existe una build compatible con el host/ABI, sin descargar el paquete completo.

## Alcance y límites

Los contratos de fotos, Android en segundo plano, cámara UVC, lotes y notificaciones no añaden permisos, UI ni servicios al APK. Los helpers de lote crean planes, no ejecutan chequeos. iPhone/iPad, EDL, MediaTek y medios booteables son referencias o candidatos sujetos a host/modelo; no se afirma integración universal. Los medios booteables y firmware no se escriben ni se flashean automáticamente.

No se incluyen ROMs/firmware, acceso IP/WEP requerido, bypass de seguridad ni redistribución no autorizada. No registrar identificadores personales/credenciales. Valores de telemetría del Android anfitrión no son mediciones eléctricas del dispositivo reparado.

## Estado de pruebas

Las pruebas deben ejecutarse localmente con el comando anterior o en CI. No declarar que pasaron hasta tener una ejecución observada; deben cubrir JSON, referencias y unicidad del catálogo, compatibilidad del buscador, casos privados y datos simulados de lotes.
