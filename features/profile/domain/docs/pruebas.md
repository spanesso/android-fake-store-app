# Pruebas — `:features:profile:domain`

## Comando

```bash
cd repository/android-fake-store-app
./gradlew :features:profile:domain:testDebugUnitTest
```

## Archivos de test

| Archivo | Capa | Tests |
|---------|------|-------|
| `ObtenerPerfilTest.kt` | domain | 9 casos |

## Casos cubiertos en `ObtenerPerfilTest`

| Nº | Escenario | Resultado esperado |
|----|-----------|-------------------|
| 1 | `userId` válido, repositorio devuelve usuario | `Either.Right(usuario)` |
| 2 | `userId = 0` | `IllegalArgumentException` |
| 3 | `userId = -1` | `IllegalArgumentException` |
| 4 | Repositorio devuelve `Network.NotFound` | `Either.Left(Network.NotFound)` propagado sin modificar |
| 5 | Repositorio devuelve `Network.NoConnection` | `Either.Left(Network.NoConnection)` |
| 6 | Repositorio devuelve `Network.Timeout` | `Either.Left(Network.Timeout)` |
| 7 | Repositorio devuelve `Network.Server(500)` | `Either.Left(Network.Server(500))` con httpCode=500 |
| 8 | Repositorio devuelve `Network.Parsing` | `Either.Left(Network.Parsing)` |
| 9 | Repositorio devuelve `Unknown` | `Either.Left(Unknown)` |

## Umbrales de cobertura

| Capa | Cobertura mínima |
|------|-----------------|
| domain | ≥ 100 % |

## Convenciones de nombrado

- `*UseCaseTest` → pruebas de casos de uso del dominio
- Los repositorios se testean en la capa `data` con `MockWebServer`
