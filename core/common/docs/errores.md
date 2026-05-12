# Errores — `:core:common`

`:core:common` no define ni emite `DomainError`. Es un módulo de utilidades puras.

Los errores del tipo `Either<DomainError, T>` se originan en `:core:error` y en los módulos `:features:*:data`. Este módulo solo provee las extensiones (`flatMapRight`, `fold`) para operar con ellos sin exponerlos.

Para el catálogo completo de errores, consultar:
- [`core/error/docs/errores.md`](../../error/docs/errores.md)
