# Tareas: ETAPA 1 — Módulos Core Fundamentales

**Input**: `specs/002-etapa-1-core/plan.md` + `spec.md` + `data-model.md` + `contracts/public-api.md`
**Rama**: `002-etapa-1-core` | **Fecha**: 2026-05-11

## Formato: `[ID] [P?] [Story?] Descripción con ruta de archivo`

- **[P]**: Paralelizable (archivos distintos, sin dependencias incompletas)
- **[Story]**: User story a la que pertenece (US1, US2, US3 del spec.md)

---

## Fase 1: Configuración (Infraestructura compartida de build)

**Objetivo**: Ajustes Gradle necesarios antes de implementar cualquier módulo.

- [ ] T001 Añadir entrada de plugin Paparazzi en `gradle/libs.versions.toml` bajo `[plugins]`: `paparazzi = { id = "app.cash.paparazzi", version.ref = "paparazzi" }`
- [ ] T002 Añadir versión explícita de `androidx-annotation` en `gradle/libs.versions.toml`: (1) en `[versions]` añadir `annotation = "1.9.1"`; (2) en `[libraries]` añadir `androidx-annotation = { group = "androidx.annotation", name = "annotation", version.ref = "annotation" }` — NO usar BOM, el proyecto no tiene AndroidX BOM general
- [ ] T003 Actualizar `core/common/build.gradle.kts`: cambiar `id("mango.kotlin.library")` por `id("mango.android.library")` + `id("mango.android.hilt")`; añadir bloque `android { namespace = "com.mango.fakestore.core.common" }` en `repository/android-fake-store-app/core/common/build.gradle.kts`
- [ ] T004 Actualizar `core/error/build.gradle.kts`: añadir dependencias `implementation(libs.arrow.core)`, `implementation(libs.arrow.fx.coroutines)`, `compileOnly(libs.androidx.annotation)` en `repository/android-fake-store-app/core/error/build.gradle.kts`
- [ ] T005 Actualizar `core/design-system/build.gradle.kts`: añadir `alias(libs.plugins.paparazzi)`, bloque `testOptions { unitTests.targetSdk = 35 }`, dependencia `implementation(project(":core:error"))` y **`testImplementation(libs.konsist)`** (requerido para T076) en `repository/android-fake-store-app/core/design-system/build.gradle.kts`
- [ ] T006 Actualizar `core/ui/build.gradle.kts`: añadir dependencias `implementation(project(":core:design-system"))`, `implementation(project(":core:error"))`, `implementation(libs.kotlinx.coroutines.android)` en `repository/android-fake-store-app/core/ui/build.gradle.kts`

**Checkpoint**: Ejecutar `./gradlew :core:common:dependencies --configuration releaseRuntimeClasspath` para verificar que Hilt está en el árbol de dependencias.

---

## Fase 2: Fundacional — `:core:common` (Prerrequisito bloqueante)

**Objetivo**: Dispatchers inyectables + helpers Either para que todos los demás módulos puedan compilar.

**⚠️ CRÍTICO**: Las fases US1, US2 y US3 dependen de que esta fase esté completa.

- [ ] T007 Crear `AppDispatchers.kt` en `repository/android-fake-store-app/core/common/src/main/kotlin/com/mango/fakestore/core/common/dispatchers/AppDispatchers.kt` — interfaz con propiedades `io`, `main`, `default`, `unconfined: CoroutineDispatcher`
- [ ] T008 [P] Crear `DefaultAppDispatchers.kt` en `core/common/src/main/kotlin/.../dispatchers/DefaultAppDispatchers.kt` — implementación `@Inject constructor()` que asigna `Dispatchers.IO`, `Dispatchers.Main`, etc.
- [ ] T009 [P] Crear `DispatchersModule.kt` en `core/common/src/main/kotlin/.../dispatchers/di/DispatchersModule.kt` — `@Module @InstallIn(SingletonComponent::class)` con `@Provides @Singleton fun provideDispatchers(): AppDispatchers`
- [ ] T010 [P] Crear `EitherExt.kt` en `core/common/src/main/kotlin/.../ext/EitherExt.kt` — extensiones `flatMap`, `getOrElse`, `mapLeft`, `fold` sobre `Either<L,R>`
- [ ] T011 [P] Crear `FlowEitherExt.kt` en `core/common/src/main/kotlin/.../ext/FlowEitherExt.kt` — extensiones `mapEitherRight`, `filterEitherRight` sobre `Flow<Either<L,R>>`
- [ ] T012 [P] Crear `KotlinExt.kt` en `core/common/src/main/kotlin/.../ext/KotlinExt.kt` — extensiones Kotlin utilitarias: `String.isNotNullOrBlank()`, `T?.ifNotNull()`, `Collection.toImmutableList()`
- [ ] T013 Escribir `AppDispatchersTest.kt` en `core/common/src/test/kotlin/.../dispatchers/AppDispatchersTest.kt` — tests con `StandardTestDispatcher`; verificar inyección y reemplazo en tests
- [ ] T014 [P] Escribir `EitherExtTest.kt` en `core/common/src/test/kotlin/.../ext/EitherExtTest.kt` — happy path + left path para `flatMap`, `getOrElse`, `mapLeft`, `fold`
- [ ] T015 [P] Escribir `FlowEitherExtTest.kt` en `core/common/src/test/kotlin/.../ext/FlowEitherExtTest.kt` — tests con Turbine para `mapEitherRight` y `filterEitherRight`
- [ ] T016 Compilar y testear: `./gradlew :core:common:build` — debe completar sin errores

**Checkpoint**: `:core:common` compila y sus tests pasan.

---

## Fase 3: User Story 1 — `:core:error` (Prioridad: P1)

**Objetivo**: Proveer la jerarquía `DomainError`, `UiError`, mappers y helpers `safeApiCall`/`safeDbCall`.

**Test independiente**: Un módulo Kotlin puro que importe solo `:core:error` puede definir `fun myUseCase(): Either<DomainError, String>` y compilar sin imports transitivos de Android UI.

### Implementación US1

- [ ] T017 [US1] Crear `DomainError.kt` en `core/error/src/main/kotlin/com/mango/fakestore/core/error/DomainError.kt` — jerarquía sealed completa (Network.NoConnection/Timeout/Server/Unauthorized/Forbidden/NotFound/Parsing, Database.ReadFailed/WriteFailed/NotFound/IntegrityViolation, Security.BiometricUnavailable/BiometricLockout/RootDetected/IntegrityFailed/SessionExpired, Validation, Unknown) según §7.2 del prompt maestro
- [ ] T018 [P] [US1] Crear `UiError.kt` en `core/error/src/main/kotlin/.../UiError.kt` — `data class UiError(@StringRes messageRes: Int, severity: Severity, actions: List<UiErrorAction>, errorCode: String)` con `enum Severity` y `sealed interface UiErrorAction` (Retry, Dismiss, Login, OpenSettings)
- [ ] T019 [P] [US1] Crear `NetworkErrorMapper.kt` en `core/error/src/main/kotlin/.../mapper/NetworkErrorMapper.kt` — mapeo de excepciones según §7.4: IOException→NoConnection, SocketTimeout→Timeout, HttpException(4xx/5xx)→según código, SerializationException→Parsing
- [ ] T020 [P] [US1] Crear `DatabaseErrorMapper.kt` en `core/error/src/main/kotlin/.../mapper/DatabaseErrorMapper.kt` — mapeo: SQLiteConstraintException→IntegrityViolation, SQLiteException→ReadFailed/WriteFailed
- [ ] T021 [US1] Crear `DomainErrorToUiErrorMapper.kt` en `core/error/src/main/kotlin/.../mapper/DomainErrorToUiErrorMapper.kt` — `when` exhaustivo sobre todas las ramas de DomainError; asignar messageRes, severity, actions y errorCode según tabla de `data-model.md`
- [ ] T022 [US1] Crear `SafeCallExt.kt` en `core/error/src/main/kotlin/.../ext/SafeCallExt.kt` — `suspend fun <T> safeApiCall(block: suspend () -> T): Either<DomainError, T>` + `safeDbCall`; propagar `CancellationException` sin envolver; capturar cualquier otro `Throwable` con los mappers correspondientes
- [ ] T023 [P] [US1] Crear strings de error (español) en `app/src/main/res/values/strings.xml` — todas las claves de la tabla de data-model.md: `error_red_sin_conexion`, `error_red_tiempo_agotado`, `error_red_servidor`, `error_red_no_autorizado`, `error_red_sin_permiso`, `error_red_no_encontrado`, `error_red_formato`, `error_bd_lectura`, `error_bd_escritura`, `error_bd_no_encontrado`, `error_bd_integridad`, `error_seg_*`, `error_validacion_formulario`, `error_desconocido`
- [ ] T024 [P] [US1] Crear strings de error (inglés) en `app/src/main/res/values-en/strings.xml` — mismas claves con traducciones al inglés

### Tests US1 (100% de cobertura en mappers — requisito SC-002)

- [ ] T025 [P] [US1] Escribir `NetworkErrorMapperTest.kt` en `core/error/src/test/kotlin/.../mapper/NetworkErrorMapperTest.kt` — un test por cada tipo de excepción → DomainError.Network esperado (7 casos)
- [ ] T026 [P] [US1] Escribir `DatabaseErrorMapperTest.kt` en `core/error/src/test/kotlin/.../mapper/DatabaseErrorMapperTest.kt` — un test por cada tipo de excepción SQL → DomainError.Database esperado (4 casos)
- [ ] T027 [US1] Escribir `DomainErrorToUiErrorMapperTest.kt` en `core/error/src/test/kotlin/.../mapper/DomainErrorToUiErrorMapperTest.kt` — un test por cada sub-tipo de DomainError (17 casos); verificar messageRes, severity, actions y errorCode
- [ ] T028 [US1] Escribir `SafeCallExtTest.kt` en `core/error/src/test/kotlin/.../ext/SafeCallExtTest.kt` — happy path retorna `Either.Right`; excepción de red retorna `Either.Left(DomainError.Network.*)`; `CancellationException` se propaga sin envolver
- [ ] T029 [US1] Compilar y testear: `./gradlew :core:error:build` — debe completar con cobertura 100% en mappers

**Checkpoint**: `:core:error` compila, todos sus tests pasan, y `./gradlew :core:error:koverVerify` no reporta violaciones de umbral.

---

## Fase 4: User Story 2 — `:core:design-system` (Prioridad: P2)

**Objetivo**: Proveer todos los tokens visuales Mango y 19 componentes parametrizables con previews y snapshot tests.

**Test independiente**: El catálogo de previews muestra correctamente cada componente en modo claro y oscuro en Android Studio sin configuración adicional (SC-007).

### Tokens y tema

- [ ] T030 [US2] Crear `MangoColors.kt` en `core/design-system/src/main/kotlin/com/mango/fakestore/core/designsystem/theme/MangoColors.kt` — `object MangoColors` con todos los colores de §5; `lightColorScheme()` y `darkColorScheme()` con asignación de roles M3
- [ ] T031 [P] [US2] Crear `TypographyConfig.kt` en `core/design-system/src/main/kotlin/.../theme/TypographyConfig.kt` — `object TypographyConfig` con `var headlineFontFamily: FontFamily = FontFamily.Serif` y `var bodyFontFamily: FontFamily = FontFamily.Default`
- [ ] T032 [P] [US2] Crear `MangoTypography.kt` en `core/design-system/src/main/kotlin/.../theme/MangoTypography.kt` — `Typography` de Compose con los 11 estilos (display, h1..h3, titleLarge, titleMedium, bodyLarge, bodyMedium, bodySmall, label, caption) referenciando `TypographyConfig`
- [ ] T033 [P] [US2] Crear `MangoSpacing.kt` en `core/design-system/src/main/kotlin/.../theme/MangoSpacing.kt` — `object MangoSpacing` con xxs=4, xs=8, sm=12, md=16, lg=24, xl=32, xxl=48, xxxl=64 en Dp
- [ ] T034 [P] [US2] Crear `MangoShapes.kt` en `core/design-system/src/main/kotlin/.../theme/MangoShapes.kt` — `object MangoShapes` con none(0dp), sm(4dp), md(8dp), lg(16dp), pill(50%); también proveer `Shapes` de M3
- [ ] T035 [P] [US2] Crear `MangoElevations.kt` en `core/design-system/src/main/kotlin/.../theme/MangoElevations.kt` — `object MangoElevations` con 0, 1, 2, 4, 8 dp
- [ ] T036 [P] [US2] Crear `MangoMotion.kt` en `core/design-system/src/main/kotlin/.../theme/MangoMotion.kt` — `object MangoMotion` con `standardEasing`, `emphasizedEasing` (CubicBezierEasing), `durationFast=150`, `durationMedium=300`, `durationSlow=500`
- [ ] T037 [US2] Crear `MangoTheme.kt` en `core/design-system/src/main/kotlin/.../theme/MangoTheme.kt` — `@Composable fun MangoTheme(darkTheme: Boolean, content: @Composable () -> Unit)` que aplica colores, tipografía y formas; exponer `MangoTheme.colors`, `MangoTheme.typography`, `MangoTheme.shapes` via `CompositionLocal`

### Componentes UI (en orden de dependencia interna)

- [ ] T038 [P] [US2] Crear `MangoIcon.kt` en `core/design-system/src/main/kotlin/.../component/MangoIcon.kt` — wrapper de `Icon` con `contentDescription: String?`, touch target ≥ 48dp cuando es interactivo, `@Preview` claro/oscuro
- [ ] T039 [P] [US2] Crear `MangoText.kt` en `core/design-system/src/main/kotlin/.../component/MangoText.kt` — wrapper parametrizado de `Text` con `MangoTypography` variants; incluye `typealias MangoLabel = MangoText` para la variante de etiqueta (RF-014 lista "MangoLabel/MangoText" como un único componente); `@Preview` claro/oscuro
- [ ] T040 [P] [US2] Crear `MangoDivider.kt` en `core/design-system/src/main/kotlin/.../component/MangoDivider.kt` — wrapper de `HorizontalDivider`/`VerticalDivider` con token de color; `@Preview`
- [ ] T041 [P] [US2] Crear `MangoLoadingIndicator.kt` en `core/design-system/src/main/kotlin/.../component/MangoLoadingIndicator.kt` — variantes: circular (`CircularProgressIndicator`), lineal (`LinearProgressIndicator`), shimmer placeholder; `@Preview` por variante
- [ ] T042 [P] [US2] Crear `MangoBadge.kt` en `core/design-system/src/main/kotlin/.../component/MangoBadge.kt` — badge con contador numérico y variante de punto; `@Preview` con número / sin número
- [ ] T043 [US2] Crear `MangoButton.kt` en `core/design-system/src/main/kotlin/.../component/MangoButton.kt` — 5 variantes (Primary, Secondary, Outline, Text, Destructive) × 3 tamaños × estados (idle, loading, pressed, disabled); icono opcional; touch target ≥ 48dp; `@Preview` por variante + estado
- [ ] T044 [US2] Crear `MangoIconButton.kt` en `core/design-system/src/main/kotlin/.../component/MangoIconButton.kt` — wrapper de `IconButton` con `contentDescription` obligatorio, touch target ≥ 48dp; estados idle/pressed/disabled; `@Preview` por estado
- [ ] T045 [US2] Crear `MangoTextField.kt` en `core/design-system/src/main/kotlin/.../component/MangoTextField.kt` — variantes Outlined/Filled/Underlined; leading/trailing icons, helper text, error state, password toggle, contador; `@Preview` por variante + estado (idle, focused, error, disabled)
- [ ] T046 [US2] Crear `MangoChip.kt` en `core/design-system/src/main/kotlin/.../component/MangoChip.kt` — 4 tipos: Filter, Assist, Input, Suggestion; estados idle/selected/disabled; `@Preview` por tipo + estado
- [ ] T047 [US2] Crear `MangoCard.kt` en `core/design-system/src/main/kotlin/.../component/MangoCard.kt` — variantes Elevated/Filled/Outlined con `content: @Composable ColumnScope.() -> Unit`; `@Preview` por variante
- [ ] T048 [US2] Crear `MangoProductCard.kt` en `core/design-system/src/main/kotlin/.../component/MangoProductCard.kt` — composición usando MangoCard + MangoText + MangoLoadingIndicator (shimmer); parámetros: imagen, título, precio, badge favorito; `@Preview` idle + shimmer
- [ ] T049 [US2] Crear `MangoTopAppBar.kt` en `core/design-system/src/main/kotlin/.../component/MangoTopAppBar.kt` — variantes Centered/Small/Medium/Large con scroll behavior; navigationIcon, actions; `@Preview` scrolled + unscrolled
- [ ] T050 [US2] Crear `MangoNavigationBar.kt` en `core/design-system/src/main/kotlin/.../component/MangoNavigationBar.kt` — `MangoNavigationBar` + `MangoNavigationBarItem`; soporte de badge con `MangoBadge`; `@Preview` con item seleccionado / no seleccionado
- [ ] T051 [US2] Crear `MangoDialog.kt` en `core/design-system/src/main/kotlin/.../component/MangoDialog.kt` — wrapper de `AlertDialog` con título, texto, confirmButton, dismissButton usando tokens Mango; `@Preview`
- [ ] T052 [US2] Crear `MangoBottomSheet.kt` en `core/design-system/src/main/kotlin/.../component/MangoBottomSheet.kt` — wrapper de `ModalBottomSheet` con drag handle y `content: @Composable ColumnScope.() -> Unit`; `@Preview`
- [ ] T053 [US2] Crear `MangoEmptyState.kt` en `core/design-system/src/main/kotlin/.../component/MangoEmptyState.kt` — ilustración/icono + título + subtítulo opcionales; `@Preview`
- [ ] T054 [US2] Crear `MangoErrorState.kt` en `core/design-system/src/main/kotlin/.../component/MangoErrorState.kt` — recibe `UiError`; muestra mensaje localizado (`stringResource(uiError.messageRes)`), botón retry cuando `onRetry != null`; `@Preview` con retry + sin retry
- [ ] T055 [US2] Crear `MangoSnackbar.kt` en `core/design-system/src/main/kotlin/.../component/MangoSnackbar.kt` — 4 severidades (Info, Success, Warning, Error) con icono y color semántico; `@Preview` por severidad
- [ ] T056 [US2] Crear `MangoOfflineBannerContent.kt` en `core/design-system/src/main/kotlin/.../component/MangoOfflineBannerContent.kt` — stateless `(isOffline: Boolean, modifier: Modifier)`, banner con color de aviso; `@Preview` isOffline=true + isOffline=false

### Snapshot tests Paparazzi (un archivo por componente)

- [ ] T057 [P] [US2] Crear `MangoIconSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoIconSnapshotTest.kt` — claro + oscuro
- [ ] T058 [P] [US2] Crear `MangoTextSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoTextSnapshotTest.kt` — claro + oscuro
- [ ] T059 [P] [US2] Crear `MangoDividerSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoDividerSnapshotTest.kt`
- [ ] T060 [P] [US2] Crear `MangoLoadingIndicatorSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoLoadingIndicatorSnapshotTest.kt` — circular + lineal + shimmer
- [ ] T061 [P] [US2] Crear `MangoBadgeSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoBadgeSnapshotTest.kt` — con número + punto
- [ ] T062 [P] [US2] Crear `MangoButtonSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoButtonSnapshotTest.kt` — todas las variantes × estados relevantes × claro/oscuro
- [ ] T063 [P] [US2] Crear `MangoTextFieldSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoTextFieldSnapshotTest.kt` — idle + error + disabled × claro/oscuro
- [ ] T064 [P] [US2] Crear `MangoChipSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoChipSnapshotTest.kt` — Filter/Assist/Input/Suggestion × idle/selected
- [ ] T065 [P] [US2] Crear `MangoCardSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoCardSnapshotTest.kt`
- [ ] T066 [P] [US2] Crear `MangoProductCardSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoProductCardSnapshotTest.kt` — idle + shimmer
- [ ] T067 [P] [US2] Crear `MangoTopAppBarSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoTopAppBarSnapshotTest.kt`
- [ ] T068 [P] [US2] Crear `MangoNavigationBarSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoNavigationBarSnapshotTest.kt`
- [ ] T069 [P] [US2] Crear `MangoDialogSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoDialogSnapshotTest.kt`
- [ ] T070 [P] [US2] Crear `MangoBottomSheetSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoBottomSheetSnapshotTest.kt`
- [ ] T071 [P] [US2] Crear `MangoEmptyStateSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoEmptyStateSnapshotTest.kt`
- [ ] T072 [P] [US2] Crear `MangoErrorStateSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoErrorStateSnapshotTest.kt` — con retry + sin retry
- [ ] T073 [P] [US2] Crear `MangoSnackbarSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoSnackbarSnapshotTest.kt` — 4 severidades
- [ ] T074 [P] [US2] Crear `MangoIconButtonSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoIconButtonSnapshotTest.kt`
- [ ] T075 [P] [US2] Crear `MangoOfflineBannerContentSnapshotTest.kt` en `core/design-system/src/test/kotlin/.../snapshot/MangoOfflineBannerContentSnapshotTest.kt` — isOffline=true + isOffline=false

### Regla Konsist

- [ ] T076 [US2] Crear `Material3IsolationKonsistTest.kt` en `core/design-system/src/test/kotlin/.../konsist/Material3IsolationKonsistTest.kt` — regla Konsist usando **`Konsist.scopeFromProject()`** (escanea todos los módulos del proyecto); la regla verifica que solo archivos del paquete `com.mango.fakestore.core.designsystem` importan `androidx.compose.material3.*`; allowlist explícita en el test: clases `Surface`, `Scaffold`, `Snackbar` cuando el archivo pertenece al paquete `com.mango.fakestore.core.ui`

- [ ] T077 [US2] Generar golden files de Paparazzi y compilar: ejecutar `./gradlew :core:design-system:recordPaparazziDebug` para generar los golden files; luego `./gradlew :core:design-system:build`

**Checkpoint**: `:core:design-system` compila, todos los snapshots pasan, la regla Konsist no reporta violaciones.

---

## Fase 5: User Story 3 — `:core:ui` (Prioridad: P3)

**Objetivo**: Proveer composables de estado transversal y utilidades Compose reutilizables.

**Test independiente**: Se puede instanciar `LoadingContent`, `EmptyContent(message="")` y `ErrorContent(uiError)` en un test de composable sin ningún módulo de feature.

### Implementación US3

- [ ] T078 [US3] Crear `ConnectivityObserver.kt` en `core/ui/src/main/kotlin/com/mango/fakestore/core/ui/connectivity/ConnectivityObserver.kt` — clase `internal` que usa `ConnectivityManager.registerDefaultNetworkCallback`; expone `Flow<Boolean>` (true = hay red); limpia el callback en `onInactive`
- [ ] T079 [P] [US3] Crear `MangoOfflineBanner.kt` en `core/ui/src/main/kotlin/.../MangoOfflineBanner.kt` — `@Composable fun MangoOfflineBanner(modifier)` que recoge `ConnectivityObserver` como `Flow<Boolean>` con `collectAsStateWithLifecycle`; delega en `MangoOfflineBannerContent(isOffline)`
- [ ] T080 [P] [US3] Crear `LoadingContent.kt` en `core/ui/src/main/kotlin/.../LoadingContent.kt` — `@Composable fun LoadingContent(modifier)` que centra un `MangoLoadingIndicator`
- [ ] T081 [P] [US3] Crear `EmptyContent.kt` en `core/ui/src/main/kotlin/.../EmptyContent.kt` — `@Composable fun EmptyContent(message, modifier, icon?)` que delega en `MangoEmptyState`
- [ ] T082 [P] [US3] Crear `ErrorContent.kt` en `core/ui/src/main/kotlin/.../ErrorContent.kt` — `@Composable fun ErrorContent(uiError, modifier, onRetry?)` que delega en `MangoErrorState`
- [ ] T083 [P] [US3] Crear `ShimmerModifier.kt` en `core/ui/src/main/kotlin/.../modifier/ShimmerModifier.kt` — `fun Modifier.shimmer(isLoading, baseColor, highlightColor)` con animación de gradiente horizontal
- [ ] T084 [P] [US3] Crear `ConditionalModifier.kt` en `core/ui/src/main/kotlin/.../modifier/ConditionalModifier.kt` — `fun Modifier.conditional(condition, ifTrue, ifFalse?)` inline
- [ ] T085 [P] [US3] Crear `PreviewAnnotations.kt` en `core/ui/src/main/kotlin/.../preview/PreviewAnnotations.kt` — `@PreviewLightDark` (multi-preview claro + oscuro) y `@PreviewFontScale` (fontScale 1.0, 1.5, 2.0) como annotation classes
- [ ] T086 [P] [US3] Crear `ContextExt.kt` en `core/ui/src/main/kotlin/.../ext/ContextExt.kt` — extensiones `Context.dpToPx(dp: Float): Float` y `Context.pxToDp(px: Float): Float`

### Tests US3

- [ ] T087 [P] [US3] Escribir `LoadingContentTest.kt` en `core/ui/src/test/kotlin/.../LoadingContentTest.kt` — verifica que el composable existe y no lanza excepción al renderizar
- [ ] T088 [P] [US3] Escribir `EmptyContentTest.kt` en `core/ui/src/test/kotlin/.../EmptyContentTest.kt` — verifica que `message` se muestra
- [ ] T089 [P] [US3] Escribir `ErrorContentTest.kt` en `core/ui/src/test/kotlin/.../ErrorContentTest.kt` — verifica que `onRetry` se llama al pulsar el botón cuando `severity = Blocking`
- [ ] T090 [P] [US3] Escribir `ConditionalModifierTest.kt` en `core/ui/src/test/kotlin/.../modifier/ConditionalModifierTest.kt` — verifica condición true aplica ifTrue, condición false aplica ifFalse o identidad
- [ ] T091 [US3] Compilar y testear: `./gradlew :core:ui:build`

**Checkpoint**: `:core:ui` compila y sus tests pasan.

---

## Fase 6: Pulido y validación cruzada

**Objetivo**: Verificar que los cuatro módulos cumplen todos los criterios de éxito del spec.

- [ ] T092 Ejecutar compilación completa: `./gradlew build` desde `repository/android-fake-store-app/` — los cuatro módulos deben compilar sin errores (SC-001)
- [ ] T093 [P] Ejecutar `validar-arquitectura` sobre los cuatro módulos — verificar 0 violaciones de la matriz de dependencias (SC-004)
- [ ] T094 [P] Ejecutar `validar-manejo-errores` sobre los cuatro módulos — verificar 0 violaciones de las reglas §7.13 (SC-005)
- [ ] T095 [P] Ejecutar `crear-pruebas-unitarias` para verificar cobertura: `./gradlew :core:error:koverVerify` (100% mappers) y `./gradlew :core:common:koverVerify` (≥ 80%) (SC-002)
- [ ] T096 [P] Verificar snapshot tests de design-system: `./gradlew :core:design-system:verifyPaparazziDebug` — todos los golden files deben coincidir (SC-003)
- [ ] T097 [P] Verificar regla Konsist: `./gradlew :core:design-system:test --tests "*.Material3IsolationKonsistTest"` — 0 violaciones (SC-006)
- [ ] T098a [P] Verificar a11y mínimos (RF-018b): revisar manualmente en Android Studio que los componentes táctiles del design-system muestran touch target ≥ 48dp (Layout Inspector) y que `MangoIcon`/`MangoIconButton` en modo interactivo tienen `contentDescription` no nulo; ejecutar AccessibilityScanner sobre el preview del catálogo
- [ ] T098b [P] Verificar SC-007 (manual): abrir el módulo `core/design-system` en Android Studio → seleccionar cualquier archivo de componente → confirmar que todos los `@Preview` renderizan sin errores de configuración en el panel de Preview (claro + oscuro visibles)
- [ ] T099 Ejecutar `documentar-modulo` para generar documentación de los cuatro módulos

---

## Dependencias y orden de ejecución

### Dependencias entre fases

- **Fase 1** (Setup): Sin dependencias — comenzar inmediatamente
- **Fase 2** (`:core:common`): Depende de Fase 1 — **BLOQUEA** Fases 3, 4 y 5
- **Fase 3** (US1 `:core:error`): Depende de Fase 2
- **Fase 4** (US2 `:core:design-system`): Depende de Fase 3 (necesita `UiError` para `MangoErrorState`)
- **Fase 5** (US3 `:core:ui`): Depende de Fase 4 (necesita componentes de design-system)
- **Fase 6** (Pulido): Depende de Fases 3, 4 y 5

### Dependencias entre módulos

```
:core:common ← :core:error ← :core:design-system ← :core:ui
```

### Oportunidades de paralelismo

- **Fase 1**: T001–T006 son todos paralelizables
- **Fase 2**: T007–T012 son paralelizables entre sí (solo T013–T015 dependen de que compile)
- **Fase 4 tokens**: T030–T037 paralelizables entre sí
- **Fase 4 componentes atómicos**: T038–T042 paralelizables
- **Fase 4 snapshot tests**: T057–T075 todos paralelizables (un archivo por componente)
- **Fase 5**: T079–T090 mayormente paralelizables

---

## Ejemplo de ejecución paralela — Fase 4 (design-system tokens)

```bash
# Lanzar todos los tokens en paralelo:
T030: MangoColors.kt
T031: TypographyConfig.kt
T032: MangoTypography.kt
T033: MangoSpacing.kt
T034: MangoShapes.kt
T035: MangoElevations.kt
T036: MangoMotion.kt
# → Cuando todos terminan: T037 MangoTheme.kt
```

---

## Estrategia de implementación

### MVP (solo US1 — `:core:common` + `:core:error`)

1. Completar Fase 1 (Setup Gradle)
2. Completar Fase 2 (`:core:common`)
3. Completar Fase 3 (`:core:error`)
4. **PARAR Y VALIDAR**: `./gradlew :core:common:build :core:error:build` + tests pasan
5. Los módulos de dominio ya pueden usar `Either<DomainError, T>`

### Entrega incremental

1. Setup → Fundacional (`:core:common`) → MVP
2. Añadir US1 (`:core:error`) → validar → commit
3. Añadir US2 (`:core:design-system`) → validar snapshots → commit
4. Añadir US3 (`:core:ui`) → validar → compilación final → abrir PR

---

## Notas

- `[P]` = archivos distintos, sin dependencias incompletas
- `[Story]` mapea la tarea a la user story del spec.md para trazabilidad
- Los golden files de Paparazzi se generan con `recordPaparazziDebug` y se commitean; CI usa `verifyPaparazziDebug`
- La regla Konsist (T076) se escribe como JUnit test estándar, no como plugin Gradle
- Cada módulo debe compilar antes de empezar el siguiente (dependencia transitiva del árbol)
- Commitear tras cada checkpoint de módulo
