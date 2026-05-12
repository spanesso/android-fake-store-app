# Módulo `:core:network`

**Propósito**: Proporciona la infraestructura HTTP de la aplicación — cliente Retrofit con OkHttp, certificate pinning, interceptor de reintento con backoff exponencial, y observación reactiva del estado de conectividad.

## Contratos públicos

| Símbolo | Descripción | Tipo |
|---------|-------------|------|
| `ConnectivityObserver` | Interfaz para observar el estado de red | `interface` |
| `ConnectivityObserver.statusFlow` | Flujo del estado actual de red | `Flow<ConnectivityStatus>` |
| `ConnectivityObserver.currentStatus()` | Snapshot síncrono del estado actual | `ConnectivityStatus` |
| `ConnectivityStatus` | Estado de conectividad: Connected / Disconnected / Unavailable | `sealed interface` |
| `NetworkErrorReporter` | Interfaz para reportar errores de red a telemetría | `interface` |
| `safeRetrofitCall { }` | Envuelve llamadas Retrofit en `Either<DomainError, T>` | extensión `suspend fun` |
| `NetworkConfig` | Configuración de timeouts, reintentos y certificate pins | `data class` |

### Inyección vía Hilt

`NetworkModule` provee:
- `NetworkConfig` — URL base desde `BuildConfig.BASE_URL` por flavor
- `OkHttpClient` — con `RetryInterceptor`, `CertificatePinner` y logging en debug
- `Retrofit` — con `kotlinx.serialization` como conversor

`NetworkBindsModule` enlaza:
- `ConnectivityObserverImpl → ConnectivityObserver`
- `NoOpNetworkErrorReporter → NetworkErrorReporter`

## Flavors y BuildConfig

| Flavor | `BuildConfig.BASE_URL` |
|--------|------------------------|
| `dev` | `https://fakestoreapi.com/` |
| `staging` | `https://fakestoreapi.com/` |
| `prod` | `https://fakestoreapi.com/` |

## Dependencias

- `:core:error` — `DomainError`, `NetworkErrorMapper`
- `:core:common` — `AppDispatchers`
- `com.squareup.retrofit2:retrofit` + `kotlinx.serialization`
- `com.squareup.okhttp3:okhttp` + `okhttp3:logging-interceptor`
- `arrow-kt:arrow-core` — `Either`
- `com.jakewharton.timber:timber`

## Ejemplo de uso

```kotlin
// En un RepositorioImpl de feature:
class ProductosRepositorioImpl @Inject constructor(
    private val api: ProductosApi,
    private val observer: ConnectivityObserver
) : ProductosRepositorio {

    override suspend fun obtenerProductos(): Either<DomainError, List<Producto>> =
        safeRetrofitCall { api.getProducts() }
            .map { dtos -> dtos.map(ProductoDto::toDomain) }

    override val conectividadFlow: Flow<ConnectivityStatus>
        get() = observer.statusFlow
}
```

## Estructura interna

```
core/network/
├── src/main/kotlin/com/mango/fakestore/core/network/
│   ├── config/
│   │   └── NetworkConfig.kt          configuración de cliente HTTP
│   ├── connectivity/
│   │   ├── ConnectivityObserver.kt   interfaz pública
│   │   ├── ConnectivityObserverImpl.kt callbackFlow frío con NetworkCallback
│   │   └── ConnectivityStatus.kt     sealed: Connected / Disconnected / Unavailable
│   ├── di/
│   │   └── NetworkModule.kt          providers + bindings Hilt
│   ├── ext/
│   │   └── SafeRetrofitCallExt.kt    safeRetrofitCall { }
│   ├── interceptor/
│   │   ├── LoggingInterceptorFactory.kt HttpLoggingInterceptor configurable
│   │   └── RetryInterceptor.kt       backoff exponencial ±300ms jitter
│   ├── reporter/
│   │   └── NetworkErrorReporter.kt   interfaz de telemetría + NoOp impl
│   └── ssl/
│       └── CertificatePinnerFactory.kt OkHttp CertificatePinner
└── src/main/res/xml/
    └── network_security_config.xml   NSC con pines SHA-256
```

## Cómo regenerar esta documentación

```
/documentar-modulo modulo=core:network
```
