# Errores — `:core:analytics`

`:core:analytics` no genera errores de dominio propios; actúa como receptor de `DomainError` de otros módulos para registrarlos en Crashlytics. Los fallos del SDK de Firebase son capturados internamente y registrados con Timber para no propagar excepciones a la UI.

## Comportamiento ante fallos de Firebase

| Situación | Comportamiento |
|-----------|---------------|
| `FirebaseCrashlytics` no inicializado | La excepción se captura en `FirebaseTelemetryImpl`; no se propaga |
| `FirebaseAnalytics` no disponible | `logEvent` falla silenciosamente (Firebase lo maneja internamente) |
| `FirebasePerformance` trace nulo | La traza no se inicia; `traza.stop()` es no-op |

## Tabla de recepción de DomainError

| `DomainError` recibido | Acción en `reportarNoFatal` | `Throwable` usado |
|------------------------|---------------------------|-------------------|
| `Network.NoConnection` | `crashlytics.recordException(cause ?: Throwable("NoConnection"))` | `cause` o sintético |
| `Network.Timeout` | ídem | `cause` o sintético |
| `Network.Server(httpCode)` | ídem + key `http_code` en contexto | `cause` o sintético |
| `Database.ReadFailed` | ídem | `cause` o sintético |
| `Unknown` | ídem | `cause` o sintético |
| `Security.*` | ídem — se registra por severidad alta | `cause` o sintético |

## Notas

- `DomainError.Validation` se puede reportar como no fatal pero raramente ocurre en producción.
- El contexto `Map<String, String>` permite anotar cada no-fatal con claves como `"modulo"`, `"usuario_anonimo_id"`, `"pantalla"` para facilitar el triage en el dashboard de Crashlytics.
