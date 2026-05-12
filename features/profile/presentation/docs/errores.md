# Errores — `:features:profile:presentation`

## Mapa de `DomainError` → `UiError` (via `PerfilUiErrorMapper`)

| `DomainError` | `errorCode` | `severity` | `R.string` | `actions` |
|---------------|-------------|------------|------------|-----------|
| `Network.NotFound` | `NET-404` | `Info` | `error_perfil_no_encontrado` | `[Retry, Dismiss]` |
| `Network.NoConnection` | `NET-000` | `Warning` | `error_red_sin_conexion` | `[Retry]` |
| `Network.Timeout` | `NET-001` | `Warning` | `error_red_tiempo` | `[Retry]` |
| `Network.Server(5xx)` | `NET-500` | `Blocking` | `error_red_servidor` | `[Retry, Dismiss]` |
| `Network.Parsing` | `NET-002` | `Blocking` | `error_red_datos` | `[Dismiss]` |
| `Network.Unauthorized` | `NET-401` | `Blocking` | `error_red_no_autorizado` | `[Dismiss]` |
| `Network.Forbidden` | `NET-403` | `Blocking` | `error_red_prohibido` | `[Dismiss]` |
| `Unknown` | `UNK-000` | `Blocking` | `error_desconocido` | `[Retry]` |
| `Database.ReadFailed` | `DB-001` | `Warning` | `error_base_datos_lectura` | `[Retry]` |

> **Nota**: Todos los `R.string` excepto `error_perfil_no_encontrado` los gestiona `DomainErrorToUiErrorMapper` de `:core:error`. `PerfilUiErrorMapper` sólo sobreescribe `Network.NotFound` para mostrar el mensaje contextual del perfil.

## Cadena de recursos de la pantalla de perfil

| `R.string` | Valor | Dónde se define |
|------------|-------|-----------------|
| `error_perfil_no_encontrado` | "Perfil no encontrado. Comprueba tu conexión e inténtalo de nuevo." | `features/profile/presentation/src/main/res/values/strings.xml` |

## Comportamiento en UI

- `severity = Info` / `Warning` → `MangoErrorState` con color de aviso suave; el usuario puede continuar.
- `severity = Blocking` → `MangoErrorState` ocupa toda la pantalla; el usuario debe resolver el error o salir.
- `actions` con `Retry` → botón "Reintentar" visible que dispara `PerfilUiEvent.Retry`.
- `actions` con `Dismiss` → botón "Cerrar" o "Ignorar" visible.

## Telemetría

Todos los `DomainError` recibidos en `PerfilViewModel.cargarPerfil()` se reportan como no fatales a `Telemetry.reportarNoFatal(error, contexto = {"vm": "PerfilViewModel", "accion": "cargarPerfil"})`.
