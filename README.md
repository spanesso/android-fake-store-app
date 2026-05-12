# Mango Fake Store App

Aplicación Android para la prueba técnica de Mango Fashion Group. Consume la [Fake Store API](https://fakestoreapi.com/) y sigue una arquitectura multi-módulo con Clean Architecture + MVVM + Jetpack Compose.

## Stack tecnológico

| Tecnología | Versión |
|------------|---------|
| Kotlin | 2.0.21 |
| Android Gradle Plugin | 9.0.1 |
| Jetpack Compose BOM | 2024.09.00 |
| Material3 | incluido en BOM |
| Arrow Core | 1.2.4 |
| Hilt | 2.52 |
| Min SDK | 24 |
| Target SDK | 36 |

## Módulos implementados

### ETAPA 1 — Módulos Core Fundamentales

| Módulo | Descripción | Tests |
|--------|-------------|-------|
| `:core:common` | Dispatchers inyectables, extensiones `Either`/`Flow`, utilidades Kotlin | 24 |
| `:core:error` | Jerarquía `DomainError` sealed, `UiError`, `safeApiCall`/`safeDbCall`, mappers | 33 |
| `:core:design-system` | Tokens visuales Mango + 19 componentes Compose con snapshot tests | 9 |
| `:core:ui` | Composables de estado, `MangoOfflineBanner`, modificadores reutilizables | 16 |

## Comandos principales

```bash
cd repository/android-fake-store-app

# Compilación
./gradlew build                                   # Compilación completa
./gradlew assembleDebug                           # APK de depuración

# Tests unitarios
./gradlew test                                    # Todos los tests unitarios
./gradlew :core:common:testDebugUnitTest          # core:common (24 tests)
./gradlew :core:error:testDebugUnitTest           # core:error  (33 tests)
./gradlew :core:ui:testDebugUnitTest              # core:ui     (16 tests)

# Snapshots Paparazzi
./gradlew :core:design-system:recordPaparazziDebug   # Grabar golden images
./gradlew :core:design-system:verifyPaparazziDebug   # Verificar snapshots

# Análisis estático
./gradlew lint
./gradlew detekt
```

## Arquitectura

```
app/                           — punto de entrada
core/
  common/                      — utilidades transversales
  error/                       — manejo tipado de errores
  design-system/               — sistema de diseño visual
  ui/                          — composables de estado reutilizables
  network/                     — (pendiente)
  database/                    — (pendiente)
features/
  products/                    — (pendiente)
  auth/                        — (pendiente)
  favorites/                   — (pendiente)
  profile/                     — (pendiente)
```

Cada feature se organiza en submódulos `:api`, `:domain`, `:data`, `:presentation`.

## Manejo de errores

El sistema usa `Either<DomainError, T>` de Arrow para propagar errores tipados desde la capa de datos hasta la UI:

```
data layer → safeApiCall/safeDbCall → DomainError
domain layer → Either<DomainError, T>
presentation → DomainErrorToUiErrorMapper → UiError → MangoErrorState
```

Ver el catálogo completo en [`core/error/docs/errores.md`](core/error/docs/errores.md).

## Documentación de módulos

- [`core/common/docs/`](core/common/docs/) — Dispatchers, EitherExt, FlowEitherExt, KotlinExt
- [`core/error/docs/`](core/error/docs/) — DomainError, UiError, mappers, catálogo de errores
- [`core/design-system/docs/`](core/design-system/docs/) — Tokens, componentes, snapshots
- [`core/ui/docs/`](core/ui/docs/) — LoadingContent, EmptyContent, ErrorContent, shimmer, conectividad
