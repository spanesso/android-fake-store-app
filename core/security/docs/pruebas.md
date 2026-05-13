# Pruebas — `:core:security`

## Tests unitarios (2 tests totales)

| Clase de test | Tests | Descripción |
|---------------|-------|-------------|
| `IntegrityCheckerTest` | 2 | Verifica contrato de `IntegrityChecker` con fake (sin Android Keystore ni RootBeer real) |

## Herramientas de test

- **MockK** — mock de `Context` para `IntegrityCheckerTest`
- **Truth** — aserciones sobre tipos y valores
- **`FakeIntegrityChecker`** — implementación in-test del contrato sin RootBeer

## Comandos Gradle

```bash
# Desde la raíz del repositorio (https://github.com/spanesso/android-fake-store-app)

./gradlew :core:security:testDebugUnitTest
```

## Umbrales de cobertura

| Componente | Estado |
|------------|--------|
| `IntegrityChecker` contrato | ✅ 2 ramas (comprometido / no comprometido) |
| `IntegrityCheckerImpl` (RootBeer real) | _(requiere dispositivo real — test instrumentado)_ |
| `SecureScreen` | _(requiere Compose + Activity — Paparazzi o test instrumentado)_ |

## Tests instrumentados (pendiente — ETAPA 7)

- `IntegrityCheckerImpl`: prueba con dispositivo rooteado real o emulador con root.
- `SecureScreen`: Paparazzi o `ComposeTestRule` con verificación de `FLAG_SECURE`.
