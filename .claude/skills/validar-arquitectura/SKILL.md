---
name: validar-arquitectura
description: 'Audita el código Kotlin del proyecto Mango Fake Store (`repository/android-fake-store-app/`) y reporta violaciones de Clean Architecture + MVVM definidas en §3 y §4 del prompt maestro: Composables de feature que importan `hiltViewModel`/`viewModel` fuera de paquetes `route`, capas `data` que exponen DTO/Entity a `presentation`, comunicación entre módulos saltándose `:api`, UseCases con lógica Android/UI, imports prohibidos de `androidx.compose.material3.*` fuera de `:core:design-system`, dependencias `data → presentation` o `presentation → data` (deben pasar por `domain`), `domain` que importa algo fuera de `:core:common` o `:core:error`, y dependencias circulares. Devuelve un informe en español listando archivo:línea + sugerencia de corrección. Usar SIEMPRE tras `/speckit-implement`, al cerrar cada ETAPA del prompt maestro, antes de abrir PR, o cuando el usuario pida "valida la arquitectura", "audita módulo X", "revisa que no haya violaciones de Clean", "comprueba dependencias entre capas". NO usar para revisar estilo de código (eso es Detekt/ktlint), ni para revisar manejo de errores (eso es `validar-manejo-errores`), ni para sugerir refactors estéticos.'
---

# Skill `validar-arquitectura` — auditoría Clean+MVVM

Recorre el código fuente Kotlin del proyecto Android Mango Fake Store y reporta cada violación de las reglas arquitectónicas definidas en §3 y §4 del prompt maestro. El informe es accionable: archivo, línea, regla violada y sugerencia concreta.

## Cuándo usar

- Antes de abrir un PR de feature/módulo.
- Tras cada `/speckit-implement` exitoso.
- Al cierre de cada ETAPA (§14) como verificación previa al "definition of done".
- Cuando el usuario diga: "valida la arquitectura", "audita módulo `<X>`", "revisa que no haya violaciones de Clean", "comprueba dependencias entre capas".

NO usar para revisar manejo de errores (eso es `validar-manejo-errores`), estilo (Detekt/ktlint) ni refactors estéticos.

## Inputs

| Input | Obligatorio | Por defecto | Ejemplo |
|---|---|---|---|
| `alcance` | no | todo el repo Android | `:features:products`, `app/src/main/kotlin/...`, `.` |
| `formato` | no | `markdown` | `markdown`, `json`, `plain` |

## Reglas auditadas

Cada regla tiene un identificador estable (`ARQ-NNN`) que aparece en el informe y permite usar `// noinspection ARQ-NNN` o suprimir individualmente.

### ARQ-001 — Composables de feature importan `hiltViewModel` o `viewModel` fuera de `route`

Comportamiento: en `features/*/presentation/src/main/kotlin/.../ui/screens/*.kt` o `.../ui/components/*.kt`, buscar:

```
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
```

Si aparece **fuera** de un archivo en paquete `ui.route`, reportar.

Sugerencia: mover el uso del ViewModel al wrapper `Route` y pasar `uiState` + `onEvent` al Composable puro.

### ARQ-002 — Imports de Material3 fuera de `:core:design-system`

Buscar `import androidx.compose.material3.*` (excepto `Surface`, `Scaffold` y `Snackbar` que están permitidos en `:core:ui` por requisitos de Compose) en cualquier archivo bajo `features/`. Reportar.

Sugerencia: usar el componente equivalente de `:core:design-system` (`MangoButton`, `MangoTextField`, etc.). Si el componente no existe, abrir issue para extender el design system antes de proceder.

### ARQ-003 — Capa `data` expone DTOs o Entities fuera de su paquete

Buscar tipos con sufijo `Dto`, `Entity` o decorados con `@Serializable`, `@Entity`, `@TypeConverter` que sean **públicos** (sin modificador `internal` o `private`) y aparezcan importados desde paquetes `presentation`, `domain` o `api` del mismo o de otro módulo. Reportar.

Sugerencia: marcar la clase `internal` y crear un mapper en `data/mappers/` que la transforme al modelo de `domain`.

### ARQ-004 — `:domain` depende de algo distinto a `:core:common` o `:core:error`

Leer `build.gradle.kts` de cada `:features:*:domain` y `:core:*` que sea Kotlin puro. Si declara dependencias distintas de `:core:common`, `:core:error`, `arrow.core`, `kotlinx.coroutines.core`, `kotlinx.serialization` (o equivalentes), reportar.

Sugerencia: revaluar si la abstracción pertenece a `domain` o si necesita un nuevo módulo de soporte en `:core:*`. Las APIs Android (`androidx.*`) están prohibidas aquí por definición.

### ARQ-005 — `data` y `presentation` se conocen entre sí

Leer `build.gradle.kts` y `import`s de los submódulos `:features:*:data` y `:features:*:presentation`. Si alguno importa un símbolo del otro o declara dependencia Gradle directa, reportar.

Sugerencia: la comunicación debe pasar por `:features:*:api` o, si es un contrato semántico, por `:features:*:domain`.

### ARQ-006 — Comunicación entre módulos sin pasar por `:api`

Si un módulo `:features:A` importa una clase de `:features:B` cuyo paquete completo no es `com.mango.fakestore.features.b.api`, reportar.

Sugerencia: extraer el contrato a `:features:b:api` o consumir solo el caso de uso de `:features:b:domain`.

### ARQ-007 — UseCases con lógica Android o de framework

En `*/domain/casosdeuso/*.kt`, buscar imports `androidx.*`, `android.*`, `com.mango.fakestore.core.designsystem.*`. Reportar.

Sugerencia: mover la lógica Android al ViewModel correspondiente; el UseCase debe permanecer agnóstico al framework para poder testearse con JUnit puro.

### ARQ-008 — UseCases que devuelven tipos crudos en vez de `Either<DomainError, T>`

En `*/domain/casosdeuso/*.kt`, identificar funciones `operator fun invoke(...)` cuya firma de retorno **no** sea `Either<DomainError, T>` ni `Flow<Either<DomainError, T>>`. Reportar.

Sugerencia: envolver el resultado con `Either.Right`/`Either.Left` desde el repositorio y propagarlo hasta el ViewModel.

### ARQ-009 — Dependencias circulares entre módulos

Construir el grafo a partir de `dependencies { implementation(project(":..."))` en cada `build.gradle.kts`. Si existe ciclo (DFS con detección de back-edge), reportar el ciclo completo.

Sugerencia: extraer un nuevo módulo `:core:*` que aloje lo compartido; o revisar si la dependencia debería invertirse.

### ARQ-010 — `:app` se salta capas y depende directamente de `:features:*:data` o `:features:*:domain`

Excepto para wiring de Hilt, `:app` debe depender solo de `:features:*:presentation` y `:core:*`. Si depende de `:data` o `:domain` directamente, reportar.

Sugerencia: realizar la inyección a través del módulo Hilt de `presentation` que importa transitivamente los demás.

## Algoritmo de auditoría

1. Si `alcance` está acotado, restringir el árbol a ese subdirectorio.
2. Construir el grafo de dependencias Gradle leyendo todos los `build.gradle.kts` recursivamente.
3. Detectar ciclos (ARQ-009) y dependencias prohibidas (ARQ-004, ARQ-005, ARQ-006, ARQ-010).
4. Recorrer archivos `.kt` y aplicar reglas de imports (ARQ-001, ARQ-002, ARQ-003, ARQ-007).
5. Parsear (o `grep` heurístico) signatures de `operator fun invoke` para ARQ-008.
6. Agregar todas las violaciones, deduplicar y ordenar por (módulo, archivo, línea).
7. Emitir informe en el `formato` solicitado.

## Formato del informe

### Markdown (por defecto)

```markdown
# Informe de validación de arquitectura

**Fecha**: 2026-MM-DD
**Alcance**: `<alcance>`
**Total violaciones**: N (M únicas)

## Resumen por regla

| Regla | Conteo | Severidad |
|---|---|---|
| ARQ-001 | 0 | Crítica |
| ARQ-002 | 2 | Alta |
| ...    | ... | ... |

## Detalle

### ARQ-001 — Composables de feature importan `hiltViewModel` fuera de `route`

**Archivos afectados**: 1

- `features/products/presentation/.../ui/screens/ProductosScreen.kt:7`
  Import: `androidx.hilt.navigation.compose.hiltViewModel`
  Sugerencia: mover la instanciación de `ProductosViewModel` a `ui/route/ProductosRoute.kt` y pasar `state` + `onEvent` al Composable puro.

### ARQ-002 — ...

...

## Sin violaciones para

- ARQ-003, ARQ-004, ARQ-005, ARQ-009, ARQ-010
```

### JSON

```json
{
  "fecha": "2026-MM-DD",
  "alcance": "...",
  "violaciones": [
    {
      "regla": "ARQ-001",
      "severidad": "critica",
      "archivo": "features/products/presentation/.../ProductosScreen.kt",
      "linea": 7,
      "detalle": "Import prohibido: androidx.hilt.navigation.compose.hiltViewModel",
      "sugerencia": "Mover la instanciación al wrapper Route."
    }
  ],
  "resumen": {"ARQ-001": 1, "ARQ-002": 2}
}
```

## Salida final

Al terminar, imprimir un veredicto:

- ✅ **0 violaciones** → "La arquitectura cumple §3 y §4 del prompt maestro."
- ⚠️ **N violaciones** → "Encontradas N violaciones. Detalle arriba; corregir antes de cerrar la ETAPA."

Y guardar el informe completo en `specs/<feature-actual>/arquitectura-report-<fecha>.md` (markdown) o `arquitectura-report-<fecha>.json` (json).

## Notas

- Las reglas ARQ-001 y ARQ-002 también las verifica Detekt mediante reglas custom; este skill es la red de seguridad y produce un reporte legible por humanos.
- Si la salida de ARQ-009 (ciclos) tiene más de 3 ciclos, agrupar y reportar la lista plana de aristas problemáticas para que el equipo decida cómo romperlos.
- Cuando el repo aún no tiene módulos (p. ej. durante ETAPA 0), el skill debe terminar con "Sin módulos auditables todavía" y exit code 0 — no fallar.
