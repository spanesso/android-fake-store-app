# Módulo `:core:datastore`

**Propósito**: Persistencia cifrada de sesión (tokens de autenticación) y preferencias de usuario (tema, notificaciones) mediante Jetpack DataStore + Tink AES-256-GCM.

## Contratos públicos

| Símbolo | Descripción | Tipo |
|---------|-------------|------|
| `MangoDataStore` | Interfaz de persistencia de sesión y preferencias | `interface` |
| `MangoDataStore.sessionFlow` | Flujo reactivo del estado de sesión actual | `Flow<SessionData>` |
| `MangoDataStore.preferencesFlow` | Flujo reactivo de preferencias de usuario | `Flow<UserPreferences>` |
| `MangoDataStore.saveSession(data)` | Guarda tokens cifrados con Tink | `suspend fun` |
| `MangoDataStore.clearSession()` | Elimina todos los tokens de sesión | `suspend fun` |
| `MangoDataStore.savePreferences(prefs)` | Guarda preferencias (tema, notificaciones) | `suspend fun` |
| `SessionData` | Modelo de sesión con `accessToken`, `refreshToken`, `userId` | `data class` |
| `SessionData.isAuthenticated` | `true` si `accessToken != null` | `Boolean` |
| `SessionData.Empty` | Singleton para sesión vacía | `companion object val` |
| `UserPreferences` | Modelo de preferencias con `theme: AppTheme` y `notificationsEnabled: Boolean` | `data class` |
| `AppTheme` | Enum: `LIGHT`, `DARK`, `SYSTEM` | `enum class` |

## Inyección vía Hilt

`DataStoreModule` provee:
- `DataStore<Preferences>` — fichero `mango_prefs.preferences_pb` en `Context.dataStoreFile`
- `TinkEncryption` — instancia con contexto para inicializar Android Keystore
- `MangoDataStoreImpl` — implementación lista para inyectar

## Dependencias

- `:core:common` — `AppDispatchers` (dispatcher IO para operaciones de escritura)
- `androidx.datastore:datastore-preferences`
- `com.google.crypto.tink:tink-android` — cifrado AES-256-GCM
- `com.jakewharton.timber:timber`

## Ejemplo de uso

```kotlin
// En ViewModel de autenticación:
class LoginViewModel @Inject constructor(
    private val dataStore: MangoDataStore
) : ViewModel() {

    val estaAutenticado: StateFlow<Boolean> = dataStore.sessionFlow
        .map { it.isAuthenticated }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun guardarSesion(token: String, refresh: String, userId: String) {
        viewModelScope.launch {
            dataStore.saveSession(SessionData(token, refresh, userId))
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch { dataStore.clearSession() }
    }
}
```

## Estructura interna

```
core/datastore/
├── src/main/kotlin/com/mango/fakestore/core/datastore/
│   ├── MangoDataStore.kt             interfaz pública
│   ├── MangoDataStoreImpl.kt         implementación con Tink + DataStore
│   ├── crypto/
│   │   └── TinkEncryption.kt         open class: encrypt/decrypt AES-256-GCM
│   ├── di/
│   │   └── DataStoreModule.kt        providers Hilt
│   ├── internal/
│   │   └── PreferencesKeys.kt        claves tipadas de Preferences
│   └── model/
│       ├── AppTheme.kt               enum LIGHT / DARK / SYSTEM
│       ├── SessionData.kt            tokens + isAuthenticated
│       └── UserPreferences.kt        theme + notificationsEnabled
└── src/test/kotlin/...
    └── MangoDataStoreTest.kt         8 tests con DataStore in-memory + FakeTinkEncryption
```

## Cómo regenerar esta documentación

```
/documentar-modulo modulo=core:datastore
```
