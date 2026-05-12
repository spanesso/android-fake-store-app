# Modelo de Datos: Módulos Core de Infraestructura (1.5–1.7)

**Fecha**: 2026-05-11 | **Rama**: `002-etapa-1-core`

> Nota: Estos módulos son puramente de **infraestructura**. No exponen entidades de dominio; exponen tipos de configuración, contratos de conectividad y modelos de sesión/preferencias.

---

## `:core:network` — Tipos e Interfaces

### `ConnectivityStatus`

```kotlin
// Paquete: com.mango.fakestore.core.network.connectivity
sealed interface ConnectivityStatus {
    data object Connected    : ConnectivityStatus  // Red disponible y validada
    data object Disconnected : ConnectivityStatus  // Sin red activa
    data object Unavailable  : ConnectivityStatus  // Red físicamente desconectada
}
```

**Invariantes**:
- `Connected` solo se emite cuando `NET_CAPABILITY_VALIDATED` está presente (descarta portales cautivos).
- `Disconnected` se emite cuando la red activa se pierde.
- `Unavailable` se emite al suscribirse si no hay ninguna red registrada.

---

### `ConnectivityObserver`

```kotlin
// Paquete: com.mango.fakestore.core.network.connectivity
interface ConnectivityObserver {
    val statusFlow: Flow<ConnectivityStatus>
    fun currentStatus(): ConnectivityStatus
}
```

**Contrato**:
- `statusFlow` es un `SharedFlow` que replayed el último valor al suscribirse.
- Emite el estado inicial al suscribirse (no espera cambio de red).

---

### `NetworkConfig`

```kotlin
// Paquete: com.mango.fakestore.core.network.config
data class NetworkConfig(
    val baseUrl: String,              // Ej: "https://fakestoreapi.com/"
    val connectTimeoutSeconds: Long,  // Default: 10
    val readTimeoutSeconds: Long,     // Default: 30
    val writeTimeoutSeconds: Long,    // Default: 30
    val maxRetries: Int,              // Default: 3
    val retryBaseDelayMs: Long,       // Default: 500
    val retryMaxDelayMs: Long,        // Default: 10_000
    val certificatePins: List<String> // SHA-256 pins; formato "sha256/BASE64=="
)
```

**Construcción por flavor**:

| Flavor | baseUrl |
|--------|---------|
| dev | `https://fakestoreapi.com/` |
| staging | `https://fakestoreapi.com/` |
| prod | `https://fakestoreapi.com/` |

---

### `NetworkErrorReporter` (interfaz de integración futura con analytics)

```kotlin
// Paquete: com.mango.fakestore.core.network.reporter
interface NetworkErrorReporter {
    fun reportNetworkError(error: DomainError.Network, context: Map<String, String> = emptyMap())
}

class NoOpNetworkErrorReporter : NetworkErrorReporter {
    override fun reportNetworkError(error: DomainError.Network, context: Map<String, String>) = Unit
}
```

---

## `:core:database` — Tipos e Interfaces

### `MangoDatabase` (base class abstracta Room)

```kotlin
// Paquete: com.mango.fakestore.core.database
@Database(entities = [], version = 1, exportSchema = true)
abstract class MangoDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "mango_db"
        const val DATABASE_VERSION = 1
    }
}
```

**Notas de diseño**:
- `entities = []` en el módulo core; los feature modules añaden sus entidades en `:app/build.gradle.kts` al ensamblar la BD final.
- `exportSchema = true` para trazabilidad de migraciones.
- La encriptación SQLCipher se configura en `DatabaseModule` vía `SupportFactory`.

---

### `DatabaseKeyManager`

```kotlin
// Paquete: com.mango.fakestore.core.database.key
interface DatabaseKeyManager {
    fun getOrCreateKey(): ByteArray   // Devuelve passphrase para SQLCipher
    fun clearKey()                     // Solo para testing / reset
}
```

**Implementación**:
- Genera clave AES-256 en Android Keystore alias `"mango_db_key"`.
- Deriva passphrase: `HKDF(keyMaterial, salt="MANGO_DB", info="sqlcipher-passphrase", 32 bytes)`.
- Passphrase derivada se entrega directamente a `SQLiteDatabaseHook` de SQLCipher.

---

### Migraciones

```kotlin
// Paquete: com.mango.fakestore.core.database.migration
object DatabaseMigrations {
    val all: Array<Migration> = emptyArray()   // ETAPA 1: sin migraciones aún
    
    val FALLBACK_STRATEGY = DestructiveMigrationOnDowngrade  // Solo para dev/staging
}
```

**Política de migración**:
- `dev`: `fallbackToDestructiveMigration()` — BD se borra y recrea en conflicto de esquema.
- `staging` y `prod`: migración explícita obligatoria; fallo de migración lanza excepción no silenciada.

---

## `:core:datastore` — Tipos e Interfaces

### `SessionData`

```kotlin
// Paquete: com.mango.fakestore.core.datastore.model
data class SessionData(
    val accessToken: String?,    // Null = no hay sesión activa
    val refreshToken: String?,   // Null = no hay refresh disponible
    val userId: String?          // Hash anonimizado del ID real
) {
    companion object {
        val Empty = SessionData(null, null, null)
    }
    
    val isAuthenticated: Boolean get() = accessToken != null
}
```

**Invariante**: Los tokens se almacenan cifrados con Tink AES-256-GCM. `SessionData` en memoria puede contener los valores en claro; son cifrados al serializar.

---

### `UserPreferences`

```kotlin
// Paquete: com.mango.fakestore.core.datastore.model
data class UserPreferences(
    val theme: AppTheme = AppTheme.SYSTEM,
    val notificationsEnabled: Boolean = true
)

enum class AppTheme { LIGHT, DARK, SYSTEM }
```

**Almacenamiento**: Valores no sensibles; almacenados en claro en Preferences DataStore.

---

### `MangoDataStore` (interfaz pública)

```kotlin
// Paquete: com.mango.fakestore.core.datastore
interface MangoDataStore {
    // --- Sesión ---
    val sessionFlow: Flow<SessionData>
    suspend fun saveSession(data: SessionData)
    suspend fun clearSession()

    // --- Preferencias ---
    val preferencesFlow: Flow<UserPreferences>
    suspend fun savePreferences(prefs: UserPreferences)
}
```

**Contratos de comportamiento**:
- `sessionFlow` emite `SessionData.Empty` como valor inicial si no hay sesión guardada.
- `clearSession()` solo borra los tokens; `UserPreferences` no se ve afectado.
- Lecturas son reactivas; cualquier escritura propaga el nuevo valor a todos los colectores.
- En caso de corrupción del DataStore: se borra el archivo, se emite `SessionData.Empty`, se registra evento de telemetría (sin PII).

---

### Claves de Preferences DataStore

```kotlin
// Paquete: com.mango.fakestore.core.datastore.internal
internal object PreferencesKeys {
    val ACCESS_TOKEN   = stringPreferencesKey("access_token_enc")   // Cifrado Tink
    val REFRESH_TOKEN  = stringPreferencesKey("refresh_token_enc")  // Cifrado Tink
    val USER_ID        = stringPreferencesKey("user_id_enc")        // Cifrado Tink
    val THEME          = stringPreferencesKey("app_theme")          // En claro
    val NOTIFICATIONS  = booleanPreferencesKey("notifications_enabled") // En claro
}
```

---

## Diagrama de dependencias entre módulos (ETAPA 1.5–1.7)

```
:core:common ─────────────────────────────────────┐
:core:error (safeApiCall, safeDbCall, mappers) ───┤
                                                   ↓
:core:network ──→ usa :core:error, :core:common    │
:core:database ──→ usa :core:error, :core:common   │
:core:datastore ──→ usa :core:common               │
                                                   │
:features:* ─────────────────────────────────────→┘
  (dependerán de :core:network, :core:database,
   :core:datastore en ETAPA 2+)
```

**Regla**: `:core:datastore` no depende de `:core:error` (manejo de errores con `try/catch` local limitado + `CorruptionHandler`).
