# Alcance y arquitectura de `tools/`

Esta carpeta es el único ámbito de cambios para el catálogo y las herramientas. La aplicación Android puede consumir `catalogo/herramientas.json`; UI e integraciones fuera de `tools/` quedan fuera de alcance salvo autorización futura explícita. Los módulos existentes de `tools/gsm_modules/` se preservan.

## Organización

- `catalogo/`: matriz por tipo de dispositivo, marca, protocolo y herramientas universales.
- `descargas/`: reglas de descarga visible, verificación y referencias oficiales.
- `generadas/`: contrato para scripts creados por el usuario/orquestador y su reutilización.
- `diagnostico_usb/`: entrada de mediciones del adaptador USB creado por el usuario y clasificación aproximada de fallas.
- `gsm_modules/`: módulos GSM existentes; no reemplazar ni modificar sin solicitud explícita.

## Botón «Buscar herramienta»

La aplicación debe poder consumir el diagnóstico disponible —descriptores USB, permiso concedido, modo/protocolo, ADB/Fastboot, identificadores expuestos y modelo/variante/placa si se pueden confirmar— y buscar scripts/herramientas compatibles. Debe mostrar por qué coincide, la fuente, versión, requisitos, nivel de confianza y alternativa manual. No debe inferir modelo exacto de VID/PID o chipset por sí solos ni ejecutar una coincidencia aproximada.

## Triaje eléctrico orientativo

Un esquemático sí documenta la topología y el recorrido de alimentación, la gestión de carga y las protecciones **para esa revisión de placa**. Con una medición temporal de voltaje/corriente de entrada, estado del cargador/USB-PD, datos del dispositivo y esquemático legalmente disponible, se puede intentar clasificar patrones y sugerir una zona inicial de inspección: entrada/centro de carga, batería/ruta de batería, protección/PMIC o una rama de carga. Esto puede ayudar a decidir si conviene abrir el equipo antes de preparar software.

La salida será una **hipótesis aproximada**, no localización garantizada de componente: por ejemplo «sospecha de sobreconsumo en la ruta de entrada; revisar primero placa de carga y flex; confianza media; la batería no quedó aislada en esta prueba». La app debe diferenciar “no medido” de “descartado”; no declarar una batería, pantalla o placa descartada sin una prueba que permita aislarla. El momento de aparición del consumo es una característica de la señal, no un reloj que localice el corto por propagación a la velocidad de la luz. Protección de entrada, cargador, batería conectada, convertidores, capacitores, negociación PD y estado de arranque afectan la firma.

El adaptador USB de medición será creado por el usuario. `tools/diagnostico_usb/formato_medicion.json` define datos de entrada y salida, no un diseño electrónico ni valores universales. El equipo debe tener límites eléctricos, calibración, procedimiento de desconexión y validación propios. No se debe inyectar alimentación ni sondear pines de datos del dispositivo con hardware no validado. Sin adaptador compatible, la app informa que no hay medición eléctrica y no inventa valores.

## Esquemáticos y conocimiento

El motor puede usar esquemáticos, boardviews, manuales de servicio y hojas de datos **solo cuando se disponga legítimamente de ellos**. Muchos esquemáticos de fabricante no son públicos o tienen restricciones; no se deben copiar, subir ni redistribuir sin permiso. Cuando no haya esquema exacto, se usarán topologías generales y fuentes citadas, indicando la limitación y la revisión de placa faltante. Los foros/videos son pistas y experiencia comunitaria, no evidencia suficiente por sí sola para afirmar una falla.

## Descargas y acciones

- Descargas visibles, bajo demanda y cancelables; opción de selección durante la instalación. El presupuesto opcional inicial de herramientas es 100 MiB, configurable. Firmware elegido por el usuario tendrá tamaño y confirmación aparte.
- No guardar binarios, ROMs, firmware o datos de clientes en Git. Descargas runtime en almacenamiento privado de la app o ubicación elegida por el usuario.
- Mostrar fuente oficial, versión, licencia, compatibilidad, tamaño y hash/firma publicados cuando existan. Un archivo público no necesariamente autoriza redistribuirlo.
- No ejecutar de manera automática código de GitHub, foros o generado por IA.
- Escritura, borrado, desbloqueo y flasheo requieren identidad compatible, revisión de efectos y aprobación explícita. Si hay incompatibilidad, explicar la causa y dejar la vía manual.
- No solicitar credenciales o información privada innecesaria; los registros deben ser revisables y borrables.

El catálogo y los esquemas JSON son contratos de datos; no implementan el descargador, el adaptador de medición ni la UI.
