---
name: crear-modulo
description: 'Hace scaffolding completo de un módulo Gradle Kotlin/Android (feature con capas api/domain/data/presentation o core con un único submódulo) dentro de `repository/android-fake-store-app/` del proyecto Mango Fake Store. Genera `build.gradle.kts` por submódulo usando convention plugins (`mango.android.feature`, `mango.android.library`, `mango.kotlin.library`), paquetes Kotlin vacíos, stub de Hilt module, README en español, stubs de test JUnit/MockK/Turbine, y para features añade stub de `ErrorMapper` + `sealed interface <Modulo>Error`. Actualiza `settings.gradle.kts` para registrar el módulo. Usar SIEMPRE que el usuario pida "crear módulo", "scaffolding de módulo", "nuevo módulo de feature/core", "añade módulo Foo", "prepara features Bar" o equivalente; también cuando la ETAPA 1+ del prompt maestro requiera preparar un módulo nuevo de core o features. NO usar para modificar módulos existentes, renombrar paquetes, mover código, refactorizar o añadir clases a un módulo ya creado.'
---

# Skill `crear-modulo` — scaffolding de módulos Gradle

Genera la estructura completa de un módulo nuevo en el proyecto Android Mango Fake Store siguiendo Clean Architecture + MVVM (§3 y §4 del prompt maestro). Tu objetivo es entregar un esqueleto **funcional desde el primer build**, no un placeholder vacío: el módulo recién creado debe compilar y sus tests deben pasar incluso sin código de feature aún.

## Cuándo usar este skill

Se dispara cuando el usuario quiere **crear un módulo desde cero**. Ejemplos de frases que disparan:

- "Crea el módulo `:features:products`."
- "Scaffolding del módulo `:core:design-system`."
- "Añade un módulo de feature llamado `auth` con dependencias en `:core:security`."
- "Prepara `:features:favorites` con la estructura api/domain/data/presentation."
- "Necesito un nuevo módulo core para `:core:network`."

NO disparar para: modificar un módulo existente, mover archivos entre módulos, renombrar paquetes, refactor de capas, añadir clases nuevas a un módulo ya creado, o si el usuario solo está documentando arquitectura.

## Inputs esperados

Pregunta al usuario lo que falte. Mínimo necesario:

| Input | Obligatorio | Ejemplo |
|---|---|---|
| `nombre` | sí | `products`, `design-system`, `auth` |
| `tipo` | sí | `feature` (4 submódulos) o `core` (1 submódulo) |
| `dependencias` | no | `[":core:common", ":core:error"]` |
| `descripcion_breve` | no | "Listado y detalle de productos con cache Room." |

Reglas de validación de inputs (rechazar y pedir corrección si fallan):

1. `nombre` en kebab-case lowercase, sin espacios ni mayúsculas (`design-system` ✅, `DesignSystem` ❌).
2. `tipo` ∈ {`feature`, `core`}.
3. El módulo no puede existir ya en `settings.gradle.kts`.
4. Si `dependencias` incluye un módulo, ese módulo debe existir (o el usuario debe confirmar que se creará después).

## Outputs

### Para `tipo: feature`

Bajo `repository/android-fake-store-app/features/<nombre>/`:

```
features/<nombre>/
├── api/
│   ├── build.gradle.kts                  (plugin: mango.kotlin.library)
│   ├── README.md
│   └── src/main/kotlin/com/mango/fakestore/features/<nombre>/api/
│       └── (paquete vacío con .gitkeep)
├── domain/
│   ├── build.gradle.kts                  (plugin: mango.kotlin.library)
│   ├── README.md
│   └── src/
│       ├── main/kotlin/com/mango/fakestore/features/<nombre>/domain/
│       │   ├── errors/<Nombre>Error.kt   (sealed interface <Nombre>Error : DomainError)
│       │   ├── repositorios/.gitkeep
│       │   ├── casosdeuso/.gitkeep
│       │   └── modelo/.gitkeep
│       └── test/kotlin/com/mango/fakestore/features/<nombre>/domain/
│           └── <Nombre>DomainSmokeTest.kt
├── data/
│   ├── build.gradle.kts                  (plugin: mango.android.library + hilt)
│   ├── README.md
│   ├── consumer-rules.pro
│   └── src/
│       ├── main/kotlin/com/mango/fakestore/features/<nombre>/data/
│       │   ├── di/<Nombre>DataModule.kt  (stub Hilt module)
│       │   ├── error/<Nombre>ErrorMapper.kt  (Throwable → <Nombre>Error)
│       │   ├── red/.gitkeep
│       │   ├── local/.gitkeep
│       │   ├── repositorios/.gitkeep
│       │   └── mappers/.gitkeep
│       └── test/kotlin/com/mango/fakestore/features/<nombre>/data/
│           └── <Nombre>ErrorMapperTest.kt
└── presentation/
    ├── build.gradle.kts                  (plugin: mango.android.feature)
    ├── README.md
    ├── consumer-rules.pro
    └── src/
        ├── main/kotlin/com/mango/fakestore/features/<nombre>/presentation/
        │   ├── di/<Nombre>PresentationModule.kt  (stub Hilt module)
        │   ├── ui/components/.gitkeep
        │   ├── ui/screens/.gitkeep
        │   ├── ui/route/.gitkeep
        │   └── viewmodel/.gitkeep
        └── test/kotlin/com/mango/fakestore/features/<nombre>/presentation/
            └── <Nombre>PresentationSmokeTest.kt
```

### Para `tipo: core`

Bajo `repository/android-fake-store-app/core/<nombre>/`:

```
core/<nombre>/
├── build.gradle.kts                      (plugin: mango.kotlin.library o mango.android.library según naturaleza)
├── README.md
├── consumer-rules.pro                    (solo si es android.library)
└── src/
    ├── main/kotlin/com/mango/fakestore/core/<nombrePaquete>/
    │   └── .gitkeep
    └── test/kotlin/com/mango/fakestore/core/<nombrePaquete>/
        └── <Nombre>SmokeTest.kt
```

Para `core`, pregunta al usuario si necesita Android (`android.library`) o solo JVM (`kotlin.library`). Por defecto: `kotlin.library` salvo que el módulo nombre `analytics`, `database`, `datastore`, `design-system`, `network`, `security`, `ui` → entonces `android.library`.

### Registro en `settings.gradle.kts`

Añadir entradas:

```kotlin
// Para feature:
include(":features:<nombre>:api")
include(":features:<nombre>:domain")
include(":features:<nombre>:data")
include(":features:<nombre>:presentation")

// Para core:
include(":core:<nombre>")
```

Mantén las inclusiones ordenadas alfabéticamente por bloque (core, features, etc.).

## Flujo de ejecución

1. **Leer inputs** y validar (sección "Inputs esperados").
2. **Cargar plantillas** desde `assets/templates/` de este skill.
3. **Resolver variables** de plantilla (ver tabla abajo).
4. **Generar el árbol de archivos** según el `tipo`.
5. **Actualizar `settings.gradle.kts`** insertando las nuevas líneas `include(...)` en el bloque alfabético correspondiente.
6. **Reportar al usuario** la lista de archivos creados y el comando para verificar (`./gradlew :features:<nombre>:domain:compileDebugKotlin` o equivalente).

## Variables de plantilla

Las plantillas usan los siguientes placeholders. Reemplázalos antes de escribir:

| Placeholder | Valor | Ejemplo |
|---|---|---|
| `{{nombre}}` | nombre tal cual lo pasó el usuario | `products` |
| `{{Nombre}}` | nombre en PascalCase | `Products` |
| `{{nombrePaquete}}` | nombre sin guiones para paquete Kotlin | `designsystem`, `products` |
| `{{paqueteRaiz}}` | ruta completa de paquete | `com.mango.fakestore.features.products` |
| `{{descripcion}}` | `descripcion_breve` o "Módulo `<nombre>` del proyecto Mango Fake Store." |
| `{{dependenciasGradle}}` | bloque `implementation(project(":..."))` formateado | ver plantillas |
| `{{tipo}}` | `feature` o `core` | `feature` |

Para el placeholder `{{Nombre}}`, convierte `design-system` → `DesignSystem`, `auth` → `Auth`, `products` → `Products`.

## Restricciones duras (no negociables)

- **Idioma**: todo el contenido generado (READMEs, comentarios KDoc del Hilt module stub, mensajes que escribas al usuario) está en **español**. El código (clases, funciones, variables) en inglés siguiendo convenciones Kotlin/Android.
- **Convention plugins**: nunca declares dependencias Android raw en el `build.gradle.kts` de un módulo. Aplica el convention plugin (`mango.android.feature`, `mango.android.library`, `mango.kotlin.library`) que vive en `build-logic/`. Las plantillas ya están así.
- **Capas Clean**: `data` y `presentation` nunca dependen entre sí; `domain` solo depende de `:core:common` y `:core:error`; la comunicación inter-módulos pasa por `:api`. Si el usuario pide algo distinto, alértalo y rechaza.
- **Errores tipados**: el stub de `ErrorMapper` devuelve `Either<DomainError, T>` (Arrow), nunca lanza `Throwable`. Las excepciones se capturan en la frontera `data` y se mapean. Ver `assets/templates/ErrorMapper.kt.template`.
- **Composables sin ViewModel**: la estructura de `presentation` separa `ui/screens` (puro) de `ui/route` (wrapper con `hiltViewModel`). El stub no incluye un Composable que mezcle ambos.
- **Sin código de feature**: este skill solo crea esqueleto. No inventes UseCases, repositorios ni entidades — eso es trabajo de `/speckit-implement` con su spec.md y plan.md.

## Reporte final al usuario

Al terminar, imprime un resumen estructurado:

```
✅ Módulo `:features:<nombre>` creado.

Archivos generados (N):
  features/<nombre>/api/...           (M archivos)
  features/<nombre>/domain/...        (M archivos)
  features/<nombre>/data/...          (M archivos)
  features/<nombre>/presentation/...  (M archivos)

settings.gradle.kts actualizado: +4 entradas.

Para verificar:
  cd repository/android-fake-store-app
  ./gradlew :features:<nombre>:domain:compileDebugKotlin
  ./gradlew :features:<nombre>:domain:test

Siguiente paso sugerido:
  /speckit-specify "<descripcion del flujo del módulo>"
```

## Plantillas

Las plantillas están en `assets/templates/`. Cárgalas con `Read` y aplica el reemplazo de placeholders en memoria antes de `Write` al destino.

- `build.gradle.kts.api.template` — submódulo `:api`.
- `build.gradle.kts.domain.template` — submódulo `:domain`.
- `build.gradle.kts.data.template` — submódulo `:data`.
- `build.gradle.kts.presentation.template` — submódulo `:presentation`.
- `build.gradle.kts.core.template` — módulo único de `:core:*`.
- `HiltModule.kt.template` — stub Hilt module (usado en `data` y `presentation`).
- `ErrorMapper.kt.template` — mapper `Throwable → <Modulo>Error`.
- `Errors.kt.template` — `sealed interface <Modulo>Error : DomainError`.
- `README.md.template` — README en español por submódulo.
- `Test.kt.template` — smoke test JUnit + MockK por submódulo.

## Notas adicionales

- Si el módulo `build-logic` aún no existe (ETAPA 0 sin terminar), advierte al usuario y ofrece referenciar `id("com.android.library")` en su lugar. No bloquees por esto.
- Si `settings.gradle.kts` tiene un bloque `pluginManagement` que aún no incluye `includeBuild("build-logic")`, advierte y ofrece añadirlo.
- Después de generar, NO ejecutes Gradle por tu cuenta (puede tardar minutos). Deja que el usuario lo invoque cuando quiera.
