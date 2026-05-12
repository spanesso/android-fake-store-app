# Informe de validación de arquitectura — ETAPA 1.5–1.7

**Fecha**: 2026-05-11
**Alcance**: `core:network`, `core:database`, `core:datastore`
**Total violaciones**: 0 (0 únicas)

## Resumen por regla

| Regla | Conteo | Severidad | Estado |
|-------|--------|-----------|--------|
| ARQ-001 | N/A | Crítica | ✅ N/A — sin Composables |
| ARQ-002 | 0 | Alta | ✅ PASS |
| ARQ-003 | 0 | Alta | ✅ PASS |
| ARQ-004 | N/A | Alta | ✅ N/A — son módulos de infraestructura |
| ARQ-005 | N/A | Alta | ✅ N/A — sin capa presentation |
| ARQ-006 | 0 | Media | ✅ PASS |
| ARQ-007 | N/A | Alta | ✅ N/A — sin UseCases |
| ARQ-008 | N/A | Media | ✅ N/A — sin UseCases |
| ARQ-009 | 0 | Crítica | ✅ PASS |
| ARQ-010 | 0 | Alta | ✅ PASS |

## Grafo de dependencias

```
core:network   → core:error, core:common
core:database  → core:error, core:common
core:datastore → core:common
```

Sin ciclos detectados. Flujo unidireccional confirmado.

## Detalle por regla

### ARQ-002 — Imports de Material3 fuera de `:core:design-system`
Ningún archivo en los tres módulos importa `androidx.compose.material3.*`. ✅

### ARQ-003 — DTOs o Entities públicos fuera de `data`
Los únicos usos de `@Entity`/`Dto` en los fuentes son comentarios que explican la
decisión de no tener `@Database` en `MangoDatabase`. No hay tipos públicos con esos
sufijos exportados. ✅

### ARQ-006 — Comunicación entre features sin pasar por `:api`
Ningún archivo importa símbolos de `com.mango.fakestore.features.*`. ✅

### ARQ-009 — Dependencias circulares
Grafo verificado con DFS: sin back-edges. Los tres módulos apuntan hacia
`core:common` y `core:error` sin rutas de retorno. ✅

### ARQ-010 — `:app` depende de `:features:*:data` o `:features:*:domain`
`app/build.gradle.kts` declara `:core:network`, `:core:database`, `:core:datastore`
como dependencias de infraestructura (no de feature), lo cual es correcto. ✅

## Observaciones positivas

- `MangoDatabase` sin `@Database` es la decisión arquitectónica correcta: las
  `@Entity` las definen los feature modules y el ensamblaje concreto queda en `:app`.
- `TinkEncryption` es `open class` (no `final`) para facilitar testing sin Keystore real.
- `ConnectivityObserverImpl` usa `callbackFlow` frío — sin `shareIn` en producción —
  lo que evita estado compartido implícito entre tests.

---

✅ **0 violaciones** — La arquitectura de `core:network`, `core:database` y
`core:datastore` cumple con §3 y §4 del prompt maestro.
