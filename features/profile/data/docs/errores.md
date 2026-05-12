# Errores — `:features:profile:data`

La capa de datos no define errores propios. Los errores de red son capturados por `safeApiCall` (de `:core:error`) y traducidos a `DomainError`.

## Mapeo de excepciones en `safeApiCall`

| Excepción capturada | `DomainError` resultante | Condición |
|---------------------|--------------------------|-----------|
| `HttpException(404)` | `Network.NotFound` | El usuario no existe en el servidor |
| `HttpException(401)` | `Network.Unauthorized` | Token no válido (preparado para auth) |
| `HttpException(403)` | `Network.Forbidden` | Sin permisos |
| `HttpException(5xx)` | `Network.Server(httpCode)` | Error interno del servidor |
| `SocketTimeoutException` | `Network.Timeout` | Timeout de lectura/escritura OkHttp |
| `IOException` / `UnknownHostException` | `Network.NoConnection` | Sin red o DNS no resuelve |
| `SerializationException` / `JsonDecodingException` | `Network.Parsing` | JSON no coincide con `UsuarioDto` |
| Cualquier otra excepción | `Unknown(cause)` | Error no categorizado |

## Configuración de timeouts (OkHttp)

Los timeouts se configuran en `:core:network` (módulo de infraestructura HTTP). En tests se usa:

```kotlin
OkHttpClient.Builder()
    .connectTimeout(1, TimeUnit.SECONDS)
    .readTimeout(1, TimeUnit.SECONDS)
    .writeTimeout(1, TimeUnit.SECONDS)
    .build()
```

## Referencia

Ver cómo estos errores se presentan en la UI: [`features/profile/presentation/docs/errores.md`](../../presentation/docs/errores.md).
