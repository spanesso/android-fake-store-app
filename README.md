# Mango Fake Store App

[![CI](https://github.com/spanesso/android-fake-store-app/actions/workflows/pr.yml/badge.svg?branch=develop)](https://github.com/spanesso/android-fake-store-app/actions/workflows/pr.yml)
[![Android](https://img.shields.io/badge/Android-24%2B-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org/)

Aplicación Android de catálogo de moda que consume la [Fake Store API](https://fakestoreapi.com/). Desarrollada como prueba técnica con arquitectura Clean Architecture + MVVM y módulos Gradle independientes.

**Funcionalidades:**
- Selección de usuario (1-10) y sesión persistente
- Catálogo de productos con imágenes cacheadas, rating y descripción
- Sistema de favoritos por usuario (cada usuario tiene sus propios favoritos)
- Perfil de usuario completo con cierre de sesión

---

## Cómo correr en local

**Requisitos:** JDK 11, Android Studio Hedgehog+, SDK minSdk 24 / compileSdk 36

```bash
git clone https://github.com/spanesso/android-fake-store-app.git
cd android-fake-store-app

./gradlew assembleDevDebug   # compilar
./gradlew installDevDebug    # instalar en emulador/dispositivo
```

La app tiene tres flavors: `dev` (desarrollo), `staging` (QA), `prod` (producción).

---

## Cómo ejecutar los tests

```bash
# Todos los tests unitarios (334 tests)
./gradlew testDevDebugUnitTest

# Análisis estático
./gradlew detekt
./gradlew lint

# Cobertura (informe HTML)
./gradlew koverHtmlReportDevDebug
open build/reports/kover/html/index.html
```

Tests por módulo si se quiere revisar uno concreto:

```bash
./gradlew :features:auth:domain:testDebugUnitTest
./gradlew :features:auth:data:testDebugUnitTest
./gradlew :features:auth:presentation:testDebugUnitTest
./gradlew :features:products:domain:testDebugUnitTest
./gradlew :features:products:presentation:testDebugUnitTest
./gradlew :features:favorites:domain:testDebugUnitTest
./gradlew :features:favorites:data:testDebugUnitTest
./gradlew :features:profile:presentation:testDebugUnitTest
./gradlew :core:error:testDebugUnitTest
./gradlew :core:network:testDevDebugUnitTest
```

---

## Arquitectura

Clean Architecture + MVVM. Cada feature tiene cuatro submódulos: `domain`, `data`, `presentation` y `api` (contratos públicos para consumo entre features).

```
:app
├── :features:auth         (login, sesión)
├── :features:products     (catálogo)
├── :features:favorites    (favoritos por usuario)
└── :features:profile      (perfil + logout)

:core
├── :design-system         (tokens visuales + componentes Compose)
├── :ui                    (estados genéricos: Loading, Error, Empty)
├── :error                 (DomainError sealed, UiError, safeApiCall)
├── :network               (Retrofit + OkHttp + certificate pinning)
├── :database              (Room + SQLCipher)
├── :datastore             (DataStore cifrado con Tink AES-256-GCM)
├── :analytics             (Firebase Crashlytics + Analytics + Performance)
├── :security              (IntegrityChecker, SecureScreen)
├── :logging               (TimberLogger en debug, NoOpLogger en prod)
├── :common                (Dispatchers, extensiones Either/Flow)
└── :testing               (helpers para tests unitarios)
```

Los errores fluyen siempre tipados:

```
Repositorio → safeApiCall → Either<DomainError, T>
    → UseCase → Either<DomainError, T>
    → ViewModel → DomainErrorToUiErrorMapper → UiError
    → UI → stringResource(uiError.messageRes)
```

La UI nunca lee `throwable.message` directamente.

---

## Stack tecnológico

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Kotlin | 2.0.21 | Lenguaje principal |
| Jetpack Compose BOM | 2024.09.00 | UI declarativa |
| Arrow Core | 1.2.4 | `Either<DomainError, T>` para errores tipados |
| Hilt | 2.52 | Inyección de dependencias |
| Room + SQLCipher | 2.7.1 / 4.6.0 | Base de datos local cifrada |
| DataStore + Tink | 1.1.1 / 1.15.0 | Preferencias cifradas AES-256-GCM |
| Retrofit + OkHttp | 2.11.0 / 4.12.0 | HTTP + certificate pinning |
| Coil | 2.7.0 | Carga de imágenes con caché |
| Firebase BOM | 33.6.0 | Crashlytics + Analytics + Performance |
| Kover | 0.9.8 | Cobertura de tests |
| Detekt | 1.23.7 | Análisis estático Kotlin |
| Konsist | 0.17.3 | Reglas de arquitectura verificadas en CI |
| Paparazzi | 1.3.5 | Snapshot tests de Compose |

---

## Documentación por módulo

### Features

| Feature | Descripción | Docs |
|---------|-------------|------|
| `:features:auth` | Login por selección de usuario, sesión en DataStore | [README](features/auth/README.md) |
| `:features:products` | Catálogo SSOT (Room + Retrofit), MangoProductCard con Coil | [README](features/products/README.md) · [diseño](features/products/docs/diseno.md) · [pruebas](features/products/docs/pruebas.md) · [errores](features/products/docs/errores.md) |
| `:features:favorites` | Favoritos por usuario en Room, toggle reactivo con Flow | [README](features/favorites/README.md) · [diseño](features/favorites/docs/diseno.md) · [pruebas](features/favorites/docs/pruebas.md) · [errores](features/favorites/docs/errores.md) |
| `:features:profile` | Perfil completo (10 campos), cerrar sesión | [README](features/profile/README.md) · [diseño](features/profile/presentation/docs/diseno.md) · [pruebas](features/profile/presentation/docs/pruebas.md) |

### Core

| Módulo | Descripción | Docs |
|--------|-------------|------|
| `:core:error` | `DomainError` sealed, `UiError`, `safeApiCall`/`safeDbCall`, mappers | [módulo](core/error/docs/modulo.md) · [diseño](core/error/docs/diseno.md) · [pruebas](core/error/docs/pruebas.md) |
| `:core:design-system` | Tokens visuales Mango + 19 componentes Compose | [módulo](core/design-system/docs/modulo.md) · [diseño](core/design-system/docs/diseno.md) |
| `:core:network` | Retrofit + certificate pinning + retry + ConnectivityObserver | [módulo](core/network/docs/modulo.md) · [diseño](core/network/docs/diseno.md) |
| `:core:database` | Room abstracta cifrada con SQLCipher + Android Keystore | [módulo](core/database/docs/modulo.md) · [diseño](core/database/docs/diseno.md) |
| `:core:datastore` | DataStore cifrado con Tink AES-256-GCM | [módulo](core/datastore/docs/modulo.md) · [diseño](core/datastore/docs/diseno.md) |
| `:core:analytics` | Telemetry + EventTracker; Firebase impl; Console + NoOp | [módulo](core/analytics/docs/modulo.md) · [diseño](core/analytics/docs/diseno.md) |
| `:core:security` | IntegrityChecker (root/Frida/debugger), SecureScreen | [módulo](core/security/docs/modulo.md) · [diseño](core/security/docs/diseno.md) |
| `:core:common` | Dispatchers inyectables, extensiones Either/Flow | [módulo](core/common/docs/modulo.md) |
| `:core:ui` | LoadingContent, ErrorContent, MangoOfflineBanner, shimmer | [módulo](core/ui/docs/modulo.md) |
| `:core:logging` | Logger interface; TimberLogger (debug) + NoOpLogger (prod) | [módulo](core/logging/docs/modulo.md) |

### Documentación transversal

| Documento | Contenido |
|-----------|-----------|
| [docs/arquitectura.md](docs/arquitectura.md) | Diagrama de módulos, matriz de dependencias, convenciones |
| [docs/seguridad.md](docs/seguridad.md) | Threat model STRIDE, certificate pinning, gestión de secretos |
| [docs/observabilidad.md](docs/observabilidad.md) | Firebase Crashlytics, Analytics, Performance; política PII |
| [docs/ci-cd.md](docs/ci-cd.md) | Pipeline GitHub Actions, jobs y secretos necesarios |
| [docs/decisiones-tecnicas.md](docs/decisiones-tecnicas.md) | Decisiones técnicas no obvias documentadas |
| [docs/adr/0001-manejo-errores.md](docs/adr/0001-manejo-errores.md) | ADR: estrategia de errores tipados con Either |
| [docs/adr/0002-stack-tier-gratuito.md](docs/adr/0002-stack-tier-gratuito.md) | ADR: elección de stack compatible con tier gratuito |
| [docs/adr/0003-observar-conteo-flow-int.md](docs/adr/0003-observar-conteo-flow-int.md) | ADR: contador de favoritos como `Flow<Int>` |
| [docs/adr/0004-app-depende-de-features-data.md](docs/adr/0004-app-depende-de-features-data.md) | ADR: wiring de Hilt en `:app` |

---

## CI/CD

El repositorio tiene tres workflows en GitHub Actions:

| Workflow | Se activa en | Qué hace |
|----------|-------------|----------|
| `pr.yml` | PR a `develop` | lint → test → cobertura → build → SonarCloud |
| `main.yml` | Push a `main` | build + test + distribución Firebase |
| `release.yml` | Tag `v*` | APK firmado + subida a Google Play (internal track) |

Los secretos de CI están documentados en [`docs/configuracion-ci.md`](docs/configuracion-ci.md).
