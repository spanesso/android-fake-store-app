# ADR 0002 — Stack tecnológico con tier gratuito (R0.12)

**Estado**: Aceptado
**Fecha**: 2026-05-11
**Decidores**: equipo del proyecto Mango Fake Store
**Referencias**: R0.12 del prompt maestro, §2, §8, §9.

## Contexto

El proyecto es una prueba técnica con calidad de producción para Mango Fashion Group. La
regla R0.12 del prompt maestro exige que **no se incorpore ninguna herramienta, SDK o
servicio externo sin tier gratuito viable** para esta prueba. Cualquier servicio de pago
(Bitrise, etc.) queda fuera del alcance.

El repositorio Android es **público** en [github.com/spanesso/android-fake-store-app](https://github.com/spanesso/android-fake-store-app), lo que desbloquea el tier gratuito de SonarCloud y los minutos ilimitados de GitHub Actions.

Esta decisión condiciona observabilidad, CI/CD, análisis estático y distribución interna.

## Decisión

Se adopta la siguiente pila por defecto, toda con tier gratuito permanente o suficiente
para el alcance del proyecto:

### Observabilidad (estack obligatoria)

| Componente | Herramienta | Tier gratuito |
|---|---|---|
| Crash reporting | **Firebase Crashlytics** | Ilimitado, gratuito. |
| Analytics de producto | **Firebase Analytics** | Ilimitado, gratuito. |
| Performance / traces | **Firebase Performance Monitoring** | Ilimitado, gratuito. |
| Logging estructurado | **Timber** (DebugTree en debug, CrashlyticsTree en release) | Open source. |
| Distribución interna | **Firebase App Distribution** | Ilimitado, gratuito. |
| Pruebas en dispositivo | **Firebase Test Lab** | Cuota gratis diaria suficiente para PRs. |

### Calidad de código

| Componente | Herramienta | Coste |
|---|---|---|
| Análisis estático Kotlin | **Detekt** | Gratis, local. |
| Formato/lint | **ktlint** vía plugin Gradle | Gratis, local. |
| Cobertura | **Kover** (de JetBrains) | Gratis, sustituye JaCoCo para Kotlin moderno. |
| Arquitectura | **Konsist** | Gratis, ejecuta tests JUnit. |
| Análisis estático cloud | **SonarCloud** | **Gratis para repos públicos** ✅ — activar en ETAPA 9 (CI/CD). |

### CI/CD

| Componente | Herramienta | Tier gratuito |
|---|---|---|
| Pipeline principal | **GitHub Actions** | Ilimitado en repos públicos ✅ (el nuestro es público). |
| Espejo opcional | Azure Pipelines | 1 800 min/mes, OPCIONAL. |
| Distribución QA | Firebase App Distribution | Ilimitado, gratis. |

### Herramientas descartadas (fuera del alcance de la prueba técnica)

| Componente | Herramienta | Por qué se descarta |
|---|---|---|
| Análisis estático self-hosted | SonarQube Community Edition | Requiere infraestructura propia (servidor + Postgres); descartado en favor de SonarCloud. |
| CI/CD mobile especializado | Bitrise | Plan gratis muy limitado para Android (≤200 builds, capping). |

## Alternativas consideradas y descartadas

1. **SonarCloud como gate de calidad opcional**: inicialmente descartada asumiendo repo
   privado. Al confirmar que el repo es público ([github.com/spanesso/android-fake-store-app](https://github.com/spanesso/android-fake-store-app)),
   SonarCloud es gratuito y se incorpora al plan de CI/CD de ETAPA 9. Detekt + Kover +
   Konsist siguen activos como análisis local; SonarCloud añade el quality gate central.
2. **Bitrise como CI principal**: descartada por R0.12; GitHub Actions es funcionalmente
   suficiente y gratuito.

## Consecuencias

### Positivas

- Cero gasto recurrente en herramientas durante la prueba técnica.
- Stack homogéneo (todo en el ecosistema Google + GitHub).
- Onboarding rápido: los desarrolladores ya conocen las herramientas.
- Detekt + Kover + Konsist cubren las reglas R0.5, §7.13 y §10 sin servidor externo.

### Negativas y mitigaciones

- **No hay APM mobile profundo**: Firebase Performance da traces y métricas básicos.
  Mitigación: aceptable para el alcance de la prueba técnica.
- **Quality gate centralizado disponible**: SonarCloud se activa en ETAPA 9 aprovechando
  que el repo es público. Hasta entonces, GitHub Actions + Detekt + Kover + Konsist cubren
  el mismo territorio.
- **Firebase Crashlytics requiere `google-services.json`**: el archivo se gestiona como
  secreto en GitHub Actions Secrets (`FIREBASE_SERVICE_ACCOUNT_JSON`) y se descarga durante
  CI; nunca se commitea (R0.7).

## Incorporación de nuevas herramientas

Si en el futuro se decide añadir una nueva herramienta externa:

1. Crear un ADR posterior (`0003-...md`, etc.) documentando la decisión.
2. Añadir la dependencia al catálogo (`gradle/libs.versions.toml`).
3. Implementar la integración detrás de la interfaz existente (`Telemetry`,
   `EventTracker`, etc.) en `:core:analytics`.
4. Actualizar este ADR con un cambio de estado a "Superseded" si aplica.

## Verificación

Esta decisión se verifica mediante:

- Revisión de `gradle/libs.versions.toml`: ninguna dependencia de pago/trial debe estar en
  el grafo por defecto.
- Revisión de `.github/workflows/*.yml`: ningún workflow obligatorio depende de tokens de
  servicios de pago. SonarCloud se integra en ETAPA 9 como step opcional controlado por
  el secreto `SONAR_TOKEN`.
- Bug tracker: cualquier issue que requiera un servicio opcional debe marcarlo como
  bloqueante de validación humana antes de incorporarse.

## Revisión

Se reabre esta decisión si:

- La empresa adopta el proyecto a producción y provee accesos pagos.
- Algún servicio cambia su política de tier gratuito (p. ej. Firebase deja de ser gratis).
- Una alternativa gratuita supera a las actuales y se justifica la migración en un ADR.
