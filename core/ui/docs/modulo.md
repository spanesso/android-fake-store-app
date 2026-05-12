# Módulo `:core:ui`

**Propósito**: Proporciona composables de estado reutilizables, modificadores Compose de propósito general y la gestión de conectividad de red para las pantallas de features.

## Contratos públicos

| Símbolo | Descripción |
|---------|-------------|
| `LoadingContent` | Wrapper que muestra `MangoLoadingIndicator` mientras `isLoading=true`; de lo contrario renderiza `content()` |
| `EmptyContent` | Wrapper que muestra `MangoEmptyState` cuando `isEmpty=true` |
| `ErrorContent` | Wrapper que muestra `MangoErrorState` cuando `uiError != null` |
| `MangoOfflineBanner` | Banner de desconexión stateful; observa `ConnectivityManager` via `ConnectivityObserver` |
| `ConnectivityObserver` | Interfaz `Flow<Boolean>` de estado de red (testeable por inyección) |
| `Modifier.shimmer(...)` | Efecto shimmer animado para placeholders de carga |
| `Modifier.conditional(...)` | Aplica un `Modifier` condicionalmente |
| `PreviewAnnotations` | `@PreviewLightDark`, `@PreviewFontScale`, `@MangoPreview` para vistas previas multi-modo |
| `Context.dpToPx(dp)` | Convierte dp a px usando la densidad del display |
| `Context.pxToDp(px)` | Convierte px a dp |

## Dependencias

```kotlin
// core/ui/build.gradle.kts
implementation(project(":core:design-system"))
implementation(project(":core:error"))
implementation(libs.kotlinx.coroutines.android)
implementation(libs.androidx.lifecycle.runtime.compose)
```

## Ejemplos de uso

```kotlin
// En una pantalla de feature
@Composable
fun ProductosScreen(uiState: ProductosUiState, onRetry: () -> Unit) {
    LoadingContent(isLoading = uiState is ProductosUiState.Loading) {
        when (uiState) {
            is ProductosUiState.Empty   -> EmptyContent(isEmpty = true, mensaje = "Sin productos")
            is ProductosUiState.Error   -> ErrorContent(uiError = uiState.error, onRetry = onRetry)
            is ProductosUiState.Content -> ProductosLista(productos = uiState.productos)
            else -> Unit
        }
    }
}

// En el layout raíz de la app
@Composable
fun AppRoot() {
    MangoTheme {
        MangoOfflineBanner()
        NavHost(...)
    }
}

// Efecto shimmer en placeholder
Box(modifier = Modifier.size(200.dp).shimmer())

// Modificador condicional
Modifier.conditional(isSelected) { border(2.dp, MangoColors.negro) }

// Preview multi-modo
@MangoPreview
@Composable
private fun PreviewBotonPrimario() {
    MangoTheme { MangoButton("Comprar", onClick = {}) }
}
```

## Estructura interna

```
core/ui/
├── src/main/kotlin/com/mango/fakestore/core/ui/
│   ├── MangoOfflineBanner.kt          (stateful; usa ConnectivityObserver)
│   ├── composable/
│   │   ├── LoadingContent.kt
│   │   ├── EmptyContent.kt
│   │   └── ErrorContent.kt
│   ├── connectivity/
│   │   └── ConnectivityObserver.kt    (interfaz + DefaultConnectivityObserver)
│   ├── ext/
│   │   └── ContextExt.kt
│   ├── modifier/
│   │   ├── ShimmerModifier.kt
│   │   └── ConditionalModifier.kt
│   └── preview/
│       └── PreviewAnnotations.kt
└── src/test/kotlin/...
    ├── composable/LoadingContentTest.kt
    ├── composable/EmptyContentTest.kt
    ├── composable/ErrorContentTest.kt
    ├── connectivity/ConnectivityObserverTest.kt
    └── modifier/ConditionalModifierTest.kt
```

## Cómo regenerar esta documentación

```bash
/documentar-modulo modulo=core:ui
```
