# Módulo `:features:auth`

**Estado**: estructura vacía (ETAPA 0.5).
**Implementación prevista**: ETAPA 2.
**Propósito**: Login, gestión de sesión, rotación de claves.

Submódulos:

| Submódulo | Capa | Convention plugin |
|---|---|---|
| `:features:/Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/repository/android-fake-store-app/authpi` | Contratos públicos (interfaces, modelos) | `mango.kotlin.library` |
| `:features:auth:domain` | Casos de uso, entidades, errores | `mango.kotlin.library` |
| `:features:auth:data` | Retrofit, Room, ErrorMapper, repositorios | `mango.android.library` + `mango.android.hilt` |
| `:features:auth:presentation` | ViewModels, Composables, Route/Screen | `mango.android.feature` |

## Reglas (R0.2, §4)

- `data` y `presentation` nunca se conocen entre sí.
- `domain` solo depende de `:core:common` y `:core:error`.
- La comunicación con otros módulos pasa por `:features:/Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/repository/android-fake-store-app/authpi`.

## Documentación detallada

Cuando el módulo se implemente, ver `features/auth/docs/` (generado por el skill
`documentar-modulo`).
