# Módulo `:features:favorites`

**Estado**: estructura vacía (ETAPA 0.5).
**Implementación prevista**: ETAPA 4.
**Propósito**: Marcar/desmarcar favoritos, lista de favoritos, persistencia local en Room.

Submódulos:

| Submódulo | Capa | Convention plugin |
|---|---|---|
| `:features:/Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/repository/android-fake-store-app/favoritespi` | Contratos públicos (interfaces, modelos) | `mango.kotlin.library` |
| `:features:favorites:domain` | Casos de uso, entidades, errores | `mango.kotlin.library` |
| `:features:favorites:data` | Retrofit, Room, ErrorMapper, repositorios | `mango.android.library` + `mango.android.hilt` |
| `:features:favorites:presentation` | ViewModels, Composables, Route/Screen | `mango.android.feature` |

## Reglas (R0.2, §4)

- `data` y `presentation` nunca se conocen entre sí.
- `domain` solo depende de `:core:common` y `:core:error`.
- La comunicación con otros módulos pasa por `:features:/Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/repository/android-fake-store-app/favoritespi`.

## Documentación detallada

Cuando el módulo se implemente, ver `features/favorites/docs/` (generado por el skill
`documentar-modulo`).
