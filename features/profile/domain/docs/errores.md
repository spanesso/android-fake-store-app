# Errores — `:features:profile:domain`

El módulo de dominio no define errores propios. Propaga los `DomainError` que recibe del repositorio sin modificarlos.

## Errores posibles en `ObtenerPerfil`

| `DomainError` | Condición | Qué hace el caso de uso |
|---------------|-----------|------------------------|
| `Network.NotFound` | El endpoint `/users/{id}` devuelve 404 | Propaga `Either.Left` sin modificar |
| `Network.NoConnection` | Sin conectividad al ejecutar la llamada | Propaga `Either.Left` |
| `Network.Timeout` | La respuesta tarda más del umbral configurado en OkHttp | Propaga `Either.Left` |
| `Network.Server(httpCode)` | Respuesta 5xx del servidor | Propaga `Either.Left` con código HTTP |
| `Network.Parsing` | El JSON recibido no coincide con `UsuarioDto` | Propaga `Either.Left` |
| `Network.Unauthorized` | Respuesta 401 (futuro, con auth) | Propaga `Either.Left` |
| `Network.Forbidden` | Respuesta 403 | Propaga `Either.Left` |
| `Unknown(cause)` | Cualquier excepción no mapeada | Propaga `Either.Left` |

## Precondición de negocio

| Condición | Excepción | Descripción |
|-----------|-----------|-------------|
| `userId ≤ 0` | `IllegalArgumentException` | Fallo rápido; nunca llega al repositorio |

## Cómo se manejan en capas superiores

Los errores del dominio son mapeados a `UiError` en `PerfilUiErrorMapper` (capa presentation). Ver [`features/profile/presentation/docs/errores.md`](../../presentation/docs/errores.md).
