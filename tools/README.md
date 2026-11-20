# Alcance de `tools/`

Esta carpeta es el único ámbito de cambios para el trabajo de catálogo de herramientas. La aplicación Android puede consumir `catalogo/herramientas.json`; la UI y las integraciones fuera de `tools/` quedan fuera de este alcance salvo autorización futura explícita.

## Organización

- `catalogo/herramientas.json`: herramientas universales y matrices por tipo de dispositivo y fabricante.
- `descargas/`: política y flujo para descargas visibles y a petición.
- `generadas/`: contrato para scripts aportados por el usuario o generados por el orquestador.
- `diagnostico_usb/`: límites de diagnóstico eléctrico y requisitos de hardware de medición.
- `gsm_modules/`: módulos GSM ya existentes; se preservan.

## Flujo previsto para el botón «Buscar herramienta»

La aplicación obtiene un perfil de diagnóstico USB/ADB/Fastboot autorizado y busca primero por protocolo, identificadores USB, fabricante, modelo y chipset. Devuelve coincidencias con nivel de confianza, compatibilidad y fuente. Si el modelo no puede determinarse, debe pedir confirmación o permitir elegirlo manualmente, nunca adivinar. La app puede mostrar enlaces oficiales y comandos informativos; este catálogo no ejecuta scripts ni implementa UI.

## Reglas acordadas

- Descargas visibles, iniciadas por el usuario, con cancelación y estimación de tamaño; puede ofrecerse selección durante la instalación. La cuota inicial orientativa para herramientas opcionales es 100 MiB, separada del espacio que el usuario decida usar para un paquete de firmware.
- No guardar herramientas ni paquetes descargados en Git. Usar almacenamiento privado de la aplicación o una ubicación elegida por el usuario.
- Para firmware, enlazar solo la fuente oficial del fabricante cuando esté disponible, revisar modelo/variante/región, versión, requisitos y hashes/firma publicados. Descargarlo solo a petición, informar del tamaño y no flashearlo automáticamente. Un archivo público no necesariamente autoriza su redistribución.
- Para proyectos GitHub, mostrar repositorio, licencia, versión/release, propietario y permisos solicitados. No tratar scripts sueltos, forks o código generado por IA como confiables por estar en GitHub.
- Si el dispositivo o herramienta no son compatibles, indicarlo claramente y ofrecer diagnóstico de solo lectura, enlace oficial o instrucciones para trabajo manual. No intentar comandos alternativos a ciegas.
- Operaciones que escriben, borran, desbloquean o flashean requieren identificación del modelo, revisión de impacto y aprobación explícita. No se implementan bypass de FRP/iCloud, MDM/financiamiento, protecciones de acceso ni cambios de IMEI.
- No solicitar ni almacenar información privada innecesaria. Los logs deben poder revisarse y borrarse por el usuario.

## Limitaciones importantes

ADB/Fastboot/EDL/MediaTek no son protocolos universales: el soporte depende del modelo, chipset, controlador, permisos, modo de arranque y en algunos casos de credenciales oficiales del fabricante. No se deben incluir métodos para eludir controles de autenticación del fabricante.

El USB del teléfono no es un multímetro. La aplicación puede enumerar el dispositivo y, cuando el sistema lo permite, leer voltaje/corriente reportados por el controlador de carga; esas lecturas no identifican un componente en corto. Para mediciones de VBUS, CC, D+/D−, resistencia o continuidad se necesita un medidor/adaptador electrónico compatible y calibrado, con su propio protocolo documentado. Sin ese equipo, reportar «sin medición eléctrica» y no inventar valores.

El catálogo es una especificación: no instala ni ejecuta herramientas por sí solo.
