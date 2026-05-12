# Errores — `:core:ui`

`:core:ui` no genera ni mapea `DomainError`. Consume `UiError` de `:core:error` para renderizar estados de error en los composables.

## ErrorContent — Parámetros de error

```kotlin
@Composable
fun ErrorContent(
    uiError: UiError?,          // null = sin error; non-null = mostrar MangoErrorState
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    content: @Composable () -> Unit,
)
```

El componente delega la presentación visual a `MangoErrorState` (de `:core:design-system`). No interpreta el tipo de error, solo lo pasa.

## MangoOfflineBanner — Estado de red

El banner emite visualmente el estado de desconexión, pero no lo clasifica como `DomainError`. La desconexión detectada a nivel de sistema se complementa con `DomainError.Network.NoConnection` que llega de las llamadas de red.

Para el catálogo completo de errores, consultar:
- [`core/error/docs/errores.md`](../../error/docs/errores.md)
