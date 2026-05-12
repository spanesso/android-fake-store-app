# Informe de validación de arquitectura

**Fecha**: 2026-05-11
**Alcance**: `core/common`, `core/error`, `core/design-system`, `core/ui`
**Total violaciones**: 0 (0 únicas)

## Resumen por regla

| Regla | Conteo | Severidad | Estado |
|-------|--------|-----------|--------|
| ARQ-001 | 0 | Crítica | ✅ PASS |
| ARQ-002 | 0 | Alta | ✅ PASS |
| ARQ-003 | 0 | Alta | ✅ PASS |
| ARQ-004 | 0 | Alta | ✅ PASS |
| ARQ-005 | 0 | Alta | ✅ PASS |
| ARQ-006 | 0 | Media | ✅ PASS |
| ARQ-007 | 0 | Alta | ✅ PASS |
| ARQ-008 | 0 | Media | ✅ PASS |
| ARQ-009 | 0 | Crítica | ✅ PASS |
| ARQ-010 | 0 | Alta | ✅ PASS |

## Grafo de dependencias (core modules)

```
:core:common        →  arrow.core, arrow.fx.coroutines, kotlinx.coroutines.*
:core:error         →  arrow.core, kotlinx.serialization.json
:core:design-system →  :core:error
:core:ui            →  :core:design-system, :core:error, lifecycle.runtime.compose
:app                →  (sin dependencias a :features:*:data o :features:*:domain)
```

Sin ciclos detectados. Flujo unidireccional confirmado.

## Sin violaciones para

ARQ-001, ARQ-002, ARQ-003, ARQ-004, ARQ-005, ARQ-006, ARQ-007, ARQ-008, ARQ-009, ARQ-010

## Detalle por regla

### ARQ-001 — Composables con `hiltViewModel` fuera de `route`
No hay módulos de features implementados aún. Regla no aplicable para `core/*`. ✅

### ARQ-002 — Imports de Material3 fuera de `:core:design-system`
Ningún archivo en `core/ui` o `core/common` importa `androidx.compose.material3.*`.
`core/design-system` consume Material3 internamente como corresponde. ✅

### ARQ-003 — DTOs o Entities públicos fuera de `data`
No hay módulos de data implementados en ETAPA 1. ✅

### ARQ-004 — `:domain` depende de algo no permitido
Los módulos `core/*` no son módulos de dominio de features. Sus dependencias externas son:
- `arrow.core`, `arrow.fx.coroutines` — permitidas
- `kotlinx.coroutines.*`, `kotlinx.serialization.json` — permitidas
- `androidx.lifecycle.runtime.compose` en `core:ui` — permitido (capa UI) ✅

### ARQ-005 — `data` y `presentation` se conocen entre sí
Feature scaffolds existentes (auth, products, favorites, profile) no tienen dependencias cruzadas entre sus submódulos `data` y `presentation`. ✅

### ARQ-006 — Comunicación entre features sin pasar por `:api`
No se detectaron imports de `com.mango.fakestore.features.*` que eviten el paquete `.api.`. ✅

### ARQ-007 — UseCases con lógica Android
No hay implementaciones de UseCases en ningún módulo `domain`. ✅

### ARQ-008 — UseCases sin retorno `Either<DomainError, T>`
No hay implementaciones de UseCases en ningún módulo `domain`. ✅

### ARQ-009 — Dependencias circulares
Grafo de dependencias entre módulos core: acíclico.
- `core:ui` → `core:design-system` → `core:error` (sin ciclo) ✅

### ARQ-010 — `:app` depende de `:features:*:data` o `:features:*:domain`
`app/build.gradle.kts` solo declara dependencias estándar AndroidX + Compose BOM.
No hay referencias a módulos `:features:*`. ✅

---

✅ **0 violaciones** — La arquitectura cumple §3 y §4 del prompt maestro.
