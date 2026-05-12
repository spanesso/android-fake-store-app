# Diseño — `:core:testing`

## Decisiones de diseño

### Android library en vez de Kotlin puro

Aunque conceptualmente sería deseable un módulo Kotlin puro (JVM) para `core:testing`, en la práctica necesita depender de `core:error` y `core:common` que son Android libraries. Gradle no permite que un módulo JVM dependa de un módulo Android sin conflictos de variante. Por ello usa `mango.android.library`.

### CoroutineTestRule con StandardTestDispatcher

`StandardTestDispatcher` (no avanzar automáticamente) es la opción correcta para tests de ViewModel porque permite afirmar sobre estados intermedios (`Loading`) antes de que la corrutina avance. El uso de `scheduler.advanceUntilIdle()` en el test da control explícito sobre el tiempo.

```kotlin
@get:Rule val coroutineRule = CoroutineTestRule()
// scheduler.advanceUntilIdle() → ejecuta todas las corrutinas encoladas
// scheduler.advanceTimeBy(5_000) → simula paso de tiempo
```

### TestAppDispatchers comparte dispatcher

Todos los dispatchers (`io`, `main`, `default`, `unconfined`) apuntan al mismo `StandardTestDispatcher`. Esto garantiza que no haya concurrencia real entre las capas en tests unitarios y el orden de ejecución es determinista.

### Builders con defaults razonables

Los builders de `DomainError` y `UiError` usan `Int = 0` para `messageRes` (no pueden referenciar `R.string` en un módulo sin recursos). En tests que necesiten un `messageRes` concreto se pasa explícitamente:

```kotlin
uiErrorWarning(messageRes = R.string.error_red_sin_conexion, errorCode = "NET-001")
```

## Guía de extensión

Para añadir un nuevo builder:
1. Añadir la función en `DomainErrorBuilders.kt` o `UiErrorBuilders.kt`.
2. Seguir el patrón: nombre en español, causa/campos opcionales con default `null`/`emptyMap()`.
3. Actualizar `modulo.md` con la nueva entrada en la tabla de contratos.
