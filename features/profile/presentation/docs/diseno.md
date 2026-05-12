# Diseño — `:features:profile:presentation`

## Diagrama de flujo

```mermaid
flowchart TB
    subgraph app["app (NavGraph)"]
        NAV["NavHost"]
    end
    subgraph presentation["features:profile:presentation"]
        NAV --> PR["PerfilRoute\n(SecureScreen)"]
        PR --> VM["PerfilViewModel\n(@HiltViewModel)"]
        PR --> PS["PerfilScreen\n(Composable puro)"]
        PS --> PIC["PerfilInfoCard\n(Composable puro)"]
        VM -->|uiState: StateFlow| PR
        VM -->|uiEffect: SharedFlow| PR
        PS -->|onEvent| VM
    end
    subgraph domain_p["features:profile:domain"]
        OP["ObtenerPerfil"]
    end
    subgraph domain_f["features:favorites:domain"]
        OCF["ObservarConteoFavoritos\n(Flow<Int>)"]
    end
    VM --> OP
    VM --> OCF
```

## Flujo reactivo del contador de favoritos

```
PerfilViewModel.cargarPerfil()
  └─ combine(
       flow { emit(obtenerPerfil(PERFIL_USER_ID)) },   // emite una sola vez
       observarConteoFavoritos()                         // Flow continuo de Room
     ) { perfilResult, conteo ->
       perfilResult.fold(
         ifLeft  → PerfilUiState.Error(errorMapper.map(error))
         ifRight → PerfilUiState.Content(PerfilContenidoUi(..., contadorFavoritos = conteo))
       )
     }.collect { estado → _uiState.update { estado } }
```

Cuando el usuario marca/desmarca un favorito desde otra pantalla, `observarConteoFavoritos()` emite el nuevo conteo y `combine` genera un nuevo `Content` con el contador actualizado sin rellamar al servidor.

## Manejo de `SecureScreen`

`PerfilRoute` envuelve `PerfilScreen` en `SecureScreen { ... }`. Este composable aplica `FLAG_SECURE` a la `Window` mediante `DisposableEffect`, evitando capturas de pantalla. Durante `LocalInspectionMode` (previews de Android Studio) el flag no se aplica para no bloquear los previews.

## Estados de UI

| Estado | Composable renderizado | Condición |
|--------|------------------------|-----------|
| `Loading` | `MangoLoadingIndicator` | Inicial y durante reintentos |
| `Content(usuario)` | `PerfilInfoCard` | Datos cargados correctamente |
| `Error(uiError)` | `MangoErrorState` | Cualquier `DomainError` |

## Gestión de errores no fatales

`PerfilViewModel` tiene un `CoroutineExceptionHandler` que captura errores no controlados del `viewModelScope`, los reporta a `Telemetry` como no fatales y emite `PerfilUiState.Error(DomainError.Unknown)`.

## Decisiones de diseño

| Decisión | Justificación |
|----------|---------------|
| `PerfilUiErrorMapper` como delegador | Reutiliza `DomainErrorToUiErrorMapper` para todos los errores excepto `NotFound`, que tiene mensaje localizado propio |
| `combine()` en lugar de `zip()` | `combine` re-emite cuando cualquiera de los dos flujos emite; permite actualizar el contador sin re-fetch |
| `PERFIL_USER_ID = 8` constante | Temporal hasta que `:features:auth` provea el userId real de sesión |
