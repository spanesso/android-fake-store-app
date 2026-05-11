---
name: validar-manejo-errores
description: 'Audita el código Kotlin del proyecto Android Mango Fake Store y reporta violaciones de la estrategia de manejo de errores tipada definida en §7 del prompt maestro: repositorios sin `safeApiCall`/`safeDbCall`, UseCases que devuelven tipos crudos en vez de `Either<DomainError, T>`, Composables que muestran `throwable.message` o reciben `DomainError`/`Throwable`, `try/catch (e: Exception)` o `try/catch (e: Throwable)` genérico fuera de barreras documentadas (ErrorMappers, `CoroutineExceptionHandler` raíz, init de SDK), `runCatching` sin `.fold` que devuelva `Either`, strings de error hardcoded en `MangoErrorState`/`MangoSnackbar`/diálogos que deberían usar `stringResource`, UseCases sin tests por cada rama de `DomainError`, mappers `Throwable→DomainError` o `DomainError→UiError` sin tests por rama, y `viewModelScope.launch` sin `CoroutineExceptionHandler` en ViewModels críticos. Devuelve un informe en español con archivo:línea, regla violada (ERR-NNN) y corrección sugerida. Usar SIEMPRE tras `/speckit-implement` o `crear-modulo`, al cerrar cada ETAPA del prompt maestro, antes de abrir PR, y cuando el usuario diga "valida los errores", "audita manejo de errores del módulo X", "comprueba que no haya throwable.message en la UI", "revisa que los UseCases devuelvan Either". NO usar para errores de compilación, ni para errores de Detekt genérico (ese es Detekt directo), ni para tests fallidos.'
---

# Skill `validar-manejo-errores` — auditoría de §7 del prompt maestro

Recorre el código Kotlin del proyecto Android Mango Fake Store y reporta cada violación de la estrategia tipada de errores: `Either<DomainError, T>`, mapeo en la frontera `data`, `UiError` con `messageRes` localizado, `CoroutineExceptionHandler` raíz, prohibición de `catch (e: Exception)` genérico salvo en barreras documentadas. El skill es la red de seguridad de Detekt: produce un informe legible por humanos.

## Cuándo usar

- Tras `/speckit-implement` o tras invocar `crear-modulo`.
- Al cierre de cada ETAPA del prompt maestro como verificación previa al "definition of done" (§15).
- Antes de abrir un PR.
- Cuando el usuario diga: "valida los errores", "audita el manejo de errores del módulo `X`", "comprueba que no haya `throwable.message` en la UI", "revisa que los UseCases devuelvan `Either`".

NO usar para errores de compilación, Detekt genérico (eso es Detekt directo) ni tests fallidos.

## Inputs

| Input | Obligatorio | Por defecto | Ejemplo |
|---|---|---|---|
| `alcance` | no | todo el repo Android | `:features:products`, ruta concreta |
| `formato` | no | `markdown` | `markdown`, `json`, `plain` |

## Reglas auditadas

### ERR-001 — Repositorio sin `safeApiCall` / `safeDbCall`

En archivos bajo `*/data/repositorios/*.kt`, identificar funciones `suspend fun` que invocan Retrofit (`api.*`) o Room (`dao.*`) sin envolverlo en `safeApiCall { ... }` o `safeDbCall { ... }`. Reportar.

Sugerencia: envolver la llamada con `safeApiCall` (red) o `safeDbCall` (BD); el helper devuelve `Either<DomainError, T>` y nunca lanza.

### ERR-002 — UseCase devuelve tipo crudo en vez de `Either<DomainError, T>`

En `*/domain/casosdeuso/*.kt`, comprobar firma del operador `invoke`. Si retorna `T`, `T?`, `Flow<T>` o `suspend fun ... : T` (sin Either), reportar.

Sugerencia: cambiar firma a `suspend operator fun invoke(...): Either<DomainError, T>`.

### ERR-003 — Composable usa `throwable.message` o `.message`

En `*/presentation/.../ui/screens/*.kt` y `ui/components/*.kt`, buscar `.message` aplicado a un identificador con tipo `Throwable` o `DomainError`. Reportar.

Sugerencia: la UI nunca debe leer `message` directamente. Mostrar `stringResource(uiError.messageRes)` a través de `MangoErrorState` o `MangoSnackbar`.

### ERR-004 — Composable importa `DomainError` o `Throwable`

En cualquier archivo bajo `*/presentation/src/main/.../ui/screens/*.kt` o `ui/components/*.kt`, buscar `import com.mango.fakestore.core.error.DomainError` o `import java.lang.Throwable` (o referencias a `Throwable`). Reportar.

Sugerencia: la UI solo conoce `UiError`. Mover la traducción a `DomainErrorToUiErrorMapper` en el ViewModel.

### ERR-005 — `catch (e: Exception)` o `catch (e: Throwable)` fuera de barrera

Barreras permitidas (whitelist):
- Archivos cuyo nombre termina en `ErrorMapper.kt` o `Mapper.kt` dentro de `*/data/error/` o `core/error/`.
- Definición de `CoroutineExceptionHandler` (linea contiene `CoroutineExceptionHandler { _, ` o `CoroutineExceptionHandler { t,`).
- Inicialización de SDKs externos (Firebase, Datadog, Sentry) en archivos `*Initializer.kt` o `App.kt`.

Reportar el resto. Sugerencia: documentar la captura con `@SuppressWithReason` y mover a una de las barreras.

### ERR-006 — `runCatching` sin `.fold` que devuelva `Either<DomainError, T>`

Buscar `runCatching` seguido por algo distinto de `.fold(`. Si el resultado se convierte con `.getOrNull()`, `.getOrThrow()`, `.isSuccess`, reportar.

Sugerencia: sustituir por `safeApiCall` / `safeDbCall`, o por `runCatching { ... }.fold(onSuccess = { it.right() }, onFailure = { errorMapper.mapException<T>(it) })`.

### ERR-007 — Strings hardcoded en `MangoErrorState`, `MangoSnackbar` o diálogos

Buscar llamadas a `MangoErrorState(...)`, `MangoSnackbar(...)`, `AlertDialog(...)`, `MangoDialog(...)` que pasen un literal `String` ("...") como `mensaje`, `titulo`, `texto`, `descripcion`. Reportar.

Sugerencia: usar `stringResource(R.string.error_<dominio>_<causa>)` (§7.11). Crear la cadena en `res/values/strings.xml` y `res/values-en/strings.xml`.

### ERR-008 — UseCase sin tests por cada rama de `DomainError`

Para cada `XxxUseCase.kt` o `Xxx.kt` en `*/domain/casosdeuso/`, comprobar la existencia de `XxxTest.kt` en `*/domain/src/test/...`. Contar los `@Test` y verificar que hay al menos 1 + N tests, donde N es el número de ramas de `DomainError` referenciadas en el body. Si no se cumple, reportar el UseCase.

Sugerencia: invocar el skill `crear-pruebas-unitarias` con `modulo=<modulo>` `objetivo=usecases`.

### ERR-009 — Mapper sin tests por rama

Para cada `*Mapper.kt` o `*ErrorMapper.kt` en `data/error/` y `core/error/`, comprobar la existencia de su test. Si el mapper tiene `when` con N ramas, exigir ≥N tests. Reportar si faltan.

Sugerencia: invocar `crear-pruebas-unitarias modulo=<modulo> objetivo=mappers`.

### ERR-010 — `viewModelScope.launch` sin `CoroutineExceptionHandler`

En `*/presentation/viewmodel/*.kt`, buscar `viewModelScope.launch {` SIN un parámetro de tipo `CoroutineExceptionHandler` declarado en la firma del `launch` (es decir, `viewModelScope.launch(errorHandler) { ... }`). Reportar.

Excepción: si el `launch` está dentro de un `flatMapLatest` o `combine` que ya tiene su propia gestión, marcar como advertencia (no error).

Sugerencia: declarar `private val errorHandler = CoroutineExceptionHandler { _, t -> telemetry.reportarNoFatal(t, ...) ; _uiState.value = UiState.Error(...) }` y pasarlo en cada `launch`.

## Algoritmo

1. Si `alcance` está acotado, restringir el árbol.
2. Recorrer `.kt` y aplicar ERR-001..ERR-007 y ERR-010 con grep + análisis ligero (lookahead).
3. Para ERR-008 y ERR-009, parsear los `when` en mappers/UseCases y cruzar con el conteo de `@Test`.
4. Agregar violaciones, deduplicar, ordenar por (módulo, severidad, archivo, línea).
5. Emitir informe.

## Formato del informe

### Markdown (por defecto)

```markdown
# Informe de validación de manejo de errores

**Fecha**: 2026-MM-DD
**Alcance**: `<alcance>`
**Total violaciones**: N (M únicas)

## Resumen por regla

| Regla | Conteo | Severidad |
|---|---|---|
| ERR-001 | 0 | Crítica |
| ERR-003 | 1 | Crítica |
| ... | ... | ... |

## Detalle

### ERR-003 — Composable usa `throwable.message`

**Archivos afectados**: 1

- `features/products/presentation/.../ProductosScreen.kt:42`
  Línea: `Text(text = error.message)`
  Sugerencia: usar `MangoErrorState(uiError = state.uiError, onRetry = { ... })` y mapear DomainError → UiError en el ViewModel.

...
```

### JSON

```json
{
  "fecha": "2026-MM-DD",
  "alcance": "...",
  "violaciones": [
    {
      "regla": "ERR-003",
      "severidad": "critica",
      "archivo": "...",
      "linea": 42,
      "detalle": "...",
      "sugerencia": "..."
    }
  ],
  "resumen": {"ERR-003": 1}
}
```

## Veredicto

- ✅ **0 violaciones** → "El manejo de errores cumple §7 del prompt maestro."
- ⚠️ **N violaciones** → "Encontradas N violaciones. Corregir antes de cerrar la ETAPA."

Guardar el informe en `specs/<feature-actual>/errores-report-<fecha>.md` o `.json`.

## Script auxiliar

En `scripts/audit-errores.sh`. Bash + grep que cubre ERR-003, ERR-004, ERR-005, ERR-006 y ERR-007 (las reglas regex-friendly). ERR-001, ERR-002, ERR-008, ERR-009, ERR-010 requieren análisis sintáctico más profundo y se delegan al razonamiento del modelo + Detekt en CI.

## Salida cuando aún no hay código

Si el repo no tiene módulos `:features:*` ni `:core:*` con código (caso ETAPA 0): emitir `{"violaciones": [], "nota": "Sin código auditable todavía (ETAPA 0)."}` y exit 0.
