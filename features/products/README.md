# Módulo `:features:products`

**Estado**: estructura vacía (ETAPA 0.5).
**Implementación prevista**: ETAPA 3.
**Propósito**: Listado de productos consumido desde Fake Store API, cache Room, paginación preparada.

Submódulos:

| Submódulo | Capa | Convention plugin |
|---|---|---|
| `:features:/Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/repository/android-fake-store-app/productspi` | Contratos públicos (interfaces, modelos) | `mango.kotlin.library` |
| `:features:products:domain` | Casos de uso, entidades, errores | `mango.kotlin.library` |
| `:features:products:data` | Retrofit, Room, ErrorMapper, repositorios | `mango.android.library` + `mango.android.hilt` |
| `:features:products:presentation` | ViewModels, Composables, Route/Screen | `mango.android.feature` |

## Reglas (R0.2, §4)

- `data` y `presentation` nunca se conocen entre sí.
- `domain` solo depende de `:core:common` y `:core:error`.
- La comunicación con otros módulos pasa por `:features:/Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/repository/android-fake-store-app/productspi`.

## Documentación detallada

Cuando el módulo se implemente, ver `features/products/docs/` (generado por el skill
`documentar-modulo`).
