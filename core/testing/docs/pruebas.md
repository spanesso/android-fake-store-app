# Pruebas — `:core:testing`

`:core:testing` es una librería de soporte de tests; no tiene tests propios. Su corrección se valida implícitamente en los tests de los módulos que la consumen.

## Verificación implícita

Cada módulo que use `testImplementation(project(":core:testing"))` ejercita los builders y utilities en sus propios tests:

| Módulo que consume | Utility de `core:testing` usada |
|--------------------|----------------------------------|
| `:features:products:domain` | `domainErrorNoConnection`, `domainErrorTimeout`, `CoroutineTestRule` |
| `:features:auth:presentation` | `CoroutineTestRule`, `TestAppDispatchers`, `uiErrorBlocking` |
| `:features:products:data` | `domainErrorServer`, `domainErrorDbLectura` |

## Comandos Gradle

```bash
# Verificar que el módulo compila sin errores
# Desde la raíz del repositorio (https://github.com/spanesso/android-fake-store-app)

./gradlew :core:testing:assembleDebug
```

## Umbrales de cobertura

No aplica — módulo de soporte sin lógica de negocio testeable directamente.
