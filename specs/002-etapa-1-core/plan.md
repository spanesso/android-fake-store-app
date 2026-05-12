# Plan de implementación: ETAPA 1 — Módulos Core Fundamentales

**Rama**: `002-etapa-1-core` | **Fecha**: 2026-05-11 | **Spec**: [spec.md](./spec.md)
**Input**: `specs/002-etapa-1-core/spec.md` + clarificaciones sesión 2026-05-11

---

## Resumen

Implementar los cuatro módulos core que son prerequisito para cualquier feature de la app:
`:core:common` (dispatchers + Either helpers), `:core:error` (jerarquía DomainError, UiError, mappers, safeApiCall/safeDbCall), `:core:design-system` (tokens Mango + 19 componentes con previews y snapshot tests), y `:core:ui` (composables de estado transversal + utilities). El orden de implementación es secuencial por dependencia: common → error → design-system → ui.

---

## Contexto técnico

**Lenguaje/Versión**: Kotlin 2.0.21 | JVM 11
**Dependencias principales**: Arrow 1.2.4, Hilt 2.52, Jetpack Compose BOM 2024.09.00, Paparazzi (con targetSdk=35), Konsist, Coroutines 1.9.0
**Almacenamiento**: N/A para estos módulos
**Testing**: JUnit 4 (`:core:common`, `:core:error`), Paparazzi snapshots (`:core:design-system`), MockK + JUnit (`:core:ui`)
**Plataforma**: Android min SDK 24 / target SDK 36
**Tipo de proyecto**: Librería Android multi-módulo
**Metas de rendimiento**: Sin metas de latencia para librerías; los snapshots deben correr en < 2 min en CI
**Restricciones**: Sin dependencias de UI en `:core:common` y `:core:error`; compliance WCAG 2.1 AA mínimo en design-system; sin imports directos de Material3 fuera de `:core:design-system`
**Escala**: 19 componentes, ~220 snapshots (2 temas × estados por componente)

---

## Verificación de constitución

### Principio I — Español obligatorio ✅
Todos los artefactos (spec, plan, tasks, docs, strings.xml) en español. Código Kotlin en inglés.

### Principio II — Clean Architecture + MVVM estricto ✅
`:core:common` y `:core:error` son capas de infraestructura sin UI. `:core:design-system` y `:core:ui` son capas de presentación sin lógica de negocio. No hay dependencias cruzadas data↔presentation.

### Principio III — Composables puros ✅
Todos los componentes del design-system reciben `UiState`/parámetros + lambdas. `MangoOfflineBanner()` stateful se implementa solo en `:core:ui`; el componente visual en `:core:design-system` es puro (`MangoOfflineBannerContent(isOffline: Boolean)`).

### Principio IV — Modularización exhaustiva ✅
Los cuatro módulos ya están declarados en `settings.gradle.kts`. Sus `build.gradle.kts` existen. Esta ETAPA implementa el contenido sin crear módulos nuevos.

**Corrección requerida**: `:core:common` tiene `mango.kotlin.library` pero necesita `mango.android.library` + `mango.android.hilt` por la clarificación Q2. Se actualiza el `build.gradle.kts` en la tarea P1-T1.

### Principio V — Design System primero ✅
`:core:design-system` se implementa antes de cualquier feature. La regla Konsist de aislamiento de Material3 se crea en esta ETAPA.

### Principio VI — Seguridad por defecto ✅ (N/A en estos módulos)
No se manejan secrets, tokens ni datos biométricos en ETAPA 1.

### Principio VII — Errores tipados ✅
`:core:error` implementa la jerarquía completa `DomainError`, `UiError`, mappers y `safeApiCall`/`safeDbCall`. 100% de cobertura en mappers requerido (SC-002).

### Principio VIII — Observabilidad con tier gratuito ✅ (N/A en estos módulos)
Sin integraciones Firebase en ETAPA 1 (estas llegan en `:core:analytics`).

### Principio IX — Testing por módulo ✅
- `:core:common`: cobertura ≥ 80% (helpers Either + AppDispatchers).
- `:core:error`: cobertura 100% en mappers (cada rama de DomainError).
- `:core:design-system`: snapshot test por cada componente × estado.
- `:core:ui`: cobertura ≥ 70% (composables LoadingContent/EmptyContent/ErrorContent + modifiers).

---

## Estructura del proyecto

### Documentación (esta feature)

```
specs/002-etapa-1-core/
├── plan.md              # Este archivo
├── research.md          # Decisiones de investigación
├── data-model.md        # Modelos, contratos internos
├── contracts/
│   └── public-api.md    # Superficies públicas por módulo
└── tasks.md             # Generado por /speckit-tasks
```

### Código fuente por módulo

```
repository/android-fake-store-app/

core/common/
├── build.gradle.kts          # CAMBIO: mango.kotlin.library → mango.android.library + mango.android.hilt
└── src/main/kotlin/com/mango/fakestore/core/common/
    ├── dispatchers/
    │   ├── AppDispatchers.kt
    │   ├── DefaultAppDispatchers.kt
    │   └── di/DispatchersModule.kt
    └── ext/
        ├── EitherExt.kt
        ├── FlowEitherExt.kt
        └── KotlinExt.kt

core/error/
├── build.gradle.kts          # Mantener mango.kotlin.library; añadir androidx.annotation
└── src/
    ├── main/kotlin/com/mango/fakestore/core/error/
    │   ├── DomainError.kt
    │   ├── UiError.kt
    │   ├── mapper/
    │   │   ├── NetworkErrorMapper.kt
    │   │   ├── DatabaseErrorMapper.kt
    │   │   └── DomainErrorToUiErrorMapper.kt
    │   └── ext/
    │       └── SafeCallExt.kt     # safeApiCall, safeDbCall
    └── test/kotlin/com/mango/fakestore/core/error/
        ├── mapper/
        │   ├── NetworkErrorMapperTest.kt
        │   ├── DatabaseErrorMapperTest.kt
        │   └── DomainErrorToUiErrorMapperTest.kt
        └── ext/
            └── SafeCallExtTest.kt

core/design-system/
├── build.gradle.kts           # Existente; añadir Paparazzi plugin + testOptions targetSdk=35
└── src/
    ├── main/kotlin/com/mango/fakestore/core/designsystem/
    │   ├── theme/
    │   │   ├── MangoTheme.kt
    │   │   ├── MangoColors.kt
    │   │   ├── MangoTypography.kt
    │   │   ├── TypographyConfig.kt
    │   │   ├── MangoSpacing.kt
    │   │   ├── MangoShapes.kt
    │   │   ├── MangoElevations.kt
    │   │   └── MangoMotion.kt
    │   └── component/
    │       ├── MangoButton.kt
    │       ├── MangoTextField.kt
    │       ├── MangoText.kt
    │       ├── MangoCard.kt
    │       ├── MangoProductCard.kt
    │       ├── MangoIconButton.kt
    │       ├── MangoIcon.kt
    │       ├── MangoChip.kt
    │       ├── MangoDivider.kt
    │       ├── MangoTopAppBar.kt
    │       ├── MangoNavigationBar.kt
    │       ├── MangoDialog.kt
    │       ├── MangoBottomSheet.kt
    │       ├── MangoLoadingIndicator.kt
    │       ├── MangoEmptyState.kt
    │       ├── MangoErrorState.kt
    │       ├── MangoBadge.kt
    │       ├── MangoSnackbar.kt
    │       └── MangoOfflineBannerContent.kt
    └── test/kotlin/com/mango/fakestore/core/designsystem/
        └── snapshot/
            ├── MangoButtonSnapshotTest.kt
            ├── MangoTextFieldSnapshotTest.kt
            └── ... (un archivo por componente)

core/ui/
├── build.gradle.kts           # Existente; añadir dependencia :core:design-system y :core:error
└── src/
    ├── main/kotlin/com/mango/fakestore/core/ui/
    │   ├── LoadingContent.kt
    │   ├── EmptyContent.kt
    │   ├── ErrorContent.kt
    │   ├── MangoOfflineBanner.kt
    │   ├── connectivity/
    │   │   └── ConnectivityObserver.kt   # internal
    │   ├── modifier/
    │   │   ├── ShimmerModifier.kt
    │   │   └── ConditionalModifier.kt
    │   └── preview/
    │       ├── PreviewLightDark.kt
    │       └── PreviewFontScale.kt
    └── test/kotlin/com/mango/fakestore/core/ui/
        ├── LoadingContentTest.kt
        ├── EmptyContentTest.kt
        └── ErrorContentTest.kt
```

### Reglas Konsist (módulo de tests de arquitectura)

```
core/design-system/src/test/.../konsist/
└── Material3IsolationKonsistTest.kt
    // Verifica que solo :core:design-system y allowlist de :core:ui usan material3.*
```

### Strings de error (recursos)

```
app/src/main/res/values/strings.xml       # Español (idioma base)
app/src/main/res/values-en/strings.xml    # Inglés
```
Claves: `error_red_sin_conexion`, `error_red_servidor`, `error_bd_lectura`, etc. (tabla completa en data-model.md).

---

## Rastreo de complejidad

No hay violaciones de constitución en este plan. No se requieren justificaciones.

---

## Secuencia de implementación

### Módulo 1 — `:core:common`
1. Actualizar `build.gradle.kts`: cambiar `mango.kotlin.library` → `mango.android.library` + `mango.android.hilt`
2. Añadir `namespace = "com.mango.fakestore.core.common"` al bloque `android {}`
3. Implementar `AppDispatchers` + `DefaultAppDispatchers` + `DispatchersModule`
4. Implementar extensiones `Either` y `Flow<Either>`
5. Implementar extensiones Kotlin utilitarias
6. Escribir tests unitarios (AppDispatchers con TestDispatcher; extensiones Either con casos positivos/negativos)
7. Compilar: `./gradlew :core:common:build`

### Módulo 2 — `:core:error`
1. Actualizar `build.gradle.kts`: añadir `androidx.annotation` como dependencia; añadir `arrow-core` y `arrow-fx-coroutines`
2. Implementar `DomainError` (jerarquía sealed completa de §7.2)
3. Implementar `UiError` con `Severity` y `UiErrorAction`
4. Implementar `NetworkErrorMapper` con todas las reglas de §7.4
5. Implementar `DatabaseErrorMapper`
6. Implementar `DomainErrorToUiErrorMapper` (tabla completa de data-model.md)
7. Implementar `safeApiCall` y `safeDbCall` (propagar `CancellationException`)
8. Escribir strings de error en `strings.xml` (es + en)
9. Escribir tests unitarios: un test por rama de DomainError en cada mapper (100% cobertura)
10. Compilar: `./gradlew :core:error:build`

### Módulo 3 — `:core:design-system`
1. Actualizar `build.gradle.kts`: añadir plugin Paparazzi; configurar `testOptions { targetSdk = 35 }`; añadir dependencia `:core:error` (para `UiError` en `MangoErrorState`)
2. Implementar tokens: `MangoColors`, `TypographyConfig`, `MangoTypography`, `MangoSpacing`, `MangoShapes`, `MangoElevations`, `MangoMotion`
3. Implementar `MangoTheme` (light + dark color schemes)
4. Implementar 19 componentes (orden: MangoIcon → MangoText → MangoButton → MangoTextField → MangoCard → ... → MangoErrorState → MangoOfflineBannerContent)
5. Para cada componente: `@Preview` claro/oscuro + `@Preview` por estado
6. Para cada componente: snapshot test Paparazzi (golden file generado en primera ejecución)
7. Implementar regla Konsist `Material3IsolationKonsistTest`
8. Compilar: `./gradlew :core:design-system:build`

### Módulo 4 — `:core:ui`
1. Actualizar `build.gradle.kts`: añadir dependencias `:core:design-system`, `:core:error`; añadir `kotlinx-coroutines-android`
2. Implementar `ConnectivityObserver` (internal, basado en `ConnectivityManager.registerDefaultNetworkCallback`)
3. Implementar `MangoOfflineBanner` (stateful, observa `ConnectivityObserver`)
4. Implementar `LoadingContent`, `EmptyContent`, `ErrorContent`
5. Implementar `Modifier.shimmer()` y `Modifier.conditional()`
6. Implementar anotaciones `@PreviewLightDark` y `@PreviewFontScale`
7. Implementar extensiones de `Context` (dpToPx, pxToDp)
8. Escribir tests unitarios de composables (MockK para ConnectivityObserver; tests de modifiers)
9. Compilar: `./gradlew :core:ui:build`
10. Compilación final completa: `./gradlew build`

---

## Notas de build

- El plugin Paparazzi puede requerir entrada en `libs.versions.toml` `[plugins]` si no existe. Verificar antes de aplicar.
- La regla Konsist se ejecuta como test JUnit (`./gradlew :core:design-system:test`), no como plugin Gradle.
- Los golden files de Paparazzi se generan con `./gradlew :core:design-system:recordPaparazziDebug` y se commitean. Las ejecuciones posteriores usan `./gradlew :core:design-system:verifyPaparazziDebug`.
