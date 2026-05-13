# Diseño — `:core:security`

## Diagrama de arquitectura

```mermaid
flowchart TB
  subgraph security[core:security]
    IC[IntegrityChecker<br>interface]
    ICI[IntegrityCheckerImpl]
    SS[SecureScreen<br>@Composable]
  end

  subgraph android[Android SDK]
    FLAG[FLAG_SECURE]
  end

  subgraph libs[Librerías]
    RB[RootBeer]
  end

  ICI -.implementa.-> IC
  ICI --> RB
  SS --> FLAG

  VM[ViewModels] --> IC
  screens[Screens] --> SS
```

## Decisiones de diseño

### IntegrityChecker como interfaz

`IntegrityChecker` es una interfaz para facilitar el testing: se puede inyectar un `FakeIntegrityChecker` en tests sin necesitar `Context` real ni RootBeer. `IntegrityCheckerImpl` requiere `@ApplicationContext`.

### SecureScreen usa DisposableEffect

`FLAG_SECURE` se añade en `DisposableEffect(Unit)` (único) y se elimina en `onDispose`. Esto garantiza que la bandera se limpia cuando el Composable abandona la composición, evitando que otras pantallas queden protegidas por error.

En `LocalInspectionMode` (preview de Android Studio) el effect no se ejecuta para evitar crashes.

## Puntos de extensión

- Añadir `DeviceIntegrityResult` (enum) si se necesita distinguir entre raíz, emulador, depuración USB, etc.
- Añadir soporte a Play Integrity API en ETAPA 7 como implementación alternativa de `IntegrityChecker`.
