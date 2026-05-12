# Investigación: Módulos Core de Infraestructura (1.5–1.7)

**Fecha**: 2026-05-11 | **Rama**: `002-etapa-1-core`

---

## Hallazgo clave: Mappers y safeCall ya implementados

**Decisión**: `safeApiCall`, `safeDbCall`, `NetworkErrorMapper` y `DatabaseErrorMapper` **ya existen en `:core:error`** (implementados en ETAPA 1.1–1.4). Los tres módulos nuevos los consumen vía dependencia, no los re-implementan.

**Impacto en el plan**:
- `:core:network` depende de `:core:error` para `safeApiCall` y `NetworkErrorMapper`.
- `:core:database` depende de `:core:error` para `safeDbCall` y `DatabaseErrorMapper`.
- Los tests de esos mappers ya existen en `:core:error` (33 tests reportados).

**Limitación identificada**: `NetworkErrorMapper.extractHttpCode()` usa regex sobre `throwable.message`. Funciona con `retrofit2.HttpException` (su mensaje sigue el patrón `"HTTP NNN ..."`) pero es frágil. El módulo `:core:network` incluirá un adaptador que lanza `HttpException` con el mensaje correcto garantizando compatibilidad.

---

## Decision 1 — Certificate Pinning

**Decisión**: OkHttp `CertificatePinner` con pin primario SHA-256 real + pin backup placeholder.

**Rationale**: El pin real `sha256/dSxOWQR+hD1HkfYEk0y+JuXzHrLTjhVPXDzGRsbO7oI=` fue obtenido el 2026-05-11 via openssl. El backup placeholder (`sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=`) se documenta con instrucciones para actualizarlo antes del despliegue a producción.

**Alternativas consideradas**:
- `network_security_config.xml` con `<pin-set>` → complementario, no exclusivo. Se implementa ambos.
- Pinnig vía interceptor manual → redundante con `CertificatePinner` de OkHttp.

**Riesgo**: Si el certificado de `fakestoreapi.com` rota sin actualizar el pin, la app queda sin conexión. Para mitigar: usar Remote Config para distribuir pins nuevos (fuera del alcance de esta ETAPA; documentado en `docs/seguridad.md`).

---

## Decision 2 — Retry Interceptor (§7.7 Prompt Maestro)

**Decisión**: OkHttp `Interceptor` personalizado con backoff exponencial, jitter Gaussian y max 3 reintentos.

**Algoritmo**:
```
delay(attempt) = min(baseDelay * 2^attempt, maxDelay) + jitter
baseDelay = 500ms, maxDelay = 10s, jitter = Random.nextLong(-300, 300)ms
```

**Condiciones de reintento**:
- Reintentar: timeout (`SocketTimeoutException`, `TimeoutException`), 5xx (incluyendo 408, 429 con `Retry-After`).
- NO reintentar: 4xx excepto 408 y 429.
- NO reintentar operaciones no idempotentes (POST sin header `Idempotency-Key`).

**Alternativas consideradas**:
- Librería `coil-retry` / `ktor-retry` → no aplica (este proyecto usa Retrofit/OkHttp).
- Jitter uniforme vs Gaussian → se eligió uniforme (`Random.nextLong(-300, 300)`) por simplicidad sin pérdida de efectividad.

---

## Decision 3 — ConnectivityObserver

**Decisión**: `ConnectivityManager.NetworkCallback` con `callbackFlow { }` que emite `ConnectivityStatus`.

**Rationale**: API recomendada en Android API 21+; más precisa que polling. Soporta múltiples tipos de red (WiFi, cellular, ethernet).

**Implementación de `ConnectivityStatus`**:
```kotlin
sealed interface ConnectivityStatus {
    data object Connected : ConnectivityStatus
    data object Disconnected : ConnectivityStatus
    data object Unavailable : ConnectivityStatus
}
```

**Nota Android**: `NetworkCapabilities.NET_CAPABILITY_INTERNET` sin `NET_CAPABILITY_VALIDATED` indica conectividad física pero no acceso real a internet. Se usa `NET_CAPABILITY_VALIDATED` para filtrar portales cautivos.

---

## Decision 4 — SQLCipher + Android Keystore

**Decisión**: `net.zetetic:sqlcipher-android:4.6.0` con clave AES-256 generada y almacenada en Android Keystore (`KeyProperties.KEY_ALGORITHM_AES`, `KeyProperties.BLOCK_MODE_GCM`).

**Estrategia de clave**:
1. Generar clave AES-256 en Android Keystore al primer arranque (si no existe).
2. Usar la clave para cifrar una passphrase aleatoria de 32 bytes.
3. La passphrase cifrada se guarda en SharedPreferences internas (cifrada con la clave del Keystore).
4. Al abrir la BD: descifrar la passphrase con la clave del Keystore → pasar a SQLCipher.

**Alternativa directa** (más simple): Usar la clave AES-GCM directa como passphrase de SQLCipher. Se descarta porque SQLCipher acepta strings, no bytes raw de Keystore.

**MinSdk 24**: Android Keystore disponible desde API 18 para claves simétricas; AES/GCM/NoPadding desde API 19. Con minSdk 24 es garantizado.

---

## Decision 5 — DataStore Encriptado (Tink + Preferences DataStore)

**Decisión**: `androidx.datastore:datastore-preferences:1.1.1` + `com.google.crypto.tink:tink-android:1.15.0` para cifrado de valores sensibles a nivel de entrada.

**Estrategia**:
- DataStore de `Preferences` para la API familiar y sin necesidad de protobuf.
- Tokens de acceso y refresco se cifran individualmente con Tink AES-256-GCM antes de almacenar como string Base64.
- Preferencias no sensibles (tema, notificaciones) se almacenan en claro.
- La clave Tink (keyset) se almacena en Android Keystore.

**Alternativas consideradas**:

| Opción | Ventaja | Descartado porque |
|--------|---------|-------------------|
| Proto DataStore + Tink Serializer | Cifra todo el archivo | Requiere protobuf schemas; más complejo para prueba técnica |
| `EncryptedSharedPreferences` | Simple | En alpha; sin acceso reactivo nativo; deprecated path |
| Proto DataStore puro | Estable | Overhead de generación de código; innecesario para prefs simples |
| Preferences DataStore + cifrado de valores (elegida) | Balance simplicidad/seguridad | — |

---

## Decision 6 — Build Flavors y BuildConfig

**Decisión**: Los flavors `dev`, `staging`, `prod` ya están configurados en `:app`. El módulo `:core:network` lee `BuildConfig.BASE_URL` usando `manifestPlaceholders` o `buildConfigField` inyectado por Hilt desde la `Application`.

**Implementación**: `NetworkConfig` es una clase de datos inyectable por Hilt con `@ApplicationContext` que lee `BuildConfig` del módulo `:app` en runtime.

**Alternativa**: Definir `BuildConfig` propio en `:core:network` → se descarta; los valores de configuración de endpoints son del `:app`, no del módulo de red.

---

## Decision 7 — NetworkErrorMapper: compatibilidad con Retrofit HttpException

**Decisión**: El `NetworkErrorMapper` existente en `:core:error` usa regex sobre `throwable.message`. El módulo `:core:network` añade un adaptador en `safeApiCall` que convierte `retrofit2.HttpException` en `DomainError.Network` directamente **antes** de llegar al mapper genérico.

**Implementación**: Extensión local en `:core:network` que envuelve `safeApiCall` de `:core:error`:

```kotlin
suspend fun <T> safeRetrofitCall(block: suspend () -> T): Either<DomainError, T> =
    try {
        Either.Right(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: retrofit2.HttpException) {
        Either.Left(networkMapper.mapHttpCode(e.code(), e))
    } catch (e: Throwable) {
        safeApiCall { throw e }  // delega al mapper genérico
    }
```

Esto elimina la dependencia del regex y hace el mapeo explícito y testeable.

---

## Versiones confirmadas (de `gradle/libs.versions.toml`)

| Librería | Versión |
|----------|---------|
| Retrofit | 2.11.0 |
| OkHttp | 4.12.0 |
| MockWebServer | 4.12.0 |
| Room | 2.6.1 |
| SQLCipher | 4.6.0 |
| DataStore Preferences | 1.1.1 |
| Security Crypto | 1.1.0-alpha06 |
| Tink Android | 1.15.0 |
| Hilt | 2.52 |
| KSP | 2.0.21-1.0.27 |
| Coroutines | 1.9.0 |
| Arrow | 1.2.4 |

Todas ya presentes en `libs.versions.toml`. No se requieren adiciones al catálogo de versiones.
