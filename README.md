# Mango Fake Store App

[![Android](https://img.shields.io/badge/Android-24%2B-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](LICENSE)

Aplicación Android para la prueba técnica de Mango Fashion Group. Consume la [Fake Store API](https://fakestoreapi.com/) y sigue una arquitectura multi-módulo con Clean Architecture + MVVM + Jetpack Compose.

**Repositorio**: [github.com/spanesso/android-fake-store-app](https://github.com/spanesso/android-fake-store-app)

## Stack tecnológico

| Tecnología | Versión |
|------------|---------|
| Kotlin | 2.0.21 |
| Android Gradle Plugin | 9.0.1 |
| Jetpack Compose BOM | 2024.09.00 |
| Material3 | incluido en BOM |
| Arrow Core | 1.2.4 |
| Hilt | 2.52 |
| Room | 2.7.1 |
| SQLCipher | 4.6.0 |
| Tink Android | 1.15.0 |
| Retrofit | 2.11.0 |
| OkHttp | 4.12.0 |
| Min SDK | 24 |
| Target SDK | 36 |

## Módulos implementados

### ETAPA 1.1–1.4 — Núcleo de errores y diseño

| Módulo | Descripción | Tests |
|--------|-------------|-------|
| `:core:common` | Dispatchers inyectables, extensiones `Either`/`Flow`, utilidades Kotlin | 24 |
| `:core:error` | Jerarquía `DomainError` sealed, `UiError`, `safeApiCall`/`safeDbCall`, mappers | 33 |
| `:core:design-system` | Tokens visuales Mango + 19 componentes Compose con snapshot tests | 9 |
| `:core:ui` | Composables de estado, `MangoOfflineBanner`, modificadores reutilizables | 16 |

### ETAPA 1.5–1.7 — Núcleo de red y persistencia

| Módulo | Descripción | Tests |
|--------|-------------|-------|
| `:core:network` | Retrofit + OkHttp, certificate pinning, `RetryInterceptor`, `ConnectivityObserver`, `safeRetrofitCall`, flavors dev/staging/prod | 22 |
| `:core:database` | Room base abstracta cifrada con SQLCipher, `DatabaseKeyManager` con Android Keystore | 6 |
| `:core:datastore` | DataStore cifrado con Tink AES-256-GCM para tokens (`SessionData`) y preferencias (`UserPreferences`) | 8 |
| `:core:analytics` | `Telemetry` + `EventTracker` interfaces; Firebase impl (Crashlytics/Analytics/Performance); Console + NoOp | 13 |
| `:core:security` | `BiometricAuthenticator` (BIOMETRIC_STRONG), `IntegrityChecker` (RootBeer), `SecureScreen` (FLAG_SECURE) | 8 |
| `:core:testing` | `CoroutineTestRule`, `TestAppDispatchers`, builders de `DomainError` y `UiError` | 0 |

**Total tests ETAPA 1**: 139 ✅

### ETAPA 2 — Logging

| Módulo | Descripción | Tests |
|--------|-------------|-------|
| `:core:logging` | `Logger` interface; `TimberLogger` (debug) + `NoOpLogger` (producción — silencioso); DI via `BuildConfig.DEBUG` | 14 |

**Total tests acumulado**: 153 ✅

### ETAPA 3 — Catálogo de productos

| Módulo | Descripción | Tests |
|--------|-------------|-------|
| `:features:products:domain` | Entidad `Producto`, `ProductosRepository` interface, `ObtenerProductos` use case | 7 |
| `:features:products:data` | `ProductosRepositoryImpl` SSOT (Room + Retrofit), mappers DTO↔Entity↔Domain, `ProductosDao` | — |
| `:features:products:presentation` | `ProductosViewModel` (MVVM), `UiState` sealed (Loading/Content/Empty/Error), `ProductosRoute` + `ProductosScreen` | 8 |

**Nuevos tests ETAPA 3**: 15 ✅  
**Total tests acumulado**: 168 ✅

### ETAPA 4 — Favoritos

| Módulo | Descripción | Tests |
|--------|-------------|-------|
| `:features:favorites:domain` | Entidad `Favorito`, `FavoritosRepository` interface, 3 use cases (ObservarFavoritos, ToggleFavorito, ObservarConteoFavoritos) | 9 |
| `:features:favorites:data` | `FavoritosRepositoryImpl` SSOT (Room local), `FavoritosDao`, `FavoritoEntity`, mapper Entity↔Domain | 7 |
| `:features:favorites:presentation` | `FavoritosViewModel` (MVVM + Turbine), `UiState` sealed (Loading/Content/Empty/Error), `FavoritosRoute` + `FavoritosScreen` puro | 6 |

**Nuevos tests ETAPA 4**: 22 ✅  
**Total tests acumulado**: 190 ✅

## Cómo ejecutar

```bash
# Clonar
git clone https://github.com/spanesso/android-fake-store-app.git
cd android-fake-store-app

# Compilación completa
./gradlew build

# APK de depuración (flavor dev)
./gradlew assembleDevDebug

# Todos los tests unitarios
./gradlew test
```

## Comandos por módulo

```bash
# Tests unitarios por módulo
./gradlew :core:common:testDebugUnitTest          # core:common   (24 tests)
./gradlew :core:error:testDebugUnitTest           # core:error    (33 tests)
./gradlew :core:ui:testDebugUnitTest              # core:ui       (16 tests)
./gradlew :core:network:testDevDebugUnitTest      # core:network  (22 tests)
./gradlew :core:database:testDebugUnitTest        # core:database  (6 tests)
./gradlew :core:datastore:testDebugUnitTest       # core:datastore (8 tests)
./gradlew :core:analytics:testDebugUnitTest       # core:analytics (13 tests)
./gradlew :core:security:testDebugUnitTest        # core:security  (8 tests)
./gradlew :core:logging:testDebugUnitTest         # core:logging   (14 tests)

# Tests features:products
./gradlew :features:products:domain:testDebugUnitTest        # products:domain       (7 tests)
./gradlew :features:products:presentation:testDebugUnitTest  # products:presentation (8 tests)

# Tests features:favorites
./gradlew :features:favorites:domain:testDebugUnitTest        # favorites:domain       (9 tests)
./gradlew :features:favorites:data:testDebugUnitTest          # favorites:data         (7 tests)
./gradlew :features:favorites:presentation:testDebugUnitTest  # favorites:presentation (6 tests)

# Snapshots Paparazzi (core:design-system)
./gradlew :core:design-system:recordPaparazziDebug   # Grabar golden images
./gradlew :core:design-system:verifyPaparazziDebug   # Verificar snapshots

# Análisis estático
./gradlew lint
./gradlew detekt
```

## Arquitectura

```
app/                           — punto de entrada + wiring Hilt (AppDatabase, NavHost)
core/
  common/      ✅              — dispatchers, Either/Flow ext, KotlinExt
  error/       ✅              — DomainError sealed, UiError, safeApiCall/safeDbCall
  design-system/ ✅            — tokens visuales + 19 componentes Compose
  ui/          ✅              — LoadingContent, ErrorContent, MangoOfflineBanner, shimmer
  network/     ✅              — HTTP: Retrofit, pinning, retry, ConnectivityObserver
  database/    ✅              — Room + SQLCipher + Android Keystore
  datastore/   ✅              — DataStore cifrado con Tink AES-256-GCM
  analytics/   ✅              — Telemetry + EventTracker; Firebase impl; Console + NoOp
  security/    ✅              — BiometricPrompt, IntegrityChecker (RootBeer), SecureScreen
  testing/     ✅              — CoroutineTestRule, TestAppDispatchers, builders
  logging/     ✅              — Logger interface; TimberLogger (debug) + NoOpLogger (prod)
features/
  products/    ✅              — catálogo SSOT (Room + Retrofit), MVVM, Either tipado, 15 tests
  favorites/   ✅              — favoritos persistidos en Room, SSOT reactivo, toggle desde catálogo, 22 tests
  auth/                        — pendiente
  profile/                     — pendiente
```

Cada feature se organiza en submódulos `:api`, `:domain`, `:data`, `:presentation`.

## Manejo de errores

El sistema usa `Either<DomainError, T>` de Arrow para propagar errores tipados desde la capa de datos hasta la UI:

```
data layer → safeApiCall/safeDbCall → Either<DomainError, T>
domain layer → propaga Either<DomainError, T>
presentation → DomainErrorToUiErrorMapper → UiError → MangoErrorState
```

Ver el catálogo completo en [`core/error/docs/errores.md`](core/error/docs/errores.md).

## Seguridad

- **Certificate Pinning**: `CertificatePinner` de OkHttp + `network_security_config.xml` (doble capa)
- **Base de datos cifrada**: SQLCipher con passphrase de 32 bytes en Android Keystore
- **Tokens cifrados**: Tink AES-256-GCM a nivel de campo en DataStore
- **Logs seguros**: `HttpLoggingInterceptor` solo en `BuildConfig.DEBUG`, tokens nunca en logs

## Documentación de módulos

| Módulo | Docs |
|--------|------|
| `:core:common` | [`core/common/docs/`](core/common/docs/) |
| `:core:error` | [`core/error/docs/`](core/error/docs/) |
| `:core:design-system` | [`core/design-system/docs/`](core/design-system/docs/) |
| `:core:ui` | [`core/ui/docs/`](core/ui/docs/) |
| `:core:network` | [`core/network/docs/`](core/network/docs/) |
| `:core:database` | [`core/database/docs/`](core/database/docs/) |
| `:core:datastore` | [`core/datastore/docs/`](core/datastore/docs/) |
| `:core:analytics` | [`core/analytics/docs/`](core/analytics/docs/) |
| `:core:security` | [`core/security/docs/`](core/security/docs/) |
| `:core:testing` | [`core/testing/docs/`](core/testing/docs/) |
| `:core:logging` | [`core/logging/docs/`](core/logging/docs/) |
| `:features:products` | [`features/products/docs/`](features/products/docs/) |
| `:features:favorites` | [`features/favorites/docs/`](features/favorites/docs/) |
