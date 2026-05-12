# Módulo `:core:security`

**Propósito**: Proveer primitivas de seguridad de dispositivo: autenticación biométrica con `BIOMETRIC_STRONG`, verificación de integridad del dispositivo con RootBeer, y protección de pantallas sensibles con `FLAG_SECURE`.

## Contratos públicos

| Símbolo | Descripción | Retorno |
|---------|-------------|---------|
| `BiometricAuthenticator.autenticar(actividad, titulo, subtitulo, cancelarTexto)` | Muestra `BiometricPrompt` con `BIOMETRIC_STRONG` y retorna el resultado | `BiometricResult` |
| `BiometricResult` | Sealed interface con estados Exito, Cancelado, BloqueadoTemporalmente, NoDisponible, Error | — |
| `IntegrityChecker.estaComprometido()` | Retorna `true` si el dispositivo tiene root o está comprometido | `Boolean` |
| `SecureScreen(contenido)` | Composable que aplica `FLAG_SECURE` a la ventana mientras está visible | `@Composable` |

## Dependencias

- `:core:error` — `DomainError.Security` (para mapear resultados biométricos en los ViewModels)
- `:core:common`
- `androidx-biometric` — `BiometricPrompt`, `BiometricManager`
- `rootbeer` — `RootBeer.isRooted`

## Ejemplos de uso

```kotlin
class LoginViewModel @Inject constructor(
    private val biometric: BiometricAuthenticator,
    private val integridad: IntegrityChecker,
) : ViewModel() {

    fun verificarIntegridad(): Either<DomainError, Unit> =
        if (integridad.estaComprometido()) Either.Left(DomainError.Security.RootDetected)
        else Either.Right(Unit)

    suspend fun autenticar(actividad: FragmentActivity) {
        when (biometric.autenticar(actividad, "Mango", "Confirma tu identidad", "Cancelar")) {
            BiometricResult.Exito -> _uiState.value = UiState.Autenticado
            BiometricResult.Cancelado -> Unit
            BiometricResult.BloqueadoTemporalmente ->
                _uiState.value = UiState.Error(/* uiError bloqueado */)
            BiometricResult.NoDisponible ->
                _uiState.value = UiState.Error(/* uiError no disponible */)
            is BiometricResult.Error ->
                _uiState.value = UiState.Error(/* uiError genérico */)
        }
    }
}
```

```kotlin
@Composable
fun PagoScreen(/* ... */) {
    SecureScreen {
        // Contenido protegido con FLAG_SECURE
    }
}
```

## Estructura interna

```
core/security/
├── src/main/kotlin/com/mango/fakestore/core/security/
│   ├── biometric/
│   │   ├── BiometricAuthenticator.kt
│   │   ├── BiometricAuthenticatorImpl.kt
│   │   └── BiometricResult.kt
│   ├── integrity/
│   │   ├── IntegrityChecker.kt
│   │   └── IntegrityCheckerImpl.kt
│   ├── ui/
│   │   └── SecureScreen.kt
│   └── di/
│       └── SecurityModule.kt
└── src/test/
    └── ...
```

## Cómo regenerar esta documentación

```
/documentar-modulo modulo=security
```
