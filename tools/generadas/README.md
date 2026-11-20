# Scripts reutilizables

Los scripts son propiedad y decisión del técnico. El módulo puede indexarlos por categoría, marca, modelo, revisión, síntoma, protocolo, requisitos y resultado de casos anteriores. Mantener el original y editar una copia/version nueva. Registrar `confirmed`, `ruled_out`, `inconclusive` o `pending` para no convertir una hipótesis en un hecho.

El índice es descriptivo: esta primera versión no ejecuta scripts. Antes de integrar ejecución en la aplicación, deberá revisar ubicación permitida, compatibilidad, contenido, argumentos, efectos y permisos; las acciones de escritura o borrado requieren una confirmación específica. No guardar datos de casos ni scripts temporales del usuario en Git.

Los scripts disponibles fuera de `tools/` no se modifican ni se copian aquí automáticamente. El orquestador podrá recibir índices locales cuando su interfaz sea definida en un cambio separado autorizado.
