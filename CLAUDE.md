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

La app tiene un único módulo (`:app`) con paquete `com.example.fakestoreapp`. El código fuente vive en `app/src/main/java/com/example/fakestoreapp/`. La arquitectura esperada para nuevas funcionalidades es MVVM con capas de Clean Architecture (data → domain → UI), siguiendo las buenas prácticas de Android con Compose.

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
