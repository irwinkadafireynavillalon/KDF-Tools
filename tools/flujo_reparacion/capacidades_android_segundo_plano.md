# Capacidades Android en segundo plano, permisos y voz

Este documento especifica capacidades futuras del módulo Android. No otorga permisos ni implementa servicios. Android conserva el control del usuario sobre permisos especiales y tareas de fondo.

## Interacción con otras aplicaciones

- La app puede seguir mostrando su chat y ejecutar tareas propias mientras el usuario usa otra app, dentro de los límites de Android y de memoria/batería.
- Para una tarea larga iniciada por el usuario, usar WorkManager o un foreground service según el tipo de trabajo; informar progreso y permitir detenerla. No mantener procesos ocultos ni impedir que el usuario cierre o pause la app.
- AccessibilityService no es un permiso genérico para controlar el teléfono. No solicitarlo para “funcionar sobre todas las apps”. Solo proponerlo para una función de accesibilidad concreta, explicar qué eventos puede observar y qué acciones hará, y dejar activación/desactivación en Ajustes del usuario. No capturar campos sensibles ni automatizar confirmaciones.
- Device Admin/Device Owner solo si una función empresarial genuina lo requiere. No pedir privilegios de propietario del dispositivo para una app de taller normal ni usarlos para bloquear el equipo.

## Permisos y ajustes especiales

- Cámara, Bluetooth y dispositivos USB: pedir el permiso contextual justo cuando el usuario inicia esa función; el acceso USB se concede por dispositivo.
- Notificaciones: solicitar el permiso de notificaciones donde la versión de Android lo requiera. Mostrar avisos locales de tarea terminada, error, reconexión o aprobación pendiente; permitir desactivarlos.
- Micrófono: solo al tocar “Conversar por voz” o iniciar explícitamente una sesión. Si se usa mientras la app no está visible, Android puede exigir un foreground service del tipo microphone iniciado desde una acción visible, permiso correspondiente y notificación persistente. Mostrar estado claro, botón para detener y no grabar en secreto.
- Instalar aplicaciones: la app puede abrir el flujo oficial PackageInstaller/instalador del sistema y pedir que el usuario revise y confirme. El permiso de “instalar apps desconocidas” es por aplicación fuente y debe habilitarse manualmente en Ajustes cuando corresponda. Nunca instalar silenciosamente ni eludir Play Protect, firmas o restricciones.
- Opciones de desarrollador/depuración USB: no se activan desde la app. Dar instrucciones para que el dueño las habilite en Ajustes y aceptar la autorización ADB en el dispositivo. No automatizar ni sortear el diálogo de confianza.
- Acceso a todos los archivos, superposición, uso de accesibilidad y administración del dispositivo no se piden como paquete; cada permiso requiere propósito independiente, explicación y alternativa sin ese permiso.

## Chat de voz y continuidad

- El usuario puede iniciar una sesión de voz con un control visible; una sesión continua en background mantiene foreground notification y acción “Detener”.
- Si el sistema mata el proceso, no prometer continuidad; guardar de manera local solo el estado necesario para reanudar al abrir la app.
- No afirmar “aprendizaje en tiempo real” del modelo base. Aprendizaje local significa guardar casos/reglas seleccionados por el usuario, con fuente, fecha, resultado confirmado/inconcluso y opción de editar/borrar/exportar. No reentrenar ni modificar el modelo automáticamente sin control.
- Conversación por voz no sube audio por defecto. Si se usa un servicio remoto, avisar antes, explicar qué se envía, pedir consentimiento y ofrecer modo texto/local cuando sea posible.

## Actualizaciones

- No usar autoactualización oculta ni cambiar scripts/configuración con privilegios propios. La app puede comprobar versiones bajo demanda, mostrar notas/origen/hash y abrir Google Play o el flujo de instalación aprobado. El usuario confirma la actualización.
- La descarga de herramientas opcionales y la actualización de la app son flujos separados, con versión, compatibilidad, licencia, tamaño, hash y confirmación.

## Reglas de seguridad y UX

1. Pantalla de capacidades con estados: no solicitado, concedido, denegado, requiere Ajustes o no disponible.
2. Solicitud contextual y mínima; botón “Continuar sin permiso” cuando haya alternativa.
3. Indicador persistente para micrófono/cámara y opción inmediata de detener.
4. Cada tarea de fondo tiene identificador, progreso, resultado y acción de cancelar.
5. No interacción oculta con apps ajenas, lectura indiscriminada de pantalla, instalación silenciosa ni concesión automática de permisos.
6. Pruebas por versión Android, fabricante y escenario de ahorro de batería; degradar con mensajes claros cuando el sistema limite la función.

## Estado de implementación

Especificación para diseño. No modifica AndroidManifest, solicita permisos ni añade todavía `Service`, `AccessibilityService`, instalador, notificaciones o interfaz de voz al APK.
