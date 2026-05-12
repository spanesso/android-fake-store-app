# Módulo `:core:common`

**Propósito**: Proporciona las utilidades transversales a todos los módulos: dispatchers de corrutinas, extensiones de `Either`/`Flow`, y funciones auxiliares de Kotlin puro.

## Contratos públicos

| Símbolo | Descripción | Retorno |
|---------|-------------|---------|
| `AppDispatchers` | Interfaz de dispatchers inyectable (IO, Main, Default, Unconfined) | — |
| `EitherExt.flatMapRight` | Encadena un `Either<L, R>` aplicando una transformación solo al lado derecho | `Either<L, R2>` |
| `EitherExt.fold` | Reduce un `Either` a un valor único según la rama | `C` |
| `FlowEitherExt.mapRight` | Mapea el valor de éxito dentro de un `Flow<Either<L, R>>` | `Flow<Either<L, R2>>` |
| `FlowEitherExt.filterRight` | Filtra y extrae los valores de éxito de un `Flow<Either<L, R>>` | `Flow<R>` |
| `KotlinExt.isNotNullOrBlank` | Comprueba que una `String?` no sea nula ni en blanco | `Boolean` |
| `KotlinExt.truncate` | Recorta una cadena al máximo de caracteres dado | `String` |
| `KotlinExt.ifNotNull` | Ejecuta un bloque solo cuando el valor no es nulo | `R?` |
| `KotlinExt.orDefault` | Devuelve el valor o un default cuando es nulo | `T` |
| `KotlinExt.toImmutableList` | Convierte cualquier `Collection<T>` en una `List<T>` inmutable | `List<T>` |

## Dependencias

```kotlin
// core/common/build.gradle.kts
implementation(libs.arrow.core)
implementation(libs.arrow.fx.coroutines)
implementation(libs.kotlinx.coroutines.core)
implementation(libs.kotlinx.coroutines.android)
```

- **Arrow Core 1.2.4** — tipo `Either<L, R>` y helpers funcionales
- **kotlinx.coroutines** — `CoroutineDispatcher`, `Flow`
- Sin dependencias entre módulos del proyecto

## Ejemplos de uso

```kotlin
// Inyección de dispatchers en un ViewModel
class ProductosViewModel @Inject constructor(
    private val dispatchers: AppDispatchers,
    private val obtenerProductos: ObtenerProductos,
) : ViewModel() {
    fun cargar() = viewModelScope.launch(dispatchers.io) { ... }
}

// Encadenamiento de Either
val resultado: Either<DomainError, String> = obtenerProductos()
    .flatMapRight { productos -> formatearNombres(productos) }
    .fold(
        onLeft  = { error -> "Error: ${error.javaClass.simpleName}" },
        onRight = { nombres -> nombres },
    )

// Flow de Either
productoFlow
    .mapRight { it.nombre }
    .filterRight()
    .collect { nombre -> println(nombre) }
```

## Estructura interna

```
core/common/
├── src/main/kotlin/com/mango/fakestore/core/common/
│   ├── dispatchers/
│   │   ├── AppDispatchers.kt        (interfaz pública)
│   │   ├── DefaultAppDispatchers.kt (implementación)
│   │   └── di/DispatchersModule.kt  (módulo Hilt)
│   └── ext/
│       ├── EitherExt.kt             (flatMapRight, fold)
│       ├── FlowEitherExt.kt         (mapRight, filterRight)
│       └── KotlinExt.kt             (utilidades generales)
└── src/test/kotlin/...
    ├── dispatchers/AppDispatchersTest.kt
    └── ext/
        ├── EitherExtTest.kt
        ├── FlowEitherExtTest.kt
        └── KotlinExtTest.kt
```

## Cómo regenerar esta documentación

```bash
/documentar-modulo modulo=core:common
```
