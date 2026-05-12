# Módulo `:features:products`

**Propósito**: Muestra el catálogo de productos de la Fake Store API con caché local, patrón SSOT y manejo tipado de errores.

## Contratos públicos

Exportados en `:features:products:domain`:

| Símbolo | Descripción | Retorno |
|---|---|---|
| `ObtenerProductos` | Caso de uso: obtiene la lista de productos (red + caché) | `Flow<Either<DomainError, List<Producto>>>` |
| `ProductosRepository` | Interfaz del repositorio de productos | `Flow<Either<DomainError, List<Producto>>>` |
| `Producto` | Entidad de dominio de un producto | `data class` |
| `Valoracion` | Puntuación y número de votaciones de un producto | `data class` |

## Dependencias

- `:core:common` — `AppDispatchers`
- `:core:error` — `DomainError`, `UiError`, `safeApiCall`, `safeDbCall`, `DomainErrorToUiErrorMapper`
- `:core:design-system` — `MangoProductCard`, `MangoErrorState`, `MangoEmptyState`, `MangoLoadingIndicator`, `MangoTopAppBar`
- `:core:ui` — utilidades de estado de UI
- `:core:analytics` — `Telemetry` para reporting de errores no fatales
- `:core:database` — `MangoDatabase` base para Room

## Ejemplos de uso

```kotlin
class MiViewModel @Inject constructor(
    private val obtenerProductos: ObtenerProductos,
) : ViewModel() {
    fun cargar() {
        viewModelScope.launch {
            obtenerProductos().collect { resultado ->
                resultado.fold(
                    ifLeft = { error -> /* manejar error tipado */ },
                    ifRight = { productos -> /* actualizar UI */ },
                )
            }
        }
    }
}
```

## Estructura interna

```
features/products/
├── api/              contratos públicos (vacío — se usa domain directamente)
├── domain/
│   ├── model/        Producto.kt, Valoracion.kt
│   ├── repository/   ProductosRepository.kt (interface)
│   └── usecase/      ObtenerProductos.kt
├── data/
│   ├── di/           ProductsDataModule.kt
│   ├── local/        ProductosDao.kt, entity/ProductoEntity.kt
│   ├── mapper/       ProductoDtoMapper.kt, ProductoEntityMapper.kt
│   ├── remote/       ProductosApi.kt, dto/ProductoDto.kt, dto/RatingDto.kt
│   └── repository/   ProductosRepositoryImpl.kt (SSOT: caché → red → caché)
└── presentation/
    ├── di/           ProductsPresentationModule.kt
    ├── mapper/       ProductoToUiMapper.kt
    ├── model/        ProductoUi.kt
    └── ui/
        ├── components/ ProductoItem.kt
        ├── route/    ProductosRoute.kt  ← instancia ViewModel con hiltViewModel()
        ├── screens/  ProductosScreen.kt ← Composable puro (state + onEvent)
        └── state/    ProductosUiState.kt, ProductosUiEvent.kt, ProductosUiEffect.kt
```

## Cómo regenerar esta documentación

```bash
/documentar-modulo modulo=products
```
