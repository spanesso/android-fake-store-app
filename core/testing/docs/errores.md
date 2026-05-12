# Errores — `:core:testing`

`:core:testing` no genera errores de dominio propios. Proporciona builders para construir instancias de `DomainError` y `UiError` en tests de otros módulos.

## Builders de DomainError disponibles

| Builder | `DomainError` resultante |
|---------|--------------------------|
| `domainErrorNoConnection()` | `Network.NoConnection` |
| `domainErrorTimeout()` | `Network.Timeout` |
| `domainErrorServer(codigo)` | `Network.Server(codigo)` |
| `domainErrorUnauthorized()` | `Network.Unauthorized` |
| `domainErrorForbidden()` | `Network.Forbidden` |
| `domainErrorNotFound()` | `Network.NotFound` |
| `domainErrorParsing()` | `Network.Parsing` |
| `domainErrorDbLectura()` | `Database.ReadFailed` |
| `domainErrorDbEscritura()` | `Database.WriteFailed` |
| `domainErrorDbNoEncontrado()` | `Database.NotFound` |
| `domainErrorValidacion(campos)` | `Validation(campos)` |
| `domainErrorDesconocido()` | `Unknown` |

## Builders de UiError disponibles

| Builder | Severidad | Acciones por defecto |
|---------|-----------|---------------------|
| `uiErrorInfo(...)` | `Info` | `[Dismiss]` |
| `uiErrorWarning(...)` | `Warning` | `[Retry]` |
| `uiErrorBlocking(...)` | `Blocking` | `[]` |
| `uiErrorFatal(...)` | `Fatal` | `[]` |
