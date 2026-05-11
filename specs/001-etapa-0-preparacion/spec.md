# Especificación de funcionalidad: ETAPA 0 — Preparación del repositorio

**Rama de funcionalidad**: `001-etapa-0-preparacion`
**Creado**: 2026-05-11
**Estado**: Borrador
**Entrada**: Descripción del usuario: "Ejecuta la ETAPA 0 completa del prompt maestro (§14, puntos 0.1 a 0.8): crear los 7 skills de §13 vía `example-skills:skill-creator`, registrar la constitución del proyecto, configurar `build-logic` con convention plugins, configurar Detekt+ktlint+Konsist+Kover incluyendo reglas de §7.13, crear la estructura vacía de módulos según §4, documentar la arquitectura y crear los ADRs 0001 y 0002. Cierre con PR contra la rama base."

> **Nota de alcance**: Esta no es una funcionalidad de producto end-user; es una **funcionalidad de plataforma de desarrollo** cuyos "usuarios" son los desarrolladores del equipo (y Claude Code). La especificación describe el resultado esperado en términos del valor que entrega al equipo, no en términos del usuario final de la app Android.

## Escenarios de usuario y pruebas *(obligatorio)*

### Historia de usuario 1 — Skills de Spec Kit listos para arrancar el desarrollo (Prioridad: P1)

Como desarrollador del equipo (o Claude Code en una sesión futura), al invocar cualquiera de los 7 skills definidos en §13 del prompt maestro (`crear-modulo`, `crear-vista`, `validar-arquitectura`, `crear-pruebas-unitarias`, `documentar-modulo`, `prompts-de-diseno`, `validar-manejo-errores`), el skill se dispara automáticamente con un brief mínimo y produce los artefactos correctos en español, alineados con las reglas R0.1–R0.13 del prompt maestro.

**Por qué esta prioridad**: Es R0.13 y bloquea el resto del trabajo. Sin estos skills, la creación de módulos, vistas, tests y documentación se haría manualmente, con riesgo de inconsistencia y omisiones. Es lo primero que el prompt maestro exige (§14, 0.1).

**Prueba independiente**: Para cada skill, invocarlo con un caso de prueba mínimo y verificar que (a) los archivos generados existen en la ruta esperada, (b) el contenido respeta las plantillas internas del skill, (c) el idioma de los outputs es español, y (d) no se produce salida basura ni warnings de frontmatter.

**Escenarios de aceptación**:

1. **Dado** un repo sin módulo `:features:demo`, **cuando** se invoca `crear-modulo` con `{ "nombre": "demo", "tipo": "feature" }`, **entonces** se crea el árbol `:features:demo:{api,domain,data,presentation}` con `build.gradle.kts`, stub de Hilt module, stubs de tests por submódulo y registro en `settings.gradle.kts`.
2. **Dado** un módulo `:features:demo:presentation`, **cuando** se invoca `crear-vista` para la pantalla `Demo`, **entonces** se generan `DemoScreen` (Composable puro sin ViewModel), `DemoRoute` (wrapper con `hiltViewModel`), `DemoUiState/Event/Effect`, previews para cada estado en claro/oscuro y stub de test Compose+snapshot.
3. **Dado** un repo con violaciones intencionales (un Composable importando `hiltViewModel` fuera de `route`), **cuando** se invoca `validar-arquitectura`, **entonces** el informe lista la violación con archivo:línea y sugerencia de corrección, todo en español.
4. **Dado** un módulo con un UseCase que devuelve `Either<DomainError, T>`, **cuando** se invoca `crear-pruebas-unitarias`, **entonces** se generan tests con MockK/Turbine cubriendo happy path + cada rama de `DomainError`.
5. **Dado** un módulo `:features:productos`, **cuando** se invoca `documentar-modulo`, **entonces** se generan los cuatro archivos `modulo.md/diseno.md/pruebas.md/errores.md` en `features/productos/docs/` con contenido en español.
6. **Dado** una pantalla `ProductosScreen` con estado de contenido descrito, **cuando** se invoca `prompts-de-diseno`, **entonces** se crea `docs/diseno/prompts/productos.md` con prompts en español para Midjourney/SD/DALL-E aplicando estética Mango (§5).
7. **Dado** un repo con un Composable que renderiza `throwable.message`, **cuando** se invoca `validar-manejo-errores`, **entonces** el informe reporta esa violación + cualquier `catch (e: Exception)` fuera de las barreras permitidas.

---

### Historia de usuario 2 — Constitución del proyecto registrada en Spec Kit (Prioridad: P1)

Como desarrollador, al ejecutar futuros comandos de Spec Kit (`/speckit-plan`, `/speckit-tasks`, `/speckit-analyze`), el sistema dispone de una constitución formal con los principios obligatorios del prompt maestro (R0.1–R0.13 condensados), de modo que cada artefacto que se genere a partir de aquí se valide contra esos principios.

**Por qué esta prioridad**: Spec Kit usa `constitution.md` como referencia transversal. Sin ella, los `/speckit-analyze` no pueden validar consistencia con las reglas. Es §14, 0.2.

**Prueba independiente**: Verificar que existe `.specify/memory/constitution.md` con los 9 principios listados en §14.0.2 (Clean+MVVM, vistas sin VM, idioma español, modularización, Design System primero, seguridad, errores tipados, observabilidad solo con tier gratuito, testing por módulo) y que cada principio se justifica con una referencia al prompt maestro.

**Escenarios de aceptación**:

1. **Dado** un repo recién preparado, **cuando** se abre `.specify/memory/constitution.md`, **entonces** contiene exactamente los 9 principios numerados y enlazados a la sección de origen del prompt maestro.
2. **Dado** la constitución, **cuando** se ejecuta `/speckit-analyze` sobre un spec inventado que viola la regla "vistas sin ViewModel", **entonces** el análisis señala la violación citando el principio.

---

### Historia de usuario 3 — Estructura modular vacía lista para `crear-modulo` (Prioridad: P2)

Como desarrollador, al iniciar la ETAPA 1 (núcleo) y siguientes, el árbol de carpetas, `settings.gradle.kts`, los convention plugins de Gradle (`build-logic/`) y las herramientas de calidad (Detekt, ktlint, Konsist, Kover) ya están configurados, de modo que el primer `crear-modulo` no requiere bootstrap adicional.

**Por qué esta prioridad**: Reduce fricción en cada nuevo módulo. Es §14, 0.3–0.5.

**Prueba independiente**: `./gradlew help` se ejecuta con éxito, los plugins de convención están resueltos, `./gradlew detekt ktlintCheck` corre sin errores sobre el repo vacío, y `./gradlew kover` produce un reporte sin tests fallidos.

**Escenarios de aceptación**:

1. **Dado** el repo con ETAPA 0 finalizada, **cuando** se ejecuta `./gradlew help` desde `repository/android-fake-store-app/`, **entonces** todos los proyectos esperados (`:app`, `:core:*` vacíos, `:features:*` vacíos, `:build-logic`) aparecen listados sin errores de resolución.
2. **Dado** el repo, **cuando** se ejecuta `./gradlew detekt`, **entonces** Detekt aplica las reglas de §7.13 (prohibir `catch (e: Exception)`/`Throwable`, `runCatching` sin `.fold`, `throwable.message` en Composables, strings hardcoded en `MangoErrorState`/`MangoSnackbar`/diálogos) y no reporta falsos positivos en archivos placeholder.
3. **Dado** el repo, **cuando** se ejecuta `./gradlew kover`, **entonces** se genera el reporte HTML/XML en `build/reports/kover/` aunque la cobertura sea 0% (no hay tests todavía).
4. **Dado** Konsist configurado, **cuando** se ejecutan sus tests de arquitectura, **entonces** verifican las reglas de dependencias entre módulos (§4) — capa data nunca importa de presentation, capa domain solo depende de `:core:common` y `:core:error`, etc.

---

### Historia de usuario 4 — Decisiones de arquitectura documentadas en ADRs y diagrama (Prioridad: P2)

Como cualquier persona que se incorpore al proyecto, al abrir `docs/arquitectura.md` y `docs/adr/`, encuentra un mapa visual de capas y módulos y dos ADRs (0001 manejo de errores con Arrow `Either<DomainError, T>`, 0002 stack tier gratuito R0.12) que justifican las decisiones técnicas críticas que se aplicarán en las siguientes etapas.

**Por qué esta prioridad**: §14, 0.6–0.8. Permite trazabilidad de decisiones y onboarding.

**Prueba independiente**: Existen los archivos `docs/arquitectura.md`, `docs/adr/0001-manejo-errores.md`, `docs/adr/0002-stack-tier-gratuito.md` con encabezado tipo MADR (Markdown Architectural Decision Record) en español: contexto, decisión, alternativas, consecuencias.

**Escenarios de aceptación**:

1. **Dado** el repo, **cuando** se abre `docs/arquitectura.md`, **entonces** contiene un diagrama Mermaid con las capas Clean + MVVM y la matriz de dependencias permitidas entre módulos.
2. **Dado** el ADR 0001, **cuando** se lee, **entonces** justifica el uso de `Arrow Either<DomainError, T>` sobre `Result<T>`, `kotlin.Result`, excepciones o `sealed Resource`, citando §7.
3. **Dado** el ADR 0002, **cuando** se lee, **entonces** lista qué servicios se adoptan (Firebase Crashlytics/Analytics/Performance, GitHub Actions, Detekt+ktlint+Kover+Konsist) y cuáles se marcan opcionales (Datadog, Sentry, SonarCloud, Bitrise) con su umbral de free tier.

---

### Casos límite

- ¿Qué pasa si `example-skills:skill-creator` está deshabilitado o no instalado al ejecutar 0.1? → El paso aborta y se documenta cómo habilitarlo desde `~/.claude/settings.json` antes de reintentar.
- ¿Qué pasa si Detekt/Kover/Konsist no tienen versiones publicadas compatibles con AGP 9.0.1 (Kotlin 2.0.21)? → Se anota la versión usada y, si hay incompatibilidades, se fija la última versión estable conocida y se registra en el ADR de stack tier gratuito.
- ¿Qué pasa si el repo Android pierde el remote o cambia el default branch? → Se documenta en `docs/ci-cd.md` cómo restaurar el remote y reconfigurar `main`/`develop`.
- ¿Qué pasa si un skill, al validarse, falla? → Se itera con `example-skills:skill-creator` en modo edición hasta que el output cumpla los criterios; el commit del skill ocurre solo tras la iteración exitosa.

## Requisitos *(obligatorio)*

### Requisitos funcionales

- **RF-001**: El repositorio DEBE contener 7 archivos `SKILL.md` válidos bajo `.claude/skills/` con los nombres exactos: `crear-modulo`, `crear-vista`, `validar-arquitectura`, `crear-pruebas-unitarias`, `documentar-modulo`, `prompts-de-diseno`, `validar-manejo-errores`.
- **RF-002**: Cada SKILL.md DEBE generarse exclusivamente vía `example-skills:skill-creator` (R0.13), nunca a mano. El frontmatter `name` y `description` DEBE cumplir los criterios del propio `skill-creator` (descripción específica que dispare correctamente sin falsos positivos).
- **RF-003**: Cada uno de los 7 skills DEBE haber sido validado con al menos un caso de prueba antes de su commit, registrado en `specs/001-etapa-0-preparacion/skills-validation.md`.
- **RF-004**: Todos los outputs de los skills (artefactos generados al invocarlos) DEBEN estar en español.
- **RF-005**: El repositorio DEBE contener `.specify/memory/constitution.md` con los 9 principios de §14.0.2, cada uno con su referencia al prompt maestro y su criterio de verificación.
- **RF-006**: El proyecto Android (`repository/android-fake-store-app/`) DEBE contener un módulo `build-logic` con convention plugins Gradle (`android-application`, `android-library`, `android-feature`, `android-test`, `jacoco`/`kover`, `detekt`, `compose`).
- **RF-007**: El proyecto DEBE tener Detekt configurado con las reglas custom de §7.13 (prohibir `catch (e: Exception)`/`Throwable` fuera de barreras documentadas, prohibir `runCatching` sin `.fold` a `Either`, prohibir `throwable.message` en Composables, prohibir strings hardcoded en `MangoErrorState`/`MangoSnackbar`/diálogos) más reglas adicionales de §10 (prohibir `viewModel()`/`hiltViewModel()` fuera de paquetes `route`, prohibir Material3 fuera de `:core:design-system`, prohibir imports de DTO/Entity fuera de `data`).
- **RF-008**: El proyecto DEBE tener ktlint configurado (4 espacios, 120 chars, trailing comma) y debe ejecutarse vía Gradle.
- **RF-009**: El proyecto DEBE tener Konsist configurado con al menos un test de arquitectura que valide la dirección de dependencias entre capas (data y presentation nunca se conocen, domain solo conoce `:core:common` y `:core:error`).
- **RF-010**: El proyecto DEBE tener Kover configurado a nivel de root con `koverHtmlReport`/`koverXmlReport` que mergea cobertura de todos los módulos.
- **RF-011**: La estructura de carpetas DEBE existir vacía (con README placeholder por módulo) para `:app`, `:core:{design-system,ui,common,error,network,database,datastore,analytics,security,testing}` y `:features:{products,favorites,profile,auth}:{api,domain,data,presentation}`.
- **RF-012**: `settings.gradle.kts` DEBE declarar todos los módulos de RF-011 y `pluginManagement` DEBE incluir el `build-logic` como composite build o como `includeBuild`.
- **RF-013**: El repositorio DEBE contener `docs/arquitectura.md` con diagrama Mermaid (capas Clean+MVVM, matriz de dependencias entre módulos) y tabla de responsabilidades por módulo, en español.
- **RF-014**: El repositorio DEBE contener `docs/adr/0001-manejo-errores.md` con la decisión de usar `Arrow Either<DomainError, T>`, alternativas consideradas (`kotlin.Result`, excepciones, `sealed Resource`) y consecuencias.
- **RF-015**: El repositorio DEBE contener `docs/adr/0002-stack-tier-gratuito.md` con la lista de herramientas adoptadas (gratis) vs opcionales (de pago/trial), citando R0.12.
- **RF-016**: TODOS los documentos generados en esta etapa (specs, plans, tasks, ADRs, READMEs, constitución, comentarios de PR) DEBEN estar en español (R0.1).
- **RF-017**: Los commits DEBEN seguir Conventional Commits en español (`chore:`, `feat(skills):`, `docs(adr):`, etc.), con scope por área cuando aplique (R0.8).
- **RF-018**: Al cierre de la etapa, DEBE existir un PR (en el repo Android) que vaya de `001-etapa-0-preparacion` a `develop`, con título y descripción en español.

### Entidades clave *(incluir si la funcionalidad involucra datos)*

- **Skill de Claude Code**: archivo `SKILL.md` con frontmatter YAML (`name`, `description`, opcionalmente `metadata`) y cuerpo Markdown con triggers, inputs, outputs y plantillas internas. Vive en `.claude/skills/<nombre>/SKILL.md`.
- **Constitución de Spec Kit**: documento `.specify/memory/constitution.md` que Spec Kit lee como contexto para `/speckit-analyze` y otros comandos de validación.
- **Convention plugin**: clase Kotlin DSL en `build-logic/src/main/kotlin/` que aplica configuración Gradle reutilizable a un módulo (`android-application`, `android-library`, etc.).
- **ADR (Architectural Decision Record)**: documento numerado en `docs/adr/NNNN-titulo.md` con secciones Contexto, Decisión, Alternativas, Consecuencias.

## Criterios de éxito *(obligatorio)*

### Resultados medibles

- **CE-001**: Los 7 skills se invocan correctamente al menos una vez en la sesión de cierre de la ETAPA 0 y producen el output esperado en menos de 60 s por skill.
- **CE-002**: Al ejecutar `./gradlew help` desde `repository/android-fake-store-app/`, todos los módulos esperados aparecen y el comando termina sin errores en menos de 90 s en una máquina con cache fría.
- **CE-003**: `./gradlew detektAll ktlintCheck konsistTest koverXmlReport` se ejecutan en menos de 5 minutos en cache fría sin errores (Detekt y ktlint con baseline vacío, Konsist con sus tests pasando, Kover con reporte generado).
- **CE-004**: Cualquier desarrollador puede leer `docs/arquitectura.md` y los dos ADRs en menos de 15 minutos y reproducir el setup en una máquina nueva siguiendo `README.md` en menos de 30 minutos.
- **CE-005**: El PR de cierre de ETAPA 0 tiene 0 violaciones reportadas por los skills `validar-arquitectura` y `validar-manejo-errores` (ambos sobre el repo placeholder; las violaciones reales solo se evaluarán cuando haya código de feature).
- **CE-006**: El 100% de los documentos producidos (>= 10 archivos entre spec, plan, tasks, constitución, ADRs, READMEs por módulo placeholder, etc.) están en español.

## Suposiciones

- El plugin `example-skills@anthropic-agent-skills` está habilitado en `.claude/settings.json` (verificado: `{"enabledPlugins": {"example-skills@anthropic-agent-skills": true}}`). Sin él, 0.1 no puede ejecutarse.
- El equipo aceptará la versión de Detekt/ktlint/Konsist/Kover más reciente compatible con AGP 9.0.1 + Kotlin 2.0.21 (sin downgrade de versiones del proyecto).
- Las pruebas en dispositivo, Firebase, certificate pinning, etc. NO se ejecutan en ETAPA 0; solo se preparan los placeholders y las dependencias en el catálogo de versiones. La activación real ocurre en ETAPAS 1+.
- El repo Android (`repository/android-fake-store-app/`) sigue siendo el único entregable de código; el repo externo `/prueba_tecnica/` aloja la infraestructura Spec Kit y no se publica.
- El git flow acordado es `main` → `develop` → `001-etapa-0-preparacion` en el repo Android, y `master` → `001-etapa-0-preparacion` en el repo externo (mirror).
- "PR contra la rama base" se interpreta como PR `develop ← 001-etapa-0-preparacion` en el repo Android; en el repo externo, al no haber remote, basta con merge local `master ← 001-etapa-0-preparacion` al cierre.
