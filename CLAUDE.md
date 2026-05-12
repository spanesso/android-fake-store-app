# CLAUDE.md

Este archivo proporciona orientación a Claude Code (claude.ai/code) cuando trabaja en este repositorio.

## Idioma

Todos los documentos generados deben estar escritos en **español**: especificaciones (`spec.md`), planes (`plan.md`), tareas (`tasks.md`), listas de verificación, análisis, `CLAUDE.md` y `README.md`.

## Descripción del proyecto

Esta es una prueba técnica que usa **Spec Kit** — un flujo de trabajo integrado con Claude Code para desarrollo guiado por especificaciones. La funcionalidad a construir vive en `repository/android-fake-store-app/`, una aplicación Android (Kotlin + Jetpack Compose) que consume la [Fake Store API](https://fakestoreapi.com/).

## Comandos de la app Android

Todos los comandos Gradle deben ejecutarse desde `repository/android-fake-store-app/`.

```bash
cd repository/android-fake-store-app

./gradlew build                          # Compilación completa
./gradlew assembleDebug                  # APK de depuración
./gradlew lint                           # Análisis estático
./gradlew test                           # Pruebas unitarias
./gradlew connectedAndroidTest           # Pruebas instrumentadas (requiere dispositivo/emulador)

# Ejecutar una sola clase de pruebas unitarias
./gradlew :app:testDebugUnitTest --tests "com.example.fakestoreapp.<NombreDeClase>"
```

## Stack tecnológico

- **Lenguaje**: Kotlin
- **UI**: Jetpack Compose + Material3
- **AGP**: 9.0.1 | **Kotlin**: 2.0.21 | **Compose BOM**: 2024.09.00
- **Min SDK**: 24 | **Target SDK**: 36
- **Catálogo de dependencias**: `gradle/libs.versions.toml`

## Arquitectura

La app sigue Clean Architecture + MVVM con módulos Gradle multi-módulo. Cada feature se organiza en submódulos `:api`, `:domain`, `:data` y `:presentation`.

### Módulos core implementados (ETAPA 1)

| Módulo | Propósito | Docs |
|--------|-----------|------|
| `:core:common` | Dispatchers inyectables, extensiones de `Either`/`Flow`, utilidades Kotlin | [`core/common/docs/`](repository/android-fake-store-app/core/common/docs/) |
| `:core:error` | `DomainError` sealed, `UiError`, `safeApiCall`/`safeDbCall`, mappers de error | [`core/error/docs/`](repository/android-fake-store-app/core/error/docs/) |
| `:core:design-system` | Tokens visuales Mango (colores, tipografía, formas, espaciado) + 19 componentes Compose | [`core/design-system/docs/`](repository/android-fake-store-app/core/design-system/docs/) |
| `:core:ui` | `LoadingContent`, `EmptyContent`, `ErrorContent`, `MangoOfflineBanner`, modificadores shimmer/conditional | [`core/ui/docs/`](repository/android-fake-store-app/core/ui/docs/) |

### Comandos específicos por módulo core

```bash
cd repository/android-fake-store-app

./gradlew :core:common:testDebugUnitTest          # Tests core:common (24 tests)
./gradlew :core:error:testDebugUnitTest           # Tests core:error  (33 tests)
./gradlew :core:ui:testDebugUnitTest              # Tests core:ui     (16 tests)

# Snapshots Paparazzi (core:design-system) — requieren golden images
./gradlew :core:design-system:recordPaparazziDebug
./gradlew :core:design-system:verifyPaparazziDebug
```

### Estructura de módulos

```
repository/android-fake-store-app/
├── app/                       (punto de entrada)
├── core/
│   ├── common/                (dispatchers, Either/Flow ext, KotlinExt)
│   ├── error/                 (DomainError, UiError, mappers, safeApiCall)
│   ├── design-system/         (tokens + 19 componentes Mango)
│   ├── ui/                    (composables de estado, modificadores, conectividad)
│   ├── network/               (pendiente ETAPA 2)
│   ├── database/              (pendiente ETAPA 2)
│   └── ...
└── features/
    ├── products/              (pendiente ETAPA 2)
    ├── auth/                  (pendiente ETAPA 2)
    ├── favorites/             (pendiente ETAPA 2)
    └── profile/               (pendiente ETAPA 2)
```

El código fuente de `:app` vive en `app/src/main/java/com/example/fakestoreapp/`. Los módulos `core:*` viven en `core/<nombre>/src/main/kotlin/com/mango/fakestore/core/<nombre>/`.

## Flujo de trabajo con Spec Kit

Spec Kit orquesta el desarrollo de funcionalidades mediante comandos slash secuenciales. Cada paso hace commit automático mediante los hooks de Git definidos en `.specify/extensions.yml`.

| Paso | Comando | Salida |
|------|---------|--------|
| 1. Inicializar constitución (una vez) | `/speckit-constitution` | `.specify/memory/constitution.md` |
| 2. Escribir especificación | `/speckit-specify <descripción>` | `spec.md` en rama de feature |
| 3. Aclarar incógnitas | `/speckit-clarify` | Actualiza `spec.md` |
| 4. Generar plan de implementación | `/speckit-plan` | `plan.md` |
| 5. Generar tareas | `/speckit-tasks` | `tasks.md` |
| 6. Validar artefactos | `/speckit-analyze` | Informe de consistencia |
| 7. Implementar | `/speckit-implement` | Cambios en el código |

Las ramas de feature se crean automáticamente por `/speckit-specify` con numeración secuencial (p. ej. `001-listado-productos`). Los archivos `spec.md` y `plan.md` actuales son la fuente de verdad del contexto de implementación — leerlos siempre antes de trabajar en una feature.

<!-- SPECKIT START -->
**Feature activa**: ETAPA 1 — Módulos Core Fundamentales (`002-etapa-1-core`)
- Spec: `specs/002-etapa-1-core/spec.md`
- Plan: `specs/002-etapa-1-core/plan.md`
- Research: `specs/002-etapa-1-core/research.md`
- Contratos: `specs/002-etapa-1-core/contracts/public-api.md`
<!-- SPECKIT END -->
