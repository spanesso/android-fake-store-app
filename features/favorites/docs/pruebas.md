# Pruebas: `:features:favorites`

## Resumen de cobertura

| Capa | Tests | Objetivo | Archivos de test |
|---|---|---|---|
| `domain` | 9 | 100% | `ObservarFavoritosTest`, `ToggleFavoritoTest`, `ObservarConteoFavoritosTest` |
| `data` | 7 | ≥ 80% | `FavoritoEntityMapperTest`, `FavoritosRepositoryImplTest` |
| `presentation` | 6 | ≥ 70% | `FavoritosViewModelTest` |

**Total**: 22 tests nuevos

## Lista de tests por capa

### Domain (`features/favorites/domain`)

**`ObservarFavoritosTest.kt`**
- `dado repo emite lista entonces invoke devuelve la misma lista`
- `dado repo emite lista vacia entonces invoke devuelve lista vacia`
- `dado repo emite ReadFailed entonces invoke propaga el error`

**`ToggleFavoritoTest.kt`**
- `dado repo inserta correctamente cuando invoke retorna Right Unit`
- `dado repo elimina existente cuando invoke retorna Right Unit`
- `dado repo devuelve WriteFailed cuando invoke retorna WriteFailed`

**`ObservarConteoFavoritosTest.kt`**
- `dado repo emite 0 entonces invoke devuelve 0`
- `dado repo emite N entonces invoke devuelve N`

### Data (`features/favorites/data`)

**`FavoritoEntityMapperTest.kt`**
- `dado FavoritoEntity cuando toDomain entonces todos los campos son correctos`
- `dado Favorito cuando toEntity entonces todos los campos son correctos`

**`FavoritosRepositoryImplTest.kt`** (Robolectric + InMemoryRoom)
- `dado favorito insertado cuando observarFavoritos emite entonces contiene el favorito`
- `dado favorito existente cuando toggleFavorito entonces lo elimina`
- `dado favorito no existente cuando toggleFavorito entonces lo inserta`
- `dado tabla vacia cuando observarConteo emite entonces devuelve 0`
- `dado N favoritos cuando observarConteo emite entonces devuelve N`

### Presentation (`features/favorites/presentation`)

**`FavoritosViewModelTest.kt`**
- `cuando se crea el viewmodel entonces emite Loading`
- `dado repo devuelve favoritos cuando se carga entonces emite Content`
- `dado repo devuelve lista vacia cuando se carga entonces emite Empty`
- `dado repo devuelve ReadFailed cuando se carga entonces emite Error`
- `dado estado Error cuando onEvent Reintentar entonces vuelve a cargar`
- `dado toggle falla cuando onEvent ToggleFavorito entonces emite MostrarSnackbar`

## Comandos Gradle

```bash
cd repository/android-fake-store-app

# Domain (debe alcanzar 100%)
./gradlew :features:favorites:domain:testDebugUnitTest

# Data (debe alcanzar ≥ 80%)
./gradlew :features:favorites:data:testDebugUnitTest

# Presentation (debe alcanzar ≥ 70%)
./gradlew :features:favorites:presentation:testDebugUnitTest

# Todos juntos
./gradlew :features:favorites:domain:testDebugUnitTest \
          :features:favorites:data:testDebugUnitTest \
          :features:favorites:presentation:testDebugUnitTest

# Reporte de cobertura Kover
./gradlew koverHtmlReport
open build/reports/kover/html/index.html
```

## Configuración de test

- **Corrutinas**: `CoroutineTestRule` de `:core:testing` con `StandardTestDispatcher`
- **Mocks**: MockK (`mockk()`, `coEvery`, `coVerify`)
- **Flow testing**: Turbine (`flow.test { awaitItem() }`)
- **Room en tests**: `Room.inMemoryDatabaseBuilder` con Robolectric (`@Config(sdk = [28])`)
- **Assertions**: JUnit 4 (`assertEquals`) + Truth (`assertThat`)

## Convenciones de nombres

- `*Test.kt` — JUnit 4, nombres en backticks en español
- Formato: `` `dado X cuando Y entonces Z` ``
- Un `@Test` por caso; fixtures reutilizables en el cuerpo del test
