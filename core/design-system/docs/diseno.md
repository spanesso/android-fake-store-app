# Diseño interno — `:core:design-system`

## Arquitectura de tokens y componentes

```mermaid
flowchart TB
    subgraph tokens ["Tokens (theme/)"]
        MC[MangoColors]
        MT[MangoTypography]
        MS[MangoShapes]
        MSp[MangoSpacing]
        ME[MangoElevations]
        MM[MangoMotion]
    end

    subgraph tema ["Tema"]
        MTh[MangoTheme<br/>«Composable raíz»]
    end

    subgraph componentes ["Componentes (component/)"]
        direction LR
        C1[MangoButton]
        C2[MangoText / MangoLabel]
        C3[MangoTextField]
        C4[MangoCard]
        C5[MangoLoadingIndicator]
        C6[MangoEmptyState]
        C7[MangoErrorState]
        C8[MangoSnackbar]
        C9[MangoOfflineBannerContent]
        C10[MangoDialog]
        C11["Otros (9 más)"]
    end

    tokens -->|aplica| MTh
    MTh -->|MaterialTheme| componentes
    C7 -->|usa| UiError[":core:error<br/>UiError"]
```

## Decisiones de diseño

### Fuente del sistema (no Playfair Display)

Se usa la fuente del sistema Android (Roboto / sans-serif) a través de `TypographyConfig.kt`. Incorporar un TTF externo requeriría acceso a la CDN de Mango y permisos de licencia; se documentó como decisión en la especificación.

### Material3 aislado en design-system

El test de Konsist `Material3IsolationKonsistTest` garantiza que ningún módulo externo importe `androidx.compose.material3.*`, excepto las excepciones declaradas en `allowedPackages` y `allowedClassesInCoreUi`.

### MangoOfflineBannerContent (stateless)

El componente de UI en design-system es deliberadamente stateless (solo recibe `isOffline: Boolean`). El estado real (observar `ConnectivityManager`) vive en `:core:ui` dentro de `MangoOfflineBanner`. Esto permite usar el componente en previews sin dependencias de plataforma.

### Paparazzi excluido del ciclo estándar

Paparazzi 1.3.5 tiene una incompatibilidad con AGP 9.0.1 (`NoSuchElementException` en `Renderer.configureBuildProperties`). Los snapshot tests se excluyen de `testDebugUnitTest` y solo se ejecutan con `verifyPaparazziDebug`.

## Puntos de extensión

- **Nuevo token**: añadir en el archivo de token correspondiente; `MangoTheme` lo recoge automáticamente si se usa `MaterialTheme.colorScheme.*`
- **Nuevo componente**: crear en `component/`, seguir el patrón `MangoXxx.kt` con previews light/dark
- **Nuevo snapshot test**: crear en `snapshot/` con la clase Paparazzi; se ejecuta por separado
