# Pruebas — `:features:products`

## Cobertura esperada

| Capa | Módulo Gradle | Umbral | Tests actuales |
|---|---|---|---|
| Domain | `:features:products:domain` | ≥ 100% | 7 tests |
| Data | `:features:products:data` | ≥ 80% | Pendiente |
| Presentation | `:features:products:presentation` | ≥ 70% | 8 tests |

## Tests existentes

### Domain — `ObtenerProductosTest` (7 tests)

| Test | Verifica |
|---|---|
| `dado un repositorio cuando se invoca el caso de uso entonces delega al repositorio` | Delegación pura |
| `dado que el repositorio retorna una lista de productos... entonces emite Right con la lista` | Happy path |
| `dado que el repositorio retorna una lista vacía... entonces emite Right vacío` | Lista vacía |
| `dado que el repositorio retorna NoConnection... entonces emite Left NoConnection` | ERR red sin conexión |
| `dado que el repositorio retorna Timeout... entonces emite Left Timeout` | ERR timeout |
| `dado que el repositorio retorna Server 500... entonces emite Left Server` | ERR error servidor |
| `dado que el repositorio retorna ReadFailed... entonces emite Left ReadFailed` | ERR base de datos |

### Presentation — `ProductosViewModelTest` (8 tests)

| Test | Verifica |
|---|---|
| `cuando se crea el viewmodel entonces emite Loading` | Estado inicial |
| `dado repo devuelve lista de productos cuando se carga entonces emite Content` | Happy path |
| `dado repo devuelve lista vacia cuando se carga entonces emite Empty` | Lista vacía |
| `dado repo devuelve NoConnection cuando se carga entonces emite Error` | ERR red |
| `dado repo devuelve Timeout cuando se carga entonces emite Error` | ERR timeout |
| `dado repo devuelve Server error cuando se carga entonces emite Error` | ERR servidor |
| `dado estado de Error cuando onEvent Retry entonces vuelve a cargar emitiendo Loading` | Retry |
| `dado repo devuelve error cuando se carga entonces emite MostrarSnackbar effect` | UiEffect |

## Comandos Gradle

```bash
cd repository/android-fake-store-app

# Domain
./gradlew :features:products:domain:testDebugUnitTest

# Presentation
./gradlew :features:products:presentation:testDebugUnitTest

# Todos los tests de products
./gradlew :features:products:domain:testDebugUnitTest \
          :features:products:data:testDebugUnitTest \
          :features:products:presentation:testDebugUnitTest

# Reporte de cobertura (Kover)
./gradlew koverHtmlReport
open repository/android-fake-store-app/build/reports/kover/html/index.html
```

## Convenciones de nombres

- UseCase tests: `*UseCaseTest.kt` o `*Test.kt` en `domain/src/test/`
- Repository tests: `*RepositoryTest.kt` en `data/src/test/`
- Mapper tests: `*MapperTest.kt` en `data/src/test/`
- ViewModel tests: `*ViewModelTest.kt` en `presentation/src/test/`

## Fixtures recomendadas

```kotlin
// En cualquier test file, construir fixtures con:
val productoEjemplo = Producto(
    id = 1, titulo = "Camiseta de lino", descripcion = "...",
    precio = 49.99, categoria = "ropa",
    imagenUrl = "https://fakestoreapi.com/img/test.jpg",
    valoracion = Valoracion(puntuacion = 4.1, numVotaciones = 259),
)
```
