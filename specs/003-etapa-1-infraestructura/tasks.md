# Tareas: Módulos Core de Infraestructura (ETAPA 1 — Sub-etapas 1.5 a 1.7)

**Input**: `specs/003-etapa-1-infraestructura/` (plan.md, spec.md, research.md, data-model.md, contracts/)  
**Prerequisitos**: `:core:common` y `:core:error` ya implementados (ETAPA 1.1–1.4). `safeApiCall`, `safeDbCall`, `NetworkErrorMapper`, `DatabaseErrorMapper` ya existen en `:core:error`.  
**Tests**: Requeridos explícitamente por §7.14 del Prompt Maestro — incluidos en cada historia.  
**Ruta base**: `repository/android-fake-store-app/`

## Formato: `[ID] [P?] [Story?] Descripción con ruta de archivo`

- **[P]**: Ejecutable en paralelo (distinto archivo, sin dependencias incompletas)
- **[US1]**: Historia 1 — Comunicación resiliente con la API (`:core:network`)
- **[US2]**: Historia 2 — Persistencia local encriptada (`:core:database`)
- **[US3]**: Historia 3 — Almacenamiento encriptado de tokens y preferencias (`:core:datastore`)

---

## Fase 1: Setup (Infraestructura compartida)

**Propósito**: Verificar prerequisites y preparar los `build.gradle.kts` de los tres módulos antes de cualquier código fuente.

- [ ] T001 Verificar que `:core:network`, `:core:database` y `:core:datastore` están declarados en `core/network/build.gradle.kts`, `core/database/build.gradle.kts`, `core/datastore/build.gradle.kts` y en `settings.gradle.kts`
- [ ] T002 [P] Actualizar `core/network/build.gradle.kts`: añadir plugins `mango.android.hilt`, `kotlin.plugin.serialization`; dependencias `retrofit-core`, `retrofit-kotlinx-serialization`, `okhttp-core`, `okhttp-logging`, `kotlinx-serialization-json`, `arrow-core`, `project(:core:error)`, `project(:core:common)`; test deps `mockwebserver`, `kotlinx-coroutines-test`, `mockk`, `truth`; `buildConfigField` BASE_URL por flavor; `buildFeatures { buildConfig = true }`
- [ ] T003 [P] Actualizar `core/database/build.gradle.kts`: añadir plugins `mango.android.hilt`, `com.google.devtools.ksp`; dependencias `room-runtime`, `room-ktx`, `ksp(room-compiler)`, `sqlcipher-android`, `security-crypto`, `project(:core:error)`, `project(:core:common)`; test deps `room-testing`, `kotlinx-coroutines-test`, `mockk`, `truth`, `robolectric`; configurar `javaCompileOptions` con `room.schemaLocation`
- [ ] T004 [P] Actualizar `core/datastore/build.gradle.kts`: añadir plugins `mango.android.hilt`; dependencias `datastore-preferences`, `google-tink-android`, `androidx-core-ktx`, `project(:core:common)`; test deps `kotlinx-coroutines-test`, `truth`, `mockk`

**Checkpoint**: Los tres `build.gradle.kts` configurados → `./gradlew :core:network:dependencies :core:database:dependencies :core:datastore:dependencies` sin errores de resolución.

---

## Fase 2: Fundamentos (Prerequisitos bloqueantes)

**Propósito**: Archivos de configuración estáticos y AndroidManifest que deben existir antes de compilar.

⚠️ **CRÍTICO**: Las fases de historias de usuario no pueden comenzar hasta completar esta fase.

- [ ] T005 [P] Crear `core/network/src/main/AndroidManifest.xml` con `package="com.mango.fakestore.core.network"` (sin actividades, solo namespace)
- [ ] T006 [P] Crear `core/network/src/main/res/xml/network_security_config.xml` con: `cleartextTrafficPermitted="false"`, pin primario `sha256/dSxOWQR+hD1HkfYEk0y+JuXzHrLTjhVPXDzGRsbO7oI=` para `fakestoreapi.com`, pin backup placeholder, `debug-overrides` que permite certificados de usuario en debug
- [ ] T007 [P] Actualizar `app/src/main/AndroidManifest.xml` añadiendo `android:networkSecurityConfig="@xml/network_security_config"` en la etiqueta `<application>`
- [ ] T008 [P] Crear `core/database/src/main/AndroidManifest.xml` con `package="com.mango.fakestore.core.database"`
- [ ] T009 [P] Crear `core/datastore/src/main/AndroidManifest.xml` con `package="com.mango.fakestore.core.datastore"`

**Checkpoint**: `./gradlew :core:network:assembleDebug :core:database:assembleDebug :core:datastore:assembleDebug` compila (aunque los módulos estén vacíos de lógica).

---

## Fase 3: Historia de Usuario 1 — Comunicación resiliente con la API (P1) 🎯 MVP

**Goal**: `:core:network` completamente funcional — Retrofit pre-configurado, certificate pinning, retry con backoff, `ConnectivityObserver` reactivo y `safeRetrofitCall` tipado que mapea todos los errores a `DomainError.Network.*`.

**Prueba independiente**: Ejecutar `./gradlew :core:network:testDebugUnitTest` — deben pasar todos los tests de `RetryInterceptorTest`, `ConnectivityObserverTest` y `SafeRetrofitCallExtTest` sin ningún módulo de feature presente.

### Tests de US1 — escribir ANTES de la implementación ⚠️

- [ ] T010 [P] [US1] Crear `core/network/src/test/kotlin/com/mango/fakestore/core/network/interceptor/RetryInterceptorTest.kt` con 7 casos: `retries_on_500_with_backoff`, `does_not_retry_on_400`, `does_not_retry_on_401`, `retries_on_408`, `retries_on_429_with_retry_after`, `retries_on_503_eventually_succeeds`, `does_not_retry_non_idempotent_post` usando `MockWebServer`
- [ ] T011 [P] [US1] Crear `core/network/src/test/kotlin/com/mango/fakestore/core/network/connectivity/ConnectivityObserverTest.kt` con 4 casos: `emits_connected_when_validated`, `emits_disconnected_when_lost`, `emits_unavailable_when_no_network`, `current_status_reflects_latest` usando Robolectric + MockK
- [ ] T012 [P] [US1] Crear `core/network/src/test/kotlin/com/mango/fakestore/core/network/ext/SafeRetrofitCallExtTest.kt` con 8 casos: `maps_401_to_unauthorized`, `maps_403_to_forbidden`, `maps_404_to_not_found`, `maps_500_to_server_error`, `maps_socket_timeout_to_timeout`, `maps_unknown_host_to_no_connection`, `maps_serialization_exception_to_parsing`, `rethrows_cancellation_exception` — verificar que estos tests FALLAN antes de implementar

### Implementación de US1

- [ ] T013 [US1] Crear `core/network/src/main/kotlin/com/mango/fakestore/core/network/config/NetworkConfig.kt`: data class con `baseUrl`, `connectTimeoutSeconds=10`, `readTimeoutSeconds=30`, `writeTimeoutSeconds=30`, `maxRetries=3`, `retryBaseDelayMs=500`, `retryMaxDelayMs=10_000`, `certificatePins=listOf("sha256/dSxOWQR+hD1HkfYEk0y+JuXzHrLTjhVPXDzGRsbO7oI=", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")` (depende de T002)
- [ ] T014 [P] [US1] Crear `core/network/src/main/kotlin/com/mango/fakestore/core/network/connectivity/ConnectivityStatus.kt`: `sealed interface ConnectivityStatus` con `Connected`, `Disconnected`, `Unavailable` como `data object`
- [ ] T015 [P] [US1] Crear `core/network/src/main/kotlin/com/mango/fakestore/core/network/reporter/NetworkErrorReporter.kt`: `interface NetworkErrorReporter { fun reportNetworkError(error: DomainError.Network, context: Map<String, String> = emptyMap()) }` y `class NoOpNetworkErrorReporter` en el mismo paquete
- [ ] T016 [US1] Crear `core/network/src/main/kotlin/com/mango/fakestore/core/network/connectivity/ConnectivityObserver.kt`: `interface ConnectivityObserver { val statusFlow: Flow<ConnectivityStatus>; fun currentStatus(): ConnectivityStatus }` (depende de T014)
- [ ] T017 [US1] Crear `core/network/src/main/kotlin/com/mango/fakestore/core/network/connectivity/ConnectivityObserverImpl.kt`: implementación con `ConnectivityManager.registerNetworkCallback` en `callbackFlow { }`, emitir estado inicial en suscripción, usar `NET_CAPABILITY_VALIDATED`, exponer como `SharedFlow(replay=1)` (depende de T016)
- [ ] T018 [US1] Crear `core/network/src/main/kotlin/com/mango/fakestore/core/network/interceptor/RetryInterceptor.kt`: `class RetryInterceptor(maxRetries=3, baseDelayMs=500L, maxDelayMs=10_000L) : Interceptor` con backoff exponencial + jitter uniforme `(-300..300)ms`, reintenta en 5xx, 408, 429 con `Retry-After`; NO reintenta 4xx (excepto 408/429) ni POST sin `Idempotency-Key` (depende de T013)
- [ ] T019 [US1] Crear `core/network/src/main/kotlin/com/mango/fakestore/core/network/interceptor/LoggingInterceptorFactory.kt`: función `fun create(): HttpLoggingInterceptor` que configura `BODY` level; solo se usa si `BuildConfig.DEBUG == true`
- [ ] T020 [US1] Crear `core/network/src/main/kotlin/com/mango/fakestore/core/network/ssl/CertificatePinnerFactory.kt`: `object CertificatePinnerFactory { fun create(config: NetworkConfig): CertificatePinner }` que añade todos los pins de `config.certificatePins` para `fakestoreapi.com` (depende de T013)
- [ ] T021 [US1] Crear `core/network/src/main/kotlin/com/mango/fakestore/core/network/ext/SafeRetrofitCallExt.kt`: función `suspend fun <T> safeRetrofitCall(block: suspend () -> T): Either<DomainError, T>` que captura `CancellationException` (rethrow), `retrofit2.HttpException` (mapa HTTP code inline: 401→Unauthorized, 403→Forbidden, 404→NotFound, 5xx→Server(code), else→NoConnection) y `Throwable` (delega a `NetworkErrorMapper().map(e)`) (depende de T002, T013)
- [ ] T022 [US1] Crear `core/network/src/main/kotlin/com/mango/fakestore/core/network/di/NetworkModule.kt`: `@Module @InstallIn(SingletonComponent::class) object NetworkModule` con `@Provides @Singleton` para `NetworkConfig` (lee `BuildConfig.BASE_URL`), `OkHttpClient` (con `RetryInterceptor`, `CertificatePinner`, logging en debug), `Retrofit` (con `kotlinxSerializationConverter(Json { ignoreUnknownKeys = true })`), `ConnectivityObserver` (bind impl), `NetworkErrorReporter` (no-op) (depende de T013–T021)

**Checkpoint**: `./gradlew :core:network:testDebugUnitTest` — todos los tests de T010–T012 deben pasar en verde.

---

## Fase 4: Historia de Usuario 2 — Persistencia local encriptada (P2)

**Goal**: `:core:database` completamente funcional — `MangoDatabase` abstracta con SQLCipher, `DatabaseKeyManager` que gestiona la passphrase via Android Keystore, `DatabaseModule` Hilt. Los módulos de feature podrán añadir sus entidades y DAOs en ETAPA 3+.

**Prueba independiente**: Ejecutar `./gradlew :core:database:testDebugUnitTest` — deben pasar todos los tests de `DatabaseKeyManagerTest` y `MangoDatabaseIntegrationTest` sin ningún módulo de feature presente.

### Tests de US2 — escribir ANTES de la implementación ⚠️

- [ ] T023 [P] [US2] Crear `core/database/src/test/kotlin/com/mango/fakestore/core/database/key/DatabaseKeyManagerTest.kt` con 4 casos (Robolectric): `creates_passphrase_on_first_call`, `returns_same_passphrase_on_subsequent_calls`, `passphrase_has_32_bytes`, `clear_removes_stored_key`
- [ ] T024 [P] [US2] Crear `core/database/src/test/kotlin/com/mango/fakestore/core/database/MangoDatabaseIntegrationTest.kt` con 2 casos (Robolectric + `InMemoryDatabaseBuilder`): `database_opens_successfully`, `database_version_is_1`; usar passphrase de test fija (no Keystore real en tests) — verificar que estos tests FALLAN antes de implementar

### Implementación de US2

- [ ] T025 [US2] Crear `core/database/src/main/kotlin/com/mango/fakestore/core/database/key/DatabaseKeyManager.kt`: `interface DatabaseKeyManager { fun getOrCreatePassphrase(): ByteArray; fun clearPassphrase() }` (depende de T003)
- [ ] T026 [US2] Crear `core/database/src/main/kotlin/com/mango/fakestore/core/database/key/AndroidKeystoreDatabaseKeyManager.kt`: implementación con `MasterKey.Builder(...).setKeyScheme(MasterKey.KeyScheme.AES256_GCM)`, `EncryptedSharedPreferences` para guardar la passphrase de 32 bytes generada con `SecureRandom`, decodificación en llamadas subsiguientes; `clearPassphrase()` borra las SharedPreferences (depende de T025)
- [ ] T027 [US2] Crear `core/database/src/main/kotlin/com/mango/fakestore/core/database/MangoDatabase.kt`: `@Database(entities=[], version=1, exportSchema=true) abstract class MangoDatabase : RoomDatabase()` con companion `DATABASE_NAME = "mango_store.db"`, `DATABASE_VERSION = 1` (depende de T003)
- [ ] T028 [US2] Crear `core/database/src/main/kotlin/com/mango/fakestore/core/database/migration/DatabaseMigrations.kt`: `object DatabaseMigrations { val all: Array<Migration> = emptyArray() }` — infraestructura lista para añadir migraciones en ETAPA 3+ (depende de T003)
- [ ] T029 [US2] Crear `core/database/src/main/kotlin/com/mango/fakestore/core/database/di/DatabaseModule.kt`: `@Module @InstallIn(SingletonComponent::class) object DatabaseModule` con `@Provides @Singleton` para `DatabaseKeyManager` (bind `AndroidKeystoreDatabaseKeyManager`) y `MangoDatabase` con builder que: (1) aplica `openHelperFactory(SupportFactory(passphrase))`, (2) añade `*DatabaseMigrations.all`, (3) aplica `fallbackToDestructiveMigration()` SOLO si `BuildConfig.FLAVOR == "dev"` — en flavors `staging` y `prod` omitir este flag para que fallos de migración sean explícitos y no silenciosos (depende de T025–T028)

**Checkpoint**: `./gradlew :core:database:testDebugUnitTest` — todos los tests de T023–T024 deben pasar en verde.

---

## Fase 5: Historia de Usuario 3 — Almacenamiento encriptado de tokens y preferencias (P3)

**Goal**: `:core:datastore` completamente funcional — `MangoDataStore` con Preferences DataStore + cifrado Tink AES-256-GCM para tokens, `clearSession()` seguro, `sessionFlow` y `preferencesFlow` reactivos.

**Prueba independiente**: Ejecutar `./gradlew :core:datastore:testDebugUnitTest` — deben pasar todos los tests de `MangoDataStoreTest` (incluyendo el de corrupción) sin ningún módulo de feature presente.

### Tests de US3 — escribir ANTES de la implementación ⚠️

- [ ] T030 [P] [US3] Crear `core/datastore/src/test/kotlin/com/mango/fakestore/core/datastore/MangoDataStoreTest.kt` con 7 casos (`runTest`, DataStore in-memory via `preferencesDataStoreFile(tmpFile)`): `save_and_read_session_returns_correct_data`, `session_flow_emits_empty_initially`, `clear_session_removes_tokens_preserves_preferences`, `save_preferences_theme_persists`, `is_authenticated_when_access_token_present`, `is_not_authenticated_when_no_token`, `corruption_handler_resets_to_empty_session` — verificar que estos tests FALLAN antes de implementar

### Implementación de US3

- [ ] T031 [P] [US3] Crear `core/datastore/src/main/kotlin/com/mango/fakestore/core/datastore/model/AppTheme.kt`: `enum class AppTheme { LIGHT, DARK, SYSTEM }` (depende de T004)
- [ ] T032 [P] [US3] Crear `core/datastore/src/main/kotlin/com/mango/fakestore/core/datastore/model/SessionData.kt`: `data class SessionData(val accessToken: String?, val refreshToken: String?, val userId: String?) { val isAuthenticated: Boolean get() = accessToken != null; companion object { val Empty = SessionData(null, null, null) } }` (depende de T004)
- [ ] T033 [P] [US3] Crear `core/datastore/src/main/kotlin/com/mango/fakestore/core/datastore/model/UserPreferences.kt`: `data class UserPreferences(val theme: AppTheme = AppTheme.SYSTEM, val notificationsEnabled: Boolean = true)` (depende de T031)
- [ ] T034 [US3] Crear `core/datastore/src/main/kotlin/com/mango/fakestore/core/datastore/internal/PreferencesKeys.kt`: `internal object PreferencesKeys { val ACCESS_TOKEN = stringPreferencesKey("access_token_enc"); val REFRESH_TOKEN = stringPreferencesKey("refresh_token_enc"); val USER_ID = stringPreferencesKey("user_id_enc"); val THEME = stringPreferencesKey("app_theme"); val NOTIFICATIONS = booleanPreferencesKey("notifications_enabled") }` (depende de T032)
- [ ] T035 [US3] Crear `core/datastore/src/main/kotlin/com/mango/fakestore/core/datastore/crypto/TinkEncryption.kt`: clase con `AndroidKeysetManager.Builder()` usando alias `"mango_tink_master_key"`, plantilla `"AES256_GCM"`, almacenamiento en `"mango_tink_prefs"`. Métodos `encrypt(plaintext: String): String` (→ Base64 del ciphertext Tink) y `decrypt(ciphertext: String): String` (← Base64 del ciphertext). Llamar `TinkConfig.registerAll()` en el bloque de inicialización lazy. (depende de T004)
- [ ] T036 [US3] Crear `core/datastore/src/main/kotlin/com/mango/fakestore/core/datastore/MangoDataStore.kt`: `interface MangoDataStore { val sessionFlow: Flow<SessionData>; val preferencesFlow: Flow<UserPreferences>; suspend fun saveSession(data: SessionData); suspend fun clearSession(); suspend fun savePreferences(prefs: UserPreferences) }` (depende de T032, T033)
- [ ] T037 [US3] Crear `core/datastore/src/main/kotlin/com/mango/fakestore/core/datastore/MangoDataStoreImpl.kt`: implementación con `PreferenceDataStoreFactory.create(corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }, scope = CoroutineScope(dispatchers.io + SupervisorJob()), produceFile = { ctx.dataStoreFile("mango_preferences.preferences_pb") })`; `saveSession()` cifra tokens con `tink.encrypt()` antes de escribir; `sessionFlow` descifra tokens en el map; `clearSession()` borra solo ACCESS_TOKEN, REFRESH_TOKEN, USER_ID con `dataStore.edit { it.remove(ACCESS_TOKEN); ... }` (depende de T034, T035, T036)
- [ ] T038 [US3] Crear `core/datastore/src/main/kotlin/com/mango/fakestore/core/datastore/di/DataStoreModule.kt`: `@Module @InstallIn(SingletonComponent::class) object DataStoreModule` con `@Provides @Singleton` para `TinkEncryption` y `MangoDataStore` (bind `MangoDataStoreImpl`) (depende de T035–T037)

**Checkpoint**: `./gradlew :core:datastore:testDebugUnitTest` — todos los tests de T030 deben pasar en verde.

---

## Fase 6: Pulido, Compilación Global y Validación

**Propósito**: Verificación integral de los tres módulos, validaciones de arquitectura y apertura del PR.

- [ ] T039 Compilar el proyecto completo: `./gradlew assembleDebug` — BUILD SUCCESSFUL sin warnings de deprecación
- [ ] T040 [P] Ejecutar tests de los tres módulos: `./gradlew :core:network:testDebugUnitTest :core:database:testDebugUnitTest :core:datastore:testDebugUnitTest` — todos en verde
- [ ] T041 [P] Ejecutar skill `validar-arquitectura` — resolver todas las violaciones antes de continuar
- [ ] T042 [P] Ejecutar skill `validar-manejo-errores` — resolver todas las violaciones antes de continuar
- [ ] T043 Ejecutar skill `crear-pruebas-unitarias` para `:core:network` — verificar cobertura ≥ 80% y completar ramas faltantes si las hay
- [ ] T044 Ejecutar skill `crear-pruebas-unitarias` para `:core:database` — verificar cobertura ≥ 80%
- [ ] T045 Ejecutar skill `crear-pruebas-unitarias` para `:core:datastore` — verificar cobertura ≥ 70%
- [ ] T046 Ejecutar skill `documentar-modulo` para `:core:network` → genera `core/network/docs/{modulo,diseno,pruebas,errores}.md`
- [ ] T047 Ejecutar skill `documentar-modulo` para `:core:database` → genera `core/database/docs/{modulo,diseno,pruebas,errores}.md`
- [ ] T048 Ejecutar skill `documentar-modulo` para `:core:datastore` → genera `core/datastore/docs/{modulo,diseno,pruebas,errores}.md`
- [ ] T049 Abrir PR en español con título `feat(core): implementa core:network, core:database y core:datastore (ETAPA 1.5–1.7)` — incluir descripción con: módulos implementados, pin de certificado, decisiones de arquitectura, cobertura por módulo, link al plan.md

---

## Dependencias y Orden de Ejecución

### Dependencias entre fases

- **Fase 1 (Setup)**: Sin dependencias — comenzar inmediatamente; T002, T003, T004 son paralelas
- **Fase 2 (Fundamentos)**: Depende de Fase 1 completa; T005–T009 son todas paralelas entre sí
- **Fase 3 (US1)**: Depende de Fase 2 completa; tests T010–T012 se escriben antes que la impl T013–T022
- **Fase 4 (US2)**: Depende de Fase 2 completa; PUEDE ejecutarse en paralelo con Fase 3 (archivos distintos)
- **Fase 5 (US3)**: Depende de Fase 2 completa; PUEDE ejecutarse en paralelo con Fases 3 y 4
- **Fase 6 (Validación)**: Depende de Fases 3, 4 y 5 completas

### Dependencias entre historias de usuario

- **US1 (P1)**: Sin dependencias en US2 ni US3
- **US2 (P2)**: Sin dependencias en US1 ni US3
- **US3 (P3)**: Sin dependencias en US1 ni US2

Las tres historias de usuario son **completamente independientes** entre sí y pueden implementarse en paralelo tras completar la Fase 2.

### Dependencias internas de US1 (`:core:network`)

```
T002 (build.gradle.kts) → T013 (NetworkConfig) → T018, T020, T021, T022
T005 (AndroidManifest)  → [necesario para compile]
T006 (network_security_config.xml) → T007 (app AndroidManifest)
T014 (ConnectivityStatus) → T016 (interfaz) → T017 (impl)
T013, T014, T015, T016   → T022 (NetworkModule — ensambla todo)
Tests T010–T012          → escribir ANTES de T018, T017, T021
```

### Dependencias internas de US2 (`:core:database`)

```
T003 (build.gradle.kts) → T025–T029
T025 (interfaz KeyManager) → T026 (impl) → T029 (Module)
T027 (MangoDatabase) → T029 (Module)
T028 (Migrations) → T029 (Module)
Tests T023–T024          → escribir ANTES de T026, T027
```

### Dependencias internas de US3 (`:core:datastore`)

```
T004 (build.gradle.kts) → T031–T038
T031 (AppTheme) → T033 (UserPreferences)
T032 (SessionData) → T034 (PreferencesKeys) → T037 (Impl)
T035 (TinkEncryption) → T037 (Impl) → T038 (Module)
T036 (interfaz) → T037 (Impl) → T038 (Module)
Test T030                → escribir ANTES de T037
```

---

## Ejemplo de Paralelismo: Implementación simultánea de los 3 módulos

```bash
# Fase 1 — Paralelo:
Tarea: "Actualizar build.gradle.kts de :core:network (T002)"
Tarea: "Actualizar build.gradle.kts de :core:database (T003)"
Tarea: "Actualizar build.gradle.kts de :core:datastore (T004)"

# Fase 2 — Paralelo:
Tarea: "Crear AndroidManifest.xml de :core:network (T005)"
Tarea: "Crear AndroidManifest.xml de :core:database (T008)"
Tarea: "Crear AndroidManifest.xml de :core:datastore (T009)"
Tarea: "Crear network_security_config.xml (T006)"

# Fases 3, 4, 5 — Pueden ejecutarse en paralelo con 3 desarrolladores:
Dev A: Fase 3 completa (US1 — :core:network)
Dev B: Fase 4 completa (US2 — :core:database)
Dev C: Fase 5 completa (US3 — :core:datastore)
```

---

## Estrategia de Implementación

### MVP Primero (Historia de Usuario 1 solamente)

1. Completar Fase 1: Setup (T001–T004)
2. Completar Fase 2: Fundamentos (T005–T009)
3. Completar Fase 3: US1 — `:core:network` (T010–T022)
4. **PARAR Y VALIDAR**: `./gradlew :core:network:testDebugUnitTest` → verde
5. Continuar con US2 y US3

### Entrega incremental

1. Setup + Fundamentos → infraestructura lista (Fases 1–2)
2. `:core:network` funcional → todos los features pueden hacer llamadas HTTP (Fase 3)
3. `:core:database` funcional → todos los features pueden persistir datos encriptados (Fase 4)
4. `:core:datastore` funcional → el módulo de auth puede guardar tokens seguros (Fase 5)
5. Validación global + PR (Fase 6)

---

## Notas

- `[P]` = tareas sobre archivos distintos, sin dependencias entre sí — se pueden ejecutar en paralelo
- `[USx]` = trazabilidad directa a la historia de usuario del spec.md
- Escribir los tests **ANTES** de la implementación; verificar que fallen primero
- Hacer commit tras cada Fase o grupo lógico de tareas
- Detener en cualquier checkpoint para validar la historia independientemente
- Evitar: nombres de archivo ambiguos, dependencias cruzadas entre US1/US2/US3
- El pin backup `sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=` en T006 y T013 DEBE documentarse como placeholder en el código con un comentario explicando cómo obtener el pin real con `openssl s_client -connect fakestoreapi.com:443 | openssl x509 -pubkey -noout | openssl pkey -pubin -outform DER | openssl dgst -sha256 -binary | openssl enc -base64`
