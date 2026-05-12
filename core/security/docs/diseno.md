# Diseño — `:core:security`

## Diagrama de arquitectura

```mermaid
flowchart TB
  subgraph security[core:security]
    BA[BiometricAuthenticator<br>interface]
    BAI[BiometricAuthenticatorImpl]
    BR[BiometricResult<br>sealed]
    IC[IntegrityChecker<br>interface]
    ICI[IntegrityCheckerImpl]
    SS[SecureScreen<br>@Composable]
  end

  subgraph android[Android SDK]
    BP[BiometricPrompt]
    BM[BiometricManager<br>BIOMETRIC_STRONG]
    FLAG[FLAG_SECURE]
  end

  subgraph libs[Librerías]
    RB[RootBeer]
  end

  BAI -.implementa.-> BA
  BAI --> BP
  BAI --> BM
  BP --> BR
  ICI -.implementa.-> IC
  ICI --> RB
  SS --> FLAG

  VM[ViewModels] --> BA
  VM --> IC
  screens[Screens] --> SS
```

## Decisiones de diseño

### BIOMETRIC_STRONG obligatorio

`BiometricPrompt.PromptInfo` se construye con `setAllowedAuthenticators(BIOMETRIC_STRONG)`. No se permite `DEVICE_CREDENTIAL` solo, ya que el PIN/patrón no es biometría fuerte. Si el dispositivo no tiene biometría fuerte, se retorna `BiometricResult.NoDisponible`.

### suspendCoroutine con bandera `reanudado`

`BiometricPrompt.AuthenticationCallback` puede llamar tanto `onAuthenticationError` como `onAuthenticationFailed`. `onAuthenticationFailed` no reanuda la corrutina (el usuario puede reintentar). Para evitar reanudar dos veces, se usa una bandera `reanudado`.

### IntegrityChecker como interfaz

`IntegrityChecker` es una interfaz para facilitar el testing: se puede inyectar un `FakeIntegrityChecker` en tests sin necesitar `Context` real ni RootBeer. `IntegrityCheckerImpl` requiere `@ApplicationContext`.

### SecureScreen usa DisposableEffect

`FLAG_SECURE` se añade en `DisposableEffect(Unit)` (único) y se elimina en `onDispose`. Esto garantiza que la bandera se limpia cuando el Composable abandona la composición, evitando que otras pantallas queden protegidas por error.

En `LocalInspectionMode` (preview de Android Studio) el effect no se ejecuta para evitar crashes.

## Puntos de extensión

- Añadir `DeviceIntegrityResult` (enum) si se necesita distinguir entre raíz, emulador, depuración USB, etc.
- Añadir soporte a Play Integrity API en ETAPA 7 como implementación alternativa de `IntegrityChecker`.
