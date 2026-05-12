# Errores — Módulo `:app`

El módulo `:app` no define `DomainError` propios. Es el punto de interceptación de **todos los errores no manejados** del sistema. La estrategia es:

1. `CoroutineExceptionHandler` en `MainActivity.lifecycleScope` captura excepciones de corrutinas
2. `AppViewModel.reportarErrorGlobal()` mapea cualquier `Throwable` → `DomainError.Unknown` → `UiError`
3. `MainContent` muestra un `Snackbar` con el mensaje localizado

---

## Tabla de errores interceptados

| Condición | `DomainError` | `UiError.severity` | `R.string` |
|---|---|---|---|
| Excepción no capturada en corrutinas de la app | `DomainError.Unknown(cause)` | `Info` | `error_desconocido` (de `:core:error`) |
| Biometría cancelada por el usuario | — (no es error, mensaje informativo) | N/A | `biometria_cancelado` |
| Biometría bloqueada temporalmente | — (mensaje informativo) | N/A | `biometria_bloqueado` |
| Biometría no disponible en el dispositivo | — (mensaje informativo) | N/A | `biometria_no_disponible` |
| Error biométrico del sistema | `BiometricResult.Error(mensaje)` | N/A | `mensaje` del sistema (string nativo) |

---

## Strings de error definidos en `:app`

**Archivo**: `app/src/main/res/values/strings.xml`

| Key | Valor | Contexto |
|---|---|---|
| `biometria_titulo` | "Acceso al perfil" | Título del prompt biométrico |
| `biometria_subtitulo` | "Verifica tu identidad" | Subtítulo del prompt |
| `biometria_cancelar` | "Cancelar" | Botón de cancelación del prompt |
| `biometria_cancelado` | "Autenticación cancelada" | Snackbar al cancelar biometría |
| `biometria_bloqueado` | "Biometría bloqueada temporalmente. Inténtalo de nuevo." | Snackbar cuando biometría bloqueada |
| `biometria_no_disponible` | "Biometría no disponible en este dispositivo." | Snackbar sin hardware biométrico |
| `error_global_inesperado` | "Ocurrió un error inesperado. La app continuará funcionando." | Snackbar de handler global |

---

## Flujo del handler global

```
Excepción en corrutina
        ↓
CoroutineExceptionHandler (MainActivity)
        ↓
AppViewModel.reportarErrorGlobal(throwable)
        ↓
DomainError.Unknown(throwable)
        ↓
telemetry.reportarNoFatal(domainError)   → Firebase Crashlytics
        ↓
errorMapper.map(domainError) → UiError(messageRes = R.string.error_desconocido, ...)
        ↓
_uiEffect.emit(MostrarErrorGlobal(uiError))
        ↓
MainContent: snackbarHostState.showSnackbar(getString(uiError.messageRes))
```

---

## Errores NO interceptados por `:app`

Los errores de dominio tipados (`Either.Left<DomainError, T>`) son manejados por cada `ViewModel` de feature de forma específica. `:app` solo intercepta las excepciones **no capturadas** (unchecked exceptions que escapan de las corrutinas).

El handler global es la última línea de defensa — no reemplaza el manejo de errores en cada feature.
