# Modelo de datos: ETAPA 1 — Módulos Core Fundamentales

**Rama**: `002-etapa-1-core` | **Fecha**: 2026-05-11

---

## Módulo `:core:common`

### `AppDispatchers`

```
AppDispatchers
  + io: CoroutineDispatcher          // Dispatchers.IO en producción; TestDispatcher en test
  + main: CoroutineDispatcher        // Dispatchers.Main
  + default: CoroutineDispatcher     // Dispatchers.Default
  + unconfined: CoroutineDispatcher  // Dispatchers.Unconfined
```

**Binding Hilt**: `@Singleton` via `@Provides` en `DispatchersModule`.
**Interfaz de test**: `TestAppDispatchers(TestDispatcher)` implementando `AppDispatchers`.

### Extensiones `Either<L, R>`

Funciones de extensión sobre `Either<DomainError, T>`:
- `mapLeft(transform: (L) -> L2): Either<L2, R>`
- `getOrElse(default: () -> R): R`
- `flatMap(transform: (R) -> Either<L, R2>): Either<L, R2>`
- `fold(onLeft: (L) -> C, onRight: (R) -> C): C`

Funciones de extensión sobre `Flow<Either<L, R>>`:
- `mapEitherRight(transform: (R) -> R2): Flow<Either<L, R2>>`
- `filterEitherRight(): Flow<R>`

### Extensiones Kotlin utilitarias

Categorías a implementar (solo si usadas en ≥ 2 módulos):
- **String**: `orEmpty()`, `isNotNullOrBlank()`, `truncate(maxLength)`
- **Nullable**: `ifNotNull(action)`, `orDefault(default)`
- **Collection**: `firstOrNull { }` (alias más legible), `toImmutableList()`

---

## Módulo `:core:error`

### `DomainError` (jerarquía sealed completa de §7.2)

```
sealed interface DomainError
  val cause: Throwable?

  sealed interface Network : DomainError
    data class NoConnection(cause: Throwable? = null) : Network
    data class Timeout(cause: Throwable? = null) : Network
    data class Server(httpCode: Int, cause: Throwable? = null) : Network
    data class Unauthorized(cause: Throwable? = null) : Network
    data class Forbidden(cause: Throwable? = null) : Network
    data class NotFound(cause: Throwable? = null) : Network
    data class Parsing(cause: Throwable? = null) : Network

  sealed interface Database : DomainError
    data class ReadFailed(cause: Throwable? = null) : Database
    data class WriteFailed(cause: Throwable? = null) : Database
    data class NotFound(cause: Throwable? = null) : Database
    data class IntegrityViolation(cause: Throwable? = null) : Database

  sealed interface Security : DomainError
    data object BiometricUnavailable : Security  // cause = null
    data object BiometricLockout : Security      // cause = null
    data object RootDetected : Security          // cause = null
    data object IntegrityFailed : Security       // cause = null
    data object SessionExpired : Security        // cause = null

  data class Validation(fields: Map<String, String>) : DomainError
    // cause = null

  data class Unknown(cause: Throwable? = null) : DomainError
```

### `UiError`

```
data class UiError
  @StringRes messageRes: Int
  severity: Severity
  actions: List<UiErrorAction>
  errorCode: String                // "NET-401", "DB-READ", etc.

  enum class Severity
    Info, Warning, Blocking, Fatal

  sealed interface UiErrorAction
    object Retry : UiErrorAction
    object Dismiss : UiErrorAction
    object Login : UiErrorAction
    object OpenSettings : UiErrorAction
```

### Mappers

```
NetworkErrorMapper
  fun map(throwable: Throwable): DomainError.Network
  // Reglas de §7.4: IOException→NoConnection, SocketTimeout→Timeout, Http4xx/5xx→...

DatabaseErrorMapper
  fun map(throwable: Throwable): DomainError.Database
  // SQLiteConstraintException→IntegrityViolation, SQLiteException→ReadFailed/WriteFailed

DomainErrorToUiErrorMapper
  fun map(error: DomainError): UiError
  // Tabla completa DomainError → UiError con messageRes, severity, actions, errorCode
```

### Helpers de captura

```
suspend fun <T> safeApiCall(block: suspend () -> T): Either<DomainError, T>
  // Captura cualquier Throwable → NetworkErrorMapper → DomainError
  // Propaga CancellationException sin envolver

suspend fun <T> safeDbCall(block: suspend () -> T): Either<DomainError, T>
  // Captura SQLiteException y derivados → DatabaseErrorMapper → DomainError
  // Propaga CancellationException sin envolver
```

### Tabla de mapeo `DomainError → UiError` (referencia para DomainErrorToUiErrorMapper)

| DomainError | messageRes (clave i18n) | Severity | Actions | errorCode |
|-------------|------------------------|----------|---------|-----------|
| Network.NoConnection | `error_red_sin_conexion` | Blocking | [Retry] | NET-000 |
| Network.Timeout | `error_red_tiempo_agotado` | Blocking | [Retry] | NET-001 |
| Network.Server(5xx) | `error_red_servidor` | Blocking | [Retry] | NET-500 |
| Network.Unauthorized | `error_red_no_autorizado` | Fatal | [Login] | NET-401 |
| Network.Forbidden | `error_red_sin_permiso` | Blocking | [Dismiss] | NET-403 |
| Network.NotFound | `error_red_no_encontrado` | Info | [Dismiss] | NET-404 |
| Network.Parsing | `error_red_formato` | Blocking | [Retry] | NET-002 |
| Database.ReadFailed | `error_bd_lectura` | Blocking | [Retry] | DB-001 |
| Database.WriteFailed | `error_bd_escritura` | Blocking | [Retry] | DB-002 |
| Database.NotFound | `error_bd_no_encontrado` | Info | [Dismiss] | DB-003 |
| Database.IntegrityViolation | `error_bd_integridad` | Blocking | [Dismiss] | DB-004 |
| Security.BiometricUnavailable | `error_seg_biometria_no_disponible` | Warning | [OpenSettings] | SEC-001 |
| Security.BiometricLockout | `error_seg_biometria_bloqueada` | Blocking | [Dismiss] | SEC-002 |
| Security.RootDetected | `error_seg_root_detectado` | Fatal | [] | SEC-003 |
| Security.IntegrityFailed | `error_seg_integridad` | Fatal | [] | SEC-004 |
| Security.SessionExpired | `error_seg_sesion_expirada` | Fatal | [Login] | SEC-005 |
| Validation | `error_validacion_formulario` | Warning | [Dismiss] | VAL-001 |
| Unknown | `error_desconocido` | Blocking | [Retry, Dismiss] | UNK-000 |

---

## Módulo `:core:design-system`

### Tokens de color: `MangoColors`

```
MangoColors (object)
  // Neutros
  neutroBlanco: Color = #FFFFFF
  neutroHueso: Color  = #F5F1EC
  neutroArena: Color  = #E6DED3
  neutroPiedra: Color = #B8AEA2
  neutroGrafito: Color= #2B2B2B
  neutroNegro: Color  = #0A0A0A
  // Acentos
  acentoOro: Color   = #B08D57
  acentoRojo: Color  = #8B1E1E
  // Semánticos
  semanticoExito: Color= #2E7D32
  semanticoError: Color= #B00020
  semanticoAviso: Color= #B26500
```

Light scheme: `lightColorScheme(primary=acentoOro, background=neutroHueso, ...)`
Dark scheme: `darkColorScheme(primary=acentoOro, background=neutroNegro, ...)`

### Tokens de tipografía: `MangoTypography`

```
MangoTypography — FontFamily configurada en TypographyConfig.kt
  display:      fontSize=57sp, lineHeight=64sp
  h1:           fontSize=45sp, lineHeight=52sp
  h2:           fontSize=36sp, lineHeight=44sp
  h3:           fontSize=28sp, lineHeight=36sp
  titleLarge:   fontSize=22sp, lineHeight=28sp
  titleMedium:  fontSize=16sp, fontWeight=Medium
  bodyLarge:    fontSize=16sp, lineHeight=24sp
  bodyMedium:   fontSize=14sp, lineHeight=20sp
  bodySmall:    fontSize=12sp, lineHeight=16sp
  label:        fontSize=11sp, fontWeight=Medium
  caption:      fontSize=10sp, lineHeight=14sp
```

`TypographyConfig.kt` expone `val HeadlineFontFamily: FontFamily` y `val BodyFontFamily: FontFamily` como vals modificables.

### Tokens de espaciado: `MangoSpacing`

```
MangoSpacing (object)
  xxs: Dp = 4.dp   xs: Dp = 8.dp    sm: Dp = 12.dp   md: Dp = 16.dp
  lg:  Dp = 24.dp  xl: Dp = 32.dp   xxl: Dp = 48.dp  xxxl: Dp = 64.dp
```

### Tokens de forma: `MangoShapes`

```
MangoShapes
  none: CornerBasedShape = RoundedCornerShape(0.dp)
  sm:   CornerBasedShape = RoundedCornerShape(4.dp)
  md:   CornerBasedShape = RoundedCornerShape(8.dp)
  lg:   CornerBasedShape = RoundedCornerShape(16.dp)
  pill: CornerBasedShape = RoundedCornerShape(50%)
```

### Tokens de elevación y movimiento

```
MangoElevations (object): 0.dp, 1.dp, 2.dp, 4.dp, 8.dp

MangoMotion (object)
  standardEasing: Easing
  emphasizedEasing: Easing
  durationFast: Int = 150   // ms
  durationMedium: Int = 300
  durationSlow: Int = 500
```

### Componentes — estados relevantes por componente

| Componente | Estados de Preview |
|-----------|-------------------|
| MangoButton (5 variantes × 3 tamaños) | idle, loading, pressed, disabled |
| MangoTextField (3 variantes) | idle, focused, filled, error, disabled |
| MangoLabel / MangoText | — (solo claro/oscuro) |
| MangoCard (3 variantes) | idle, pressed |
| MangoProductCard | idle, loading (shimmer) |
| MangoIconButton | idle, pressed, disabled |
| MangoIcon | — (claro/oscuro) |
| MangoChip (4 tipos) | idle, selected, disabled |
| MangoDivider | — |
| MangoTopAppBar (4 variantes) | scrolled, unscrolled |
| MangoBottomBar / MangoNavigationBar | item selected, unselected, badge |
| MangoDialog | — |
| MangoBottomSheet | — |
| MangoLoadingIndicator (3 tipos) | — (circular, lineal, shimmer) |
| MangoEmptyState | — |
| MangoErrorState | con botón retry, sin botón retry |
| MangoBadge | con número, sin número (punto) |
| MangoSnackbar (4 severidades) | — |
| MangoOfflineBannerContent | isOffline=true, isOffline=false |

---

## Módulo `:core:ui`

### Composables de estado

```
LoadingContent(modifier: Modifier = Modifier)
  // Delega en MangoLoadingIndicator

EmptyContent(
  message: String,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null
)
  // Delega en MangoEmptyState

ErrorContent(
  uiError: UiError,
  onRetry: (() -> Unit)? = null,
  modifier: Modifier = Modifier
)
  // Delega en MangoErrorState
```

### `MangoOfflineBanner` (stateful)

```
MangoOfflineBanner(modifier: Modifier = Modifier)
  // Internalamente: ConnectivityObserver → Flow<Boolean> → MangoOfflineBannerContent
  // ConnectivityObserver es internal, no forma parte de la API pública del módulo
```

### Modifiers de utilidad

```
fun Modifier.shimmer(
  isLoading: Boolean,
  baseColor: Color = MangoColors.neutroArena,
  highlightColor: Color = MangoColors.neutroHueso
): Modifier

fun Modifier.conditional(
  condition: Boolean,
  ifTrue: Modifier.() -> Modifier,
  ifFalse: (Modifier.() -> Modifier)? = null
): Modifier
```

### Anotaciones y extensiones de Preview

```
@PreviewLightDark       // Combina @Preview light + @Preview dark
@PreviewFontScale       // Variantes de fontScale: 1.0, 1.5, 2.0

// Extensiones Context
fun Context.dpToPx(dp: Float): Float
fun Context.pxToDp(px: Float): Float
```
