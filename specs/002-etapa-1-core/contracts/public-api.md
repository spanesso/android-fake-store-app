# Contratos de API pública — ETAPA 1 módulos core

**Fecha**: 2026-05-11 | **Rama**: `002-etapa-1-core`

Estos contratos definen las superficies públicas que cada módulo expone a sus consumidores.
Cualquier cambio en estas firmas requiere un bump de versión y actualización del plan.

---

## `:core:common` — API pública

### Paquete `com.mango.fakestore.core.common.dispatchers`

```kotlin
// Interfaz inyectable de dispatchers
interface AppDispatchers {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}

// Implementación de producción (binding Hilt @Singleton)
class DefaultAppDispatchers @Inject constructor() : AppDispatchers

// Módulo Hilt
@Module @InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides @Singleton fun provideDispatchers(): AppDispatchers
}
```

### Paquete `com.mango.fakestore.core.common.either`

```kotlin
// Extensiones Either
fun <L, R, R2> Either<L, R>.flatMap(f: (R) -> Either<L, R2>): Either<L, R2>
fun <L, R> Either<L, R>.getOrElse(default: () -> R): R
fun <L, L2, R> Either<L, R>.mapLeft(f: (L) -> L2): Either<L2, R>
fun <L, R, C> Either<L, R>.fold(onLeft: (L) -> C, onRight: (R) -> C): C

// Extensiones Flow<Either>
fun <L, R, R2> Flow<Either<L, R>>.mapEitherRight(f: (R) -> R2): Flow<Either<L, R2>>
fun <L, R> Flow<Either<L, R>>.filterEitherRight(): Flow<R>
```

---

## `:core:error` — API pública

### Paquete `com.mango.fakestore.core.error`

```kotlin
sealed interface DomainError { val cause: Throwable? }
// (jerarquía completa — ver data-model.md)

data class UiError(
    @StringRes val messageRes: Int,
    val severity: UiError.Severity,
    val actions: List<UiError.UiErrorAction>,
    val errorCode: String,
) {
    enum class Severity { Info, Warning, Blocking, Fatal }
    sealed interface UiErrorAction {
        object Retry : UiErrorAction
        object Dismiss : UiErrorAction
        object Login : UiErrorAction
        object OpenSettings : UiErrorAction
    }
}
```

### Paquete `com.mango.fakestore.core.error.mapper`

```kotlin
class NetworkErrorMapper {
    fun map(throwable: Throwable): DomainError.Network
}

class DatabaseErrorMapper {
    fun map(throwable: Throwable): DomainError.Database
}

class DomainErrorToUiErrorMapper {
    fun map(error: DomainError): UiError
}
```

### Paquete `com.mango.fakestore.core.error.ext`

```kotlin
suspend fun <T> safeApiCall(block: suspend () -> T): Either<DomainError, T>
suspend fun <T> safeDbCall(block: suspend () -> T): Either<DomainError, T>
```

---

## `:core:design-system` — API pública

### Paquete `com.mango.fakestore.core.designsystem.theme`

```kotlin
// Tema principal — wrapper obligatorio en toda la app
@Composable
fun MangoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
)

// Acceso a tokens desde cualquier Composable dentro de MangoTheme
object MangoTheme {
    val colors: ColorScheme @Composable get()
    val typography: Typography @Composable get()
    val shapes: Shapes @Composable get()
}

// Tokens directos (sin necesitar el bloque MangoTheme)
object MangoColors { /* tokens de color */ }
object MangoSpacing { /* tokens de espaciado */ }
object MangoElevations { /* tokens de elevación */ }
object MangoMotion { /* easings y duraciones */ }
```

### Paquete `com.mango.fakestore.core.designsystem.typography`

```kotlin
// Punto de configuración de fuentes — reemplazar para cambiar tipografía global
object TypographyConfig {
    var headlineFontFamily: FontFamily = FontFamily.Serif
    var bodyFontFamily: FontFamily = FontFamily.Default
}
```

### Paquete `com.mango.fakestore.core.designsystem.component`

Componentes con contrato de parámetros mínimos:

```kotlin
@Composable fun MangoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: MangoButtonVariant = Primary,
    size: MangoButtonSize = Medium,
    state: MangoButtonState = Idle,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
)

@Composable fun MangoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    variant: MangoTextFieldVariant = Outlined,
    isError: Boolean = false,
    supportingText: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    enabled: Boolean = true,
    isPassword: Boolean = false,
)

@Composable fun MangoErrorState(
    uiError: UiError,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
)

@Composable fun MangoOfflineBannerContent(
    isOffline: Boolean,
    modifier: Modifier = Modifier,
)

// (Contratos completos de los demás componentes en implementación)
```

---

## `:core:ui` — API pública

### Paquete `com.mango.fakestore.core.ui`

```kotlin
@Composable fun LoadingContent(modifier: Modifier = Modifier)

@Composable fun EmptyContent(
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
)

@Composable fun ErrorContent(
    uiError: UiError,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
)

@Composable fun MangoOfflineBanner(modifier: Modifier = Modifier)
```

### Paquete `com.mango.fakestore.core.ui.modifier`

```kotlin
fun Modifier.shimmer(
    isLoading: Boolean,
    baseColor: Color = MangoColors.neutroArena,
    highlightColor: Color = MangoColors.neutroHueso,
): Modifier

fun Modifier.conditional(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: (Modifier.() -> Modifier)? = null,
): Modifier
```

### Anotaciones de preview

```kotlin
@PreviewLightDark   // annotation class — genera preview claro + oscuro
@PreviewFontScale   // annotation class — genera preview con fontScale 1.0, 1.5, 2.0
```
