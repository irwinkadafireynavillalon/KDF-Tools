# Módulo de herramientas

Esta carpeta contiene módulos de diagnóstico GSM existentes y un catálogo nuevo de capacidades. El catálogo no afirma que todas las funciones sean implementaciones completas: `status` distingue comandos detectables, referencias manuales e interfaces pendientes.

## Buscar capacidades localmente

Desde la raíz del repositorio:

```sh
python3 -m tools.catalogo.buscar_herramientas --protocol fastboot
python3 -m tools.catalogo.buscar_herramientas --device phones --brand Xiaomi --available
python3 -m tools.catalogo.buscar_herramientas --chipset MediaTek --json
```

La búsqueda lee `catalogo/herramientas.json` y revisa comandos/módulos presentes en PATH o Python. No descarga, instala ni ejecuta herramientas. Si falta un comando, muestra el paquete Nix sugerido; Nix-on-Droid u otro host puede resolverlo por separado. Esto evita descargar un conjunto completo, pero la herramienta individual debe existir en un build compatible con el host y sus permisos.

## Pruebas

```sh
python3 -m unittest discover -s tools/tests -v
```

Las pruebas usan datos simulados y directorios temporales; no requieren conectar dispositivos ni ejecutar comandos reales.

## Alcance actual y límites

- Catálogo por celulares, tabletas, wearables, computadoras, pantallas/TV, sistemas automotrices y otros USB.
- Detección local de dependencias y sugerencias de paquetes Nix, sin instalación automática.
- Formatos de informe y muestras USB; la validación estructural no es diagnóstico eléctrico.
- Registro local de casos opt-in, fuera del repositorio, y búsqueda por marca/modelo/síntoma.
- Los módulos GSM existentes se preservan. Algunos necesitan revisión antes de automatizarlos; no se invocan desde el buscador.
- Sin ROMs/firmware, descarga automática, flasheo, escritura de particiones o bypass de autenticación.
- ADB/Fastboot/EDL/MediaTek dependen del modelo, modo y permisos. Sin identidad confirmada se ofrece una coincidencia aproximada o ruta manual.
- El adaptador USB de medición es hardware externo del usuario. La corriente de entrada sola no localiza un componente; un resultado debe incluir evidencia, incertidumbre y pruebas que no se hicieron.

Las descargas futuras deben ser visibles, bajo demanda, desde fuentes revisadas, con versión/licencia/tamaño/hash cuando se publique. Los archivos de runtime y scripts generados se guardan fuera del checkout, en almacenamiento privado o una ubicación elegida por el usuario.
