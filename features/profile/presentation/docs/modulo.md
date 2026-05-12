# Módulo `:features:profile:presentation`

**Propósito**: Expone la pantalla de perfil de usuario con datos reactivos, contador de favoritos actualizado en tiempo real y manejo completo de estados de error.

## Contratos públicos

| Símbolo | Descripción | Consumidores |
|---------|-------------|--------------|
| `PerfilRoute` | Composable de punto de entrada; crea el `PerfilViewModel` y observa estado | `:app` (NavGraph) |
| `PerfilUiState` | Estado de UI: `Loading`, `Content(usuario)`, `Error(uiError)` | `PerfilScreen` |
| `PerfilUiEvent` | Eventos de usuario: `Retry` | `PerfilScreen` → `PerfilViewModel` |
| `PerfilUiEffect` | Efectos de un solo disparo: `MostrarSnackbar(uiError)` | `PerfilRoute` |

## Dependencias

- `:features:profile:domain` — `ObtenerPerfil`, `Usuario`
- `:features:favorites:domain` — `ObservarConteoFavoritos` (Flow reactivo)
- `:core:error` — `DomainError`, `UiError`, `DomainErrorToUiErrorMapper`
- `:core:design-system` — componentes `Mango*`
- `:core:ui` — `MangoLoadingIndicator`, `MangoErrorState`
- `:core:analytics` — `Telemetry`
- `:core:security` — `SecureScreen` (FLAG_SECURE)

## Estructura interna

```
features/profile/presentation/
└── src/main/kotlin/.../presentation/
    ├── di/
    │   └── PerfilPresentationModule.kt    @Provides PerfilUiErrorMapper
    ├── mapper/
    │   └── PerfilUiErrorMapper.kt         sobreescribe Network.NotFound → NET-404
    ├── model/
    │   └── PerfilContenidoUi.kt           modelo de vista con contadorFavoritos
    ├── ui/
    │   ├── components/
    │   │   └── PerfilInfoCard.kt          card de datos del usuario (pura)
    │   ├── route/
    │   │   └── PerfilRoute.kt             punto de entrada con hiltViewModel
    │   ├── screens/
    │   │   └── PerfilScreen.kt            pantalla pura (sin ViewModel)
    │   └── state/
    │       ├── PerfilUiState.kt
    │       ├── PerfilUiEvent.kt
    │       └── PerfilUiEffect.kt
    └── viewmodel/
        └── PerfilViewModel.kt             @HiltViewModel; combine() con contador
```

## Cómo regenerar esta documentación

```bash
/documentar-modulo features:profile
```
