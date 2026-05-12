# Pruebas — `:core:logging`

## Tests unitarios (14 tests totales)

| Clase de test | Tests | Descripción |
|---------------|-------|-------------|
| `NoOpLoggerTest` | 6 | Verifica que ningún método lanza excepción y que `NoOpLogger` implementa `Logger` |
| `TimberLoggerTest` | 8 | Verifica logging via árbol de test (no usa `android.util.Log`); verifica plantado de árbol |

## Estrategia de test para `TimberLogger`

`Timber.DebugTree` usa `android.util.Log` que no está disponible en tests JVM. La solución es plantar un árbol de prueba anónimo en `@Before` que captura logs sin llamar a la API de Android:

```kotlin
@Before
fun setUp() {
    Timber.uprootAll()
    Timber.plant(object : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            mensajesCapturados += priority to message
        }
    })
}
```

Con el árbol de test ya plantado, `TimberLogger()` no vuelve a plantar `DebugTree` (porque `treeCount > 0`), por lo que los logs van al árbol de test — sin `android.util.Log`.

## Comandos Gradle

```bash
# Desde la raíz del repositorio (https://github.com/spanesso/android-fake-store-app)

./gradlew :core:logging:testDebugUnitTest
```

## Umbrales de cobertura

| Componente | Estado |
|------------|--------|
| `NoOpLogger` — todos los métodos | ✅ cubierto (6 tests) |
| `TimberLogger` — info/warn/error | ✅ cubierto (6 tests) |
| `TimberLogger` — lógica de plantado | ✅ cubierto (2 tests) |
| `LoggingModule` — selección por DEBUG | _(requiere BuildConfig — validación manual o test instrumentado)_ |
