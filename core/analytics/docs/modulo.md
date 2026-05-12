# Módulo `:core:analytics`

**Propósito**: Centralizar toda la observabilidad de la app: reporte de errores no fatales a Crashlytics, registro de eventos a Firebase Analytics, trazas de rendimiento y logging de debug.

## Contratos públicos

| Símbolo | Descripción | Retorno |
|---------|-------------|---------|
| `Telemetry.reportarNoFatal(error, contexto)` | Registra un `DomainError` no fatal en Crashlytics con contexto adicional | `Unit` |
| `Telemetry.registrarEvento(nombre, params)` | Envía un evento con parámetros clave-valor a Firebase Analytics | `Unit` |
| `Telemetry.iniciarTraza(nombre)` | Inicia una traza de rendimiento en Firebase Performance | `TraceHandle` |
| `TraceHandle.detener()` | Detiene la traza activa | `Unit` |
| `EventTracker.registrar(evento)` | Registra un `AnalyticsEvent` tipado | `Unit` |
| `AnalyticsEvent` | Sealed interface con eventos predefinidos del dominio | — |

## Implementaciones

| Implementación | Uso | Activa en |
|----------------|-----|-----------|
| `FirebaseTelemetryImpl` | Crashlytics + Analytics + Performance | Release y Debug con Firebase |
| `ConsoleTelemetryImpl` | Timber logs estructurados | Debug sin Firebase |
| `NoOpTelemetryImpl` | Sin efecto | Tests unitarios |
| `FirebaseEventTrackerImpl` | Delega en `Telemetry` | Release |
| `NoOpEventTrackerImpl` | Sin efecto | Tests unitarios |

## Eventos predefinidos

| `AnalyticsEvent` | `nombre` | Parámetros |
|-----------------|----------|-----------|
| `PantallaVista(pantalla)` | `screen_view` | `screen_name` |
| `AccionUsuario(nombre, params)` | libre | libres |
| `ErrorRegistrado(codigo, detalle)` | `error_registrado` | `codigo`, `detalle` |
| `BusquedaRealizada(consulta, resultados)` | `busqueda_realizada` | `consulta`, `resultados` |

## Dependencias

- `:core:error` — `DomainError` para `reportarNoFatal`
- `:core:common` — `AppDispatchers`
- Firebase BoM: `firebase-crashlytics`, `firebase-analytics`, `firebase-performance`
- `timber` (logging)

## Ejemplos de uso

```kotlin
class ProductosViewModel @Inject constructor(
    private val telemetry: Telemetry,
    private val eventTracker: EventTracker,
) : ViewModel() {

    fun cargarProductos() {
        val traza = telemetry.iniciarTraza("cargar_productos")
        viewModelScope.launch(errorHandler) {
            // ...
            traza.detener()
            eventTracker.registrar(AnalyticsEvent.PantallaVista("productos"))
        }
    }

    private val errorHandler = CoroutineExceptionHandler { _, t ->
        val error = DomainError.Unknown(t)
        telemetry.reportarNoFatal(error, mapOf("viewmodel" to "ProductosViewModel"))
    }
}
```

## Estructura interna

```
core/analytics/
├── src/main/kotlin/com/mango/fakestore/core/analytics/
│   ├── Telemetry.kt              # interface + TraceHandle
│   ├── EventTracker.kt           # interface
│   ├── AnalyticsEvent.kt         # sealed interface
│   ├── impl/
│   │   ├── FirebaseTelemetryImpl.kt
│   │   ├── ConsoleTelemetryImpl.kt
│   │   ├── NoOpTelemetryImpl.kt
│   │   ├── FirebaseEventTrackerImpl.kt
│   │   └── NoOpEventTrackerImpl.kt
│   └── di/
│       └── AnalyticsModule.kt
└── src/test/
    └── ...
```

## Cómo regenerar esta documentación

```
/documentar-modulo modulo=analytics
```
