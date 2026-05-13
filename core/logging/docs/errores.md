# Errores — `:core:logging`

`:core:logging` no genera `DomainError` ni `UiError`. Es una utilidad de infraestructura sin lógica de negocio.

## Comportamiento ante fallos internos

| Situación | Comportamiento |
|-----------|---------------|
| Timber no inicializado | `TimberLogger.init` planta `DebugTree` automáticamente; nunca lanza |
| Mensaje nulo o vacío | Timber acepta cadenas vacías; `NoOpLogger` las ignora |
| `causa: Throwable?` es null | `TimberLogger` llama al overload de Timber sin `Throwable` |
| `BuildConfig.DEBUG` no disponible | Imposible — la clase se genera en tiempo de compilación por AGP |

## Niveles de log y su uso semántico

| Método | Nivel Timber | Cuándo usarlo |
|--------|-------------|---------------|
| `info` | `DEBUG` (d) | Flujo normal: éxito de operación, estado actual del sistema |
| `warn` | `WARN` (w) | Situación atípica recuperada: retry de red, root detectado |
| `error` | `ERROR` (e) | Fallo capturado: excepción atrapada en barrera, estado inesperado |

**Nota**: No existe nivel `fatal` en `Logger`. Los errores fatales se reportan a través de `Telemetry.reportarNoFatal()` en `:core:analytics` → Crashlytics.
