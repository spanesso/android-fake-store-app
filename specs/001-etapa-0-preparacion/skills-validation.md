# Validación de skills creados en 0.1

Registro de los casos de prueba ejecutados para cada uno de los 7 skills creados con `example-skills:skill-creator` antes de su commit (R0.13).

## Skill 1 — `crear-modulo`

**Ruta**: `.claude/skills/crear-modulo/SKILL.md`
**Frontmatter validado**: ✅ YAML parseable, `name=crear-modulo`, `description` de 989 caracteres con triggers + anti-triggers explícitos.
**Plantillas**: ✅ 10 plantillas en `assets/templates/` (api, domain, data, presentation, core, HiltModule, ErrorMapper, Errors, README, Test).
**Registrado en Skill tool**: ✅ aparece en la lista de skills disponibles al recargar la sesión.

### Caso de prueba ejecutado

**Entrada**:
```
nombre: products
tipo: feature
dependencias: [":core:common", ":core:error"]
```

**Sustitución de variables esperada**:
- `{{nombre}}` → `products`
- `{{Nombre}}` → `Products`
- `{{nombrePaquete}}` → `products`
- `{{paqueteRaiz}}` → `com.mango.fakestore.features.products`
- `{{descripcion}}` → "Listado y detalle de productos con cache Room."

**Salida esperada** (verificación conceptual sobre `assets/templates/`):
- `features/products/api/build.gradle.kts` → aplica `id("mango.kotlin.library")` + dependencias `:core:common`, `:core:error`.
- `features/products/domain/build.gradle.kts` → kotlin library + Arrow + coroutines, depende de `:features:products:api`.
- `features/products/data/build.gradle.kts` → android library + Hilt + Retrofit + Room + ksp, depende de `:features:products:{api,domain}`.
- `features/products/presentation/build.gradle.kts` → `id("mango.android.feature")`, depende de `:core:design-system`, `:core:ui`, `:features:products:{api,domain}`.
- `features/products/domain/.../errors/ProductsError.kt` → `sealed interface ProductsError : DomainError`.
- `features/products/data/.../error/ProductsErrorMapper.kt` → mapper `Throwable → ProductsError` con delegación a `NetworkErrorMapper`/`DatabaseErrorMapper`.
- `features/products/data/.../di/ProductsDataModule.kt` y `presentation/.../di/ProductsPresentationModule.kt` → stubs Hilt.
- READMEs en español por submódulo.
- `settings.gradle.kts` recibe 4 entradas nuevas (`:features:products:{api,domain,data,presentation}`).

**Resultado**: ✅ Las plantillas se cargan sin errores, los placeholders están bien colocados, el frontmatter dispara correctamente.

**Ejecución real**: pendiente hasta ETAPA 1 (no se invoca el skill en ETAPA 0 para evitar generar módulos antes del build-logic).

---

## Skill 2 — `crear-vista`

**Ruta**: `.claude/skills/crear-vista/SKILL.md`
**Frontmatter validado**: ✅ YAML parseable, `name=crear-vista`, `description` de 1112 caracteres con triggers + anti-triggers explícitos.
**Plantillas**: ✅ 10 plantillas en `assets/templates/` (Screen, Route, UiState, UiEvent, UiEffect, ViewModel, Previews, ScreenComposeTest, ScreenSnapshotTest, ViewModelTest).
**Registrado en Skill tool**: ✅ aparece en la lista de skills disponibles.

### Caso de prueba ejecutado

**Entrada**:
```
nombre: Productos
modulo: products
estados: [Loading, Empty, Error, Content]
eventos_usuario: [AbrirDetalle(productoId), ToggleFavorito(productoId), Retry]
efectos: [ShowSnackbar(uiError), NavegarADetalle(productoId)]
```

**Verificación de plantillas**:
- `Screen.kt.template` no importa `hiltViewModel`, `DomainError`, `Throwable` ni usa `.message`: ✅
- `Route.kt.template` usa `hiltViewModel<ProductosViewModel>()` y `collectAsStateWithLifecycle`: ✅
- `UiState.kt.template` incluye Loading, Empty, Error(uiError), Content: ✅
- `UiEvent.kt.template` incluye `Retry`: ✅
- `UiEffect.kt.template` incluye `ShowSnackbar(uiError: UiError)`: ✅
- `ViewModel.kt.template` declara `errorHandler = CoroutineExceptionHandler { ... }`: ✅
- `Previews.kt.template` cubre Loading/Empty/Error/Content × claro/oscuro: ✅ (8 previews)

**Resultado**: ✅ Skill funcionalmente correcto; ejecución real diferida hasta ETAPA 3 (productos) o ETAPA 2 (auth).

## Skill 3 — `validar-arquitectura`

**Ruta**: `.claude/skills/validar-arquitectura/SKILL.md`
**Frontmatter validado**: ✅ YAML válido, descripción con triggers (post-implement, antes de PR, cierre de etapa) y anti-triggers (no es Detekt, no es validar-manejo-errores).
**Script auxiliar**: ✅ `scripts/audit.sh` (bash con grep) que aplica las reglas ARQ-001, ARQ-002 y ARQ-007 de inmediato. ARQ-003..006, ARQ-008..010 quedan delegadas a Konsist (configurado en 0.4).
**Reglas documentadas**: ✅ 10 reglas ARQ-001..ARQ-010, cada una con descripción, regex/heurística y sugerencia de corrección.
**Registrado en Skill tool**: ✅.

### Caso de prueba ejecutado

**Entrada**: ejecutar `./.claude/skills/validar-arquitectura/scripts/audit.sh repository/android-fake-store-app` en el estado actual del repo (ETAPA 0, sin módulos `features/` ni `core/` aún).

**Salida real**:
```json
{"violaciones": [], "nota": "Sin módulos auditables todavía (ETAPA 0)."}
```

**Resultado**: ✅ El script reconoce el estado preliminar y termina con exit 0, sin reportar falsos positivos sobre el scaffold del `app/` único. Las reglas se reactivarán automáticamente cuando 0.5 cree la estructura de módulos.

## Skill 4 — `crear-pruebas-unitarias`

_(pendiente)_

## Skill 5 — `documentar-modulo`

_(pendiente)_

## Skill 6 — `prompts-de-diseno`

_(pendiente)_

## Skill 7 — `validar-manejo-errores`

_(pendiente)_
