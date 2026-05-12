# Contratos de API Pública — Módulos Core de Infraestructura (1.5–1.7)

**Fecha**: 2026-05-11 | **Rama**: `002-etapa-1-core`

> Los contratos de esta sección definen las superficies públicas que otros módulos (`:features:*`, `:app`) consumen. Lo que no figura aquí es un detalle de implementación que puede cambiar sin notificación.

---

## `:core:network`

### Superficie pública (Hilt DI)

Los módulos consumidores obtienen estas dependencias vía Hilt; **no** instancian clases directamente.

#### `Retrofit` — cliente HTTP listo para usar

```kotlin
// Obtenido vía @Inject en módulos data de features
@Inject lateinit var retrofit: Retrofit

// Uso típico en un feature:
val api = retrofit.create(ProductsApiService::class.java)
```

**Garantías**: pre-configurado con base URL correcta para el flavor activo, kotlinx.serialization conversor, interceptor de logging (debug only), retry interceptor, certificate pinning.

---

#### `OkHttpClient` — cliente base (si se necesita customizar)

```kotlin
@Inject @Named("MangoOkHttpClient") lateinit var okHttpClient: OkHttpClient
```

**Uso**: para módulos que necesiten añadir interceptores adicionales (ej. auth token en ETAPA 2).

---

#### `ConnectivityObserver` — estado de red reactivo

```kotlin
@Inject lateinit var connectivityObserver: ConnectivityObserver

// En un ViewModel:
connectivityObserver.statusFlow
    .collect { status ->
        when (status) {
            ConnectivityStatus.Connected    -> hideOfflineBanner()
            ConnectivityStatus.Disconnected -> showOfflineBanner()
            ConnectivityStatus.Unavailable  -> showOfflineBanner()
        }
    }
```

---

#### `safeRetrofitCall` — wrapper tipado para llamadas Retrofit

```kotlin
// Importado desde :core:network
import com.mango.fakestore.core.network.ext.safeRetrofitCall

// En un repositorio:
suspend fun fetchProducts(): Either<DomainError, List<ProductDto>> =
    safeRetrofitCall { apiService.getProducts() }
```

**Diferencia con `safeApiCall` de `:core:error`**: maneja `retrofit2.HttpException` de forma explícita sin depender del regex del mensaje.

---

#### `NetworkConfig` — acceso a configuración de red

```kotlin
@Inject lateinit var networkConfig: NetworkConfig

// networkConfig.baseUrl, networkConfig.maxRetries, etc.
```

---

### Artefactos de seguridad (no inyectados directamente, configurados automáticamente)

- **`network_security_config.xml`**: referenciado desde `AndroidManifest.xml` del `:app`. Contiene TLS 1.2+, dominios permitidos, pins de certificado.
- **`CertificatePinner`**: configurado dentro de `OkHttpClient`; no expuesto públicamente.
- **`RetryInterceptor`**: parte del `OkHttpClient`; no expuesto.

---

### Errores que puede emitir `:core:network` (via `safeRetrofitCall`)

| Error | Causa |
|-------|-------|
| `DomainError.Network.NoConnection` | `IOException`, `UnknownHostException`, fallo SSL |
| `DomainError.Network.Timeout` | `SocketTimeoutException`, `TimeoutException` |
| `DomainError.Network.Unauthorized` | HTTP 401 |
| `DomainError.Network.Forbidden` | HTTP 403 |
| `DomainError.Network.NotFound` | HTTP 404 |
| `DomainError.Network.Server(code)` | HTTP 5xx |
| `DomainError.Network.Parsing` | `SerializationException` |
| `DomainError.Unknown` | Cualquier otra excepción no mapeada |

---

## `:core:database`

### Superficie pública (Hilt DI)

#### `MangoDatabase` — instancia de Room + SQLCipher

```kotlin
@Inject lateinit var db: MangoDatabase

// Los feature modules obtienen sus DAOs del db:
val productsDao: ProductsDao = db.productsDao()
```

**Garantías**: BD encriptada con SQLCipher, clave gestionada vía Android Keystore, abierta en hilo IO de Hilt.

---

#### `safeDbCall` — wrapper tipado para operaciones Room

> `safeDbCall` está en `:core:error`, no en `:core:database`. Los feature modules dependen de `:core:error` para usarlo.

```kotlin
// Importado desde :core:error
import com.mango.fakestore.core.error.ext.safeDbCall

// En un repositorio:
suspend fun insertFavorite(entity: FavoriteEntity): Either<DomainError, Unit> =
    safeDbCall { favoritesDao.insert(entity) }
```

---

### Errores que puede emitir `:core:database` (via `safeDbCall`)

| Error | Causa |
|-------|-------|
| `DomainError.Database.IntegrityViolation` | `SQLiteConstraintException` (duplicado de PK, FK rota) |
| `DomainError.Database.WriteFailed` | `SQLiteException` en escritura |
| `DomainError.Database.ReadFailed` | Cualquier otra `SQLiteException` o corrupción |
| `DomainError.Database.NotFound` | DAO devuelve `null` — debe manejarse en el repositorio |

> **Nota**: `DomainError.Database.NotFound` **no** se emite desde `safeDbCall`; es responsabilidad del repositorio verificar el resultado nulo y emitir el error correspondiente.

---

## `:core:datastore`

### Superficie pública (Hilt DI)

#### `MangoDataStore` — almacenamiento encriptado de sesión y preferencias

```kotlin
@Inject lateinit var dataStore: MangoDataStore

// Lectura reactiva de sesión:
dataStore.sessionFlow
    .map { it.isAuthenticated }
    .collect { isAuth -> updateAuthState(isAuth) }

// Guardar sesión tras login:
dataStore.saveSession(SessionData(
    accessToken = "Bearer xyz...",
    refreshToken = "refresh...",
    userId = hashUserId(rawId)
))

// Cerrar sesión:
dataStore.clearSession()

// Cambiar tema:
dataStore.savePreferences(UserPreferences(theme = AppTheme.DARK))
```

---

### Modelo de sesión

```kotlin
data class SessionData(
    val accessToken: String?,
    val refreshToken: String?,
    val userId: String?
) {
    val isAuthenticated: Boolean get() = accessToken != null
    companion object { val Empty = SessionData(null, null, null) }
}
```

---

### Modelo de preferencias

```kotlin
data class UserPreferences(
    val theme: AppTheme = AppTheme.SYSTEM,
    val notificationsEnabled: Boolean = true
)

enum class AppTheme { LIGHT, DARK, SYSTEM }
```

---

### Garantías de seguridad del DataStore

- Tokens (`accessToken`, `refreshToken`, `userId`) se cifran con Tink AES-256-GCM antes de persisitirse.
- La clave Tink se gestiona en Android Keystore; nunca aparece en texto plano en almacenamiento.
- `clearSession()` no deja rastros legibles de tokens anteriores en el archivo del DataStore.
- En caso de corrupción: el `CorruptionHandler` borra el archivo y emite `SessionData.Empty`.

---

## Resumen de dependencias inter-módulo (ETAPA 1.5–1.7)

```
:core:network
  implementa: :core:error, :core:common
  expone públicamente: Retrofit, OkHttpClient, ConnectivityObserver, NetworkConfig, safeRetrofitCall

:core:database
  implementa: :core:error, :core:common
  expone públicamente: MangoDatabase, DatabaseKeyManager

:core:datastore
  implementa: :core:common
  expone públicamente: MangoDataStore, SessionData, UserPreferences, AppTheme
```
