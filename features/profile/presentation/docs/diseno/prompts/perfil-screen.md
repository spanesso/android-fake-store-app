# Prompts de diseño — Pantalla `PerfilScreen`

**Estética**: Mango Fashion Group — minimalismo editorial.
**Última actualización**: 2026-05-12
**Mantenedor**: skill `prompts-de-diseno`.
**Estados cubiertos**: Loading · Error (NoConnection) · Error (NotFound) · Content

---

## Estado: Loading — móvil claro

### Midjourney v6

> Fashion app profile screen UI mockup, mobile phone screen, loading skeleton state, three horizontal shimmer placeholder bars for user name and data, soft warm off-white background (#F5F1EC), generous negative space, thin elegant serif title "Mi Perfil" top bar, brushed gold accent shimmer animation effect, premium minimalist aesthetic, deep charcoal text (#0A0A0A), refined sans-serif body font (Inter), natural soft lighting, professional editorial fashion brand UI, top app bar visible, content area with skeleton cards, no actual content --ar 9:19 --style raw --s 100 --v 6

### Negativo (Midjourney)

> `--no neon, no garish colors, no Instagram-style filters, no overlapping text, no watermark, no children, no cluttered backgrounds, no flat 2D illustration, no cartoon, no anime, no loading spinner icon, no bright colors`

### Stable Diffusion XL

**Prompt positivo**:
> Fashion app profile page, loading skeleton state, mobile UI, off-white background F5F1EC, shimmer placeholder bars, Mango premium minimalist design, editorial aesthetic, charcoal typography, gold accent, refined layout, empty content cards

**Prompt negativo**:
> neon, garish colors, Instagram filters, overlapping text, watermarks, children, cluttered backgrounds, cartoon, anime, bright gradients, busy UI

**Parámetros**: `steps=35, cfg=7, sampler=DPM++ 2M Karras, size=896x1664`

### DALL·E 3

> Crea una pantalla de aplicación móvil de moda premium con el estado de carga de la sección "Mi Perfil". El fondo es blanco cálido (#F5F1EC), con tres barras esqueleto apiladas verticalmente que representan el nombre del usuario, el email y los datos de dirección. Las barras tienen un efecto shimmer dorado suave. La barra de navegación superior muestra el título "Mi Perfil" en tipografía serif elegante. El diseño es minimalista y editorial, propio de una marca de moda premium como Mango.

---

## Estado: Loading — móvil oscuro

### Midjourney v6

> Fashion app profile screen UI mockup dark mode, mobile phone screen, loading skeleton state, three horizontal shimmer placeholder bars, deep charcoal background (#0A0A0A), soft off-white shimmer animation, brushed gold accent, premium minimalist editorial fashion brand aesthetic, thin serif top bar title, generous negative space, dark UI skeleton cards, professional fashion brand --ar 9:19 --style raw --s 100 --v 6

### Negativo (Midjourney)

> `--no neon, no garish colors, no Instagram-style filters, no overlapping text, no watermark, no children, no cluttered backgrounds, no flat 2D illustration, no cartoon, no anime`

### Stable Diffusion XL

**Prompt positivo**:
> Fashion app profile page dark mode, loading skeleton state, mobile UI, charcoal background 0A0A0A, soft shimmer placeholder bars, Mango premium minimalist design, editorial aesthetic, warm white typography hint, gold accent, refined dark layout

**Prompt negativo**:
> neon, bright colors, Instagram filters, overlapping text, watermarks, children, cluttered backgrounds, cartoon, anime

**Parámetros**: `steps=35, cfg=7, sampler=DPM++ 2M Karras, size=896x1664`

### DALL·E 3

> Crea una pantalla de aplicación móvil de moda premium en modo oscuro con el estado de carga de "Mi Perfil". Fondo negro carbón (#0A0A0A) con barras esqueleto de shimmer dorado suave. Tipografía serif en la barra de navegación superior. Diseño minimalista editorial de marca de moda premium.

---

## Estado: Error (NoConnection) — móvil claro

### Midjourney v6

> Fashion app profile screen UI mockup, mobile phone screen, network error no connection state, centered minimal error illustration with subtle wifi-off icon, off-white background (#F5F1EC), elegant error message text in charcoal, brushed gold outlined retry button, thin serif title "Mi Perfil" in top bar, premium minimalist editorial fashion brand UI, generous negative space, soft natural lighting, no connection indicator --ar 9:19 --style raw --s 100 --v 6

### Negativo (Midjourney)

> `--no neon, no garish colors, no Instagram-style filters, no overlapping text, no watermark, no children, no cluttered backgrounds, no flat 2D illustration, no cartoon, no anime, no alert icons`

### Stable Diffusion XL

**Prompt positivo**:
> Fashion app profile page, error no connection state, mobile UI, off-white background F5F1EC, minimal wifi-off icon centered, charcoal error text, gold outline retry button, Mango editorial style, generous whitespace, serif top bar

**Prompt negativo**:
> neon, garish colors, Instagram filters, overlapping text, watermarks, children, cluttered, cartoon, anime, red alert icons, warning triangles

**Parámetros**: `steps=35, cfg=7, sampler=DPM++ 2M Karras, size=896x1664`

### DALL·E 3

> Crea una pantalla de aplicación móvil de moda premium mostrando el error de "Sin conexión" en la sección "Mi Perfil". Fondo blanco cálido (#F5F1EC), ícono minimalista de wifi desconectado en el centro, texto de error elegante en carbón oscuro, botón de reintentar con borde dorado. Diseño editorial minimalista de marca de moda premium. Sin colores llamativos ni iconografía agresiva.

---

## Estado: Error (NoConnection) — móvil oscuro

### Midjourney v6

> Fashion app profile screen dark mode UI mockup, mobile phone, network error no connection state, dark charcoal background (#0A0A0A), centered minimal wifi-off icon in warm white, elegant error message, brushed gold outlined retry button, editorial fashion premium aesthetic, serif top bar, generous negative space --ar 9:19 --style raw --s 100 --v 6

### Negativo (Midjourney)

> `--no neon, no garish colors, no Instagram filters, no overlapping text, no watermark, no children, no cluttered, no cartoon, no red error alerts`

### Stable Diffusion XL

**Prompt positivo**:
> Fashion app dark mode, error state no connection, mobile UI, charcoal background 0A0A0A, minimal wifi-off icon, warm white typography, gold outlined button, Mango brand editorial style

**Prompt negativo**:
> neon, bright colors, Instagram filters, overlapping text, watermarks, cluttered, cartoon, red alert

**Parámetros**: `steps=35, cfg=7, sampler=DPM++ 2M Karras, size=896x1664`

### DALL·E 3

> Crea una pantalla de aplicación móvil de moda premium en modo oscuro mostrando el error "Sin conexión" en Mi Perfil. Fondo negro carbón, ícono minimalista de wifi desconectado en blanco cálido, texto de error elegante, botón de reintentar con contorno dorado.

---

## Estado: Error (NotFound) — móvil claro

### Midjourney v6

> Fashion app profile screen UI mockup, mobile phone screen, profile not found error state, off-white background (#F5F1EC), centered minimal person-outline icon with subtle slash, elegant error message "Perfil no encontrado" in charcoal serif, brushed gold outlined retry button, top app bar "Mi Perfil" in thin serif typography, premium editorial fashion brand design, generous whitespace, soft natural lighting --ar 9:19 --style raw --s 100 --v 6

### Negativo (Midjourney)

> `--no neon, no garish colors, no Instagram filters, no overlapping text, no watermark, no children, no cluttered backgrounds, no flat 2D illustration, no cartoon, no anime, no 404 numbers`

### Stable Diffusion XL

**Prompt positivo**:
> Fashion app profile page, 404 not found error state, mobile UI, off-white background F5F1EC, minimal person-outline icon with slash centered, elegant charcoal error text, gold outline retry button, Mango editorial premium design, serif typography

**Prompt negativo**:
> neon, garish colors, Instagram filters, overlapping text, watermarks, children, cluttered, cartoon, anime, 404 text, red icons

**Parámetros**: `steps=35, cfg=7, sampler=DPM++ 2M Karras, size=896x1664`

### DALL·E 3

> Crea una pantalla de aplicación móvil de moda premium mostrando el estado "Perfil no encontrado" en la sección Mi Perfil. Fondo blanco cálido (#F5F1EC), ícono minimalista de perfil de persona con indicador de no disponible, mensaje de error elegante en tipografía serif carbón, botón de reintentar con borde dorado. Diseño editorial minimalista.

---

## Estado: Error (NotFound) — móvil oscuro

### Midjourney v6

> Fashion app profile screen dark mode, mobile phone, profile not found error state, deep charcoal background (#0A0A0A), centered person-outline icon in warm white with subtle slash, elegant serif error message in off-white, brushed gold outlined retry button, editorial fashion premium aesthetic, generous negative space --ar 9:19 --style raw --s 100 --v 6

### Negativo (Midjourney)

> `--no neon, no garish colors, no Instagram filters, no overlapping text, no watermark, no cartoon, no 404 text`

### Stable Diffusion XL

**Prompt positivo**:
> Fashion app dark mode profile not found error, mobile UI, charcoal background 0A0A0A, minimal person-outline icon, warm white error text, gold outline button, Mango editorial design

**Prompt negativo**:
> neon, bright colors, Instagram filters, overlapping text, watermarks, cartoon, red icons, 404 numbers

**Parámetros**: `steps=35, cfg=7, sampler=DPM++ 2M Karras, size=896x1664`

### DALL·E 3

> Crea una pantalla de aplicación móvil de moda premium en modo oscuro mostrando "Perfil no encontrado" en Mi Perfil. Fondo negro carbón, ícono minimalista de persona no disponible en blanco cálido, tipografía serif elegante, botón de reintentar con borde dorado.

---

## Estado: Content — móvil claro

### Midjourney v6

> Fashion app profile screen UI mockup, mobile phone screen, content loaded state, user profile data visible, off-white background (#F5F1EC), circular avatar placeholder with warm gradient initials, user full name in elegant serif heading, email and phone in refined sans-serif body text (Inter), city and postal address in smaller light text, brushed gold badge showing "5 favoritos" count, MangoCard outlined with subtle shadow, top bar "Mi Perfil" in serif, MangoDivider thin gold line between sections, premium minimalist editorial fashion brand UI, generous negative space, soft natural lighting, photographic neutral product imagery thumbnails --ar 9:19 --style raw --s 100 --v 6

### Negativo (Midjourney)

> `--no neon, no garish colors, no Instagram-style filters, no overlapping text, no watermark, no children, no cluttered backgrounds, no flat 2D illustration, no cartoon, no anime, no product photos`

### Stable Diffusion XL

**Prompt positivo**:
> Fashion app profile page content state, mobile UI, off-white background F5F1EC, circular avatar with initials, user name in serif heading, email phone address in sans-serif, gold badge favorites count 5, MangoCard outlined, MangoDivider thin gold line, Mango brand premium editorial design, generous whitespace

**Prompt negativo**:
> neon, garish colors, Instagram filters, overlapping text, watermarks, children, cluttered backgrounds, cartoon, anime, excessive content, social media style

**Parámetros**: `steps=35, cfg=7, sampler=DPM++ 2M Karras, size=896x1664`

### DALL·E 3

> Crea una pantalla de aplicación móvil de moda premium mostrando el perfil completo del usuario en la sección "Mi Perfil". Fondo blanco cálido (#F5F1EC), avatar circular con iniciales en gradiente cálido, nombre del usuario en tipografía serif elegante, email y teléfono en sans-serif refinada, dirección completa en texto secundario, badge dorado con "5 favoritos", tarjeta con borde sutil y divisor dorado fino. Diseño editorial minimalista de Mango.

---

## Estado: Content — móvil oscuro

### Midjourney v6

> Fashion app profile screen dark mode UI mockup, mobile phone, content loaded state, deep charcoal background (#0A0A0A), circular avatar placeholder with warm gradient initials, user full name in elegant off-white serif heading, email and phone in warm white refined sans-serif, city and address in lighter gray text, brushed gold badge "5 favoritos", dark MangoCard with subtle dark border, thin gold MangoDivider, editorial fashion premium aesthetic, generous negative space --ar 9:19 --style raw --s 100 --v 6

### Negativo (Midjourney)

> `--no neon, no garish colors, no Instagram filters, no overlapping text, no watermark, no children, no cluttered backgrounds, no cartoon, no anime`

### Stable Diffusion XL

**Prompt positivo**:
> Fashion app dark mode profile content, mobile UI, charcoal background 0A0A0A, circular avatar initials warm gradient, off-white serif name heading, warm white body text, gold badge favorites count, dark MangoCard, thin gold divider, Mango editorial premium design

**Prompt negativo**:
> neon, bright colors, Instagram filters, overlapping text, watermarks, children, cluttered, cartoon, anime

**Parámetros**: `steps=35, cfg=7, sampler=DPM++ 2M Karras, size=896x1664`

### DALL·E 3

> Crea una pantalla de aplicación móvil de moda premium en modo oscuro mostrando el perfil completo del usuario. Fondo negro carbón (#0A0A0A), avatar circular con iniciales en gradiente cálido, nombre en tipografía serif blanco cálido, datos de contacto en sans-serif refinada, badge dorado con contador de favoritos, tarjeta oscura con borde sutil y divisor dorado fino. Estética editorial minimalista Mango.

---

## Notas de uso

- Los prompts de Midjourney se pegan directamente en Discord `/imagine`.
- Para SDXL usar Automatic1111 con sampler `DPM++ 2M Karras`, 35 pasos.
- Los mockups generados sirven como referencia visual — no reemplazan el diseño final en Figma.
- Actualizar los prompts si se añaden nuevos componentes visuales a `PerfilScreen`.
