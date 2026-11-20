# Administrar aplicaciones Android por ADB

Este módulo propone una función para listar, inspeccionar, inhabilitar de forma reversible y desinstalar aplicaciones en un dispositivo conectado con autorización ADB válida. **Es una especificación; no incorpora un ejecutor ADB ni realiza cambios por sí sola.**

## Flujo para la app

1. Detectar el dispositivo conectado y mostrar el identificador de destino de forma clara. Si hay más de uno, el usuario elige uno.
2. Verificar que ADB responde y que el dispositivo ya autorizó esa sesión. Si está en bootloop/brick y ADB no está disponible, informar el límite y ofrecer solo los modos de diagnóstico que el equipo exponga.
3. Listar paquetes y distinguir, cuando los datos disponibles lo permitan, aplicaciones del usuario, preinstaladas y estado conocido/desconocido. No concluir que una app “se instala sola” solo por su nombre.
4. Permitir seleccionar un único paquete y mostrar nombre visible, identificador del paquete, origen/estado detectado, acción y efectos posibles.
5. Preferir inhabilitar de manera reversible para software preinstalado no esencial. La desinstalación normal es para una app seleccionada y confirmada. No borrar el APK de la partición de sistema.
6. Mostrar una vista previa sin cambios primero; luego pedir confirmación individual. No aplicar lotes ni elegir paquetes automáticamente.
7. Después, volver a consultar el estado y ofrecer restaurar la app si fue inhabilitada y el sistema lo permite.
8. Registrar resultado técnico (correcto, rechazado, no compatible o error), sin guardar datos privados innecesarios.

## Límites

- No usar root, exploits ni otros métodos para saltarse restricciones de Android, políticas de propietario del dispositivo o controles del fabricante.
- No ayudar a retirar o neutralizar FRP, Activation Lock, MDM/administración empresarial, controles de financiación, antirrobo o aplicaciones de control de acceso.
- No inhabilitar servicios críticos, paquetes desconocidos, componentes de llamadas/arranque/actualización/accesibilidad o políticas de dispositivo. Si no puede clasificarse con confianza, dejarlo en solo lectura.
- La autorización ADB permite operaciones de desarrollo/diagnóstico, pero no garantiza permiso para cualquier cambio; Android, OEM y propietario pueden restringirlas.
- Si una aplicación reaparece, investigar la fuente legítima (administración del dispositivo, instalador aprobado o actualización). No repetir a la fuerza una eliminación bloqueada.

Mantener la función separada por completo de la interfaz Android: este módulo define comportamiento esperado y datos del catálogo dentro de `tools/`.
