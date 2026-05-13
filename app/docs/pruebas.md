# Pruebas — Módulo `:app`

## Comandos Gradle

```bash
cd repository/android-fake-store-app

# Tests unitarios (no requieren dispositivo)
./gradlew :app:testDevDebugUnitTest

# Tests instrumentados E2E (requieren emulador o dispositivo)
./gradlew :app:connectedDevDebugAndroidTest

# Compilación completa + todos los tests
./gradlew :app:assembleDevDebug && ./gradlew :app:testDevDebugUnitTest
```

---

## Tests unitarios — `AppViewModelTest`

**Archivo**: `app/src/test/java/com/example/fakestoreapp/ui/AppViewModelTest.kt`

| Nombre del test | Cubre | Estado |
|---|---|---|
| `dado Connected cuando observa entonces isOffline es false` | US3 — ConnectivityObserver connected | ✅ |
| `dado Disconnected cuando observa entonces isOffline es true` | US3 — ConnectivityObserver disconnected | ✅ |
| `dado Unavailable cuando observa entonces isOffline es true` | US3 — ConnectivityObserver unavailable | ✅ |
| `dado recupera conexion entonces isOffline vuelve a false` | US3 — Reconexión reactiva | ✅ |
| `dado throwable inesperado cuando reportarErrorGlobal entonces emite MostrarErrorGlobal` | US4 — Handler global | ✅ |
| `dado throwable cuando reportarErrorGlobal entonces telemetria reporta noFatal` | US4 — Telemetría | ✅ |

**Total**: 6 tests — 0 fallos

---

## Tests instrumentados E2E — `NavigacionE2ETest`

**Archivo**: `app/src/androidTest/java/com/example/fakestoreapp/NavigacionE2ETest.kt`

| Nombre del test | Cubre | Estado |
|---|---|---|
| `app_arranca_y_muestra_pantalla_productos` | US1 — Arranque en Productos | ⚙️ (requiere emulador) |
| `navegar_a_favoritos_muestra_pantalla_favoritos` | US1 — Navegación a Favoritos | ⚙️ |
---

## Cobertura esperada

| Capa | Método | Cobertura mínima |
|---|---|---|
| `AppViewModel` | Unit tests con MockK + Turbine | ≥ 85% |
| Navegación E2E | Compose UI Tests con HiltAndroidTest | Flujo golden completo |
| `MangoNavHost` | No testable directamente — cubierto por E2E | — |
| `MainContent` | No testable directamente — cubierto por E2E | — |

---

## Nombres convencionales

- `AppViewModelTest` — tests unitarios del ViewModel raíz
- `NavigacionE2ETest` — tests E2E de navegación completa
