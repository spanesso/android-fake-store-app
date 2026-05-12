# ADR 0004 — `:app` depende de `:features:*:data` y `:features:*:domain` para wiring Hilt

**Estado**: Aceptado  
**Fecha**: 2026-05-12  
**Autores**: Equipo Mango Fake Store

---

## Contexto

La regla ARQ-010 dice que `:app` solo debe depender de `:features:*:presentation` y `:core:*`. Sin embargo, `app/build.gradle.kts` declara dependencias directas a:
- `:features:products:domain` y `:features:products:data`
- `:features:favorites:api` (ya corregido para usar `:api` en vez de `:domain`)
- `:features:favorites:data`
- `:features:profile:domain` y `:features:profile:data`

---

## Decisión

Mantener las dependencias de `:app` a los módulos `:data` y `:domain` de features como excepción documentada cuando su propósito sea exclusivamente wiring Hilt.

---

## Justificación

El módulo `:app` contiene `AppModule` (objeto Hilt `@InstallIn(SingletonComponent::class)`) que provee:
- `AppDatabase` (Room) y los DAOs (`ProductosDao`, `FavoritosDao`)
- `IntegrityPolicy` derivada de `BuildConfig`

Los DAOs pertenecen a los módulos `:data` de cada feature. Dado que `AppModule` es el punto de composición del grafo de dependencias de Hilt para toda la aplicación, necesita acceso a los tipos de `:data` y `:domain` para resolver las inyecciones.

Alternativa evaluada: mover los `@Provides` de DAOs a módulos Hilt dentro de cada `:data` module con `@InstallIn(SingletonComponent::class)`. Esta sería la solución arquitectónicamente correcta, pero requiere refactorizar `AppModule` y mover la instanciación de `AppDatabase` a un módulo compartido (`:core:database`). El alcance supera el tiempo disponible para esta prueba técnica.

---

## Consecuencias

- **Positivo**: `:app` puede construir el grafo completo sin cambios invasivos en los módulos existentes.
- **Negativo**: `:app` conoce los tipos internos de `:data` y `:domain`. El skill `validar-arquitectura` reportará ARQ-010 para estas dependencias.
- **Deuda técnica**: En producción, cada módulo `:data` debería declarar sus propios módulos Hilt con `@Provides` para DAOs y repositorios, eliminando la necesidad de que `:app` dependa directamente de `:data`.
