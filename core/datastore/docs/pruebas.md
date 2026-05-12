# Pruebas — `:core:datastore`

## Tests unitarios (8 tests totales)

| Test | Descripción |
|------|-------------|
| `save_and_read_session_returns_correct_data` | Guarda y lee tokens; verifica valores exactos |
| `session_flow_emits_empty_initially` | `sessionFlow` emite `SessionData.Empty` antes de guardar nada |
| `clear_session_removes_tokens_preserves_preferences` | `clearSession()` borra tokens pero conserva tema y preferencias |
| `save_preferences_theme_persists` | `savePreferences(DARK)` persiste y `preferencesFlow` refleja el cambio |
| `is_authenticated_when_access_token_present` | `isAuthenticated = true` cuando hay `accessToken` |
| `is_not_authenticated_when_no_token` | `isAuthenticated = false` en sesión vacía |
| `corruption_handler_resets_to_empty_session` | Fichero de preferencias corrupto → handler lo reemplaza por preferencias vacías |
| `decrypt_failure_returns_null_token_so_session_appears_empty` | Fallo de Tink → token = null → `isAuthenticated = false` |

## Herramientas de test

- **`PreferenceDataStoreFactory.create(...)`** con `TemporaryFolder` — DataStore en fichero temporal de JVM
- **`FakeTinkEncryption`** — subclase de `TinkEncryption(null)` que cifra con prefijo `"enc_"` sin Keystore
- **Turbine** — colección de `Flow<SessionData>` y `Flow<UserPreferences>`
- **`StandardTestDispatcher`** — control determinista de corrutinas

## Comandos Gradle

```bash
# Desde la raíz del repositorio (https://github.com/spanesso/android-fake-store-app)

./gradlew :core:datastore:testDebugUnitTest
```

## Umbrales de cobertura

| Componente | Estado |
|------------|--------|
| `MangoDataStoreImpl.sessionFlow` | ✅ cubierto (happy path, vacío, fallo de descifrado) |
| `MangoDataStoreImpl.preferencesFlow` | ✅ cubierto (DARK, por defecto SYSTEM) |
| `MangoDataStoreImpl.saveSession` | ✅ cubierto |
| `MangoDataStoreImpl.clearSession` | ✅ cubierto |
| `MangoDataStoreImpl.savePreferences` | ✅ cubierto |
| `MangoDataStoreImpl.decryptOrNull` | ✅ cubierto (éxito y fallo de Tink) |
| `TinkEncryption` | _(requiere Android Keystore — test instrumentado)_ |

## Tests instrumentados (pendiente — ETAPA 7)

`TinkEncryption` con `AndroidKeysetManager` real requiere emulador o dispositivo. Añadir como `@MediumTest` instrumentado en ETAPA 7 junto con los tests de `AndroidKeystoreDatabaseKeyManager`.
