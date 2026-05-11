---
name: crear-vista
description: 'Genera una pantalla Compose completa dentro de un módulo `:features:<modulo>:presentation` del proyecto Mango Fake Store siguiendo MVVM puro y la regla R0.3 (vistas sin ViewModel). Crea cinco artefactos: `XxxScreen` (Composable puro que recibe `XxxUiState` y `(XxxUiEvent) -> Unit`), `XxxRoute` (wrapper con `hiltViewModel`), `XxxViewModel` (con StateFlow + SharedFlow + CoroutineExceptionHandler), `XxxUiState/Event/Effect` (sealed interfaces que incluyen ramas `Loading`, `Empty`, `Error(uiError)`, `Content`, `Retry`, `ShowSnackbar(uiError)`), `@Preview` por cada estado en claro/oscuro usando componentes de `:core:design-system` (`MangoErrorState`, `MangoLoadingIndicator`, etc.), y stubs de Compose UI test + snapshot test. Usar SIEMPRE que el usuario pida "crea la pantalla X", "nueva vista Y", "genera screen Z", "scaffolding de UI para favoritos/productos/perfil/auth" o equivalente. NO usar para retocar pantallas existentes, añadir un nuevo estado a una pantalla ya creada, mover un Composable entre paquetes, ni para componentes del design system (eso vive en `:core:design-system` y se hace a mano).'
---

# Skill `crear-vista` — pantallas Compose Route+Screen sin ViewModel

Genera el scaffolding completo de una pantalla Compose siguiendo la regla R0.3 (vistas sin ViewModel) del prompt maestro. El objetivo es que cualquier desarrollador pueda generar una pantalla nueva en un módulo `:features:*:presentation` y obtener un esqueleto con todos los estados (Loading/Empty/Error/Content), todos los previews y los stubs de test ya listos.

## Cuándo usar

Frases que disparan:

- "Crea la pantalla de productos."
- "Nueva vista de favoritos."
- "Genera el screen de perfil."
- "Scaffolding de UI para `LoginBiometrico`."
- "Necesito el route + screen + viewmodel para la pantalla de detalle."

NO disparar para:

- Modificar pantallas existentes (añadir/quitar estados, cambiar Composables internos).
- Generar componentes del design system (`MangoButton`, `MangoChip`, etc.) — eso se hace a mano en `:core:design-system`.
- Retoques visuales (espaciados, colores, tipografía).
- Generar archivos en `:app` (NavHost raíz, etc.).

## Inputs

| Input | Obligatorio | Ejemplo |
|---|---|---|
| `nombre` | sí | `Productos`, `Favoritos`, `Perfil`, `LoginBiometrico` |
| `modulo` | sí | `products`, `favorites`, `profile`, `auth` |
| `estados` | sí | lista de estados extra del modelo (siempre incluye Loading/Empty/Error/Content); ej. `["ContentConFavoritos"]` |
| `eventos_usuario` | sí | lista de `UiEvent` esperados; ej. `["AbrirDetalle(productoId)", "ToggleFavorito(productoId)", "Retry"]` |
| `efectos` | no | lista de `UiEffect`; por defecto `["ShowSnackbar(uiError)"]` |
| `descripcion_breve` | no | "Listado de productos con favoritos toggleables." |

Validaciones:

1. `nombre` en PascalCase, sin "Screen" ni "Route" al final (el skill los añade).
2. `modulo` existe en `repository/android-fake-store-app/features/` y tiene submódulo `presentation`.
3. `estados` siempre debe incluir las cuatro bases (`Loading`, `Empty`, `Error`, `Content`).
4. Si el usuario sugiere mostrar `throwable.message` en la UI: **rechazar**. Solo se acepta `UiError` con `messageRes`.

## Outputs

Bajo `features/<modulo>/presentation/src/main/kotlin/com/mango/fakestore/features/<modulo>/presentation/`:

```
ui/screens/<Nombre>Screen.kt              ← Composable puro, sin ViewModel
ui/route/<Nombre>Route.kt                 ← Wrapper con hiltViewModel
ui/screens/<Nombre>UiState.kt             ← sealed interface
ui/screens/<Nombre>UiEvent.kt             ← sealed interface
ui/screens/<Nombre>UiEffect.kt            ← sealed interface
viewmodel/<Nombre>ViewModel.kt            ← @HiltViewModel
ui/screens/<Nombre>ScreenPreviews.kt      ← @Preview por cada estado en claro+oscuro
```

Y bajo `features/<modulo>/presentation/src/test/kotlin/...`:

```
<Nombre>ViewModelTest.kt                  ← Turbine + dispatcher de test
ui/screens/<Nombre>ScreenSnapshotTest.kt  ← Paparazzi/Roborazzi por estado
```

Y bajo `features/<modulo>/presentation/src/androidTest/kotlin/...`:

```
ui/screens/<Nombre>ScreenComposeTest.kt   ← createAndroidComposeRule, smoke + retry
```

## Reglas duras (R0.3, R0.4, §3, §7)

1. **`<Nombre>Screen` (Composable puro)** debe recibir `(state: <Nombre>UiState, onEvent: (<Nombre>UiEvent) -> Unit, modifier: Modifier = Modifier)` y ni un solo parámetro de ViewModel ni de Hilt.
2. **`<Nombre>Screen` no importa**: `androidx.hilt.navigation.compose.hiltViewModel`, `androidx.lifecycle.viewmodel.*`, `com.mango.fakestore.core.error.DomainError`, `Throwable`. Si la plantilla los introduce por error, fallar.
3. **`<Nombre>Route`** instancia el ViewModel (`val vm = hiltViewModel<<Nombre>ViewModel>()`), colecta `uiState` con `collectAsStateWithLifecycle()` y maneja `uiEffect` con `LaunchedEffect`.
4. **Errores en UI**: render del estado `Error` usa `MangoErrorState(uiError = ..., onRetry = { onEvent(<Nombre>UiEvent.Retry) })`. **Prohibido** mostrar `throwable.message` o concatenar strings hardcoded.
5. **Loading**: `MangoLoadingIndicator()` del design system.
6. **Empty**: `MangoEmptyState(...)` con `stringResource(R.string.<modulo>_vacio_titulo)`.
7. **Snackbar**: en `<Nombre>Route`, ante `<Nombre>UiEffect.ShowSnackbar(uiError)`, mostrar `MangoSnackbar(severity = uiError.severity, messageRes = uiError.messageRes, actions = uiError.actions)`.
8. **Previews**: uno por cada estado declarado + variantes `@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)`. Usar nombres descriptivos: `@Preview(name = "Loading - claro")`, `@Preview(name = "Error - oscuro")`.
9. **Test Compose**: smoke test que renderiza `<Nombre>Screen` con cada estado y verifica que existe un `Text` clave o un `MangoErrorState`/`MangoLoadingIndicator`. Test específico de Retry que dispara `<Nombre>UiEvent.Retry` y verifica la lambda.
10. **Test ViewModel**: con Turbine, verifica las transiciones `Loading → Content` (happy path) y `Loading → Error(uiError)` (un test por cada `DomainError` que el ViewModel pueda recibir).
11. **`CoroutineExceptionHandler`**: el `<Nombre>ViewModel` declara `private val errorHandler = CoroutineExceptionHandler { _, t -> reportarYEmitirError(t) }` y lo usa en cada `viewModelScope.launch(errorHandler)`. Nunca `try/catch` genérico en el ViewModel.

## Variables de plantilla

| Placeholder | Valor | Ejemplo |
|---|---|---|
| `{{Nombre}}` | nombre PascalCase | `Productos` |
| `{{nombre}}` | nombre lowercase para strings de recursos | `productos` |
| `{{modulo}}` | módulo | `products` |
| `{{paqueteRaiz}}` | `com.mango.fakestore.features.<modulo>.presentation` | |
| `{{estadosContent}}` | bloque de `data class` para variantes de Content | |
| `{{eventosUsuario}}` | bloque de `data class` para `UiEvent` | |
| `{{efectos}}` | bloque de `data class` para `UiEffect` | |
| `{{previewsPorEstado}}` | bloque de `@Preview` funciones por estado | |

## Flujo de ejecución

1. Validar inputs (sección "Inputs").
2. Cargar plantillas de `assets/templates/`.
3. Generar los 5 archivos de `main`, los 2 de `test` y el 1 de `androidTest`.
4. Verificar que `<Nombre>Screen.kt` no contiene los imports prohibidos (regla 2).
5. Reportar al usuario lista de archivos y comandos para previews + test.

## Plantillas

Las plantillas viven en `assets/templates/`:

- `Screen.kt.template` — `<Nombre>Screen` Composable puro.
- `Route.kt.template` — `<Nombre>Route` wrapper.
- `UiState.kt.template` — `sealed interface <Nombre>UiState`.
- `UiEvent.kt.template` — `sealed interface <Nombre>UiEvent`.
- `UiEffect.kt.template` — `sealed interface <Nombre>UiEffect`.
- `ViewModel.kt.template` — `@HiltViewModel class <Nombre>ViewModel`.
- `Previews.kt.template` — `@Preview` por cada estado.
- `ScreenComposeTest.kt.template` — Compose UI test.
- `ScreenSnapshotTest.kt.template` — snapshot test (Paparazzi/Roborazzi).
- `ViewModelTest.kt.template` — test con Turbine.

## Verificación pos-generación

Tras escribir los archivos, ejecutar un grep rápido para garantizar las reglas duras:

```bash
grep -nE "(hiltViewModel|DomainError|throwable\.message|\.message)" features/<modulo>/presentation/.../<Nombre>Screen.kt
# debe imprimir 0 líneas
```

Si el grep encuentra coincidencias, **abortar** y volver a generar usando las plantillas correctas.

## Reporte final

```
✅ Pantalla `<Nombre>` creada en `:features:<modulo>:presentation`.

Archivos generados (10):
  ui/screens/<Nombre>Screen.kt
  ui/screens/<Nombre>UiState.kt
  ui/screens/<Nombre>UiEvent.kt
  ui/screens/<Nombre>UiEffect.kt
  ui/screens/<Nombre>ScreenPreviews.kt
  ui/route/<Nombre>Route.kt
  viewmodel/<Nombre>ViewModel.kt
  test/.../<Nombre>ViewModelTest.kt
  test/.../<Nombre>ScreenSnapshotTest.kt
  androidTest/.../<Nombre>ScreenComposeTest.kt

Previews disponibles en Android Studio: <N> previews (Loading, Empty, Error, Content × claro/oscuro).

Para verificar:
  ./gradlew :features:<modulo>:presentation:lint
  ./gradlew :features:<modulo>:presentation:test
  ./gradlew :features:<modulo>:presentation:verifyPaparazziDebug

Siguiente paso sugerido:
  Conectar `<Nombre>Route` al NavHost en `:app` y añadir las cadenas en `res/values/strings.xml`.
```
