# Guía rápida — Mango Fake Store · Chuleta de entrevista

> Repasa esto en los 5 minutos previos a la sala. Para el detalle completo, ver `entrevista-tecnica.md`.

---

## Arquitectura en una frase

> **Clean Architecture + MVVM + multi-módulo Gradle**: la UI solo conoce `UiState`, el dominio devuelve `Either<DomainError, T>` y los datos viven en Room (cifrado con SQLCipher) o DataStore (cifrado con Tink).

---

## El flujo de datos de punta a punta

```
Retrofit API
    ↓ safeApiCall { ... }          ← barrera: convierte excepciones en DomainError
    ↓ Either<DomainError, ProductoDto>
    ↓ ProductoMapper.toDomain()    ← data layer: DTO → modelo de dominio
Room DAO (caché)
    ↓ ObtenerProductos (UseCase)   ← domain: puro Kotlin, sin Android
    ↓ Either<DomainError, List<Producto>>
    ↓ ProductosViewModel           ← presentation: collect + map → UiState
    ↓ StateFlow<ProductosUiState>
ProductosScreen (Composable)       ← UI: solo renderiza estado
```

---

## Módulos = capas físicas

| Submodulo | Qué contiene | Qué NO puede importar |
|-----------|-------------|----------------------|
| `:api` | typealiases, contratos públicos | nada del mismo feature |
| `:domain` | UseCases, interfaces repo, modelos | `androidx.*`, `:data` |
| `:data` | Room DAOs, Retrofit services, mappers | `:presentation` |
| `:presentation` | ViewModels, Composables | `:data` directamente |

Los features se comunican **solo** a través de `:api`:
```
:features:profile:presentation → :features:favorites:api → :features:favorites:domain
```

---

## Build Flavors

| Flavor | Certificate pinning | Logs | IntegrityCheck |
|--------|--------------------|----- |----------------|
| `dev` | OFF | DEBUG (Timber) | LOG |
| `staging` | ON | WARN | WARN |
| `prod` | ON | SILENT (NoOp) | BLOCK |

```kotlin
// build.gradle.kts
productFlavors {
    create("dev")     { buildConfigField("String", "LOG_LEVEL", "\"DEBUG\"") }
    create("staging") { buildConfigField("String", "LOG_LEVEL", "\"WARN\"")  }
    create("prod")    { buildConfigField("String", "LOG_LEVEL", "\"SILENT\"") }
}
```

---

## Estados de UI

Cada pantalla tiene un `sealed interface XxxUiState`:

```kotlin
sealed interface ProductosUiState {
    object Loading : ProductosUiState        // Shimmer skeleton
    data class Content(...) : ProductosUiState  // Lista de productos
    object Empty : ProductosUiState          // MangoEmptyState
    data class Error(val uiError: UiError) : ProductosUiState  // MangoErrorState
}
```

El ViewModel emite en `StateFlow<ProductosUiState>`. El Composable hace `when (state)` y renderiza el componente Mango correspondiente.

---

## Manejo de errores (flujo completo)

```
Exception (red/BD)
    → safeApiCall / safeDbCall  →  DomainError.Network.Timeout
    → ObtenerProductos.invoke() →  Either.Left(DomainError)
    → ProductosViewModel        →  DomainErrorToUiErrorMapper
    → UiError(messageRes=R.string.error_red_tiempo, severity=Warning)
    → ProductosUiState.Error    →  MangoErrorState en pantalla
```

**Regla de oro**: La UI nunca lee `.message` de un `Throwable`. Solo conoce `UiError`.

---

## Hilt DI — cadena de inyección

```kotlin
// :core:network — @Provides crea el objeto
@Provides @Singleton
fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()...

// :features:products:data — @Binds enlaza interfaz → implementación
@Binds
abstract fun bindProductosRepository(
    impl: ProductosRepositoryImpl
): ProductosRepository

// :features:products:presentation — @HiltViewModel recibe el UseCase inyectado
@HiltViewModel
class ProductosViewModel @Inject constructor(
    private val obtenerProductos: ObtenerProductos
) : ViewModel()
```

**@Binds**: interfaz → implementación (cero código en tiempo de ejecución).  
**@Provides**: cuando necesitas construir manualmente (Retrofit, Room, etc.).

---

## Reactive programming

| Tipo | Cuándo usarlo |
|------|---------------|
| `StateFlow` | UI state persistente (el último valor siempre disponible) |
| `SharedFlow` | Efectos de un solo disparo (Snackbar, navegación) |
| `Flow<T>` | Streams de BD (Room devuelve `Flow<List<T>>`) |

**combine() en PerfilViewModel** (dos Flows → un UiState):
```kotlin
combine(
    obtenerPerfil(PERFIL_USER_ID),
    observarConteoFavoritos()
) { perfilEither, conteo ->
    perfilEither.fold(
        ifLeft  = { PerfilUiState.Error(mapper.map(it)) },
        ifRight = { perfil -> PerfilUiState.Content(perfil, conteo) }
    )
}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PerfilUiState.Loading)
```

---

## Persistencia

| Sistema | Dato | Cifrado |
|---------|------|---------|
| Room + SQLCipher | Productos, Favoritos | AES-256 (clave en Android Keystore) |
| DataStore + Tink | Token de sesión, preferencias | AES-256-GCM (clave en Android Keystore) |

**Single Source of Truth**: el ViewModel siempre lee de Room. La API solo actualiza Room; la UI nunca consume la respuesta de red directamente.

---

## Seguridad — cada medida y su amenaza

| Medida | Protege contra |
|--------|---------------|
| SQLCipher AES-256 | extracción del APK/backup sin pin |
| DataStore + Tink | tokens expuestos en `shared_prefs` plano |
| Android Keystore | clave de BD extraída fuera del dispositivo |
| Certificate pinning | MITM con cert de CA comprometida |
| FLAG_SECURE / SecureScreen | captura de pantalla y previews en task switcher |
| RootBeer + IntegrityChecker | ejecución en entorno rooteado / Frida / Xposed |
| R8 + ProGuard | ingeniería inversa del APK |

---

## Tests — comandos exactos

```bash
# Unit tests por módulo
./gradlew :core:common:testDebugUnitTest
./gradlew :core:error:testDebugUnitTest
./gradlew :core:network:testDevDebugUnitTest
./gradlew :features:products:domain:testDebugUnitTest
./gradlew :features:products:data:testDebugUnitTest
./gradlew :features:products:presentation:testDebugUnitTest   # Robolectric + Compose
./gradlew :features:favorites:domain:testDebugUnitTest
./gradlew :features:favorites:presentation:testDebugUnitTest  # Robolectric + Compose
./gradlew :features:profile:presentation:testDebugUnitTest    # Robolectric + Compose
./gradlew :app:testDevDebugUnitTest                          # Konsist incluido

# Snapshots (design-system)
./gradlew :core:design-system:recordPaparazziDebug
./gradlew :core:design-system:verifyPaparazziDebug
```

**Cobertura actual**: ~319 tests, todas las capas cubiertas.

**Robolectric + Compose**: los UI tests corren en JVM sin emulador. Requiere `isIncludeAndroidResources = true` en `build.gradle.kts` y la anotación `@RunWith(RobolectricTestRunner::class)`.

**Konsist**: verifica en tiempo de test que ningún UseCase importe de `.data.` y que todos tengan `@Inject`.

---

## CI/CD — pipeline de PR (6 jobs)

```
setup → lint → test → coverage → build → sonarcloud
```

- **Secretos requeridos**: `FIREBASE_GOOGLE_SERVICES_JSON`
- **Secretos opcionales**: `SONAR_TOKEN`, `GOOGLE_CREDENTIALS_JSON`
- El pipeline ejecuta `./gradlew :app:testDevDebugUnitTest` + Kover para cobertura

---

## Design System

- Todos los componentes prefijados con `Mango` (ej. `MangoButton`, `MangoTextField`).
- Los tokens (colores, tipografía) viven en `MangoTheme` — **no se usa `MaterialTheme` directamente** en las features.
- `MangoTextStyles` extiende `Typography` de Material3 con la fuente Mango.

---

## ADRs clave

| ADR | Decisión | Razón |
|-----|----------|-------|
| ADR-0001 | `Either<DomainError, T>` en vez de `Result<T>` | Arrow tiene `fold`, `flatMap`, `mapLeft` — más expresivo |
| ADR-0002 | Multi-módulo Gradle por feature | Compilación incremental, límites de API forzados |
| ADR-0003 | `ObservarConteoFavoritos` devuelve `Flow<Int>` (no Either) | Room COUNT(*) nunca falla — Either sería ruido |
| ADR-0004 | `:app` depende de `:features:*:data` y `:domain` | Wiring de Hilt necesita los @Module de data; excepción explícita ARQ-010 |
| ADR-0005 | Design System propio en vez de Material3 puro | Tokens de marca Mango aislados; fácil theming |

---

## Las 5 preguntas más probables

**1. ¿Por qué multi-módulo?**
> Cada módulo compila de forma incremental. Si cambio solo un Composable, Gradle solo recompila `:features:products:presentation`, no todo el proyecto. Además, los límites de `:api` me obligan a no acoplar features entre sí.

**2. Explica el flujo de un error de red.**
> La excepción `IOException` nace en Retrofit → la captura `safeApiCall` → devuelve `Either.Left(DomainError.Network.NoConnection)` → el UseCase la propaga → el ViewModel llama a `DomainErrorToUiErrorMapper` → obtiene `UiError(messageRes=R.string.error_red_sin_conexion, severity=Warning)` → emite `ProductosUiState.Error(uiError)` → el Composable muestra `MangoErrorState` con el texto localizado. La UI nunca ve ni una `Exception` ni un `String` hardcoded.

**3. ¿Cómo está cifrada la base de datos?**
> Room usa SQLCipher como `SupportFactory`. La clave AES-256 la genera `DatabaseKeyManager` con `KeyGenerator` de Android Keystore (`AndroidKeyStore` provider). La clave nunca sale del hardware del dispositivo (TEE o StrongBox). Cuando el usuario abre la app, `MangoDatabase.create()` pide la clave al Keystore y la pasa a `SupportFactory`. Si alguien extrae el archivo `.db` del APK/backup, no puede abrirlo sin el Keystore del dispositivo original.

**4. ¿Cómo funciona el contador de favoritos en Perfil?**
> `PerfilViewModel` hace `combine(obtenerPerfil(...), observarConteoFavoritos())`. Cada vez que el usuario agrega o quita un favorito, Room emite un nuevo valor en `Flow<Int>` desde `ObservarConteoFavoritos`. El `combine` une ese valor con el perfil y emite un nuevo `PerfilUiState.Content(perfil, conteo)`. No hay polling ni callbacks manuales; todo es reactivo.

**5. ¿Cómo están organizados los tests?**
> Tres niveles: (a) unit tests en `src/test/` — UseCases con MockK + Turbine, Repositorios con Room en memoria, ViewModels con CoroutineTestRule; (b) UI tests en `src/test/` con Robolectric + `createComposeRule` — comprueban los cuatro estados de UI sin emulador; (c) Konsist en `app` — reglas arquitectónicas que fallan el build si alguien rompe las capas. Paparazzi hace snapshots visuales del design system.

---

*Última actualización: 2026-05-12 — ver `entrevista-tecnica.md` para detalle completo y 24 Q&A adicionales.*
