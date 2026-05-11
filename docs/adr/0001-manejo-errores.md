# ADR 0001 — Manejo de errores con `Arrow Either<DomainError, T>`

**Estado**: Aceptado
**Fecha**: 2026-05-11
**Decidores**: equipo del proyecto Mango Fake Store
**Referencias**: §7 del prompt maestro (`prompt.txt`), R0.11 de la constitución.

## Contexto

La aplicación debe propagar errores entre capas sin perder información, sin exponer
`Throwable` a la UI y sin que cada caso de uso reinvente su propio mecanismo de manejo de
fallos. El prompt maestro exige (R0.11) que ninguna capa propague excepciones crudas hacia
arriba y que todos los caminos de error sean explícitos y tipados desde `domain`. Adicional-
mente, los Composables nunca deben leer `throwable.message` (§7.6 y §7.13).

Se evaluaron cuatro opciones para representar el resultado de un caso de uso o de una
operación de repositorio.

## Alternativas consideradas

### 1. Excepciones crudas (`throw`)

Cada repositorio/usecase lanza la excepción que le llega de Retrofit/Room y la UI la
captura. Es el patrón por defecto en Android antes de Kotlin/Compose.

- ✅ Simple, idiomático para JVM tradicional.
- ❌ Las excepciones no aparecen en la firma; el llamador no sabe qué capturar.
- ❌ Romper R0.11: el sistema "fail-soft" en errores esperados queda imposible sin
  envoltorios manuales.
- ❌ Difícil de testear: cada test debe simular el `throw` con la excepción correcta.

### 2. `kotlin.Result<T>`

Wrapper de Kotlin estándar (`Result<T>`) con `success`/`failure`.

- ✅ Está en stdlib, sin dependencia extra.
- ✅ Captura excepciones automáticamente vía `runCatching`.
- ❌ El tipo de error es siempre `Throwable`; el llamador no puede pattern-matchear sin
  perder seguridad de tipos.
- ❌ `Result` no permite anidación en otros tipos sealed (`Result<Result<T>>` es legal pero
  no útil).
- ❌ Pierde la riqueza de un `sealed interface` de errores con casos específicos del dominio.

### 3. `sealed class DomainResult<out T>` propio

Reproducir Either pero con clases del propio proyecto:

```kotlin
sealed class DomainResult<out T> {
    data class Success<T>(val value: T) : DomainResult<T>()
    data class Failure(val error: DomainError) : DomainResult<Nothing>()
}
```

- ✅ Sin dependencia externa.
- ✅ Total control sobre la API.
- ❌ Reinventa funciones de transformación (`map`, `flatMap`, `fold`) que ya existen en
  Arrow.
- ❌ Cada equipo tiende a divergir en la API (algunos llaman al success `Ok`, otros `Right`,
  etc.); aumenta la fricción de onboarding.
- ❌ Pierde interoperabilidad con `arrow.fx.coroutines` (parallel, race, etc.).

### 4. **`Arrow Either<DomainError, T>` (elegida)**

Usar la mónada `Either` de la librería Arrow (`io.arrow-kt:arrow-core`). Convención: el lado
izquierdo (`Left`) representa error (`DomainError`); el derecho (`Right`), éxito (`T`).

```kotlin
suspend fun obtenerProductos(): Either<DomainError, List<Producto>>
```

- ✅ Tipado fuerte: la firma del UseCase declara con precisión todos los errores posibles
  porque `DomainError` es `sealed`.
- ✅ API rica: `map`, `flatMap`, `fold`, `mapLeft`, `getOrElse`, comprehensions con `either {
  ... }`, integración con coroutines (`arrow-fx-coroutines`).
- ✅ Familiar para desarrolladores que vienen de Kotlin funcional, Scala, Haskell.
- ✅ Permite componer múltiples llamadas sin `if (resultado.isError) return resultado.error`
  repetitivos.
- ❌ Añade una dependencia (`arrow-core` ~600 KB, `arrow-fx-coroutines` ~200 KB).
- ❌ Pequeña curva de aprendizaje para quien viene de Java imperativo.

## Decisión

Adoptar **`Arrow Either<DomainError, T>`** como tipo de retorno obligatorio en:

- Todas las funciones públicas de `:features:*:domain` (casos de uso y contratos de
  repositorio).
- Todas las funciones públicas de los repositorios en `:features:*:data` y `:core:*` que
  puedan fallar por red, BD, validación o seguridad.

Las funciones que devuelven streams usan `Flow<Either<DomainError, T>>`.

### Estructura del error de dominio

`DomainError` vive en `:core:error` y es la jerarquía sealed de §7.2 del prompt maestro:

```
DomainError
├── Network
│   ├── NoConnection
│   ├── Timeout
│   ├── Server(httpCode)
│   ├── Unauthorized
│   ├── Forbidden
│   ├── NotFound
│   └── Parsing
├── Database
│   ├── ReadFailed
│   ├── WriteFailed
│   ├── NotFound
│   └── IntegrityViolation
├── Security
│   ├── BiometricUnavailable
│   ├── BiometricLockout
│   ├── RootDetected
│   ├── IntegrityFailed
│   └── SessionExpired
├── Validation(fields)
└── Unknown
```

Cada módulo de feature puede declarar errores propios en `:features:X:domain/errors/XError.kt`
extendiendo `DomainError`.

### Mapeo en la frontera `data`

Las excepciones se capturan exclusivamente en helpers genéricos:

```kotlin
suspend fun <T> safeApiCall(block: suspend () -> T): Either<DomainError, T>
suspend fun <T> safeDbCall(block: suspend () -> T): Either<DomainError, T>
```

Estos helpers usan `NetworkErrorMapper` y `DatabaseErrorMapper` para traducir cada
`Throwable` al `DomainError` correcto. La tabla de mapeo está documentada en §7.4 del prompt
maestro y se replicará en `docs/manejo-errores.md`.

### Traducción a UI

`presentation` nunca recibe `DomainError`. El ViewModel aplica `DomainErrorToUiErrorMapper`
para convertir cada error en `UiError` con `messageRes` localizado, `severity` y `actions`
disponibles. La UI muestra los mensajes via `stringResource(uiError.messageRes)` dentro de
`MangoErrorState` o `MangoSnackbar`.

## Consecuencias

### Positivas

- Las firmas de los casos de uso documentan todos los caminos de error.
- Los tests cubren cada rama de `DomainError` (umbral 100% en `domain`, §11.5 del prompt
  maestro).
- La UI obtiene mensajes localizados y accionables sin acoplarse a `Throwable`.
- Composición funcional limpia (`fold`, comprehensions) sin `try/catch` anidados.
- Detekt y los skills `validar-arquitectura` / `validar-manejo-errores` pueden verificar
  mecánicamente el cumplimiento.

### Negativas y mitigaciones

- **Dependencia adicional Arrow (~800 KB)**: aceptable; aporta valor estructural que
  justifica el peso.
- **Curva de aprendizaje**: mitigada con plantillas en los skills `crear-modulo` y
  `crear-pruebas-unitarias`, además de ejemplos en `docs/manejo-errores.md`.
- **Posible sobre-ingeniería para casos triviales**: aceptamos la disciplina porque la
  homogeneidad evita decisiones case-by-case en cada PR.

### Reglas que se derivan

- Detekt prohíbe `catch (e: Exception)` y `catch (e: Throwable)` fuera de las barreras
  documentadas (ErrorMapper, `CoroutineExceptionHandler` raíz, init de SDK). §7.13.
- Detekt prohíbe `runCatching` sin un `.fold` que devuelva `Either`.
- El skill `validar-manejo-errores` complementa Detekt con un informe en español por
  archivo:línea.
- Cada `XErrorMapper` requiere tests por cada rama (§7.14, §11.5).

## Revisión

Esta decisión se revisará si:

- Arrow deja de mantenerse o introduce cambios incompatibles importantes.
- Aparece una alternativa en la stdlib (`kotlin.Either` propuesta como KEEP) y se aprueba.
- El equipo decide moverse a un stack puramente reactivo (RxJava/Coroutines `Flow` con
  efectos), que requeriría una segunda revisión.

Mientras tanto, este ADR es la fuente única de verdad para el manejo de errores.
