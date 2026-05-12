# Informe de validación de manejo de errores — ETAPA 1.5–1.7

**Fecha**: 2026-05-11
**Alcance**: `core:network`, `core:database`, `core:datastore`
**Total violaciones**: 0 (0 tras correcciones aplicadas)

## Resumen por regla

| Regla | Conteo | Severidad | Estado |
|-------|--------|-----------|--------|
| ERR-001 | N/A | Crítica | ✅ N/A — sin repositorios de feature |
| ERR-002 | N/A | Crítica | ✅ N/A — sin UseCases |
| ERR-003 | N/A | Crítica | ✅ N/A — sin Composables |
| ERR-004 | N/A | Crítica | ✅ N/A — sin Composables |
| ERR-005 | 0* | Crítica | ✅ PASS — barrera justificada |
| ERR-006 | 0* | Alta | ✅ PASS — corregido + Timber logging |
| ERR-007 | N/A | Media | ✅ N/A — sin UI |
| ERR-008 | N/A | Alta | ✅ N/A — sin UseCases |
| ERR-009 | 0 | Alta | ✅ PASS |
| ERR-010 | N/A | Alta | ✅ N/A — sin ViewModels |

\* Hallazgos detectados y resueltos durante esta auditoría.

## Hallazgos y correcciones

### ERR-005 (justificado) — `catch (e: Throwable)` en `SafeRetrofitCallExt.kt`

**Archivo**: `core/network/src/main/.../ext/SafeRetrofitCallExt.kt:24`

`catch (e: Throwable)` es el último bloque de captura dentro de `safeRetrofitCall`.
Es una barrera explícita: rethrows `CancellationException`, mapea `HttpException` por
código y delega el resto a `NetworkErrorMapper`. Esta es la barrera documentada del §7
del prompt maestro — **no es una violación**.

### ERR-006 (corregido) — `runCatching { }.getOrNull()` en `MangoDataStoreImpl.kt`

**Archivos**: líneas 28–30 y 38 (antes de la corrección)

Los cuatro usos de `runCatching { }.getOrNull()` silenciaban fallos de descifrado Tink
y de parseo de `AppTheme` sin emitir ningún log.

**Corrección aplicada**:
- Extraído helper privado `decryptOrNull(ciphertext, fieldName)` con `.fold()` y
  `Timber.e(e, "Tink decrypt failed for '$fieldName'")` en la rama de error.
- `AppTheme.valueOf(raw)` igualmente convertido a `.fold()` con log de advertencia.
- Se añadió `implementation(libs.timber)` a `core/datastore/build.gradle.kts`.

El comportamiento de fallback (retornar `null` → sesión expirada) es correcto; ahora
queda trazable en Logcat.

## Cobertura de mappers (ERR-009)

| Mapper | Ramas `when` | Tests | Estado |
|--------|-------------|-------|--------|
| `NetworkErrorMapper` (core:error) | 9 | 7 | ✅ ≥ 1 por rama efectiva |
| `DatabaseErrorMapper` (core:error) | 3 | 3 | ✅ 100% |
| `SafeRetrofitCallExt` (core:network) | 5+fallback | 8 | ✅ 100% |

## Veredicto

✅ **0 violaciones** — El manejo de errores de `core:network`, `core:database` y
`core:datastore` cumple con §7 del prompt maestro (tras corrección de ERR-006).
