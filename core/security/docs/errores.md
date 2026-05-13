# Errores — `:core:security`

`:core:security` no genera `DomainError` directamente. Los resultados de integridad se mapean a `DomainError.Security` en la capa `presentation`, nunca dentro de este módulo.

## Comportamiento de `IntegrityChecker`

| `estaComprometido()` | Acción recomendada | `DomainError.Security` |
|---------------------|--------------------|------------------------|
| `true` | Bloquear acceso, mostrar pantalla de aviso | `RootDetected` |
| `false` | Continuar normalmente | — |

## Notas

- `DomainError.Security.IntegrityFailed` se reserva para fallos de Play Integrity API (ETAPA 7).
- `DomainError.Security.SessionExpired` es emitido por `:core:datastore` cuando los tokens expiran, no por este módulo.
