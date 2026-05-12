# Lista de Verificación de Especificación: Módulos Core de Infraestructura (1.5–1.7)

**Propósito**: Validar la completitud y calidad de la especificación antes de proceder al plan  
**Creado**: 2026-05-11  
**Feature**: [spec.md](../spec.md)

## Calidad del Contenido

- [x] Sin detalles de implementación (lenguajes, frameworks, APIs) — la spec habla de QUÉS, no de CÓMOs
- [x] Centrado en el valor para el desarrollador/negocio y la corrección arquitectónica
- [x] Escrito con claridad técnica suficiente para el equipo Android
- [x] Todas las secciones obligatorias completadas

## Completitud de Requisitos

- [x] No quedan marcadores `[NEEDS CLARIFICATION]`
- [x] Los requisitos son verificables e inequívocos (cada RF tiene un criterio claro)
- [x] Los criterios de éxito son medibles (porcentajes, conteos, tiempos)
- [x] Los criterios de éxito son agnósticos a la implementación
- [x] Todos los escenarios de aceptación están definidos (Happy path + error branches)
- [x] Los casos límite están identificados (6 casos documentados)
- [x] El alcance está claramente delimitado (3 módulos, sin UI, sin features)
- [x] Las dependencias y supuestos están identificados

## Preparación del Feature

- [x] Todos los requisitos funcionales tienen criterios de aceptación claros
- [x] Los escenarios de usuario cubren los flujos primarios (red, BD, datastore)
- [x] El feature cumple los resultados medibles definidos en los Criterios de Éxito
- [x] No hay detalles de implementación en la especificación

## Cobertura de Ramas de Error (§7.4 y §7.14 del Prompt Maestro)

- [x] `NetworkErrorMapper` — todas las ramas documentadas: NoConnection, Timeout, Server, Unauthorized, Forbidden, NotFound, Parsing, Unknown
- [x] `DatabaseErrorMapper` — todas las ramas: ReadFailed, WriteFailed, NotFound, IntegrityViolation
- [x] Tests de `MockWebServer` — 6 escenarios especificados: 200, 4xx, 5xx, JSON inválido, timeout, sin red
- [x] Tests de `InMemoryRoom` — constraint violation especificado
- [x] Certificate pinning — pin real obtenido (SHA-256: `dSxOWQR+hD1HkfYEk0y+JuXzHrLTjhVPXDzGRsbO7oI=`)

## Notas

Análisis `/speckit-analyze` completado el 2026-05-11. 5 remediaciones aplicadas:
- **I1** — RF-NET-002: logging scope cambiado a `BuildConfig.DEBUG` (build type, no flavor).
- **I2** — RF-NET-005/006 y RF-DB-003/004: requisitos actualizados para reflejar que mappers y safeCall ya existen en `:core:error`.
- **I3** — RF-DB-006: descripción del patrón Room multi-módulo corregida (`:app` ensambla entidades).
- **I4** — RF-DS-003: limitado a "valores sensibles" (tokens); prefs no-sensibles almacenadas en claro.
- **U1** — tasks.md T029: `fallbackToDestructiveMigration()` solo en flavor `dev`.

0 violaciones de constitución. Listo para `/speckit-implement`.
