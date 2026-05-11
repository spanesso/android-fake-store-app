# Constitución del proyecto Mango Fake Store

Esta constitución condensa las decisiones inviolables del proyecto. Es la referencia que `/speckit-analyze` y los revisores humanos usan para validar cualquier `spec.md`, `plan.md`, `tasks.md`, código, test o documentación. Las violaciones deben justificarse explícitamente o, en su defecto, corregirse antes de cerrar la ETAPA.

> Fuente única de verdad: `prompt.txt` en la raíz del proyecto externo. En caso de conflicto entre esta constitución y `prompt.txt`, prevalece `prompt.txt`.

## Principios fundamentales

### I. Idioma — español obligatorio

Todos los artefactos generados (specs, plans, tasks, ADRs, READMEs, comentarios de alto nivel, mensajes de commit, descripciones de PR, errores localizados al usuario) se escriben en **español**. El código fuente (clases, funciones, variables) sigue convenciones idiomáticas de Kotlin/Android en inglés. Única excepción documentada: los prompts a generadores de imagen del skill `prompts-de-diseno` (los modelos están entrenados en inglés).

**Origen**: R0.1 del prompt maestro.
**Verifica**: revisión manual + los revisores deben rechazar PRs con docs en inglés.

### II. Clean Architecture + MVVM estricto

Tres capas separadas por feature: `data → domain → presentation`. Las dependencias **apuntan al dominio**: `data` y `presentation` nunca se conocen entre sí; ambas dependen de `domain`. La comunicación entre módulos pasa exclusivamente por interfaces expuestas en módulos `:*:api`. `presentation` aplica MVVM (`ViewModel + UiState + UiEvent + UiEffect`).

**Origen**: R0.2, §3, §4 del prompt maestro.
**Verifica**: skill `validar-arquitectura` (reglas ARQ-001..ARQ-010) + Konsist en CI.

### III. Vistas sin ViewModel — Composables puros + Route

Los Composables que renderizan UI (`ui/screens`, `ui/components`) **nunca** reciben ni instancian un ViewModel. Reciben un `UiState` inmutable y lambdas `(UiEvent) -> Unit`. El wrapper `XxxRoute` en `ui/route/` es la única capa que conoce el ViewModel. Esta separación permite previews exhaustivos, reutilización y testeo aislado.

Adicionalmente: cada Composable de vista tiene `@Preview` para CADA estado de UI (Loading, Empty, Error, Content) en variantes claro/oscuro.

**Origen**: R0.3 y R0.4 del prompt maestro.
**Verifica**: skill `crear-vista` genera la estructura correcta; `validar-arquitectura` regla ARQ-001 bloquea Composables con `hiltViewModel` fuera de `route`.

### IV. Modularización exhaustiva

El proyecto se descompone en `:app`, `:core:*` (design-system, ui, common, error, network, database, datastore, analytics, security, testing) y `:features:*:{api, domain, data, presentation}` para cada feature. `:build-logic` aloja los convention plugins Gradle (`mango.android.feature`, `mango.android.library`, `mango.kotlin.library`, etc.). Reglas de dependencias estrictas (matriz en `docs/arquitectura.md`); no se permiten dependencias circulares.

**Origen**: R0.2 y §4 del prompt maestro.
**Verifica**: skill `validar-arquitectura` regla ARQ-009 (ciclos) + ARQ-004/005/006/010 (direcciones permitidas) + Konsist.

### V. Design System primero

Ningún componente visual (botón, input, label, card, badge, dialog, snackbar, banner) se usa directamente desde Material3 en las pantallas de feature. Todo pasa por wrappers parametrizables del módulo `:core:design-system` (`MangoButton`, `MangoTextField`, `MangoCard`, `MangoErrorState`, `MangoSnackbar`, `MangoOfflineBanner`, etc.). Cada componente tiene previews por estado y snapshot tests Paparazzi/Roborazzi.

**Origen**: R0.6 y §5 del prompt maestro.
**Verifica**: skill `validar-arquitectura` regla ARQ-002 (Material3 fuera de `:core:design-system` queda prohibido).

### VI. Seguridad por defecto

Ningún secreto, API key, endpoint privado o token vive en el repositorio (R0.7). Biometría con `BIOMETRIC_STRONG`, `FLAG_SECURE` por pantalla sensible, detección de root, Certificate Pinning, Room cifrado con SQLCipher, EncryptedDataStore, R8 con reglas agresivas, ofuscación de strings críticos. La estrategia completa vive en `docs/seguridad.md` con un threat model documentado.

**Origen**: R0.7 y §6 del prompt maestro.
**Verifica**: revisión por checklist + `security-review` antes de cerrar ETAPA 7.

### VII. Errores tipados — `Either<DomainError, T>`

Ninguna capa propaga `Throwable` o `Exception` cruda hacia arriba. Toda salida de un repositorio o caso de uso devuelve `Either<DomainError, T>` (Arrow). Las excepciones se capturan en la frontera `data` y se traducen a `DomainError` mediante mappers testeados. La UI nunca muestra `throwable.message`: muestra mensajes vía `stringResource(...)` desde un `UiError` tipado.

`try/catch` genérico está prohibido salvo en barreras documentadas (`ErrorMapper`, `CoroutineExceptionHandler` raíz, init de SDK).

**Origen**: R0.11 y §7 del prompt maestro.
**Verifica**: skill `validar-manejo-errores` (reglas ERR-001..ERR-010) + Detekt reglas custom de §7.13.

### VIII. Observabilidad con tier gratuito

Solo se incorporan herramientas externas con tier gratuito viable para esta prueba técnica. La pila obligatoria es: Firebase Crashlytics + Analytics + Performance, GitHub Actions, Firebase App Distribution, Detekt + ktlint + Kover + Konsist. Servicios "trial" (Datadog, Bitrise pago, SonarCloud privado, Sentry sobre 5K eventos/mes) quedan **opcionales** y solo se activan si la empresa los provee.

**Origen**: R0.12 del prompt maestro + ADR 0002.
**Verifica**: revisión de `gradle/libs.versions.toml` y `.github/workflows/*.yml`; cualquier dependencia de pago debe estar tras un flag o gradle property opt-in.

### IX. Testing por módulo

Cada módulo creado lleva sus pruebas en el mismo PR. Umbrales de cobertura:

- `domain` ≥ 100% (happy path + un test por cada rama de `DomainError` que el caso de uso pueda recibir).
- `data` ≥ 80% (Repository con `MockWebServer` + InMemoryRoom + Mappers por rama).
- `presentation` ≥ 70% (ViewModel con Turbine; Compose UI test por pantalla; snapshot tests del design system con Paparazzi/Roborazzi para cada estado en claro y oscuro).

Cada `DomainError → UiError` mapper tiene un test por rama. Cada componente del design system tiene snapshot test.

**Origen**: R0.5 + §11 del prompt maestro.
**Verifica**: skill `crear-pruebas-unitarias` genera la batería; Kover en CI valida los umbrales.

## Restricciones técnicas

- **Stack**: Kotlin 2.0.x, Jetpack Compose + Material3, Coil 2.x, Coroutines + Flow, Retrofit 2 + OkHttp 4, kotlinx.serialization, Room 2.6+ con KSP, SQLCipher, Hilt, Paging 3, Arrow `Either`, BiometricPrompt, Tink.
- **Build**: AGP 9.0.1, minSdk 24, targetSdk 36, JDK 11 source/target. Versiones se gestionan en `gradle/libs.versions.toml`. La configuración Gradle vive en `build-logic/` como convention plugins.
- **Build flavors**: `dev` / `staging` / `prod` con `BuildConfig` y endpoints separados.
- **CI/CD**: GitHub Actions como pipeline principal; `pr.yml`, `main.yml`, `release.yml`. Distribución interna vía Firebase App Distribution.
- **Conventional Commits en español**: `feat(<scope>):`, `fix:`, `docs:`, `test:`, `refactor:`, `chore:`, `ci:`.

## Flujo de trabajo (Spec Kit)

Para cada feature / módulo nuevo seguir el ciclo:
`/speckit-specify` → `/speckit-clarify` → `/speckit-plan` → `/speckit-tasks` → `/speckit-analyze` → `/speckit-implement`.

Antes de codificar una pantalla o módulo, leer su `spec.md` y `plan.md`. Si algo no está claro, ejecutar `/speckit-clarify`; **no asumir**.

Tras `/speckit-implement` y antes de abrir PR, ejecutar:

1. `validar-arquitectura` — 0 violaciones.
2. `validar-manejo-errores` — 0 violaciones.
3. `crear-pruebas-unitarias` si quedan ramas de `DomainError` sin cubrir.
4. Detekt + ktlint + Konsist sin warnings.
5. Kover con cobertura ≥ umbral del módulo.

PR contra `develop`; el merge a `main` requiere CI verde y revisión humana.

## Gobernanza

- Esta constitución supera prácticas informales; cualquier desviación se documenta en un ADR (`docs/adr/NNNN-titulo.md`).
- Las enmiendas requieren:
  1. ADR explicando el cambio, alternativas y consecuencias.
  2. Actualización sincronizada de `prompt.txt`, esta constitución, los skills afectados y `CLAUDE.md`.
  3. PR con etiqueta `constitution-amendment` y aprobación humana.
- Cada PR debe verificar compatibilidad con esta constitución; los revisores rechazan PRs que la violen sin justificación.
- La complejidad debe estar justificada: tres líneas similares son mejor que una abstracción prematura; no se introducen abstracciones para casos hipotéticos.

**Versión**: 1.0.0 | **Ratificada**: 2026-05-11 | **Última enmienda**: 2026-05-11
