# Pruebas — `:features:profile:data`

## Comando

```bash
cd repository/android-fake-store-app
./gradlew :features:profile:data:testDebugUnitTest
```

## Archivos de test

| Archivo | Capa | Tests |
|---------|------|-------|
| `UsuarioDtoMapperTest.kt` | mapper | 10 casos |
| `PerfilRepositoryImplTest.kt` | repositorio (MockWebServer) | 6 casos |

## Casos cubiertos en `UsuarioDtoMapperTest`

| Nº | Escenario | Campo verificado |
|----|-----------|-----------------|
| 1 | Mapeo completo del DTO | `id` correcto |
| 2 | Concatenación de nombre | `"John Doe"` |
| 3 | Nombre de usuario | `username` directo |
| 4 | Email | `email` directo |
| 5 | Teléfono | `phone` directo |
| 6 | Calle con número | `"new road 7835"` |
| 7 | Ciudad | `city` directo |
| 8 | Código postal | `zipcode` directo |
| 9 | `address.number = 0` | `calle` incluye "0" correctamente |
| 10 | `firstname` vacío | `nombreCompleto` empieza con espacio |

## Casos cubiertos en `PerfilRepositoryImplTest` (MockWebServer)

| Nº | Escenario | Resultado esperado |
|----|-----------|-------------------|
| 1 | HTTP 200 OK con JSON válido | `Either.Right(Usuario)` con id=8, nombreCompleto="John Doe" |
| 2 | HTTP 404 | `Either.Left(Network.NotFound)` |
| 3 | HTTP 500 | `Either.Left(Network.Server(500))` con httpCode=500 |
| 4 | JSON inválido (malformado) | `Either.Left(Network.Parsing)` |
| 5 | `bodyDelay(5s)` con OkHttp `readTimeout(1s)` | `Either.Left(Network.Timeout)` |
| 6 | `DISCONNECT_AT_START` | `Either.Left(Network.NoConnection)` o `Network.Timeout` (timing) |

## Umbrales de cobertura

| Capa | Cobertura mínima |
|------|-----------------|
| data | ≥ 80 % |

## Convenciones de nombrado

- `*RepositoryImplTest` → pruebas de repositorio con `MockWebServer`
- `*MapperTest` → pruebas unitarias de funciones de mapeo puras
