# Módulo `:core:error`

**Propósito**: Define la jerarquía tipada de errores del dominio (`DomainError`), su representación UI (`UiError`), los mappers desde excepciones Java/Android, y las funciones de barrera `safeApiCall`/`safeDbCall` que transforman excepciones en `Either`.

## Contratos públicos

| Símbolo | Descripción | Retorno |
|---------|-------------|---------|
| `DomainError` | Sealed class raíz con subjerarquías `Network`, `Database`, `Security`, `Validation`, `Unknown` | — |
| `UiError` | Data class con `messageRes`, `severity`, `actions`, `errorCode` para consumo en Compose | — |
| `UiError.Severity` | Enum: `Info`, `Warning`, `Blocking`, `Fatal` | — |
| `UiError.UiErrorAction` | Enum: `Retry`, `Dismiss`, `Login`, `OpenSettings` | — |
| `safeApiCall { }` | Envuelve una llamada de red en `Either<DomainError.Network, T>` | `Either<DomainError, T>` |
| `safeDbCall { }` | Envuelve una operación de BD en `Either<DomainError.Database, T>` | `Either<DomainError, T>` |
| `NetworkErrorMapper.map(Throwable)` | Traduce excepciones de red a `DomainError.Network` | `DomainError.Network` |
| `DatabaseErrorMapper.map(Throwable)` | Traduce excepciones SQLite a `DomainError.Database` | `DomainError.Database` |
| `DomainErrorToUiErrorMapper.map(DomainError)` | Traduce `DomainError` a `UiError` para la capa UI | `UiError` |

## Dependencias

```kotlin
// core/error/build.gradle.kts
implementation(libs.arrow.core)
implementation(libs.arrow.fx.coroutines)
compileOnly(libs.androidx.annotation)
implementation(libs.kotlinx.coroutines.core)
implementation(libs.kotlinx.serialization.json)
```

- **Arrow Core** — tipo `Either`
- **kotlinx.serialization** — `SerializationException` para el mapper de red
- **androidx.annotation** — solo para anotaciones de StringRes (compile-only)
- Sin dependencias entre módulos del proyecto

## Ejemplos de uso

```kotlin
// En un repositorio de datos (features:*:data)
class ProductosRepositoryImpl @Inject constructor(
    private val api: ProductosApi,
    private val dao: ProductosDao,
) : ProductosRepository {

    override suspend fun obtenerProductos(): Either<DomainError, List<Producto>> =
        safeApiCall { api.getProducts() }
            .flatMapRight { dtos -> dtos.map { it.toDomain() }.right() }

    override suspend fun obtenerProductoLocal(id: Int): Either<DomainError, Producto> =
        safeDbCall { dao.findById(id) ?: throw NoSuchElementException() }
}

// En un ViewModel (features:*:presentation)
class ProductosViewModel @Inject constructor(
    private val obtenerProductos: ObtenerProductos,
    private val errorMapper: DomainErrorToUiErrorMapper,
) : ViewModel() {
    fun cargar() = viewModelScope.launch(errorHandler) {
        val result = obtenerProductos()
        _uiState.value = result.fold(
            onLeft  = { UiState.Error(errorMapper.map(it)) },
            onRight = { UiState.Content(it) },
        )
    }
}
```

## Estructura interna

```
core/error/
├── src/main/kotlin/com/mango/fakestore/core/error/
│   ├── DomainError.kt                      (sealed class raíz)
│   ├── UiError.kt                          (data class para UI)
│   ├── ext/
│   │   └── SafeCallExt.kt                  (safeApiCall, safeDbCall)
│   └── mapper/
│       ├── NetworkErrorMapper.kt
│       ├── DatabaseErrorMapper.kt
│       └── DomainErrorToUiErrorMapper.kt
└── src/test/kotlin/...
    ├── ext/SafeCallExtTest.kt
    └── mapper/
        ├── NetworkErrorMapperTest.kt
        ├── DatabaseErrorMapperTest.kt
        └── DomainErrorToUiErrorMapperTest.kt
```

## Cómo regenerar esta documentación

```bash
/documentar-modulo modulo=core:error
```
