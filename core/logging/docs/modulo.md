# Módulo `:core:logging`

**Propósito**: Proveer una interfaz unificada de logging (`Logger`) con dos implementaciones: una activa en entornos de desarrollo (debug) y una silenciosa en producción — garantizando que ningún log se expone en builds de release.

## Contratos públicos

| Símbolo | Descripción |
|---------|-------------|
| `Logger.info(tag, mensaje)` | Log de nivel informativo — flujo normal, eventos esperados |
| `Logger.warn(tag, mensaje, causa?)` | Log de advertencia — situación atípica que no interrumpe el flujo |
| `Logger.error(tag, mensaje, causa?)` | Log de error — fallo recuperado, estado inesperado |

## Implementaciones

| Implementación | Activa en | Comportamiento |
|----------------|-----------|----------------|
| `TimberLogger` | `BuildConfig.DEBUG = true` (debug/dev) | Usa `Timber.tag(tag)` para emitir logs en logcat. Planta `DebugTree` automáticamente si no hay ningún árbol registrado. |
| `NoOpLogger` | `BuildConfig.DEBUG = false` (staging/release/producción) | Todos los métodos son no-ops. Cero output, cero impacto en rendimiento. |

## Regla de producción

`LoggingModule` decide la implementación en tiempo de compilación usando `BuildConfig.DEBUG` del propio módulo `:core:logging`. En cualquier build de release, **ningún log se escribe en logcat ni en disco**. No se necesita ProGuard/R8 para suprimir los logs — el `NoOpLogger` los elimina en la fuente.

## Dependencias

- `timber` (Jakewharton Timber 5.x)
- `mango.android.hilt` — inyección de `Logger` en módulos consumidores

## Módulos que consumen `Logger`

| Módulo | Uso |
|--------|-----|
| `:core:analytics` — `FirebaseTelemetryImpl` | `warn` en no-fatales, `info` en eventos y trazas |
| `:core:analytics` — `ConsoleTelemetryImpl` | `info`/`warn` para debugging local |
| `:core:security` — `IntegrityCheckerImpl` | `warn` si detecta root, `info` si dispositivo es íntegro |

## Ejemplos de uso en feature modules

```kotlin
class ProductosRepositoryImpl @Inject constructor(
    private val api: ProductosApi,
    private val logger: Logger,
) : ProductosRepository {

    override suspend fun obtenerProductos(): Either<DomainError, List<Producto>> =
        safeApiCall {
            val lista = api.getProducts()
            logger.info(TAG, "Productos cargados: ${lista.size} items")
            lista.map { it.toDomain() }
        }.also { resultado ->
            if (resultado.isLeft()) {
                logger.warn(TAG, "Fallo al cargar productos: ${resultado}")
            }
        }

    private companion object { const val TAG = "ProductosRepo" }
}
```

## Estructura interna

```
core/logging/
├── src/main/kotlin/com/mango/fakestore/core/logging/
│   ├── Logger.kt
│   ├── impl/
│   │   ├── TimberLogger.kt
│   │   └── NoOpLogger.kt
│   └── di/
│       └── LoggingModule.kt
└── src/test/
    ├── NoOpLoggerTest.kt     (6 tests)
    └── TimberLoggerTest.kt   (8 tests)
```

## Cómo regenerar esta documentación

```
/documentar-modulo modulo=logging
```
