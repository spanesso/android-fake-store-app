# Plan de implementación: Módulos Core de Infraestructura (ETAPA 1 — Sub-etapas 1.5 a 1.7)

**Rama**: `002-etapa-1-core` | **Fecha**: 2026-05-11 | **Spec**: [spec.md](./spec.md)  
**Artefactos**: [research.md](./research.md) | [data-model.md](./data-model.md) | [contracts/public-api.md](./contracts/public-api.md)

---

## Resumen

Implementar los tres módulos core de infraestructura que son prerequisito para los módulos de feature de ETAPA 2+:

1. **`:core:network`** — cliente OkHttp/Retrofit con certificate pinning real (`sha256/dSxOWQR+hD1HkfYEk0y+JuXzHrLTjhVPXDzGRsbO7oI=`), interceptor de retry con backoff exponencial (§7.7), `ConnectivityObserver` reactivo y `safeRetrofitCall` tipado.
2. **`:core:database`** — Room abstracto + SQLCipher con clave gestionada por Android Keystore, infraestructura de migraciones y `DatabaseModule` Hilt.
3. **`:core:datastore`** — Preferences DataStore con tokens cifrados vía Tink AES-256-GCM, modelo de sesión/preferencias y `clearSession()` seguro.

**Hallazgo clave de research**: `safeApiCall`, `safeDbCall`, `NetworkErrorMapper` y `DatabaseErrorMapper` **ya existen en `:core:error`**. Los módulos nuevos los consumen; no los reimplementan. El módulo `:core:network` añade `safeRetrofitCall` para manejar `retrofit2.HttpException` de forma explícita.

Orden de implementación: **network → database → datastore** (el network puede ir en paralelo con database/datastore, pero datastore no depende de ninguno de los otros dos).

---

## Contexto técnico

**Lenguaje/Versión**: Kotlin 2.0.21 | JVM 11  
**Dependencias clave**:
- network: Retrofit 2.11.0, OkHttp 4.12.0, MockWebServer 4.12.0, kotlinx.serialization 1.7.3
- database: Room 2.6.1 + KSP 2.0.21-1.0.27, SQLCipher 4.6.0
- datastore: DataStore Preferences 1.1.1, Tink Android 1.15.0
- transversal: Hilt 2.52, Coroutines 1.9.0, Arrow 1.2.4

**Almacenamiento**: Room + SQLCipher (`:core:database`), DataStore encriptado (`:core:datastore`)  
**Testing**: JUnit 4, MockK 1.13.13, Truth 1.4.4, MockWebServer 4.12.0, coroutines-test 1.9.0  
**Plataforma**: Android minSdk 24 / targetSdk 36  
**Tipo de proyecto**: Módulos Android library de infraestructura (sin UI, sin Composables)  
**Constraints**: Sin imports de Material3 ni Compose; sin lógica de negocio; sin UI. Dependencias Android Keystore garantizadas (minSdk 24 → API 24 ≥ API 18 para Keystore simétrico).

---

## Verificación de constitución

### Principio I — Español obligatorio ✅
Todos los artefactos (plan, research, data-model, contracts, docs, strings de error) en español. Código Kotlin en inglés.

### Principio II — Clean Architecture + MVVM estricto ✅
Los tres módulos son capas de infraestructura sin lógica de dominio. No hay dependencias cruzadas. `:core:network` y `:core:database` dependen de `:core:error` (capa de infraestructura transversal); `:core:datastore` solo depende de `:core:common`. Ninguno conoce a los feature modules.

### Principio III — Composables puros ✅ (N/A)
Estos módulos no tienen UI. No aplica.

### Principio IV — Modularización exhaustiva ✅
Los tres módulos ya están declarados en `settings.gradle.kts` con sus `build.gradle.kts` mínimos. Esta ETAPA implementa el contenido.

### Principio V — Design System primero ✅ (N/A)
Sin componentes visuales en estos módulos.

### Principio VI — Seguridad por defecto ✅
- Certificate pinning con pin real de `fakestoreapi.com`.
- TLS 1.2+ via `network_security_config.xml`.
- SQLCipher + clave Android Keystore.
- Tokens cifrados con Tink AES-256-GCM.
- `clearSession()` limpia completamente los tokens.
- Sin PII en logs.

### Principio VII — Errores tipados ✅
- `:core:network` provee `safeRetrofitCall` → `Either<DomainError, T>`.
- `:core:database` usa `safeDbCall` de `:core:error` → `Either<DomainError, T>`.
- `:core:datastore` no usa `Either` directamente (no tiene repositorios); expone `Flow<T>` con `CorruptionHandler`.
- Ningún módulo propaga `Throwable` crudo hacia arriba.

### Principio VIII — Observabilidad con tier gratuito ✅ (N/A en estos módulos)
Sin integraciones Firebase en estas sub-etapas. La interfaz `NetworkErrorReporter` es no-op hasta ETAPA 1.8.

### Principio IX — Testing por módulo ✅
- `:core:network`: cobertura ≥ 80%; `MockWebServer` para 6 escenarios + tests `RetryInterceptor` + tests `ConnectivityObserver`.
- `:core:database`: cobertura ≥ 80%; `InMemoryRoom` para CRUD + constraint violation + `DatabaseKeyManager` stub.
- `:core:datastore`: cobertura ≥ 70%; DataStore in-memory con coroutines-test.

---

## Estructura del proyecto

### Documentación (esta feature)

```
specs/003-etapa-1-infraestructura/
├── plan.md              # Este archivo
├── spec.md              # Especificación de los 3 módulos
├── research.md          # Decisiones de investigación
├── data-model.md        # Modelos e interfaces
├── contracts/
│   └── public-api.md    # Superficies públicas por módulo
├── checklists/
│   └── requirements.md  # Checklist de calidad del spec
└── tasks.md             # Generado por /speckit-tasks
```

### Código fuente

```
repository/android-fake-store-app/

── core/network/
   ├── build.gradle.kts                        [ACTUALIZAR]
   └── src/
       ├── main/
       │   ├── AndroidManifest.xml              [CREAR — namespace]
       │   ├── res/xml/network_security_config.xml  [CREAR]
       │   └── kotlin/com/mango/fakestore/core/network/
       │       ├── config/
       │       │   └── NetworkConfig.kt         [CREAR]
       │       ├── connectivity/
       │       │   ├── ConnectivityStatus.kt    [CREAR]
       │       │   └── ConnectivityObserverImpl.kt [CREAR]
       │       ├── interceptor/
       │       │   ├── RetryInterceptor.kt      [CREAR]
       │       │   └── LoggingInterceptorFactory.kt [CREAR]
       │       ├── ssl/
       │       │   └── CertificatePinnerFactory.kt  [CREAR]
       │       ├── reporter/
       │       │   ├── NetworkErrorReporter.kt  [CREAR — interfaz]
       │       │   └── NoOpNetworkErrorReporter.kt [CREAR]
       │       ├── ext/
       │       │   └── SafeRetrofitCallExt.kt   [CREAR]
       │       └── di/
       │           └── NetworkModule.kt         [CREAR — Hilt]
       └── test/kotlin/com/mango/fakestore/core/network/
           ├── interceptor/
           │   └── RetryInterceptorTest.kt      [CREAR]
           └── connectivity/
               └── ConnectivityObserverTest.kt  [CREAR]

── core/database/
   ├── build.gradle.kts                        [ACTUALIZAR]
   └── src/
       ├── main/kotlin/com/mango/fakestore/core/database/
       │   ├── MangoDatabase.kt                 [CREAR — Room abstract]
       │   ├── key/
       │   │   └── DatabaseKeyManager.kt        [CREAR]
       │   ├── migration/
       │   │   └── DatabaseMigrations.kt        [CREAR]
       │   └── di/
       │       └── DatabaseModule.kt            [CREAR — Hilt]
       └── test/kotlin/com/mango/fakestore/core/database/
           ├── key/
           │   └── DatabaseKeyManagerTest.kt    [CREAR]
           └── MangoDatabaseIntegrationTest.kt  [CREAR]

── core/datastore/
   ├── build.gradle.kts                        [ACTUALIZAR]
   └── src/
       ├── main/kotlin/com/mango/fakestore/core/datastore/
       │   ├── MangoDataStore.kt                [CREAR — interfaz + impl]
       │   ├── model/
       │   │   ├── SessionData.kt               [CREAR]
       │   │   ├── UserPreferences.kt           [CREAR]
       │   │   └── AppTheme.kt                  [CREAR]
       │   ├── crypto/
       │   │   └── TinkEncryption.kt            [CREAR — Tink helper]
       │   └── di/
       │       └── DataStoreModule.kt           [CREAR — Hilt]
       └── test/kotlin/com/mango/fakestore/core/datastore/
           └── MangoDataStoreTest.kt            [CREAR]
```

---

## Fases de implementación

### Fase 1 — `:core:network` (Priority: P1)

> **Prerequisito**: `:core:error` implementado (ya hecho en ETAPA 1.1–1.4). `:core:common` implementado (ya hecho). Los mappers y `safeApiCall` ya existen en `:core:error`.

#### T1.1 — Actualizar `build.gradle.kts` de `:core:network`

```kotlin
plugins {
    id("mango.android.library")
    id("mango.android.hilt")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.mango.fakestore.core.network"
    
    buildFeatures { buildConfig = true }

    buildTypes {
        debug   { buildConfigField("String", "BASE_URL", "\"https://fakestoreapi.com/\"") }
        release { buildConfigField("String", "BASE_URL", "\"https://fakestoreapi.com/\"") }
    }
    
    productFlavors {
        create("dev")     { buildConfigField("String", "BASE_URL", "\"https://fakestoreapi.com/\"") }
        create("staging") { buildConfigField("String", "BASE_URL", "\"https://fakestoreapi.com/\"") }
        create("prod")    { buildConfigField("String", "BASE_URL", "\"https://fakestoreapi.com/\"") }
    }
}

dependencies {
    implementation(project(":core:error"))
    implementation(project(":core:common"))
    
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.arrow.core)
    
    testImplementation(libs.junit)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
}
```

**Problema conocido de flavors**: Los `productFlavors` en `:core:network` deben coincidir con los declarados en `:app`. Verificar que el `flavorDimensions` sea compatible.

---

#### T1.2 — Crear `network_security_config.xml`

```xml
<!-- res/xml/network_security_config.xml -->
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system"/>
        </trust-anchors>
    </base-config>
    <domain-config>
        <domain includeSubdomains="true">fakestoreapi.com</domain>
        <pin-set>
            <pin digest="SHA-256">dSxOWQR+hD1HkfYEk0y+JuXzHrLTjhVPXDzGRsbO7oI=</pin>
            <!-- Backup pin: actualizar antes de producción -->
            <pin digest="SHA-256">AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=</pin>
        </pin-set>
    </domain-config>
    <debug-overrides>
        <trust-anchors>
            <certificates src="user"/>
        </trust-anchors>
    </debug-overrides>
</network-security-config>
```

**Referencia en** `AndroidManifest.xml` del `:app`: `android:networkSecurityConfig="@xml/network_security_config"`.

---

#### T1.3 — Crear `NetworkConfig.kt`

```kotlin
data class NetworkConfig(
    val baseUrl: String,
    val connectTimeoutSeconds: Long = 10L,
    val readTimeoutSeconds: Long = 30L,
    val writeTimeoutSeconds: Long = 30L,
    val maxRetries: Int = 3,
    val retryBaseDelayMs: Long = 500L,
    val retryMaxDelayMs: Long = 10_000L,
    val certificatePins: List<String> = listOf(
        "sha256/dSxOWQR+hD1HkfYEk0y+JuXzHrLTjhVPXDzGRsbO7oI=",
        "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=" // backup
    )
)
```

---

#### T1.4 — Crear `ConnectivityStatus.kt` y `ConnectivityObserverImpl.kt`

```kotlin
sealed interface ConnectivityStatus {
    data object Connected    : ConnectivityStatus
    data object Disconnected : ConnectivityStatus
    data object Unavailable  : ConnectivityStatus
}

interface ConnectivityObserver {
    val statusFlow: Flow<ConnectivityStatus>
    fun currentStatus(): ConnectivityStatus
}
```

`ConnectivityObserverImpl` usa `ConnectivityManager.registerNetworkCallback` dentro de `callbackFlow { }`, con `NET_CAPABILITY_VALIDATED` para filtrar portales cautivos. Exponerse como `SharedFlow(replay=1)`.

---

#### T1.5 — Crear `RetryInterceptor.kt`

```kotlin
class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val baseDelayMs: Long = 500L,
    private val maxDelayMs: Long = 10_000L
) : Interceptor {
    override fun intercept(chain: Chain): Response {
        var attempt = 0
        var lastException: IOException? = null
        while (attempt <= maxRetries) {
            try {
                val response = chain.proceed(chain.request())
                if (shouldRetry(response, chain.request()) && attempt < maxRetries) {
                    val delay = retryAfterDelay(response) ?: backoffDelay(attempt)
                    Thread.sleep(delay)
                    attempt++
                    response.close()
                    continue
                }
                return response
            } catch (e: IOException) {
                if (attempt >= maxRetries) throw e
                lastException = e
                Thread.sleep(backoffDelay(attempt))
                attempt++
            }
        }
        throw lastException ?: IOException("Max retries exceeded")
    }

    private fun shouldRetry(response: Response, request: Request): Boolean {
        if (!request.method.equals("GET", ignoreCase = true) &&
            !request.method.equals("HEAD", ignoreCase = true) &&
            request.header("Idempotency-Key") == null) return false
        return response.code in setOf(408, 429) || response.code in 500..599
    }
    
    private fun retryAfterDelay(response: Response): Long? {
        if (response.code != 429) return null
        return response.header("Retry-After")?.toLongOrNull()?.times(1000)
    }

    private fun backoffDelay(attempt: Int): Long {
        val base = minOf(baseDelayMs * (1L shl attempt), maxDelayMs)
        val jitter = (-300L..300L).random()
        return maxOf(0L, base + jitter)
    }
}
```

---

#### T1.6 — Crear `CertificatePinnerFactory.kt`

```kotlin
object CertificatePinnerFactory {
    fun create(config: NetworkConfig): CertificatePinner =
        CertificatePinner.Builder().apply {
            config.certificatePins.forEach { pin ->
                add("fakestoreapi.com", pin)
            }
        }.build()
}
```

---

#### T1.7 — Crear `SafeRetrofitCallExt.kt`

```kotlin
@Suppress("TooGenericExceptionCaught")
suspend fun <T> safeRetrofitCall(
    mapper: NetworkErrorMapper = NetworkErrorMapper(),
    block: suspend () -> T
): Either<DomainError, T> =
    try {
        Either.Right(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: retrofit2.HttpException) {
        Either.Left(mapper.mapHttpCode(e.code(), e))
    } catch (e: Throwable) {
        Either.Left(mapper.map(e))
    }
```

> **Nota**: Requiere añadir `fun mapHttpCode(code: Int, cause: Throwable): DomainError.Network` al `NetworkErrorMapper` de `:core:error`. Si el equipo prefiere no modificar `:core:error`, esta lógica puede vivir íntegramente en `:core:network`.

---

#### T1.8 — Crear `NetworkModule.kt` (Hilt)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideNetworkConfig(@ApplicationContext ctx: Context): NetworkConfig =
        NetworkConfig(baseUrl = BuildConfig.BASE_URL)

    @Provides @Singleton
    fun provideOkHttpClient(config: NetworkConfig): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(config.connectTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(config.readTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(config.writeTimeoutSeconds, TimeUnit.SECONDS)
            .certificatePinner(CertificatePinnerFactory.create(config))
            .addInterceptor(RetryInterceptor(config.maxRetries, config.retryBaseDelayMs, config.retryMaxDelayMs))
            .apply {
                if (BuildConfig.DEBUG) addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
            .build()

    @Provides @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, config: NetworkConfig): Retrofit =
        Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(
                kotlinxSerializationConverter(Json { ignoreUnknownKeys = true })
            )
            .build()

    @Provides @Singleton
    fun provideConnectivityObserver(@ApplicationContext ctx: Context): ConnectivityObserver =
        ConnectivityObserverImpl(ctx)

    @Provides @Singleton
    fun provideNetworkErrorReporter(): NetworkErrorReporter = NoOpNetworkErrorReporter()
}
```

---

#### T1.9 — Tests de `:core:network`

**`RetryInterceptorTest.kt`** (MockWebServer):
- `retries_on_500_with_exponential_backoff` — 3 reintentos, backoff verificado
- `does_not_retry_on_400` — 0 reintentos para 400 Bad Request
- `does_not_retry_on_401` — 0 reintentos para 401 Unauthorized
- `retries_on_408_request_timeout` — reintenta 408
- `retries_on_429_with_retry_after_header` — espera el delay de `Retry-After`
- `retries_on_503_eventually_succeeds` — 2 fallos, tercer intento exitoso
- `does_not_retry_non_idempotent_post` — POST sin `Idempotency-Key` no reintenta

**`ConnectivityObserverTest.kt`** (Robolectric + MockK):
- `emits_connected_when_network_validated`
- `emits_disconnected_when_network_lost`
- `emits_unavailable_when_no_network_registered`
- `current_status_reflects_latest_emission`

**`SafeRetrofitCallExtTest.kt`**:
- `maps_http_401_to_unauthorized`
- `maps_http_403_to_forbidden`
- `maps_http_404_to_not_found`
- `maps_http_500_to_server_error`
- `maps_socket_timeout_to_timeout`
- `maps_unknown_host_to_no_connection`
- `maps_serialization_exception_to_parsing`
- `rethrows_cancellation_exception`

---

#### T1.10 — Compilar `:core:network`

```bash
cd repository/android-fake-store-app
./gradlew :core:network:assembleDebug
./gradlew :core:network:testDebugUnitTest
```

---

### Fase 2 — `:core:database` (Priority: P2)

> **Prerequisito**: `:core:error` implementado (ya hecho).

#### T2.1 — Actualizar `build.gradle.kts` de `:core:database`

```kotlin
plugins {
    id("mango.android.library")
    id("mango.android.hilt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.mango.fakestore.core.database"
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
    }
}

dependencies {
    implementation(project(":core:error"))
    implementation(project(":core:common"))
    
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.sqlcipher.android)
    implementation(libs.androidx.security.crypto)
    implementation(libs.kotlinx.coroutines.android)
    
    testImplementation(libs.junit)
    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
    testImplementation(libs.robolectric)
}
```

---

#### T2.2 — Crear `DatabaseKeyManager.kt`

```kotlin
interface DatabaseKeyManager {
    fun getOrCreatePassphrase(): ByteArray
    fun clearPassphrase()
}

class AndroidKeystoreDatabaseKeyManager(
    private val context: Context,
    private val keyAlias: String = "mango_db_master_key"
) : DatabaseKeyManager {

    override fun getOrCreatePassphrase(): ByteArray {
        val masterKey = MasterKey.Builder(context, keyAlias)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val prefs = EncryptedSharedPreferences.create(
            context,
            "mango_db_key_store",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val existingKey = prefs.getString("db_passphrase_b64", null)
        if (existingKey != null) {
            return Base64.decode(existingKey, Base64.NO_WRAP)
        }

        val newPassphrase = ByteArray(32).also { SecureRandom().nextBytes(it) }
        prefs.edit().putString("db_passphrase_b64", Base64.encodeToString(newPassphrase, Base64.NO_WRAP)).apply()
        return newPassphrase
    }

    override fun clearPassphrase() {
        // Solo para testing; en producción nunca se limpia la clave
        context.getSharedPreferences("mango_db_key_store", Context.MODE_PRIVATE).edit().clear().apply()
    }
}
```

---

#### T2.3 — Crear `MangoDatabase.kt`

```kotlin
@Database(
    entities = [],
    version = 1,
    exportSchema = true,
    autoMigrations = []
)
abstract class MangoDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "mango_store.db"
        const val DATABASE_VERSION = 1
    }
}
```

---

#### T2.4 — Crear `DatabaseModule.kt` (Hilt)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDatabaseKeyManager(@ApplicationContext ctx: Context): DatabaseKeyManager =
        AndroidKeystoreDatabaseKeyManager(ctx)

    @Provides @Singleton
    fun provideMangoDatabase(
        @ApplicationContext ctx: Context,
        keyManager: DatabaseKeyManager
    ): MangoDatabase {
        val passphrase = keyManager.getOrCreatePassphrase()
        val factory = SupportFactory(passphrase)
        return Room.databaseBuilder(ctx, MangoDatabase::class.java, MangoDatabase.DATABASE_NAME)
            .openHelperFactory(factory)
            .addMigrations(*DatabaseMigrations.all)
            .build()
    }
}
```

---

#### T2.5 — Tests de `:core:database`

**`DatabaseKeyManagerTest.kt`** (Robolectric):
- `creates_new_passphrase_on_first_call`
- `returns_same_passphrase_on_subsequent_calls`
- `passphrase_has_expected_length_32_bytes`
- `clear_passphrase_removes_stored_key` (test only)

**`MangoDatabasIntegrationTest.kt`** (InMemoryRoom + Robolectric):
- `database_opens_successfully_with_test_passphrase`
- `database_version_matches_expected`

> **Nota**: Los tests exhaustivos de CRUD (constraint violation, ReadFailed, WriteFailed) se harán en los módulos de feature que añadan sus DAOs (ETAPA 3+). Los tests de esta ETAPA verifican que la infraestructura base funciona.

---

#### T2.6 — Compilar `:core:database`

```bash
./gradlew :core:database:assembleDebug
./gradlew :core:database:testDebugUnitTest
```

---

### Fase 3 — `:core:datastore` (Priority: P3)

#### T3.1 — Actualizar `build.gradle.kts` de `:core:datastore`

```kotlin
plugins {
    id("mango.android.library")
    id("mango.android.hilt")
}

android {
    namespace = "com.mango.fakestore.core.datastore"
}

dependencies {
    implementation(project(":core:common"))
    
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.google.tink.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)
    testImplementation(libs.mockk)
}
```

---

#### T3.2 — Crear modelos: `SessionData.kt`, `UserPreferences.kt`, `AppTheme.kt`

(Ver data-model.md para los detalles completos de estos tipos.)

---

#### T3.3 — Crear `TinkEncryption.kt`

```kotlin
internal class TinkEncryption(context: Context) {
    private val keysetHandle: KeysetHandle by lazy {
        AndroidKeysetManager.Builder()
            .withSharedPref(context, "mango_tink_keyset", "mango_tink_prefs")
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri("android-keystore://mango_tink_master_key")
            .build()
            .keysetHandle
    }
    private val aead: Aead by lazy { keysetHandle.getPrimitive(Aead::class.java) }

    fun encrypt(plaintext: String): String {
        val encrypted = aead.encrypt(plaintext.toByteArray(Charsets.UTF_8), null)
        return Base64.encodeToString(encrypted, Base64.NO_WRAP)
    }

    fun decrypt(ciphertext: String): String {
        val decoded = Base64.decode(ciphertext, Base64.NO_WRAP)
        return String(aead.decrypt(decoded, null), Charsets.UTF_8)
    }
}
```

---

#### T3.4 — Crear `MangoDataStore.kt` (interfaz e implementación)

**Interfaz** (ver contracts/public-api.md para el contrato completo).

**Implementación** `MangoDataStoreImpl`:
- Usa `PreferenceDataStoreFactory.create(corruptionHandler, scope, path)`.
- `CorruptionHandler { exception -> emptyPreferences() }` que loguea (sin PII) antes de devolver vacío.
- `saveSession()` cifra tokens con `TinkEncryption` antes de escribir.
- `sessionFlow` descifra tokens al leer.
- `clearSession()` borra únicamente las claves de tokens, no las preferencias.

---

#### T3.5 — Crear `DataStoreModule.kt` (Hilt)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides @Singleton
    fun provideTinkEncryption(@ApplicationContext ctx: Context): TinkEncryption =
        TinkEncryption(ctx)

    @Provides @Singleton
    fun provideMangoDataStore(
        @ApplicationContext ctx: Context,
        tink: TinkEncryption,
        dispatchers: AppDispatchers
    ): MangoDataStore =
        MangoDataStoreImpl(ctx, tink, dispatchers)
}
```

---

#### T3.6 — Tests de `:core:datastore`

**`MangoDataStoreTest.kt`** (TestDataStore en memoria + `runTest`):
- `save_and_read_session_returns_correct_data`
- `session_flow_emits_empty_initially`
- `clear_session_removes_tokens_preserves_preferences`
- `save_preferences_theme_persists`
- `session_is_authenticated_when_access_token_present`
- `session_is_not_authenticated_when_no_token`
- `corruption_handler_resets_to_empty_session`

---

#### T3.7 — Compilar `:core:datastore`

```bash
./gradlew :core:datastore:assembleDebug
./gradlew :core:datastore:testDebugUnitTest
```

---

### Fase 4 — Compilación global y validación

#### T4.1 — Compilación completa

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

#### T4.2 — Skills de validación

```
/validar-arquitectura
/validar-manejo-errores
/crear-pruebas-unitarias   (por cada módulo)
/documentar-modulo         (core:network, core:database, core:datastore)
```

#### T4.3 — Criterios de cierre de ETAPA

- `validar-arquitectura` → 0 violaciones.
- `validar-manejo-errores` → 0 violaciones.
- `./gradlew assembleDebug` → BUILD SUCCESSFUL.
- `./gradlew :core:network:testDebugUnitTest` → todos los tests verdes.
- `./gradlew :core:database:testDebugUnitTest` → todos los tests verdes.
- `./gradlew :core:datastore:testDebugUnitTest` → todos los tests verdes.
- Cobertura `:core:network` ≥ 80%, `:core:database` ≥ 80%, `:core:datastore` ≥ 70%.
- Documentación en español en `core/network/docs/`, `core/database/docs/`, `core/datastore/docs/`.
- PR en español con la descripción de los tres módulos.

---

## Riesgos y mitigaciones

| Riesgo | Impacto | Mitigación |
|--------|---------|-----------|
| Certificado `fakestoreapi.com` rota entre sesiones | Alto — app sin conexión | Documentar en `docs/seguridad.md`; usar Remote Config en producción real para distribuir nuevos pins |
| Flavors en `:core:network` entran en conflicto con los de `:app` | Medio — error de build | Usar `missingDimensionStrategy` en `:core:network` si los `flavorDimensions` no coinciden |
| SQLCipher incompatible con Room Testing (`InMemoryDatabaseBuilder`) | Medio — tests fallan | Usar `JournalMode.WRITE_AHEAD_LOGGING` + factory stub en tests; la BD en memoria no aplica SQLCipher |
| Tink en Android requiere `TinkConfig.registerAll()` en `Application.onCreate()` | Bajo — crash en primer uso | Incluir en `DataStoreModule` una llamada a `TinkConfig.registerAll()` en `@Provides @Singleton` |
| `EncryptedSharedPreferences` en alpha (`1.1.0-alpha06`) puede cambiar | Bajo — breaking change en actualización | Encapsular detrás de `DatabaseKeyManager` para facilitar sustitución |

---

## Decisiones de arquitectura (para `docs/adr/`)

- **ADR-0003** (si no existe): "Uso de SQLCipher para encriptación de Room" — registrar la decisión, alternativas (androidx.security-crypto Room integration, sin encriptación), y el tradeoff aceptado (overhead de performance ~5%).
- **ADR-0004** (si no existe): "DataStore encriptado con Tink a nivel de valores" — registrar la decisión vs Proto DataStore completo y vs EncryptedSharedPreferences.
