# Pruebas — `:core:common`

## Inventario de tests

| Archivo | Tests | Cobertura |
|---------|-------|-----------|
| `AppDispatchersTest.kt` | 2 | `AppDispatchers` (dispatchers correctos) |
| `EitherExtTest.kt` | 6 | `flatMapRight` (Right/Left), `fold` (ambas ramas) |
| `FlowEitherExtTest.kt` | 2 | `mapRight`, `filterRight` |
| `KotlinExtTest.kt` | 14 | `isNotNullOrBlank` (3), `truncate` (4), `ifNotNull` (2), `orDefault` (2), `toImmutableList` (2) + edge cases |
| **Total** | **24** | |

## Ejecución

```bash
# Desde la raíz del repositorio (https://github.com/spanesso/android-fake-store-app)
./gradlew :core:common:testDebugUnitTest
```

## Convenciones

- Nombres en backticks en español: `` `dado X cuando Y entonces Z` ``
- `runTest { ... }` para todo lo que involucre corrutinas
- Tests de extensiones `Either`/`Flow`: sin mocks, solo datos puros
- Tests de `AppDispatchers`: verifican que el dispatcher retornado no es nulo y tiene el tipo esperado

## Umbrales

`:core:common` es un módulo de utilidades sin UseCases ni ViewModels. Umbral informal: **toda lógica pura debe tener cobertura de ramas completa**. Las funciones sin lógica condicional (delegaciones directas) no requieren test.
