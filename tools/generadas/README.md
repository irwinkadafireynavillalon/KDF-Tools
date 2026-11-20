# Scripts generados por el orquestador

Esta carpeta del repositorio documenta la ubicación, pero **no debe contener scripts generados durante el uso**. El orquestador debe escribirlos en el directorio privado de la aplicación, por ejemplo `filesDir/KDF-LuX/generated/`, para evitar que datos del usuario o contenido ejecutable se añadan al repositorio.

## Controles mínimos

- Generar solo desde plantillas versionadas y revisadas; no ejecutar directamente texto producido por IA.
- Rechazar comandos arbitrarios, rutas fuera del directorio privado, enlaces simbólicos inesperados y escrituras sobre firmware/particiones.
- Mostrar diff, objetivo, dispositivo detectado y efectos potenciales antes de guardar o ejecutar.
- Exigir aprobación expresa para acciones que modifican el dispositivo; deshabilitar borrados, desbloqueo y flasheo automático.
- Validar sintaxis, permisos mínimos y lista permitida de operaciones; probar en simulador o dispositivo de laboratorio.
- Registrar hash, origen de plantilla, versión del orquestador, aprobaciones y resultado. No registrar tokens ni datos personales innecesarios.
- Permitir revocar/eliminar un script y conservar una copia de auditoría segura.

Este scaffold no agrega ejecución ni generación automática de código.
