# Módulo `:core:design-system`

**Propósito**: Implementa los tokens visuales (colores, tipografía, formas, espaciado, elevaciones, animaciones) y los 19 componentes atómicos del sistema de diseño Mango, garantizando consistencia visual en toda la aplicación.

## Tokens de diseño

| Token | Archivo | Descripción |
|-------|---------|-------------|
| `MangoColors` | `theme/MangoColors.kt` | Paleta completa (negro, blanco, grises, arena, beige; dinámicos claro/oscuro) |
| `MangoTypography` | `theme/MangoTypography.kt` | Escala tipográfica Material3 con fuente del sistema |
| `MangoShapes` | `theme/MangoShapes.kt` | Bordes redondeados: `extraSmall`(4dp) → `extraLarge`(16dp) |
| `MangoSpacing` | `theme/MangoSpacing.kt` | Espaciado: `xs`(4dp), `sm`(8dp), `md`(16dp), `lg`(24dp), `xl`(32dp), `xxl`(48dp) |
| `MangoElevations` | `theme/MangoElevations.kt` | Elevaciones: `none`, `low`(2dp), `medium`(4dp), `high`(8dp) |
| `MangoMotion` | `theme/MangoMotion.kt` | Duraciones: `fast`(150ms), `medium`(300ms), `slow`(500ms) |
| `MangoTheme` | `theme/MangoTheme.kt` | Composable raíz que aplica todos los tokens |

## Contratos públicos — Componentes

| Componente | Descripción |
|------------|-------------|
| `MangoButton` | Botón primario/secundario/text con estado loading/disabled |
| `MangoText` + `MangoLabel` | Texto con estilo tipográfico Mango |
| `MangoTextField` | Campo de entrada con estado error/disabled/focus |
| `MangoCard` | Tarjeta con elevación configurable |
| `MangoLoadingIndicator` | Spinner circular o lineal (`MangoLoadingVariant`) |
| `MangoEmptyState` | Pantalla de estado vacío con icono y acción opcional |
| `MangoErrorState` | Pantalla de estado de error con botón de reintento |
| `MangoSnackbar` | Notificación con 4 severidades (`MangoSnackbarSeverity`) |
| `MangoOfflineBannerContent` | Banner stateless de desconexión (stateful en `:core:ui`) |
| `MangoDialog` | Diálogo de confirmación con botones positivo/negativo |
| `MangoIcon` | Wrapper de iconos Material3 con tamaño semántico |
| `MangoIconButton` | Botón de icono con estado |
| `MangoTopAppBar` | Barra superior con título y navegación |
| `MangoNavigationBar` | Barra inferior de navegación |
| `MangoBottomSheet` | Hoja inferior modal |
| `MangoBadge` | Distintivo numérico/de punto |
| `MangoChip` | Chip de filtro/acción |
| `MangoDivider` | Separador horizontal/vertical |
| `MangoProductCard` | Tarjeta de producto (imagen, título, precio, favorito) |

## Dependencias

```kotlin
// core/design-system/build.gradle.kts
implementation(project(":core:error"))
```

- `:core:error` — para `UiError` en `MangoErrorState`
- Material3 — consumido internamente; **prohibido en módulos fuera de `:core:design-system`** (salvo `Surface`, `Scaffold`, `Snackbar` en `:core:ui`)

## Ejemplos de uso

```kotlin
// Envolver la app con el tema Mango
@Composable
fun App() {
    MangoTheme {
        Surface { NavHost(...) }
    }
}

// Usar componentes en una pantalla
MangoErrorState(
    uiError = state.error,
    onRetry = { viewModel.reintentar() },
)

MangoLoadingIndicator(variant = MangoLoadingVariant.Circular)
```

## Estructura interna

```
core/design-system/
├── src/main/kotlin/com/mango/fakestore/core/designsystem/
│   ├── component/    (19 componentes)
│   └── theme/        (7 archivos de tokens + MangoTheme)
└── src/test/kotlin/...
    ├── konsist/      (Material3IsolationKonsistTest)
    └── snapshot/     (Paparazzi — ejecutar por separado)
```

## Cómo regenerar esta documentación

```bash
/documentar-modulo modulo=core:design-system
```
