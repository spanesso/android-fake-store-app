---
name: crear-pruebas-unitarias
description: 'Genera baterías completas de pruebas unitarias en español para un módulo del proyecto Mango Fake Store: tests de UseCases (happy path + un test por cada rama de `DomainError` que pueda emitir), tests de Repository con `MockWebServer` + InMemoryRoom (200, 4xx, 5xx, JSON inválido, timeout, sin red), tests de Mappers (DTO↔Domain, Entity↔Domain, Throwable→DomainError, DomainError→UiError) con un caso por cada rama, tests de ViewModel con Turbine y `StandardTestDispatcher` cubriendo Loading→Content/Error→Retry, y snapshot tests del design system (Paparazzi/Roborazzi) para cada estado. Reporta cobertura esperada (domain 100%, data ≥80%, presentation ≥70%) y los comandos Gradle para ejecutarlos. Usar SIEMPRE que el usuario pida "genera tests del módulo X", "crea pruebas unitarias para Y", "cubre las ramas de error de Z", "añade snapshot tests del design system", o tras `/speckit-implement` cuando el módulo recién creado no tenga aún tests. NO usar para tests de integración end-to-end con dispositivo (eso es Espresso + Firebase Test Lab), ni para tests de carga/rendimiento.'
---

# Skill `crear-pruebas-unitarias` — baterías de tests por módulo

Genera el set completo de pruebas unitarias que un módulo necesita para alcanzar los umbrales de cobertura del prompt maestro (§11.5: domain 100%, data ≥80%, presentation ≥70%). El skill produce código listo para ejecutar con `./gradlew :modulo:test`.

## Cuándo usar

- Después de `/speckit-implement` de un módulo, antes de cerrar el PR.
- Cuando el usuario pida "genera tests del módulo X", "cubre las ramas de error de Y", "añade snapshot tests del design system".
- Para cubrir nuevas ramas de `DomainError` agregadas al sealed interface del módulo.

NO usar para tests de integración con dispositivo (Espresso, Firebase Test Lab), tests de carga/rendimiento, o smoke tests E2E.

## Inputs

| Input | Obligatorio | Por defecto | Ejemplo |
|---|---|---|---|
| `modulo` | sí | — | `products`, `favorites`, `core:network` |
| `objetivo` | no | `todo` | `usecases`, `repository`, `mappers`, `viewmodel`, `snapshots`, `todo` |
| `caso_uso` | no si `objetivo=todo` | — | `ObtenerProductos`, `MarcarFavorito` |

## Detección de qué generar

1. Inspeccionar el módulo destino. Si no existe, abortar con error.
2. Detectar archivos a cubrir:
   - `domain/casosdeuso/*.kt` → tests de UseCase.
   - `data/repositorios/*.kt` → tests de Repository.
   - `data/mappers/*.kt` y `domain/errors/*Error.kt` → tests de Mapper.
   - `presentation/viewmodel/*.kt` → tests de ViewModel.
   - `core/design-system/.../componentes/*.kt` → snapshot tests.
3. Por cada archivo identificado, leer la firma:
   - Para UseCase: las ramas de `DomainError` declaradas en `Either<DomainError, T>` del retorno o referenciadas en el body.
   - Para Repository: los métodos públicos y los códigos HTTP esperados.
   - Para Mapper: cada rama `when` que produce un `DomainError` o `UiError`.
4. Generar tests usando las plantillas de `assets/templates/`.

## Reglas por tipo de test

### UseCase (`domain`)

Para `class ObtenerProductos @Inject constructor(private val repo: ProductosRepository) { suspend operator fun invoke(): Either<DomainError, List<Producto>> = ...`:

- 1 test happy path: `given repo devuelve Right(lista) when invoke then resultado es Right(lista esperada)`.
- 1 test por cada rama de `DomainError` que el caso de uso pueda recibir del repositorio (`Network.NoConnection`, `Network.Timeout`, `Network.Server(httpCode)`, `Database.ReadFailed`, `Unknown`, etc.).
- Si el caso de uso aplica validaciones, 1 test por cada rama de `DomainError.Validation`.

Convenciones:
- Nombres de test en formato backtick natural en español: `` `cuando repo devuelve NoConnection invoke retorna NoConnection`  ``.
- `runTest { ... }` para corrutinas.
- `mockk<Repo>()` con `coEvery { ... } returns ...`.

### Repository (`data`)

- `MockWebServer` para HTTP: tests por código (200 happy, 401, 403, 404, 408, 429, 500-599, JSON inválido, body vacío).
- `MockKAnnotations` o `mockk()` para DAOs Room, salvo que se use `InMemoryRoom` con `Room.inMemoryDatabaseBuilder(...)`.
- Verificar que cada excepción capturada se traduce al `DomainError` correcto (delega al `ErrorMapper` del módulo).
- 1 test específico de timeout: `MockWebServer.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE))`.

### Mapper

- DTO→Domain: 1 test por DTO con todos los campos no nulos, 1 con campos opcionales nulos, 1 con valor extremo (longitudes, números negativos).
- Entity→Domain: análogo.
- Throwable→DomainError: 1 test por **cada rama** de §7.4 del prompt maestro (IOException, UnknownHostException, SocketTimeoutException, HttpException por código, SerializationException, SQLiteConstraintException, SQLiteException, otra).
- DomainError→UiError: 1 test por cada caso del `sealed interface DomainError` que el módulo pueda recibir, verificando `messageRes` correcto y `severity` adecuada.

### ViewModel

- Inyectar `StandardTestDispatcher` y usar `Dispatchers.setMain` en `@Before`.
- Usar `Turbine.test { ... }` para colectar `uiState`.
- Tests obligatorios:
  - Construcción: emite `Loading` como primer valor.
  - Happy path: `Loading → Content` con datos esperados.
  - Cada rama de `DomainError` que el ViewModel puede recibir: `Loading → Error(uiError)` con `uiError.messageRes` esperado.
  - Retry: tras `Error`, recibir `UiEvent.Retry` → `Loading → Content/Error` según escenario.
- No usar `runBlocking`; usar `runTest`.

### Snapshot test (design system)

- Paparazzi con `DeviceConfig.PIXEL_5` por defecto.
- Cada componente: 1 test en claro, 1 en oscuro, 1 por cada estado declarado (idle, pressed, disabled, error, loading).
- `MangoErrorState`, `MangoSnackbar`, `MangoOfflineBanner`: tests por cada `severity` (Info, Warning, Blocking, Fatal).

## Output esperado

```
✅ Tests generados para `:features:products`.

Archivos creados (N):
  features/products/domain/src/test/.../ObtenerProductosTest.kt           (5 tests)
  features/products/data/src/test/.../ProductosRepositoryTest.kt           (8 tests)
  features/products/data/src/test/.../ProductosErrorMapperTest.kt          (6 tests)
  features/products/data/src/test/.../ProductoDtoMapperTest.kt             (3 tests)
  features/products/presentation/src/test/.../ProductosViewModelTest.kt    (6 tests)

Cobertura esperada (Kover):
  features/products/domain         ≥ 100%
  features/products/data           ≥  80%
  features/products/presentation   ≥  70%

Para ejecutar:
  cd repository/android-fake-store-app
  ./gradlew :features:products:domain:test
  ./gradlew :features:products:data:test
  ./gradlew :features:products:presentation:test
  ./gradlew koverHtmlReport && open repository/android-fake-store-app/build/reports/kover/html/index.html
```

## Plantillas

Bajo `assets/templates/`:

- `UseCaseTest.kt.template`
- `RepositoryTest.kt.template`
- `MapperTest.kt.template`
- `ErrorMapperTest.kt.template`
- `ViewModelTest.kt.template`
- `SnapshotTest.kt.template`
- `Fixtures.kt.template` — builders de fixtures (`producto()`, `productoDto()`, `domainErrorNoConnection()`) reutilizables.

## Buenas prácticas

- Nombres de test en backticks en español, formato `dado X cuando Y entonces Z` o equivalente.
- Una sola assertion por test cuando sea posible; si hay varias, agruparlas en un `softAssertions { }` (AssertK) o usar `assertThat(state).isEqualTo(esperado)` exhaustivo.
- Tests de mapper sin mocks: trabajo con datos puros.
- Fixtures: si dos tests construyen el mismo DTO/Entity, mover a `Fixtures.kt` del módulo `core:testing`.
- `@DisplayName` (JUnit 5) o `@Test` con backticks (JUnit 4) — elegir según el flavor configurado en `build-logic`.

## Reporte de cobertura tras generar

Tras generar los tests, NO ejecutar Gradle por defecto (puede tardar varios minutos). Solo imprimir los comandos sugeridos para que el usuario los corra. Si el usuario pide explícitamente correrlos, hacerlo con `--no-daemon --console=plain` y reportar el resultado.
