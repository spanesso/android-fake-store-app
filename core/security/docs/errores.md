# Errores — `:core:security`

`:core:security` no genera `DomainError` directamente; expone resultados tipados (`BiometricResult`) que los ViewModels mapean a `DomainError.Security`. La traducción ocurre en la capa `presentation`, nunca dentro de este módulo.

## Mapeo esperado por ViewModels

| `BiometricResult` | `DomainError.Security` sugerido | `UiError.severity` | Acción UI |
|-------------------|---------------------------------|---------------------|-----------|
| `Exito` | — (no es error) | — | Navegar |
| `Cancelado` | — (acción del usuario) | — | No mostrar error |
| `BloqueadoTemporalmente` | `BiometricLockout` | `Blocking` | Mostrar banner con instrucciones |
| `NoDisponible` | `BiometricUnavailable` | `Warning` | Ofrecer alternativa (PIN) |
| `Error(mensaje)` | `Unknown(Throwable(mensaje))` | `Warning` | Reintentar o cancelar |

## Comportamiento de `IntegrityChecker`

| `estaComprometido()` | Acción recomendada | `DomainError.Security` |
|---------------------|--------------------|------------------------|
| `true` | Bloquear acceso, mostrar pantalla de aviso | `RootDetected` |
| `false` | Continuar normalmente | — |

## Notas

- `DomainError.Security.IntegrityFailed` se reserva para fallos de Play Integrity API (ETAPA 7).
- `DomainError.Security.SessionExpired` es emitido por `:core:datastore` cuando los tokens expiran, no por este módulo.
