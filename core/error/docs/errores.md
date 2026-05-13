# Catálogo de errores — `:core:error`

Tabla maestra de todos los `DomainError` del sistema y su traducción a `UiError`.

| `DomainError` | Condición de origen | `UiError.severity` | `UiError.errorCode` | `R.string` |
|---------------|--------------------|--------------------|---------------------|-----------|
| `Network.NoConnection` | `IOException`, `UnknownHostException`, sin red | `Blocking` | `NET-000` | `error_red_sin_conexion` |
| `Network.Timeout` | `SocketTimeoutException`, `TimeoutException` | `Blocking` | `NET-001` | `error_red_tiempo_agotado` |
| `Network.Server` | HTTP 5xx | `Blocking` | `NET-500` | `error_red_servidor` |
| `Network.Unauthorized` | HTTP 401 | `Fatal` | `NET-401` | `error_red_no_autorizado` |
| `Network.Forbidden` | HTTP 403 | `Blocking` | `NET-403` | `error_red_sin_permiso` |
| `Network.NotFound` | HTTP 404 | `Info` | `NET-404` | `error_red_no_encontrado` |
| `Network.Parsing` | `SerializationException` | `Blocking` | `NET-002` | `error_red_formato` |
| `Database.ReadFailed` | Excepción genérica de BD | `Blocking` | `DB-001` | `error_bd_lectura` |
| `Database.WriteFailed` | `SQLiteException` | `Blocking` | `DB-002` | `error_bd_escritura` |
| `Database.NotFound` | Elemento no hallado en BD | `Info` | `DB-003` | `error_bd_no_encontrado` |
| `Database.IntegrityViolation` | `SQLiteConstraintException` | `Blocking` | `DB-004` | `error_bd_integridad` |
| `Security.RootDetected` | Dispositivo comprometido | `Fatal` | `SEC-001` | `error_seg_root_detectado` |
| `Security.IntegrityFailed` | Firma de la app inválida | `Fatal` | `SEC-004` | `error_seg_integridad` |
| `Security.SessionExpired` | Token expirado / revocado | `Fatal` | `SEC-005` | `error_seg_sesion_expirada` |
| `Validation` | Campo inválido o faltante | `Warning` | `VAL-001` | `error_validacion_formulario` |
| `Unknown` | Excepción no clasificada | `Blocking` | `UNK-000` | `error_desconocido` |

## Severidades y su comportamiento en UI

| Severidad | Comportamiento esperado |
|-----------|------------------------|
| `Info` | Banner informativo; el usuario puede descartar |
| `Warning` | Snackbar de advertencia; permite continuar |
| `Blocking` | Pantalla de error con botón de reintentar; bloquea la interacción |
| `Fatal` | Diálogo no descartable; redirige a login o cierra la app |

## Acciones disponibles

| `UiErrorAction` | Descripción |
|-----------------|-------------|
| `Retry` | Reintenta la última operación |
| `Dismiss` | Descarta el error |
| `Login` | Navega a la pantalla de inicio de sesión |
| `OpenSettings` | Abre los ajustes del sistema |

## Añadir un nuevo error

1. Añadir subclase en `DomainError.kt` bajo la categoría apropiada
2. Añadir rama en `DomainErrorToUiErrorMapper.kt` (el `when` es exhaustivo — compilará si falta)
3. Añadir `R.string.error_<dominio>_<caso>` en `res/values/strings.xml` y `res/values-en/strings.xml`
4. Si viene de excepción, añadir rama en `NetworkErrorMapper` o `DatabaseErrorMapper`
5. Añadir test en `DomainErrorToUiErrorMapperTest.kt` para la nueva rama
