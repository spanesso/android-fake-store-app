---
name: prompts-de-diseno
description: 'Genera prompts en español para generadores de imágenes (Midjourney v6, Stable Diffusion XL, DALL·E 3) que describan cada pantalla del proyecto Mango Fake Store con estética de la marca Mango Fashion Group (§5 del prompt maestro): paleta neutra (#FFFFFF, #F5F1EC, #E6DED3, #B8AEA2, #2B2B2B, #0A0A0A con acentos #B08D57 oro y #8B1E1E rojo), tipografía serif elegante para titulares y sans-serif fina para cuerpo, mucho espacio en blanco, fotografía protagonista de moda premium, transiciones suaves. Produce un archivo por pantalla en `repository/android-fake-store-app/docs/diseno/prompts/<pantalla>.md` con un prompt por cada estado de UI (Loading, Empty, Error, Content) y variantes claro/oscuro, móvil/tablet. Cada prompt incluye encuadre, composición, layout con coordenadas relativas, prompt negativo y parámetros recomendados (`--ar 9:19`, `--style raw`, etc.). Usar SIEMPRE que el usuario pida "prompts de diseño para la pantalla X", "genera mockups con IA de la pantalla Y", "promp Midjourney para la app", o tras crear una nueva pantalla con `crear-vista` para acompañar la documentación visual. NO usar para escribir descripciones de marca, mood boards en texto plano sin instrucciones de IA, ni para generar las imágenes en sí (eso lo hace el generador externo).'
---

# Skill `prompts-de-diseno` — prompts visuales para IA generativa

Genera prompts en español, listos para pegar en Midjourney v6 / Stable Diffusion XL / DALL·E 3, que describan cada pantalla del proyecto Mango Fake Store con la estética visual de la marca. Cada prompt es preciso: layout, paleta, tipografía, encuadre, parámetros.

## Cuándo usar

- Tras `/crear-vista` cuando una pantalla nueva entra al proyecto y necesita mockups visuales.
- Cuando el usuario pida "prompts de diseño para la pantalla X", "genera mockups de la pantalla Y con IA", "Midjourney prompts para la app".

NO usar para:
- Generar las imágenes en sí (eso lo hace el generador de imágenes externo).
- Texto plano de marca, mood boards sin instrucciones de IA.
- Wireframes técnicos (esos viven en Figma o equivalente).

## Inputs

| Input | Obligatorio | Por defecto | Ejemplo |
|---|---|---|---|
| `pantalla` | sí | — | `Productos`, `Favoritos`, `Perfil`, `LoginBiometrico`, `DetalleProducto` |
| `estados` | no | `[Loading, Empty, Error, Content]` | subset |
| `dispositivos` | no | `[movil, tablet]` | subset |
| `modos` | no | `[claro, oscuro]` | subset |
| `descripcion_layout` | no | inferido | "lista vertical de productos con imagen + nombre + precio" |
| `generador` | no | `midjourney_v6` | `midjourney_v6`, `sdxl`, `dalle_3` |

## Estética Mango (siempre presente en cada prompt)

Resumen condensado para incluir como bloque común al final de cada prompt:

> Editorial fashion app UI, premium minimalist aesthetic, generous negative space, off-white background (#F5F1EC) or deep charcoal (#0A0A0A) for dark mode, thin elegant serif typography for titles (Playfair Display or Cormorant) and refined sans-serif for body (Inter or Manrope), subtle warm undertones, soft natural lighting, photographic product imagery with neutral backgrounds, restrained accent of brushed gold (#B08D57), no garish colors, no neon, no clutter.

## Plantillas

En `assets/templates/`:

- `pantalla.md.template` — archivo Markdown maestro por pantalla.
- `prompt-midjourney.template` — un prompt formateado para MJ v6 con `--ar`, `--style raw`, `--s 100`.
- `prompt-sdxl.template` — un prompt para SDXL con `prompt` + `negative_prompt`.
- `prompt-dalle.template` — prompt narrativo para DALL·E 3.

## Estructura del archivo por pantalla

```markdown
# Prompts de diseño — Pantalla `<Pantalla>`

**Estética**: Mango Fashion Group — minimalismo editorial.
**Última actualización**: 2026-MM-DD
**Mantenedor**: skill `prompts-de-diseno`.

## Estado: Content — móvil claro

### Midjourney v6

> [prompt completo en inglés con todos los descriptores] --ar 9:19 --style raw --s 100 --v 6

### Negativo (Midjourney)

> `--no neon, no garish colors, no Instagram-style filters, no overlapping text, no watermark, no children, no cluttered backgrounds`

### Stable Diffusion XL

**Prompt positivo**:
> [descripción]

**Prompt negativo**:
> [exclusiones]

**Parámetros**: `steps=35, cfg=7, sampler=DPM++ 2M Karras, size=896x1664`

### DALL·E 3

> Crea una vista de aplicación móvil para [descripción] ...

## Estado: Loading — móvil claro

...

## Estado: Empty — móvil claro

...

## Estado: Error — móvil claro

...

[repetir bloque por cada combinación dispositivo × modo solicitada]
```

## Composición por pantalla (guía de layout)

Si `descripcion_layout` no se aporta, inferir según el nombre estándar:

| Pantalla | Layout principal | Componentes Mango |
|---|---|---|
| `Productos` | grid 2 cols, imagen 4:5 + título + precio + corazón favorito | `MangoProductCard`, `MangoTopAppBar`, `MangoBadge` |
| `DetalleProducto` | hero imagen pantalla completa + título serif + precio + descripción + CTA | `MangoTopAppBar` (transparente), `MangoButton` Primary |
| `Favoritos` | lista vertical de cards minimalistas con imagen 1:1 + acción quitar | `MangoProductCard`, `MangoEmptyState` para vacío |
| `Perfil` | avatar circular + nombre + datos + contador favoritos | `MangoBadge`, `MangoCard` Filled |
| `LoginBiometrico` | logo Mango centrado + huella grande + texto biométrico + botón fallback | `MangoButton` Outline |

## Prompt negativo común (cualquier generador)

> no neon, no garish colors, no Instagram filters, no overlapping text, no watermarks, no children, no cluttered backgrounds, no flat 2D illustration, no cartoon, no anime, no Unsplash overused props, no AI-generated artifacts.

## Reporte final

```
✅ Prompts de `<Pantalla>` creados.

Archivo: docs/diseno/prompts/<pantalla>.md
Prompts generados: N (estados × dispositivos × modos)
Generadores cubiertos: Midjourney v6, SDXL, DALL·E 3

Para usar:
  1. Copia el prompt de Midjourney en el canal de Discord.
  2. O pega el prompt SDXL en Automatic1111 / ComfyUI.
  3. O pega el prompt DALL·E en ChatGPT con el modelo correspondiente.
```

## Reglas adicionales

- Los prompts a IA generativa van **en inglés** (modelos están entrenados mayoritariamente en inglés), pero los **comentarios, encabezados y descripciones** del archivo `.md` están en español. Esta es la única excepción a R0.1.
- No incluir nombres de marcas competencia (Zara, H&M, Massimo Dutti).
- No incluir personas reconocibles (modelos famosos, celebridades).
- Preferir composiciones aspiracionales pero realistas; evitar IA con estética genérica.
- Si la pantalla muestra precios, usar valores plausibles en EUR (`€49`, `€129`).
