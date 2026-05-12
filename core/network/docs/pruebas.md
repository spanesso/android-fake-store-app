# Pruebas — `:core:network`

## Tests unitarios (22 tests totales)

| Clase de test | Tests | Descripción |
|---------------|-------|-------------|
| `ConnectivityObserverTest` | 4 | Emite Connected / Disconnected / Unavailable; snapshot de currentStatus |
| `SafeRetrofitCallExtTest` | 8 | Mapeo de 401, 403, 404, 500, SocketTimeout, UnknownHost, SerializationException, CancellationException |
| `RetryInterceptorTest` | 9 | 500+backoff, 400 no-retry, 401 no-retry, 408, 429+Retry-After, 503, POST sin key, POST+Idempotency-Key, agotamiento maxRetries |

## Herramientas de test

- **MockWebServer** (`com.squareup.okhttp3:mockwebserver`) — servidor HTTP en memoria para `RetryInterceptorTest`
- **MockK** — mocks de `ConnectivityManager`, `Context`, `NetworkCapabilities`
- **Robolectric** — Android SDK en JVM para `ConnectivityObserverTest` (requiere `NetworkRequest.Builder`)
- **Turbine** — test de Flows (`observer.statusFlow.test { ... }`)

## Comandos Gradle

```bash
# Desde la raíz del repositorio (https://github.com/spanesso/android-fake-store-app)

# Solo network
./gradlew :core:network:testDevDebugUnitTest

# Con informe HTML
./gradlew :core:network:testDevDebugUnitTest koverHtmlReport
open build/reports/kover/html/index.html
```

## Umbrales de cobertura

| Capa | Umbral objetivo | Estado |
|------|----------------|--------|
| `safeRetrofitCall` | 100% ramas | ✅ 8/8 casos cubiertos |
| `RetryInterceptor` | 100% ramas | ✅ 9/9 casos cubiertos |
| `ConnectivityObserverImpl` | ≥ 80% | ✅ 4 flujos cubiertos |

## Convenciones de nombres

```
dado_<precondición>_cuando_<acción>_entonces_<resultado>
emits_connected_when_validated
retries_on_500_with_backoff
does_not_retry_non_idempotent_post
```

## Tests de integración (pendiente — ETAPA 2+)

`ConnectivityObserverImpl` sobre un dispositivo real requiere test instrumentado con `@Rule ActivityScenarioRule`. No forma parte de los tests unitarios.
