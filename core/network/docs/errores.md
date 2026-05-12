# Manejo de errores — `:core:network`

## Mapeo de errores de red

| Excepción capturada | `DomainError` resultante | Condición típica |
|--------------------|--------------------------|-----------------|
| `retrofit2.HttpException` 401 | `DomainError.Network.Unauthorized` | Token expirado o ausente |
| `retrofit2.HttpException` 403 | `DomainError.Network.Forbidden` | Permisos insuficientes |
| `retrofit2.HttpException` 404 | `DomainError.Network.NotFound` | Recurso inexistente |
| `retrofit2.HttpException` 5xx | `DomainError.Network.Server(httpCode)` | Error del servidor |
| `retrofit2.HttpException` (otro) | `DomainError.Network.NoConnection` | Código HTTP inesperado |
| `java.net.SocketTimeoutException` | `DomainError.Network.Timeout` | Read/connect timeout |
| `java.net.UnknownHostException` | `DomainError.Network.NoConnection` | Sin DNS / sin red |
| `java.io.IOException` | `DomainError.Network.NoConnection` | Error de I/O genérico |
| `kotlinx.serialization.SerializationException` | `DomainError.Network.Parsing` | JSON malformado |
| `kotlinx.coroutines.CancellationException` | _(se re-lanza, no se captura)_ | Cancelación de corrutina |

## Tabla UiError

| `DomainError` | `UiError.severity` | `R.string` |
|---------------|-------------------|------------|
| `Network.NoConnection` | `Warning` | `error_red_sin_conexion` |
| `Network.Timeout` | `Warning` | `error_red_tiempo_agotado` |
| `Network.Unauthorized` | `Blocking` | `error_red_no_autorizado` |
| `Network.Forbidden` | `Blocking` | `error_red_sin_permiso` |
| `Network.NotFound` | `Blocking` | `error_red_no_encontrado` |
| `Network.Server` | `Warning` | `error_red_servidor` |
| `Network.Parsing` | `Blocking` | `error_red_formato` |

> Los `R.string` están definidos en `app/src/main/res/values/strings.xml` y `values-en/strings.xml`.
> La traducción de `DomainError → UiError` ocurre en `DomainErrorToUiErrorMapper` de `:core:error`.

## Comportamiento de `RetryInterceptor`

Los códigos 408, 429 y 5xx activan el mecanismo de reintento **antes** de que `safeRetrofitCall` mapee la excepción. Si el reintento agota los intentos máximos, la última respuesta de error se convierte en `HttpException` y fluye hacia el mapeo habitual.

## Notas de seguridad

- Los errores de red **nunca** incluyen el body de la respuesta en `DomainError` (solo el código HTTP).
- Los logs de `HttpLoggingInterceptor` solo están activos en `BuildConfig.DEBUG = true`.
- `NetworkErrorReporter.reportNetworkError` no debe incluir datos de usuario en el `context` map.
