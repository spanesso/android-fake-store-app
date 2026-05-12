# Pruebas — `:core:analytics`

## Tests unitarios (13 tests totales)

| Clase de test | Tests | Descripción |
|---------------|-------|-------------|
| `NoOpTelemetryTest` | 5 | Verifica que NoOp no lanza para ningún método |
| `FirebaseEventTrackerTest` | 4 | Verifica que delegación a Telemetry es correcta por tipo de evento |
| `AnalyticsEventTest` | 4 | Verifica nombres y parámetros de cada subclase de `AnalyticsEvent` |

## Herramientas de test

- **MockK** — mock de `Telemetry` en `FirebaseEventTrackerTest`
- **Truth** — aserciones en `AnalyticsEventTest`
- **`NoOpTelemetryImpl`** — usado directamente como fake sin mocks

## Comandos Gradle

```bash
# Desde la raíz del repositorio (https://github.com/spanesso/android-fake-store-app)

./gradlew :core:analytics:testDebugUnitTest
```

## Umbrales de cobertura

| Componente | Estado |
|------------|--------|
| `NoOpTelemetryImpl` | ✅ todos los métodos cubiertos |
| `NoOpEventTrackerImpl` | ✅ cubierto en `FirebaseEventTrackerTest` |
| `FirebaseEventTrackerImpl` | ✅ cubierto (3 tipos de evento) |
| `AnalyticsEvent` subclases | ✅ cubiertos (4 subclases) |
| `FirebaseTelemetryImpl` | _(requiere Firebase SDK — test instrumentado o stub)_ |
| `ConsoleTelemetryImpl` | _(opcional; se puede testear con Timber stub)_ |

## Tests instrumentados (pendiente — ETAPA 7)

`FirebaseTelemetryImpl` requiere Firebase SDK y google-services.json configurado. Añadir como `@LargeTest` en ETAPA 7 junto con los tests de integración de otros módulos.
