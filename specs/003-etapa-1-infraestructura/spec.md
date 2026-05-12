# Especificación de Feature: Módulos Core de Infraestructura (ETAPA 1 — Sub-etapas 1.5 a 1.7)

**Rama**: `002-etapa-1-core`  
**Directorio de spec**: `specs/003-etapa-1-infraestructura/`  
**Creado**: 2026-05-11  
**Estado**: Borrador  
**Contexto**: ETAPA 1 del Prompt Maestro (§14) — continuación de los módulos core fundamentales (1.1–1.4 ya implementados).

---

## Alcance

Esta especificación cubre tres módulos core de infraestructura que deben implementarse antes de los módulos de feature:

| Sub-etapa | Módulo Gradle | Responsabilidad |
|-----------|---------------|-----------------|
| 1.5 | `:core:network` | Cliente HTTP, interceptores, certificate pinning, resiliencia de red, observabilidad de conectividad |
| 1.6 | `:core:database` | Base de datos Room encriptada con SQLCipher, migraciones, mapeo de errores de BD |
| 1.7 | `:core:datastore` | Persistencia encriptada de tokens y preferencias de usuario |

---

## Escenarios de Usuario y Pruebas

### Historia 1 — Comunicación resiliente con la API (Prioridad: P1)

El equipo de desarrollo necesita que cualquier módulo de feature pueda realizar llamadas HTTP a `fakestoreapi.com` de forma segura, con reintentos automáticos, autenticación TLS verificada y traducción tipada de errores de red al modelo `DomainError`.

**Por qué esta prioridad**: Todos los módulos de feature (productos, favoritos, perfil) dependen de esta capa. Sin ella ningún dato puede cargarse desde la API.

**Prueba independiente**: Se puede verificar ejecutando los tests de `NetworkErrorMapper` y los tests de repositorio con `MockWebServer`, sin ningún módulo de feature implementado.

**Escenarios de aceptación**:

1. **Dado** que hay conexión de red, **Cuando** un repositorio llama a `safeApiCall { api.getProducts() }`, **Entonces** se obtiene `Either.Right<List<ProductoDto>>` con los datos de la API.
2. **Dado** que `fakestoreapi.com` devuelve HTTP 401, **Cuando** se ejecuta `safeApiCall`, **Entonces** el resultado es `Either.Left<DomainError.Network.Unauthorized>`.
3. **Dado** que `fakestoreapi.com` devuelve HTTP 500, **Cuando** se ejecuta `safeApiCall` y los 3 reintentos con backoff exponencial fallan, **Entonces** el resultado es `Either.Left<DomainError.Network.Server(500)>`.
4. **Dado** que el dispositivo no tiene conexión a internet, **Cuando** se ejecuta `safeApiCall`, **Entonces** el resultado es `Either.Left<DomainError.Network.NoConnection>` sin reintentos.
5. **Dado** que la respuesta JSON de la API está malformada, **Cuando** se deserializa, **Entonces** el resultado es `Either.Left<DomainError.Network.Parsing>`.
6. **Dado** que el certificado del servidor no coincide con los pins configurados, **Cuando** se establece la conexión TLS, **Entonces** la conexión se rechaza y se emite `DomainError.Network.NoConnection` (fallo SSL mapeado).
7. **Dado** que `fakestoreapi.com` devuelve HTTP 429 con cabecera `Retry-After: 5`, **Cuando** el interceptor de retry procesa la respuesta, **Entonces** espera el tiempo indicado antes del siguiente intento (máx. 3 intentos).

---

### Historia 2 — Persistencia local encriptada (Prioridad: P2)

El módulo de favoritos y el caché de productos necesitan una base de datos Room encriptada con SQLCipher que gestione errores de BD de forma tipada.

**Por qué esta prioridad**: La funcionalidad offline (favoritos, caché) depende directamente de esta capa. Sin ella, los módulos de feature no pueden persistir datos localmente.

**Prueba independiente**: Se puede verificar con `InMemoryRoom` + tests del `DatabaseErrorMapper` sin ningún feature implementado.

**Escenarios de aceptación**:

1. **Dado** que la base de datos está inicializada con SQLCipher, **Cuando** se intenta escribir una entidad, **Entonces** la operación se completa y la BD permanece encriptada en disco.
2. **Dado** que se viola una restricción de integridad (duplicado de clave primaria), **Cuando** se ejecuta `safeDbCall { dao.insert(entidad) }`, **Entonces** el resultado es `Either.Left<DomainError.Database.IntegrityViolation>`.
3. **Dado** que ocurre un error de lectura de SQLite (corrupción de BD), **Cuando** se ejecuta `safeDbCall { dao.getAll() }`, **Entonces** el resultado es `Either.Left<DomainError.Database.ReadFailed>`.
4. **Dado** que se busca un registro que no existe, **Cuando** el DAO devuelve `null`, **Entonces** el repositorio emite `Either.Left<DomainError.Database.NotFound>`.
5. **Dado** que la clave de encriptación de la BD se gestiona via Android Keystore, **Cuando** la app se reinstala, **Entonces** la BD no es accesible sin la clave del Keystore asociada al dispositivo.

---

### Historia 3 — Almacenamiento encriptado de tokens y preferencias (Prioridad: P3)

El módulo de autenticación y otros features necesitan persistir tokens de sesión y preferencias de usuario de forma encriptada sin exponer datos sensibles en SharedPreferences planas.

**Por qué esta prioridad**: Es necesario para ETAPA 2 (Auth), pero puede implementarse sin features activos. Sin él, los tokens se perderían al cerrar la app.

**Prueba independiente**: Tests unitarios del `DataStoreManager` con `PreferenceDataStore` en memoria y verificación de encriptación/desencriptación.

**Escenarios de aceptación**:

1. **Dado** que el usuario se autentica, **Cuando** se persiste el token de sesión via DataStore, **Entonces** el token se almacena encriptado y no es legible en texto plano desde el sistema de archivos.
2. **Dado** que existe un token guardado, **Cuando** la app se reinicia y lee el DataStore, **Entonces** el token se recupera correctamente y el usuario permanece autenticado.
3. **Dado** que el usuario cierra sesión, **Cuando** se llama a `clearSession()`, **Entonces** todos los tokens y preferencias de sesión se eliminan del DataStore.
4. **Dado** que se cambia una preferencia de usuario (ej. tema oscuro), **Cuando** se escribe en DataStore, **Entonces** la preferencia se recupera correctamente tras reiniciar la app.

---

### Casos Límite

- ¿Qué ocurre si el pin de certificado de backup ya no es válido y el primario tampoco? → La conexión falla con `DomainError.Network.NoConnection`; se loguea el fallo SSL con Timber para diagnóstico (sin datos sensibles).
- ¿Qué ocurre si la base de datos SQLCipher está corrupta al arrancar? → El `DatabaseErrorMapper` captura la excepción, se emite `DomainError.Database.ReadFailed`, y la app ofrece opción de borrar y recrear la BD.
- ¿Qué ocurre si el dispositivo no tiene acceso al Android Keystore (API <23 o dispositivo sin TEE)? → Se usa una clave derivada de forma segura sin TEE, documentando la limitación en `docs/seguridad.md`.
- ¿Qué ocurre si el DataStore está corrompido? → Se captura la excepción de serialización, se borra el datastore y se emite evento de telemetría (sin PII).
- ¿Qué ocurre si el interceptor de conectividad detecta cambio de red durante una llamada? → La llamada en vuelo se cancela cooperativamente; el `ConnectivityObserver` emite `Disconnected` y la UI muestra `MangoOfflineBanner`.
- ¿Qué ocurre si todos los reintentos agotan el backoff para un endpoint de 5xx? → Se emite `DomainError.Network.Server(httpCode)` y se activa el circuit breaker para ese endpoint por N segundos.

---

## Requisitos

### Requisitos Funcionales

#### Módulo `:core:network`

- **RF-NET-001**: El módulo DEBE proveer un cliente OkHttp singleton configurado con timeout de conexión (10 s), lectura (30 s) y escritura (30 s).
- **RF-NET-002**: El cliente OkHttp DEBE incluir un interceptor de logging que active solo en builds de tipo debug (`BuildConfig.DEBUG == true`); desactivado en cualquier build de tipo release independientemente del flavor. Decisión: `BuildConfig.DEBUG` es más seguro que chequear el flavor (garantiza que nunca hay logging en producción).
- **RF-NET-003**: El cliente OkHttp DEBE incluir certificate pinning con el pin primario SHA-256 real de `fakestoreapi.com` (`dSxOWQR+hD1HkfYEk0y+JuXzHrLTjhVPXDzGRsbO7oI=`) y un pin de backup placeholder documentado.
- **RF-NET-004**: El módulo DEBE proveer un interceptor de reintento con backoff exponencial: máx. 3 intentos, jitter incluido, para errores transitorios (timeouts, 5xx); sin reintentar 4xx excepto 408 y 429 con `Retry-After`.
- **RF-NET-005**: El módulo DEBE usar `NetworkErrorMapper` de `:core:error` (ya implementado en ETAPA 1.2). `:core:network` NO re-implementa este mapper; lo consume como dependencia.
- **RF-NET-006**: El módulo DEBE exponer `safeRetrofitCall` — un wrapper tipado específico para `retrofit2.HttpException` que mapea HTTP codes directamente a `DomainError.Network.*` sin depender del regex de mensaje. Distinto de `safeApiCall` (que ya existe en `:core:error`): `safeRetrofitCall` maneja `HttpException` de forma explícita y tipada.
- **RF-NET-007**: El módulo DEBE proveer `ConnectivityObserver` como `Flow<ConnectivityStatus>` (Connected / Disconnected / Unavailable) usando `ConnectivityManager.NetworkCallback`.
- **RF-NET-008**: El módulo DEBE exponer vía `BuildConfig` la URL base de la API por flavor: `dev` → `https://fakestoreapi.com/`, `staging` → `https://fakestoreapi.com/`, `prod` → `https://fakestoreapi.com/`. Los endpoints pueden diferir en proyectos reales.
- **RF-NET-009**: El módulo DEBE proveer una instancia de Retrofit pre-configurada con `kotlinx.serialization` como conversor.
- **RF-NET-010**: El `NetworkSecurityConfig` del módulo DEBE forzar TLS 1.2+ y definir los dominios permitidos para certificate pinning.
- **RF-NET-011**: El módulo DEBE exponer una interfaz `NetworkErrorReporter` que permita a `:core:analytics` recibir errores de red para telemetría (implementación vacía/no-op por ahora).

#### Módulo `:core:database`

- **RF-DB-001**: El módulo DEBE proveer la clase base `MangoDatabase` (Room + SQLCipher) con configuración de encriptación via Android Keystore.
- **RF-DB-002**: La clave de encriptación SQLCipher DEBE generarse y almacenarse en Android Keystore; NUNCA en código fuente ni SharedPreferences planas.
- **RF-DB-003**: El módulo DEBE usar `DatabaseErrorMapper` de `:core:error` (ya implementado en ETAPA 1.2). `:core:database` NO re-implementa este mapper; lo consume como dependencia transitiva de `:core:error`.
- **RF-DB-004**: El módulo DEBE usar `safeDbCall` de `:core:error` (ya implementado en ETAPA 1.2). Los repositorios de feature usarán `safeDbCall { dao.operation() }` importándolo de `:core:error`, no de `:core:database`.
- **RF-DB-005**: El módulo DEBE incluir la infraestructura de migraciones de Room con estrategia fallback documentada.
- **RF-DB-006**: `:core:database` define `MangoDatabase` como clase abstracta (`abstract class MangoDatabase : RoomDatabase()`) con `@Database(entities = [], version = 1)`. El módulo `:app` DEBE declarar una clase concreta que extienda `MangoDatabase` y liste todas las entidades de los feature modules vía `@Database(entities = [ProductEntity::class, FavoriteEntity::class, ...])`. Room no permite extensión de entidades entre módulos — el ensamblaje ocurre siempre en `:app`. Los feature modules exponen sus `Entity` y `DAO` como clases públicas; `:app` los registra en su `@Database`.

#### Módulo `:core:datastore`

- **RF-DS-001**: El módulo DEBE proveer `MangoDataStore` usando `EncryptedDataStore` (basado en Tink o `EncryptedSharedPreferences` con migración a DataStore).
- **RF-DS-002**: El módulo DEBE exponer métodos tipados para leer/escribir: token de acceso, token de refresco, ID de usuario, preferencias de tema (claro/oscuro/sistema).
- **RF-DS-003**: Los valores **sensibles** (tokens de acceso, tokens de refresco, userId) DEBEN estar cifrados individualmente con Tink AES-256-GCM antes de persistirse; no se debe poder leer estos valores en texto plano desde el sistema de archivos del dispositivo. Las preferencias no-sensibles (tema, notificaciones) pueden almacenarse en claro dentro del Preferences DataStore (que ya reside en almacenamiento interno de la app, no accesible externamente sin root).
- **RF-DS-004**: El módulo DEBE proveer `clearSession()` que borre todos los tokens de sesión dejando intactas las preferencias de usuario.
- **RF-DS-005**: Las operaciones de lectura DEBEN exponer `Flow<T?>` para permitir reactividad a cambios.
- **RF-DS-006**: El módulo DEBE manejar la excepción de corrupción del DataStore borrando y recreando el archivo, emitiendo un evento de telemetría (sin PII).

### Entidades Clave

- **ConnectivityStatus**: `sealed interface` con `Connected`, `Disconnected`, `Unavailable`.
- **NetworkConfig**: datos de configuración por flavor (baseUrl, timeouts, pines de certificado).
- **DatabaseKey**: clave SQLCipher derivada del Android Keystore (no es una entidad de dominio, es infraestructura).
- **SessionData**: token de acceso, token de refresco, ID de usuario (persiste en DataStore encriptado).
- **UserPreferences**: tema, notificaciones, etc. (persiste en DataStore encriptado).

---

## Criterios de Éxito

### Resultados Medibles

- **CE-001**: Los tests de `NetworkErrorMapper` cubren el 100% de las ramas definidas en §7.4 del Prompt Maestro (9 ramas mínimas).
- **CE-002**: Los tests de `DatabaseErrorMapper` cubren el 100% de las ramas definidas en §7.4 (4 ramas mínimas).
- **CE-003**: Los tests de repositorio con `MockWebServer` simulan exitosamente los escenarios: HTTP 200, 4xx (401, 403, 404), 5xx, JSON inválido, timeout, sin red (6 escenarios mínimos).
- **CE-004**: El interceptor de retry reintenta exactamente 3 veces para errores transitorios y no reintenta para errores 4xx (verificable en tests con `MockWebServer`).
- **CE-005**: La cobertura de `:core:network` y `:core:database` es ≥ 80%; la de `:core:datastore` es ≥ 70%.
- **CE-006**: `validar-arquitectura` reporta 0 violaciones tras la implementación de los tres módulos.
- **CE-007**: `validar-manejo-errores` reporta 0 violaciones (ningún `try/catch` genérico fuera de mappers; ningún `throwable.message` expuesto).
- **CE-008**: La compilación `./gradlew assembleDebug` es verde tras cada módulo implementado.
- **CE-009**: El `ConnectivityObserver` emite el estado correcto en ≤ 1 segundo tras un cambio real de conectividad (verificable en test instrumentado simple).
- **CE-010**: Los tres módulos están documentados en español en sus carpetas `docs/` (`modulo.md`, `diseno.md`, `pruebas.md`, `errores.md`).

---

## Supuestos

- Los tres módulos son puramente de infraestructura (sin UI); no requieren Composables ni ViewModel propios.
- `fakestoreapi.com` usa HTTPS con certificado TLS estándar (verificado: SHA-256 `dSxOWQR+hD1HkfYEk0y+JuXzHrLTjhVPXDzGRsbO7oI=` obtenido el 2026-05-11). El pin de backup se documenta como placeholder con instrucciones para actualizarlo.
- Android Keystore está disponible en API 23+ (MinSDK=24 en este proyecto, por lo que es garantizado).
- `EncryptedDataStore` se implementa usando `androidx.security:security-crypto` o la integración Tink de DataStore; se elige la opción con tier gratuito y mantenimiento activo de Google.
- La configuración de flavors (`dev`, `staging`, `prod`) en `:core:network` se integra con el `build.gradle.kts` del módulo `:app` que ya define los flavors.
- La migración de datos (Room migrations) comienza en la versión 1 de la BD; estrategia `fallbackToDestructiveMigration` solo para desarrollo, desactivada en `staging` y `prod`.
- Los módulos 1.5–1.7 no incluyen implementaciones de `:core:analytics` (eso es ETAPA 1.8); solo exponen las interfaces `NetworkErrorReporter` como no-op.
- El certificado de `fakestoreapi.com` puede rotar; se incluye en la documentación el procedimiento para actualizar los pins sin un redespliegue urgente (via Remote Config o similar como opción futura).
