# Pruebas — `:features:profile:presentation`

## Comando

```bash
cd repository/android-fake-store-app
./gradlew :features:profile:presentation:testDebugUnitTest
```

## Archivos de test

| Archivo | Capa | Tests |
|---------|------|-------|
| `PerfilUiErrorMapperTest.kt` | mapper | 12 casos |
| `PerfilViewModelTest.kt` | viewmodel (Turbine) | 5 casos |

## Casos cubiertos en `PerfilUiErrorMapperTest`

| Nº | `DomainError` | Verificación |
|----|---------------|--------------|
| 1 | `Network.NotFound` | `messageRes = R.string.error_perfil_no_encontrado` |
| 2 | `Network.NotFound` | `actions` contiene `Retry` |
| 3 | `Network.NotFound` | `actions` contiene `Dismiss` |
| 4 | `Network.NotFound` | `severity = Info` |
| 5 | `Network.NoConnection` | Delega al mapper base; `errorCode = "NET-000"` |
| 6 | `Network.Timeout` | Delega; `errorCode = "NET-001"` |
| 7 | `Network.Server(500)` | Delega; `errorCode = "NET-500"` |
| 8 | `Network.Parsing` | Delega; `errorCode = "NET-002"` |
| 9 | `Network.Unauthorized` | Delega; `errorCode = "NET-401"` |
| 10 | `Network.Forbidden` | Delega; `errorCode = "NET-403"` |
| 11 | `Unknown` | Delega; `errorCode = "UNK-000"` |
| 12 | `Database.ReadFailed` | Delega; `errorCode = "DB-001"` |

## Casos cubiertos en `PerfilViewModelTest`

| Nº | Escenario | Secuencia de estados |
|----|-----------|---------------------|
| 1 | Creación del ViewModel | `Loading` como estado inicial |
| 2 | Repo OK + Flow emite 5 favoritos | `Loading → Content(contadorFavoritos=5)` |
| 3 | Repo devuelve `NotFound` | `Loading → Error(errorCode="NET-404")` |
| 4 | Repo devuelve `NoConnection` | `Loading → Error(errorCode="NET-000")` |
| 5 | `onEvent(Retry)` tras error | `Error → Loading → Content` |

## Umbrales de cobertura

| Capa | Cobertura mínima |
|------|-----------------|
| presentation | ≥ 70 % |

## Convenciones de nombrado

- `*ViewModelTest` → pruebas de ViewModel con `CoroutineTestRule` + Turbine
- `*MapperTest` → pruebas unitarias de mappers de presentación
- Los Composables se testean con snapshots Paparazzi (pendiente, ver `*SnapshotTest`)
