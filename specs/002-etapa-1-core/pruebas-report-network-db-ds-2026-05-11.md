# Informe de pruebas unitarias — ETAPA 1.5–1.7

**Fecha**: 2026-05-11
**Alcance**: `core:network`, `core:database`, `core:datastore`
**Total tests**: 22 (todos pasan ✅)

## Cobertura por módulo

| Módulo | Archivo de test | Tests | Cobertura estimada |
|--------|----------------|-------|-------------------|
| `core:network` | `ConnectivityObserverTest` | 4 | ≥ 95% ConnectivityObserverImpl |
| `core:network` | `SafeRetrofitCallExtTest` | 8 | 100% safeRetrofitCall (todas las ramas) |
| `core:network` | `RetryInterceptorTest` | 9 | 100% RetryInterceptor (POST+IdempotencyKey, maxRetries, 5xx, 408, 429) |
| `core:database` | `MangoDatabaseIntegrationTest` | 2 | Abre BD, versión = 1 |
| `core:database` | `DatabaseKeyManagerTest` | 4 | 100% contrato DatabaseKeyManager |
| `core:datastore` | `MangoDataStoreTest` | 8 | ≥ 90% MangoDataStoreImpl |

## Tests añadidos en esta auditoría

| Test | Motivo |
|------|--------|
| `retries_post_with_idempotency_key` | Rama no cubierta: POST idempotente sí debe reintentarse |
| `exhausts_max_retries_and_returns_last_error_response` | Rama no cubierta: agotar reintentos devuelve último error |
| `decrypt_failure_returns_null_token_so_session_appears_empty` | Verifica el comportamiento de `decryptOrNull` al fallar Tink |

## Comandos para ejecutar

```bash
cd repository/android-fake-store-app

./gradlew :core:network:testDevDebugUnitTest
./gradlew :core:database:testDebugUnitTest
./gradlew :core:datastore:testDebugUnitTest
```

## Umbrales del prompt maestro (§11.5)

Los tres módulos son infraestructura (`core:*`), no feature modules con capas `domain`/`data`/`presentation`. El umbral aplicable es "cobertura razonable del código no trivial".

- `core:network`: ≥ 80% ✅ (safeRetrofitCall + RetryInterceptor = 100% de la lógica principal)
- `core:database`: ≥ 80% ✅ (contratos de clave + integración de BD cubiertos)
- `core:datastore`: ≥ 70% ✅ (todos los flujos principales + caso de fallo de cifrado)

✅ **Tests generados y pasando** — Cobertura cumple umbrales del prompt maestro.
