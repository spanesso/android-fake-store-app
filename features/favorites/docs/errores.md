# Manejo de errores: `:features:favorites`

## Tabla de errores

| `DomainError` | Condición | `UiError.severity` | `R.string` | Comportamiento en UI |
|---|---|---|---|---|
| `Database.ReadFailed` | Fallo al leer la tabla `favoritos` al iniciar observación | `Blocking` | `error_bd_lectura_fallida` | `FavoritosUiState.Error` → `MangoErrorState` con botón "Reintentar" |
| `Database.WriteFailed` | Fallo al insertar o eliminar un favorito | `Warning` | `error_bd_escritura_fallida` | `FavoritosUiEffect.MostrarSnackbar` (no bloquea la pantalla) |
| `Database.IntegrityViolation` | Violación de clave primaria (rara, cubierta por ON CONFLICT REPLACE) | `Warning` | `error_bd_escritura_fallida` | `FavoritosUiEffect.MostrarSnackbar` |
| `Unknown` | Excepción inesperada capturada por `CoroutineExceptionHandler` | `Blocking` | `error_desconocido` | `FavoritosUiState.Error` vía `errorHandler` del ViewModel |

## Flujo de mapeo

```
FavoritosDao (Room)
    → SQLiteException / SQLiteConstraintException
        → safeDbCall { } en FavoritosRepositoryImpl
            → DomainError.Database.ReadFailed / WriteFailed / IntegrityViolation
                → FavoritosViewModel
                    → DomainErrorToUiErrorMapper.map(domainError)
                        → UiError(messageRes, severity)
                            → FavoritosUiState.Error o FavoritosUiEffect.MostrarSnackbar
```

## Strings de error en UI

| `R.string` | Valor ES | Severidad |
|---|---|---|
| `error_bd_lectura_fallida` | "No se pudieron cargar los favoritos. Inténtalo de nuevo." | `Blocking` |
| `error_bd_escritura_fallida` | "No se pudo actualizar el favorito. Inténtalo de nuevo." | `Warning` |
| `error_desconocido` | "Ha ocurrido un error inesperado." | `Blocking` |

> **Nota**: Los strings de error genéricos (`error_bd_*`, `error_desconocido`) viven en `:core:error/src/main/res/values/strings_errors.xml`.

## Reglas aplicadas (§7 del prompt maestro)

- **ERR-001**: `FavoritosRepositoryImpl` usa `safeDbCall` en todas las operaciones de escritura y lectura puntual.
- **ERR-002**: `ObservarFavoritos`, `ToggleFavorito` y `ObservarConteoFavoritos` devuelven `Either<DomainError, T>` o `Flow<T>` directamente.
- **ERR-010**: `FavoritosViewModel.cargarFavoritos()` y `toggleFavorito()` usan `viewModelScope.launch(errorHandler)` con `CoroutineExceptionHandler` definido en el constructor del ViewModel.
- La UI (`FavoritosScreen`) nunca importa `DomainError` y nunca lee `.message` de throwables.
