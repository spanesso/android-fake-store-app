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
| `dado biometria exitosa cuando autentica entonces sesionAutenticada es true` | US2 — Gateway Exito | ✅ |
| `dado biometria cancelada cuando autentica entonces sesionAutenticada es false` | US2 — Gateway Cancelado | ✅ |
| `dado biometria bloqueada cuando autentica entonces sesionAutenticada es false` | US2 — Gateway Bloqueado | ✅ |
| `dado throwable inesperado cuando reportarErrorGlobal entonces emite MostrarErrorGlobal` | US4 — Handler global | ✅ |
| `dado throwable cuando reportarErrorGlobal entonces telemetria reporta noFatal` | US4 — Telemetría | ✅ |

**Total**: 9 tests — 0 fallos

---

## Tests instrumentados E2E — `NavigacionE2ETest`

**Archivo**: `app/src/androidTest/java/com/example/fakestoreapp/NavigacionE2ETest.kt`

| Nombre del test | Cubre | Estado |
|---|---|---|
| `app_arranca_y_muestra_pantalla_productos` | US1 — Arranque en Productos | ⚙️ (requiere emulador) |
| `navegar_a_favoritos_muestra_pantalla_favoritos` | US1 — Navegación a Favoritos | ⚙️ |
| `navegar_a_perfil_con_biometria_exitosa_muestra_pantalla_perfil` | US2 — Gateway OK | ⚙️ |
| `navegar_a_perfil_sin_biometria_permanece_en_pantalla_anterior` | US2 — Gateway cancelado | ⚙️ |

**Nota**: Los tests E2E usan `FakeBiometricAuthenticator` (configurable) a través de `@TestInstallIn(replaces = [SecurityModule::class])`.

---

## Fake modules para tests

**`TestSecurityModule.kt`**:
```kotlin
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [SecurityModule::class])
object TestSecurityModule {
    @Provides @Singleton
    fun provideBiometricAuthenticator(): BiometricAuthenticator =
        FakeBiometricAuthenticator(defaultResult = BiometricResult.Exito)
}
```

`FakeBiometricAuthenticator.defaultResult` es mutable — permite cambiar el resultado en cada test:
```kotlin
(biometricAuthenticator as? FakeBiometricAuthenticator)?.defaultResult = BiometricResult.Cancelado
```

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
- `TestSecurityModule` — módulo Hilt de test que reemplaza `SecurityModule`
- `FakeBiometricAuthenticator` — fake configurable del autenticador biométrico
