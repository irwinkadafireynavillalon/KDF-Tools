# Descargas visibles y a petición

Este directorio contiene reglas y metadatos, no binarios. La app puede ofrecer una selección durante la instalación y permitir descargas posteriores bajo demanda. El presupuesto inicial sugerido para herramientas opcionales es 100 MiB. El firmware, si el usuario decide descargarlo desde una fuente oficial para un modelo exacto, muestra tamaño y confirmación aparte.

## Herramientas

Antes de ofrecer una herramienta: mostrar fuente, propietario, versión/release, licencia, compatibilidad de host/ABI/modelo, tamaño y requisitos. Descargar solo por HTTPS desde la fuente revisada; validar tamaño y SHA-256/firma cuando el proveedor publique un valor de confianza. Si no existe hash o firma verificable, marcarlo claramente como no verificado. Mantener el archivo temporal en almacenamiento privado y no ejecutarlo automáticamente. Permitir cancelar, actualizar y borrar herramientas sin tocar otros datos del usuario.

Android SDK Platform-Tools se distribuye para sistemas host; no asumir que sus binarios de escritorio se ejecutan dentro de Android. Verificar por separado Android/Termux, ABI, permisos y licencia.

## Firmware/ROM a petición

No se guarda firmware, ROM, imagen de partición ni programador en Git. Solo mostrar una descarga del fabricante cuando la fuente y el modelo/variante/región exactos hayan sido revisados. Explicar versión, tamaño, hash/firma publicada y riesgos; la descarga la inicia el usuario y no se ejecuta flasheo automático. Disponibilidad pública no equivale automáticamente a permiso de redistribución o a compatibilidad.

## GitHub y foros

El enlace opcional de GitHub debe usar OAuth oficial, permisos mínimos y revocables; nunca guardar tokens en el catálogo, scripts o logs. Para proyectos, mostrar owner, URL, licencia, release/tag y compatibilidad. Foros, vídeos y repositorios comunitarios pueden aportar pistas para el diagnóstico y la IA, citando fuente y fecha; no son por sí solos prueba de compatibilidad ni autorización para redistribuir o ejecutar scripts.

La revisión de licencia sirve para saber si se puede incluir, empaquetar o redistribuir una herramienta. Si no se conoce, enlazar la fuente para evaluación manual, sin copiar el software.
