# Pruebas — `:core:ui`

## Inventario de tests

| Archivo | Tests | Cobertura |
|---------|-------|-----------|
| `LoadingContentTest.kt` | 3 | Estado loading true/false; variante Linear |
| `EmptyContentTest.kt` | 3 | Estado vacío true/false; mensaje personalizado |
| `ErrorContentTest.kt` | 4 | Error null/non-null; severidad Warning/Blocking |
| `ConnectivityObserverTest.kt` | 3 | Initial state; online→offline; offline→online |
| `ConditionalModifierTest.kt` | 3 | Condición true/false; sin modificador |
| **Total** | **16** | |

## Ejecución

```bash
# Desde la raíz del repositorio (https://github.com/spanesso/android-fake-store-app)
./gradlew :core:ui:testDebugUnitTest
```

## Convenciones

- Tests de composables de estado: lógica pura (verifican si la rama correcta se activa según los parámetros de entrada), sin Compose UI
- `ConnectivityObserverTest`: usa `FakeConnectivityObserver` que implementa la interfaz con un `MutableStateFlow`
- `runTest { }` para corrutinas; Turbine para coleccionar flows

## Archivos sin test (justificado)

| Archivo | Razón |
|---------|-------|
| `ShimmerModifier.kt` | Usa `composed` + `animateFloat`: requiere runtime Compose (no testeable en JVM) |
| `ContextExt.kt` | Extiende `android.content.Context`: requiere mock de Android |
| `PreviewAnnotations.kt` | Solo anotaciones; sin lógica |
| `MangoOfflineBanner.kt` | Cubierto indirectamente por `ConnectivityObserverTest` |

## Umbrales

`:core:ui` es capa UI de propósito general. Umbral informal: **toda lógica de estado testeable en JVM debe tener cobertura de ramas completa**.
