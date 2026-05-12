# Módulo `:features:favorites`

**Propósito**: Gestionar la persistencia local de productos marcados como favoritos, exponiendo un SSOT reactivo mediante Room que puede ser consultado desde cualquier feature.

## Contratos públicos

Exportados en `:features:favorites:domain`:

| Símbolo | Descripción | Retorno |
|---|---|---|
| `ObservarFavoritos.invoke()` | Emite la lista completa de favoritos en tiempo real | `Flow<Either<DomainError, List<Favorito>>>` |
| `ToggleFavorito.invoke(favorito)` | Inserta o elimina el favorito según su estado actual | `Either<DomainError, Unit>` |
| `ObservarConteoFavoritos.invoke()` | Emite el conteo de favoritos (para badge de perfil) | `Flow<Int>` |
| `FavoritosRepository` | Interfaz que describe el contrato de persistencia | — |

Exportados en `:features:favorites:api`:

| Símbolo | Descripción |
|---|---|
| *(vacío por ahora)* | La API pública se amplía cuando otros módulos consuman favoritos vía contrato `:api` |

## Dependencias

```
:features:favorites:domain
    ├── :core:common       (AppDispatchers)
    └── :core:error        (DomainError, safeDbCall)

:features:favorites:data
    ├── :features:favorites:domain
    ├── :core:database     (MangoDatabase, base cifrada Room)
    └── room-ktx, room-compiler (ksp)

:features:favorites:presentation
    ├── :features:favorites:domain
    ├── :core:design-system
    ├── :core:ui
    ├── :core:analytics    (Telemetry)
    └── hilt-navigation-compose
```

## Ejemplos de uso

### Observar favoritos desde otro ViewModel

```kotlin
class ProfileViewModel @Inject constructor(
    private val observarConteoFavoritos: ObservarConteoFavoritos,
) : ViewModel() {
    val conteo: StateFlow<Int> = observarConteoFavoritos()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)
}
```

### Toggle desde catálogo de productos

```kotlin
class ProductosViewModel @Inject constructor(
    private val obtenerProductos: ObtenerProductos,
    private val observarFavoritos: ObservarFavoritos,
    private val toggleFavorito: ToggleFavorito,
) : ViewModel() {
    private fun cargarProductos() {
        viewModelScope.launch(errorHandler) {
            combine(obtenerProductos(), observarFavoritos()) { prods, favs -> prods to favs }
                .collect { (prodResult, favResult) -> /* enriquecer con esFavorito */ }
        }
    }
}
```

## Estructura interna

```
features/favorites/
├── api/
│   └── build.gradle.kts              contrato público (pendiente de poblar)
├── domain/
│   ├── model/Favorito.kt             entidad de dominio
│   ├── repository/FavoritosRepository.kt
│   └── usecase/
│       ├── ObservarFavoritos.kt
│       ├── ToggleFavorito.kt
│       └── ObservarConteoFavoritos.kt
├── data/
│   ├── di/FavoritosDataModule.kt     binding Hilt
│   ├── local/
│   │   ├── entity/FavoritoEntity.kt  @Entity Room
│   │   └── FavoritosDao.kt           @Dao Room
│   ├── mapper/FavoritoEntityMapper.kt
│   └── repository/FavoritosRepositoryImpl.kt
├── presentation/
│   ├── di/                           (vacío — binding Hilt de VM es automático con @HiltViewModel)
│   ├── mapper/FavoritoToUiMapper.kt
│   ├── model/FavoritoUi.kt
│   ├── ui/
│   │   ├── components/FavoritoItem.kt
│   │   ├── route/FavoritosRoute.kt
│   │   ├── screens/FavoritosScreen.kt
│   │   └── state/
│   │       ├── FavoritosUiState.kt
│   │       ├── FavoritosUiEvent.kt
│   │       └── FavoritosUiEffect.kt
│   └── viewmodel/FavoritosViewModel.kt
└── docs/                             esta carpeta
```

## Cómo regenerar esta documentación

```bash
/documentar-modulo modulo=favorites
```
