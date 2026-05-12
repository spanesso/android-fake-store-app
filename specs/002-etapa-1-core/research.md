# Research: ETAPA 1 — Módulos Core Fundamentales

**Rama**: `002-etapa-1-core` | **Fecha**: 2026-05-11 | **Fase**: Phase 0

---

## Decisión 1 — Tipo de módulo para `:core:common`

**Decisión**: Android library (`mango.android.library` + `mango.android.hilt`), no Kotlin JVM puro.

**Rationale**: `AppDispatchers` se inyecta con Hilt, que requiere el plugin Android. La restricción "sin dependencias Android" se refiere a ausencia de UI (Compose, Views, Context); Hilt es infraestructura de DI. Usar `mango.kotlin.library` impediría registrar el `@Module` de Hilt, obligando a mover el binding a `:app` (acoplamiento no deseado).

**Alternativas consideradas**:
- `mango.kotlin.library` puro: válido si `AppDispatchers` fuera solo una interfaz sin binding, pero el binding en `:app` es un antipatrón para módulos core transversales.
- Sub-módulo `:core:common-di`: exceso de complejidad para este tamaño de proyecto.

---

## Decisión 2 — Tipo de módulo para `:core:error`

**Decisión**: Mantener `mango.kotlin.library`; añadir `androidx.annotation` como dependencia de compilación para `@StringRes`.

**Rationale**: `UiError` usa `@StringRes Int messageRes` solo como anotación de tipo seguro en tiempo de compilación; no necesita el runtime Android. `androidx.annotation:annotation` es una librería Java pura (sin runtime Android) compatible con módulos Kotlin JVM. Esto permite testear la jerarquía de errores con JUnit puro en JVM, sin emulador.

**Alternativas consideradas**:
- Android library: válido pero agrega sobrecarga de Android test runner innecesaria para un módulo que no toca Android.
- Eliminar `@StringRes`: reduce la seguridad de tipos; rechazado.

---

## Decisión 3 — Framework de snapshot tests

**Decisión**: Paparazzi con `testOptions { targetSdk = 35 }` en los módulos de design-system.

**Rationale**: Paparazzi renderiza composables en JVM (sin emulador), lo que acelera CI. `compileSdk = 36` (Android 16) es muy reciente; Paparazzi 1.3.x soporta hasta SDK 35. La opción `testOptions.targetSdk` permite separar el target de tests del compileSdk de producción sin crear módulos adicionales.

**Alternativas consideradas**:
- Roborazzi: más flexible con SDK 36, pero requiere Robolectric con más configuración y tests más lentos.
- Esperar soporte Paparazzi para SDK 36: bloquearía la implementación.

---

## Decisión 4 — Enforcement de la regla de aislamiento Material3

**Decisión**: Regla Konsist en módulo dedicado de arquitectura, con allowlist explícita de excepciones (`Surface`, `Scaffold`, `Snackbar` en `:core:ui`).

**Rationale**: Konsist permite expresar reglas de arquitectura como tests de Kotlin, se ejecutan en CI y producen mensajes de error claros. La allowlist explícita fuerza a hacer consciente cualquier nueva excepción.

**Alternativas consideradas**:
- `ForbiddenImport` de Detekt: más simple pero menos granular (no distingue por módulo origen/destino).
- Sin enforcement: no auditable; cualquier desarrollador puede romper la regla sin feedback.

---

## Decisión 5 — Observabilidad de red: MangoOfflineBanner

**Decisión**: Split stateless/stateful. `MangoOfflineBannerContent(isOffline: Boolean)` en `:core:design-system`; `MangoOfflineBanner()` stateful en `:core:ui` que observa `ConnectivityManager` vía `Flow<Boolean>`.

**Rationale**: Mantiene `:core:design-system` sin dependencias de Android system services, lo que permite snapshot tests puros del componente UI. La lógica de red pertenece a `:core:ui` o idealmente a `:core:network` (ETAPA 1.5), pero como `:core:network` aún no existe en ETAPA 1, el `ConnectivityObserver` simple se implementa en `:core:ui` como clase interna, sin exponer contratos públicos que contradigan el diseño final.

**Alternativas consideradas**:
- Todo en design-system: viola el principio de que design-system no depende de Android services.
- Todo en core:ui: diseño válido, pero pierde la capacidad de testear el componente visual en Paparazzi sin mocks.

---

## Decisión 6 — Accesibilidad (a11y) mínima

**Decisión**: WCAG 2.1 AA mínimos: `contentDescription` en íconos interactivos, touch target ≥ 48dp, ratio de contraste ≥ 4.5:1 (texto normal) / ≥ 3:1 (texto grande).

**Rationale**: Estas son las restricciones más impactantes y verificables en tiempo de desarrollo. No requieren herramientas externas; se verifican con `MangoAccessibilityTest` (basado en `AccessibilityChecks` de Espresso) o manualmente con TalkBack. Dejar a11y para después genera deuda técnica difícil de pagar en componentes ya usados por múltiples features.

**Alternativas consideradas**:
- Sin a11y en ETAPA 1: alto riesgo de deuda a11y en toda la app; rechazado.
- WCAG 2.1 AA completo (fontScale, order de foco): más costoso; diferido a ETAPA de pulido.

---

## Dependencias verificadas en `libs.versions.toml`

| Librería | Versión | Disponible |
|----------|---------|-----------|
| `arrow-core` | 1.2.4 | ✅ |
| `arrow-fx-coroutines` | 1.2.4 | ✅ |
| `hilt-android` | 2.52 | ✅ |
| `kotlinx-coroutines-core` | 1.9.0 | ✅ |
| `kotlinx-coroutines-test` | 1.9.0 | ✅ |
| `paparazzi` | ver `libs.versions.toml` | ✅ |
| `konsist` | ver `libs.versions.toml` | ✅ |
| `mockk` | 1.13.13 | ✅ |
| `turbine` | 1.2.0 | ✅ |
| `androidx.annotation` | N/A (de BOM) | ✅ |

**Dependencias pendientes de añadir a `libs.versions.toml`**:
- `paparazzi` plugin: necesita entrada en `[plugins]` si no está ya (verificar en implementación).
- `androidx.annotation` standalone: ya incluida transitivamente por `hilt-android`; declarar explícita en `:core:error` para claridad.
