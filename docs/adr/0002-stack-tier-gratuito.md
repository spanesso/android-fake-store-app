# ADR 0002 — Stack tecnológico con tier gratuito (R0.12)

**Estado**: Aceptado
**Fecha**: 2026-05-11
**Decidores**: equipo del proyecto Mango Fake Store
**Referencias**: R0.12 del prompt maestro, §2, §8, §9.

## Contexto

El proyecto es una prueba técnica con calidad de producción para Mango Fashion Group. La
regla R0.12 del prompt maestro exige que **no se incorpore ninguna herramienta, SDK o
servicio externo sin tier gratuito viable** para esta prueba. Cualquier servicio "trial"
(Datadog, Bitrise pago, SonarCloud privado, Sentry sobre cuota gratuita, etc.) queda
marcado como opcional y se activa solo si la empresa lo provee.

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

### CI/CD

| Componente | Herramienta | Tier gratuito |
|---|---|---|
| Pipeline principal | **GitHub Actions** | 2 000 min/mes en repos privados, ilimitado en públicos. |
| Espejo opcional | Azure Pipelines | 1 800 min/mes, OPCIONAL. |
| Distribución QA | Firebase App Distribution | Ilimitado, gratis. |

### Stack opcional (solo si la empresa lo provee)

Estos servicios quedan registrados pero **no se activan** mientras el proyecto vive en
modo prueba técnica. Su integración se documenta y queda detrás de un gradle property o
flag opt-in.

| Componente | Herramienta opcional | Por qué se descarta como default |
|---|---|---|
| APM/RUM mobile | Datadog Mobile RUM + Logs + APM | Sin tier gratuito viable; solo trial. |
| Crash reporting alternativo | Sentry | Free tier limitado a 5 000 eventos/mes; bastará para fase post-piloto si crece. |
| Análisis estático cloud | SonarCloud | Gratis solo si el repo es público; el nuestro es privado. |
| Análisis estático self-hosted | SonarQube Community Edition | Requiere infraestructura propia (servidor + Postgres). |
| CI/CD mobile especializado | Bitrise | Plan gratis muy limitado para Android (≤200 builds, capping). |

## Alternativas consideradas y descartadas

1. **Adoptar Datadog Mobile RUM como observabilidad principal**: descartada por R0.12 (no
   tiene tier gratuito viable). Queda como _opcional_ documentado.
2. **SonarCloud como gate de calidad obligatorio**: descartada porque el repo es privado;
   SonarCloud no es gratis en repos privados. Detekt + Kover + Konsist cubren el mismo
   territorio sin coste.
3. **Bitrise como CI principal**: descartada por R0.12; GitHub Actions es funcionalmente
   suficiente y gratuito.
4. **Sentry como crash reporter principal**: descartada porque Firebase Crashlytics es
   ilimitado y ya está integrado en el ecosistema Google que usa el proyecto (Analytics +
   Performance + App Distribution + Test Lab).

## Consecuencias

### Positivas

- Cero gasto recurrente en herramientas durante la prueba técnica.
- Stack homogéneo (todo en el ecosistema Google + GitHub).
- Onboarding rápido: los desarrolladores ya conocen las herramientas.
- Detekt + Kover + Konsist cubren las reglas R0.5, §7.13 y §10 sin servidor externo.

### Negativas y mitigaciones

- **No hay APM mobile profundo**: Firebase Performance da traces básicos pero menos
  granular que Datadog. Mitigación: aceptable para el alcance de la prueba; documentamos
  el upgrade path a Datadog si la empresa lo aprueba.
- **Detekt y Kover requieren disciplina manual**: no hay quality gate central como Sonar.
  Mitigación: GitHub Actions valida en cada PR; el skill `validar-arquitectura` actúa como
  red de seguridad humana legible.
- **Firebase Crashlytics requiere `google-services.json`**: el archivo se gestiona como
  secreto en GitHub Actions Secrets (`FIREBASE_SERVICE_ACCOUNT_JSON`) y se descarga durante
  CI; nunca se commitea (R0.7).

## Activación de servicios opcionales

Cuando la empresa decida activar un servicio opcional:

1. Crear un ADR posterior (`0003-...md`, etc.) documentando la decisión.
2. Añadir la dependencia al catálogo (`gradle/libs.versions.toml`) tras una gradle property
   opt-in (p. ej. `mango.observabilidad.datadog=true`).
3. Implementar la integración detrás de la interfaz existente (`Telemetry`,
   `EventTracker`, etc.) en `:core:analytics`.
4. Actualizar este ADR con un cambio de estado a "Superseded" si aplica.

## Verificación

Esta decisión se verifica mediante:

- Revisión de `gradle/libs.versions.toml`: ninguna dependencia de pago/trial debe estar en
  el grafo por defecto.
- Revisión de `.github/workflows/*.yml`: ningún workflow obligatorio depende de tokens de
  servicios opcionales (Datadog, Sentry, SonarCloud).
- Bug tracker: cualquier issue que requiera un servicio opcional debe marcarlo como
  bloqueante de validación humana antes de incorporarse.

## Revisión

Se reabre esta decisión si:

- La empresa adopta el proyecto a producción y provee accesos pagos.
- Algún servicio cambia su política de tier gratuito (p. ej. Firebase deja de ser gratis).
- Una alternativa gratuita supera a las actuales y se justifica la migración en un ADR.
