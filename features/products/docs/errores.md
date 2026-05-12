# Errores — `:features:products`

## Tabla de errores

| `DomainError` | Condición | `UiError.severity` | `R.string` |
|---|---|---|---|
| `Network.NoConnection` | Sin conexión de red al cargar productos | `Blocking` | `error_red_sin_conexion` |
| `Network.Timeout` | Timeout (>10s) al llamar a la API | `Blocking` | `error_red_tiempo_agotado` |
| `Network.Server(≥500)` | Error interno del servidor de la API | `Blocking` | `error_red_servidor` |
| `Network.Unauthorized(401)` | Token inválido o sesión expirada | `Blocking` | `error_red_no_autorizado` |
| `Network.Forbidden(403)` | Sin permisos para el recurso | `Blocking` | `error_red_prohibido` |
| `Network.NotFound(404)` | Endpoint de productos no encontrado | `Warning` | `error_red_no_encontrado` |
| `Network.Parsing` | Respuesta JSON inválida o inesperada | `Blocking` | `error_red_parsing` |
| `Database.ReadFailed` | Error de lectura en Room al cargar caché | `Warning` | `error_base_datos_lectura` |
| `Database.WriteFailed` | Error de escritura en Room al actualizar caché | `Warning` | `error_base_datos_escritura` |
| `Unknown(Throwable)` | Excepción no esperada (capturada por `CoroutineExceptionHandler`) | `Fatal` | `error_desconocido` |

## Flujo de manejo de errores

```
API call
  └─ safeApiCall { }
      ├─ Either.Right → guardar en Room → emitir datos frescos
      └─ Either.Left(DomainError) → (si no había caché) emitir error
                                     (si había caché) silenciar

ViewModel
  ├─ Either.Left → errorMapper.map(domainError) → UiError
  │                telemetry.reportarNoFatal(...)
  │                _uiState.update { Error(uiError) }
  │                _uiEffect.emit(MostrarSnackbar(uiError))
  └─ Exception inesperada → CoroutineExceptionHandler
                              DomainError.Unknown(t)
                              telemetry.reportarNoFatal(...)
                              _uiState.update { Error(errorMapper.map(unknown)) }
```

## Comportamiento en UI

| Estado | Componente | Acción disponible |
|---|---|---|
| `ProductosUiState.Error(uiError)` | `MangoErrorState(uiError, onRetry)` | Botón "Reintentar" (`UiEvent.Retry`) |
| `ProductosUiEffect.MostrarSnackbar(uiError)` | `MangoSnackbar` | Dismissible |
| `ProductosUiState.Empty` | `MangoEmptyState` | Sin acción (no es error) |

## Cadenas de recursos

Definidas en `features/products/presentation/src/main/res/values/strings_products.xml`:

```xml
<string name="productos_lista_vacia">No hay productos disponibles en este momento.</string>
<string name="productos_titulo_pantalla">Productos</string>
```

Los mensajes de error están en `:core:error/src/main/res/values/strings.xml`.
