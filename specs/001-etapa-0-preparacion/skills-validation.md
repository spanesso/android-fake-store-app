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

_(pendiente)_

## Skill 3 — `validar-arquitectura`

_(pendiente)_

## Skill 4 — `crear-pruebas-unitarias`

_(pendiente)_

## Skill 5 — `documentar-modulo`

_(pendiente)_

## Skill 6 — `prompts-de-diseno`

_(pendiente)_

## Skill 7 — `validar-manejo-errores`

_(pendiente)_
