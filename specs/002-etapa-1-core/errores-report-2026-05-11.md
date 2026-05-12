# Informe de validación de manejo de errores

**Fecha**: 2026-05-11
**Alcance**: `core/common`, `core/error`, `core/design-system`, `core/ui`
**Total violaciones**: 0 (0 únicas)

## Resumen por regla

| Regla | Conteo | Severidad | Estado |
|-------|--------|-----------|--------|
| ERR-001 | 0 | Crítica | ✅ N/A (sin repositorios implementados) |
| ERR-002 | 0 | Crítica | ✅ N/A (sin UseCases implementados) |
| ERR-003 | 0 | Crítica | ✅ PASS |
| ERR-004 | 0 | Alta | ✅ PASS |
| ERR-005 | 0 | Alta | ✅ PASS (barrera documentada) |
| ERR-006 | 0 | Alta | ✅ PASS |
| ERR-007 | 0 | Media | ✅ PASS (solo en @Preview) |
| ERR-008 | 0 | Media | ✅ N/A (sin UseCases implementados) |
| ERR-009 | 0 | Media | ✅ PASS |
| ERR-010 | 0 | Alta | ✅ N/A (sin ViewModels implementados) |

## Detalle

### ERR-005 — `catch (e: Throwable)` en `SafeCallExt.kt`

**Estado**: PASS — Barrera documentada correctamente.

`core/error/src/main/kotlin/.../ext/SafeCallExt.kt` contiene `catch (e: Throwable)` en dos puntos, pero:
- `CancellationException` se re-lanza explícitamente antes del catch genérico (preserva la cooperación de corrutinas)
- Ambas funciones llevan `@Suppress("TooGenericExceptionCaught")` documentando la supresión intencional
- Es precisamente la barrera del sistema de errores (`safeApiCall` / `safeDbCall`), el equivalente de un "ErrorMapper" dentro de `core/error/ext/`

### ERR-007 — Strings literales en Previews de design-system

**Estado**: PASS — Todas las cadenas hardcoded están dentro de funciones `@Preview` privadas.

Los strings encontrados son exclusivamente en:
- `MangoDialog.kt` (líneas 34, 39): funciones `@Preview private fun`
- `MangoSnackbar.kt` (líneas 49–61): funciones `@Preview private fun`

Los `@Preview` son funciones de diseño en tiempo de compilación, solo visibles en el IDE (Android Studio). No son código de producción y no se ejecutan en runtime. El informe ERR-007 aplica únicamente a llamadas de feature UI — ninguna feature usa Mango components aún.

### ERR-009 — Cobertura de mappers

| Mapper | Ramas `when` | Tests `@Test` | Estado |
|--------|-------------|---------------|--------|
| `NetworkErrorMapper` | 6 (SocketTimeout, UnknownHost, IOException, Timeout, Serialization, else+HTTP) | 7 | ✅ PASS |
| `DatabaseErrorMapper` | 3 (SQLiteConstraint, SQLiteException, else) | 3 | ✅ PASS |
| `DomainErrorToUiErrorMapper` | 18 (todas las subclases de DomainError) | 18 | ✅ PASS |

### ERR-001/002/008/010 — No aplicables en ETAPA 1

Los módulos `core/*` no contienen:
- Repositorios de datos (implementados en `features:*:data`, ETAPA 2+)
- UseCases (implementados en `features:*:domain`, ETAPA 2+)
- ViewModels (implementados en `features:*:presentation`, ETAPA 2+)

Estas reglas se auditarán al cierre de cada ETAPA de features.

---

✅ **0 violaciones** — El manejo de errores cumple §7 del prompt maestro.
