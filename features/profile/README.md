# Módulo `:features:profile`

**Estado**: estructura vacía (ETAPA 0.5).
**Implementación prevista**: ETAPA 5.
**Propósito**: Perfil de usuario (endpoint /users/8) con contador de favoritos. Pantalla con FLAG_SECURE.

Submódulos:

| Submódulo | Capa | Convention plugin |
|---|---|---|
| `:features:/Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/repository/android-fake-store-app/profilepi` | Contratos públicos (interfaces, modelos) | `mango.kotlin.library` |
| `:features:profile:domain` | Casos de uso, entidades, errores | `mango.kotlin.library` |
| `:features:profile:data` | Retrofit, Room, ErrorMapper, repositorios | `mango.android.library` + `mango.android.hilt` |
| `:features:profile:presentation` | ViewModels, Composables, Route/Screen | `mango.android.feature` |

## Reglas (R0.2, §4)

- `data` y `presentation` nunca se conocen entre sí.
- `domain` solo depende de `:core:common` y `:core:error`.
- La comunicación con otros módulos pasa por `:features:/Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/repository/android-fake-store-app/profilepi`.

## Documentación detallada

Cuando el módulo se implemente, ver `features/profile/docs/` (generado por el skill
`documentar-modulo`).
