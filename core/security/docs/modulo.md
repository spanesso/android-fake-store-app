# Módulo `:core:security`

**Propósito**: Proveer primitivas de seguridad de dispositivo: verificación de integridad del dispositivo con RootBeer y protección de pantallas sensibles con `FLAG_SECURE`.

## Contratos públicos

| Símbolo | Descripción | Retorno |
|---------|-------------|---------|
| `IntegrityChecker.estaComprometido()` | Retorna `true` si el dispositivo tiene root o está comprometido | `Boolean` |
| `SecureScreen(contenido)` | Composable que aplica `FLAG_SECURE` a la ventana mientras está visible | `@Composable` |

## Dependencias

- `:core:common`
- `rootbeer` — `RootBeer.isRooted`

## Ejemplos de uso

```kotlin
class LoginViewModel @Inject constructor(
    private val integridad: IntegrityChecker,
) : ViewModel() {

    fun verificarIntegridad(): Either<DomainError, Unit> =
        if (integridad.estaComprometido()) Either.Left(DomainError.Security.RootDetected)
        else Either.Right(Unit)
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
