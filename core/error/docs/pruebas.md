# Pruebas — `:core:error`

## Inventario de tests

| Archivo | Tests | Cobertura |
|---------|-------|-----------|
| `SafeCallExtTest.kt` | 5 | `safeApiCall` (éxito, network error, CancellationException), `safeDbCall` (éxito, db error) |
| `NetworkErrorMapperTest.kt` | 7 | Todas las ramas: SocketTimeout, UnknownHost, IOException, TimeoutException, SerializationException, HTTP (401, 404, 500, desconocido) |
| `DatabaseErrorMapperTest.kt` | 3 | SQLiteConstraintException → IntegrityViolation, SQLiteException → WriteFailed, Throwable → ReadFailed |
| `DomainErrorToUiErrorMapperTest.kt` | 18 | Una rama por cada subclase de DomainError (cobertura exhaustiva) |
| **Total** | **33** | |

## Ejecución

```bash
# Desde la raíz del repositorio (https://github.com/spanesso/android-fake-store-app)
./gradlew :core:error:testDebugUnitTest
```

## Convenciones

- Tests de mappers: **sin mocks**, solo datos puros (instanciar excepción → verificar tipo de `DomainError`)
- `SafeCallExtTest`: usa `mockk<suspend () -> T>()` con `coEvery`
- Verificación: `assertTrue(result is DomainError.Network.Timeout)` (instancia, no valor)

## Umbrales

`:core:error` es una barrera crítica. Requisito informal: **cobertura de ramas del 100% en mappers** (verificado con las 33 pruebas actuales).
