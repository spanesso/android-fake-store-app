# Módulo `:core:testing`

**Propósito**: Proveer utilidades de test compartidas entre todos los módulos del proyecto: reglas de corrutinas, dispatchers de test, fakes reutilizables y builders de `DomainError`/`UiError`.

## Contratos públicos

| Símbolo | Descripción |
|---------|-------------|
| `CoroutineTestRule` | `TestWatcher` JUnit4 que configura `Dispatchers.Main` con `StandardTestDispatcher` |
| `TestAppDispatchers` | Implementación de `AppDispatchers` que usa un único `TestDispatcher` en todos los dispatchers |
| `domainErrorNoConnection()` | Builder de `DomainError.Network.NoConnection` con causa opcional |
| `domainErrorTimeout()` | Builder de `DomainError.Network.Timeout` |
| `domainErrorServer(codigo)` | Builder de `DomainError.Network.Server(codigo)` |
| `domainErrorUnauthorized()` | Builder de `DomainError.Network.Unauthorized` |
| `domainErrorForbidden()` | Builder de `DomainError.Network.Forbidden` |
| `domainErrorNotFound()` | Builder de `DomainError.Network.NotFound` |
| `domainErrorParsing()` | Builder de `DomainError.Network.Parsing` |
| `domainErrorDbLectura()` | Builder de `DomainError.Database.ReadFailed` |
| `domainErrorDbEscritura()` | Builder de `DomainError.Database.WriteFailed` |
| `domainErrorDbNoEncontrado()` | Builder de `DomainError.Database.NotFound` |
| `domainErrorValidacion(campos)` | Builder de `DomainError.Validation` |
| `domainErrorDesconocido()` | Builder de `DomainError.Unknown` |
| `uiErrorInfo(...)` | Builder de `UiError` con `Severity.Info` |
| `uiErrorWarning(...)` | Builder de `UiError` con `Severity.Warning` y acción `Retry` |
| `uiErrorBlocking(...)` | Builder de `UiError` con `Severity.Blocking` |
| `uiErrorFatal(...)` | Builder de `UiError` con `Severity.Fatal` |

## Uso en otros módulos

```kotlin
// build.gradle.kts del módulo que usa estas utilidades
testImplementation(project(":core:testing"))
```

## Dependencias

- `:core:error` — `DomainError`, `UiError`
- `:core:common` — `AppDispatchers`
- `junit`, `kotlinx-coroutines-test`, `mockk`, `truth`, `turbine`, `arrow-core`

## Ejemplos de uso

```kotlin
class ProductosUseCaseTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private val dispatchers = TestAppDispatchers(coroutineRule.scheduler)

    @Test
    fun `cuando repo retorna NoConnection then retorna Left NoConnection`() = runTest {
        coEvery { repo.obtener() } returns Either.Left(domainErrorNoConnection())
        val resultado = sut.invoke()
        assertThat(resultado).isEqualTo(Either.Left(domainErrorNoConnection()))
    }

    @Test
    fun `uiState pasa a Error con severity Warning cuando hay Timeout`() = runTest {
        coEvery { useCase() } returns Either.Left(domainErrorTimeout())
        viewModel.cargar()
        coroutineRule.scheduler.advanceUntilIdle()
        val estado = viewModel.uiState.value
        assertThat((estado as UiState.Error).error.severity)
            .isEqualTo(UiError.Severity.Warning)
    }
}
```

## Estructura interna

```
core/testing/
├── src/main/kotlin/com/mango/fakestore/core/testing/
│   ├── CoroutineTestRule.kt
│   ├── dispatchers/
│   │   └── TestAppDispatchers.kt
│   └── builders/
│       ├── DomainErrorBuilders.kt
│       └── UiErrorBuilders.kt
└── (sin tests propios — es una librería de soporte)
```

## Cómo regenerar esta documentación

```
/documentar-modulo modulo=testing
```
