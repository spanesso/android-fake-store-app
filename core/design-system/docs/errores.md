# Errores — `:core:design-system`

`:core:design-system` no emite `DomainError`. Es un módulo de presentación visual pura.

## MangoErrorState — Entrada de errores UI

El componente `MangoErrorState` recibe un `UiError` de `:core:error` para renderizar el mensaje localizado, la severidad y las acciones disponibles.

| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `uiError` | `UiError` | El error a mostrar (messageRes, severity, actions) |
| `onRetry` | `(() -> Unit)?` | Callback para la acción `UiErrorAction.Retry` |
| `modifier` | `Modifier` | Modificador Compose |

## MangoSnackbar — Severidades

| `MangoSnackbarSeverity` | Color | Uso |
|-------------------------|-------|-----|
| `Info` | Azul Mango | Información no crítica |
| `Success` | Verde | Operación completada |
| `Warning` | Naranja | Advertencia que requiere atención |
| `Error` | Rojo Mango | Error que debe corregirse |

Para el catálogo completo de `DomainError` → `UiError`, consultar:
- [`core/error/docs/errores.md`](../../error/docs/errores.md)
