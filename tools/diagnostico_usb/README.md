# Diagnóstico USB y función de multímetro

Una aplicación Android puede enumerar dispositivos USB Host cuando el hardware, Android, los permisos y el accesorio lo permiten. Puede mostrar VID/PID, interfaces y permiso concedido. En Android, el controlador de carga puede exponer lecturas agregadas de batería/corriente/temperatura; su disponibilidad, resolución y calibración dependen del fabricante.

Eso **no convierte el cable USB en un multímetro**. La app no puede medir directamente resistencia, continuidad ni tensión en cada pin del teléfono solo por conectar un cable. Para esas funciones hace falta un medidor USB/placa de prueba compatible que exponga mediciones mediante un protocolo documentado, o equipo de banco (multímetro/fuente limitada). El adaptador necesita aislamiento y límites eléctricos seguros para evitar dañar el dispositivo.

## Estados que debe comunicar la app

- `USB_ENUMERATED`: dispositivo/interfaz enumerados; identidad exacta puede seguir desconocida.
- `POWER_TELEMETRY_AVAILABLE`: lecturas informadas por el sistema; incluir origen, timestamp y advertir que son indicios.
- `EXTERNAL_METER_REQUIRED`: no existe medidor compatible; no inventar valores de VBUS, D+/D−, CC, resistencia o continuidad.
- `SHORT_NOT_DIAGNOSED`: no declarar «sin corto» por falta de una alerta de software. Para localizar un corto, indicar que se requiere prueba eléctrica adecuada y un técnico.

No concluir que un componente, línea o chip está en corto basándose únicamente en un USB VID/PID, porcentaje de batería, lectura de corriente del sistema o calentamiento estimado.
