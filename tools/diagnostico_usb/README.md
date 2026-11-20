# Triaje de falla eléctrica mediante adaptador USB de medición

Este módulo reserva el contrato de datos para el adaptador de medición USB que desarrolla el usuario. No convierte el OTG/cable normal en multímetro y no contiene diseño de puntas, cableado ni límites eléctricos. El adaptador debe tener su propio diseño validado, calibración, protección, desconexión segura y protocolo documentado antes de conectarse a un dispositivo.

## Qué aporta el esquemático

Para una revisión exacta de placa, un esquemático puede describir la ruta VBUS, conector/placa de carga, protecciones, IC de carga/gestión de energía, batería, interruptores de carga y ramas principales. También muestra señales que activan o desactivan etapas y condiciones de protección especificadas por el fabricante del circuito. Un esquema genérico o de otra revisión no basta para atribuir un resultado al mismo punto físico.

El análisis puede combinar esa topología con una serie temporal calibrada al conectar la fuente, estado de negociación/protección y estado conocido de batería/pantalla para producir una lista de zonas probables y pruebas siguientes. Ejemplo de resultado: «sospecha de ruta de entrada/placa de carga; confianza media; patrón de consumo anormal aparece antes de la secuencia de carga; batería no aislada y no descartada; confirmar al abrir con inspección/medición». Esto ayuda a decidir si conviene abrir primero o evitar una sesión de software sin evidencia.

La medida de corriente de entrada representa cargas y controladores combinados. Las protecciones limitan o cortan corriente; los cargadores pueden reintentar; el consumo cambia con la negociación PD, la batería, el estado del equipo y las ramas activas. La marca temporal ayuda a comparar firmas conocidas, pero el tiempo de respuesta no revela por sí solo la distancia ni el componente. Las zonas son hipótesis, nunca confirmación exacta.

## Datos y aprendizaje local

Usar `formato_medicion.json` como contrato. Adjuntar fuente/revisión del esquemático, modelo/board revision, calibración, versión de reglas y resultado final del técnico. Distinguir `not_measured`, `not_ruled_out` y `ruled_out_with_test`; no entrenar una sospecha como diagnóstico confirmado. El usuario decide qué casos incorpora al modelo local y debe poder revisar/exportar/eliminar sus datos.

Las imágenes térmicas, pruebas de continuidad y mediciones de banco son opciones complementarias; no son requisito universal y no deben inventarse si no están disponibles. Cuando no haya datos suficientes, devolver «inconcluso» y proponer la ruta manual del técnico.

## Fuentes de referencia iniciales

Estas fuentes explican conceptos generales, no sustituyen el esquemático y manual de servicio del modelo exacto:

- Android USB Host y permisos: https://developer.android.com/develop/connectivity/usb/host
- USB Implementers Forum, documentación USB Power Delivery: https://www.usb.org/document-library/usb-power-delivery
- Texas Instruments, *Understanding Power Path Management* (SLUA737): https://www.ti.com/lit/pdf/slua737
- Analog Devices, *PowerPath Management*: https://www.analog.com/en/resources/technical-articles/powerpath-management.html
- iFixit, guías y desmontajes por modelo: https://www.ifixit.com/Device

Para cada caso, registrar título, URL, fecha de consulta y revisión de placa. Los esquemáticos/boardviews pueden estar sujetos a derechos de autor o restricciones de acceso: utilizar solo material que el usuario tenga derecho a consultar; no descargar ni redistribuir material filtrado o de acceso no autorizado.
