# Pruebas — `:core:design-system`

## Inventario de tests

| Archivo | Tipo | Tests | Cobertura |
|---------|------|-------|-----------|
| `Material3IsolationKonsistTest.kt` | Konsist (arquitectura) | 1 | Verifica que Material3 no se importe fuera del design-system |
| `MangoThemeSnapshotTest.kt` | Paparazzi (snapshot) | 2 | `MangoTheme` en modo claro y oscuro |
| `MangoSnackbarSnapshotTest.kt` | Paparazzi (snapshot) | 4 | `MangoSnackbar` en 4 severidades (Info, Success, Warning, Error) |
| `MangoOfflineBannerContentSnapshotTest.kt` | Paparazzi (snapshot) | 2 | Banner en estado online y offline |
| **Total** | | **9** | |

## Ejecución

```bash
# Desde la raíz del repositorio (https://github.com/spanesso/android-fake-store-app)

# Tests Konsist (incluidos en el ciclo estándar)
./gradlew :core:design-system:testDebugUnitTest

# Snapshots Paparazzi (ejecutar por separado)
./gradlew :core:design-system:recordPaparazziDebug    # Grabar golden images (primera vez)
./gradlew :core:design-system:verifyPaparazziDebug    # Verificar contra golden images
```

## Convenciones de snapshots

- Un test por modo claro/oscuro con `nightMode = NightMode.NIGHT` o `NightMode.NOTNIGHT`
- `DeviceConfig.PIXEL_5` como dispositivo de referencia
- Las golden images se almacenan en `src/test/snapshots/` (no en VCS; se generan en CI)

## Umbrales

Los componentes de design-system no tienen lógica de negocio. La cobertura es estructural:
- **Konsist**: garantiza restricciones arquitectónicas (Material3 isolation)
- **Snapshots**: garantizan consistencia visual entre cambios de tokens
