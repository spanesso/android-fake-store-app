# Feature Specification: ETAPA 1 — Módulos Core Fundamentales

**Feature Branch**: `002-etapa-1-core`
**Created**: 2026-05-11
**Status**: Draft
**Input**: ETAPA 1 del prompt maestro (§4, §5, §7): módulos :core:common, :core:error, :core:design-system, :core:ui

## User Scenarios & Testing *(mandatory)*

### User Story 1 — Desarrollador usa herramientas comunes y manejo de errores (Priority: P1)

Un desarrollador del equipo comienza a implementar una feature. Necesita dispatchers de coroutines
testeables, helpers para trabajar con `Either<DomainError, T>`, y la jerarquía completa de
`DomainError` y `UiError` disponibles desde sus módulos de dominio y presentación sin escribir
código de infraestructura desde cero.

**Why this priority**: Sin `:core:common` y `:core:error` ningún módulo de feature puede ser
construido, ya que toda la lógica de dominio depende de estas abstracciones (regla de
dependencias §4).

**Independent Test**: Al agregar solo `:core:common` y `:core:error` como dependencias en un
módulo Kotlin puro, se puede compilar una clase UseCase que retorna `Either<DomainError, T>` y
un ViewModel que la consume transformándola en `UiError`.

**Acceptance Scenarios**:

1. **Dado** un módulo de dominio que importa `:core:common` y `:core:error`, **cuando** se
   define una función que retorna `Either<DomainError.Network.NoConnection, Producto>`, **entonces**
   la clase compila sin dependencias transitivas no deseadas.
2. **Dado** un mapper de errores, **cuando** recibe un `DomainError.Network.Unauthorized`,
   **entonces** el `DomainErrorToUiErrorMapper` devuelve un `UiError` con `messageRes` localizado,
   `severity = Blocking` y `actions = [Login]`.
3. **Dado** el código de cualquier módulo de feature, **cuando** un desarrollador intenta importar
   un `Throwable` directamente en un Composable, **entonces** Detekt falla el build con la regla
   correspondiente (§7.13).

---

### User Story 2 — Diseñador y desarrollador construyen pantallas con el Design System (Priority: P2)

Un desarrollador implementa una nueva pantalla de la app. Usa componentes del `:core:design-system`
(botones, campos de texto, tarjetas, indicadores de carga, estados de error) que ya incorporan
la paleta Mango, la tipografía y el espaciado aprobados, sin tener que recrear estilos ni tomar
decisiones de diseño en cada pantalla.

**Why this priority**: El design system es prerequisito para que cualquier pantalla de feature
tenga consistencia visual y cumpla la estética Mango. Sin él, cada desarrollador de feature
duplica estilos y se desvía de la identidad de marca.

**Independent Test**: Puede probarse de forma aislada levantando un catálogo de componentes
(pantalla de Previews) que muestre cada componente en claro/oscuro y todos sus estados, sin
necesidad de ningún módulo de feature.

**Acceptance Scenarios**:

1. **Dado** un `@Preview` de `MangoButton(variant = Primary, state = Loading)` en modo oscuro,
   **cuando** se renderiza el preview, **entonces** el botón muestra spinner, usa el color de
   acento correcto y el fondo es el tema oscuro.
2. **Dado** un `MangoErrorState` que recibe un `UiError` con `severity = Blocking` y
   `actions = [Retry]`, **cuando** el usuario pulsa el botón de reintento, **entonces**
   se invoca el callback `onRetry`.
3. **Dado** un snapshot test de `MangoSnackbar(severity = Warning)`, **cuando** corre en CI,
   **entonces** la imagen generada coincide con el golden file registrado, y el test falla
   si cambia el color o la tipografía.
4. **Dado** que se eliminan todos los imports directos de Material3 fuera de `:core:design-system`,
   **cuando** se corre la regla Konsist correspondiente, **entonces** no se reportan violaciones.

---

### User Story 3 — Desarrollador gestiona estados de carga, vacío y error de forma uniforme (Priority: P3)

Al construir cualquier pantalla de lista o detalle, el desarrollador usa composables de
`:core:ui` (`LoadingContent`, `EmptyContent`, `ErrorContent`) para mostrar los tres estados
transversales del ciclo de vida de datos, en lugar de reimplementar spinners o mensajes de
error en cada pantalla.

**Why this priority**: Reduce la duplicación y garantiza coherencia entre pantallas. Depende de
`:core:design-system` y `:core:error` ya resueltos (P1 y P2 primero).

**Independent Test**: Se puede probar instanciando `LoadingContent`, `EmptyContent` y
`ErrorContent` con distintas configuraciones sin ningún módulo de feature.

**Acceptance Scenarios**:

1. **Dado** una pantalla que pasa `UiState.Loading` a `LoadingContent`, **cuando** se
   renderiza, **entonces** se muestra `MangoLoadingIndicator` sin texto de error.
2. **Dado** una pantalla que pasa `UiState.Empty`, **cuando** se renderiza, **entonces**
   se muestra `MangoEmptyState` con mensaje configurable y sin botón de reintentar.
3. **Dado** una pantalla que pasa `UiState.Error(uiError)` a `ErrorContent`, **cuando**
   la `severity = Blocking`, **entonces** se muestra `MangoErrorState` con botón de reintentar
   visible y habilitado.

---

### Edge Cases

- ¿Qué ocurre si se instancia `MangoButton(state = Loading)` sin pasar texto? → Debe compilar
  y mostrar solo el spinner (texto vacío no rompe el componente).
- ¿Cómo se comporta `DomainError.Validation(fields = emptyMap())`? → Es válido; el mapper lo
  convierte en `UiError` genérico de validación.
- ¿Qué pasa si el tema oscuro/claro del sistema cambia en runtime? → Los tokens `ColorScheme`
  de Compose reaccionan automáticamente; los snapshot tests cubren ambos modos.
- ¿Puede `safeApiCall` recibir una coroutine cancelada? → Propaga `CancellationException` sin
  envolverla en `DomainError`.

## Requirements *(mandatory)*

### Functional Requirements

**Módulo :core:common**

- **RF-001**: El módulo DEBE exportar `AppDispatchers` (IO, Main, Default, Unconfined) como
  abstracciones testeables inyectables vía Hilt.
- **RF-002**: El módulo DEBE exportar helpers de extensión para `Either<L, R>`: `mapLeft`,
  `getOrElse`, `flatMap`, `fold`, y equivalentes para `Flow<Either<L, R>>`.
- **RF-003**: El módulo DEBE exportar extensiones Kotlin utilitarias (nullables, colecciones,
  strings) usadas en más de dos módulos.
- **RF-004**: El módulo NO DEBE tener dependencias Android; debe ser un módulo Kotlin puro.

**Módulo :core:error**

- **RF-005**: El módulo DEBE declarar la jerarquía `DomainError` exacta especificada en §7.2
  del prompt maestro (Network, Database, Security, Validation, Unknown con sus sub-tipos).
- **RF-006**: El módulo DEBE exportar `UiError` con `messageRes`, `severity`, `actions` y
  `errorCode` según §7.5.
- **RF-007**: El módulo DEBE proveer `safeApiCall<T>` y `safeDbCall<T>` como helpers de captura
  de excepciones que retornan `Either<DomainError, T>`.
- **RF-008**: El módulo DEBE incluir `DomainErrorToUiErrorMapper` que mapea cada sub-tipo de
  `DomainError` a un `UiError` con mensaje localizado y severidad correcta.
- **RF-009**: El módulo DEBE incluir `NetworkErrorMapper` y `DatabaseErrorMapper` según la tabla
  de §7.4 del prompt maestro.
- **RF-010**: El módulo NO DEBE importar nada de Android UI; debe ser Kotlin puro excepto
  las referencias a `@StringRes`.

**Módulo :core:design-system**

- **RF-011**: El módulo DEBE declarar todos los tokens de color `MangoColors` con sus
  variantes claro y oscuro, según §5 del prompt maestro.
- **RF-012**: El módulo DEBE declarar `MangoTypography` con los estilos listados en §5
  usando Playfair Display (o similar serif) para titulares e Inter/Manrope para cuerpo.
- **RF-013**: El módulo DEBE declarar `MangoSpacing`, `MangoShapes`, `MangoElevations` y
  `MangoMotion` con los valores exactos de §5.
- **RF-014**: El módulo DEBE implementar todos los componentes listados en §5: MangoButton,
  MangoTextField, MangoLabel/MangoText, MangoCard, MangoProductCard, MangoIconButton,
  MangoIcon, MangoChip, MangoDivider, MangoTopAppBar, MangoBottomBar/MangoNavigationBar,
  MangoDialog, MangoBottomSheet, MangoLoadingIndicator, MangoEmptyState, MangoErrorState,
  MangoBadge, MangoSnackbar, MangoOfflineBanner.
- **RF-015**: Cada componente DEBE tener `@Preview` en modo claro y oscuro, y `@Preview` por
  cada estado relevante (idle, loading, pressed, disabled, error, etc.).
- **RF-016**: Cada componente DEBE tener al menos un test de snapshot con Paparazzi o
  Roborazzi que falle si cambia su apariencia.
- **RF-017**: El módulo DEBE proveer `MangoTheme { }` como wrapper de tema que aplica colores,
  tipografía y formas a toda la jerarquía Compose.
- **RF-018**: Ningún import directo de `androidx.compose.material3.*` está permitido fuera de
  este módulo (salvo excepciones documentadas: Surface, Scaffold, Snackbar como contenedores
  en `:core:ui`).

**Módulo :core:ui**

- **RF-019**: El módulo DEBE exportar `LoadingContent`, `EmptyContent` y `ErrorContent` como
  composables que delegan en `MangoLoadingIndicator`, `MangoEmptyState` y `MangoErrorState`
  respectivamente.
- **RF-020**: El módulo DEBE exportar modifiers de utilidad Compose usados en más de dos
  features (p. ej. `Modifier.shimmer()`, `Modifier.conditional()`).
- **RF-021**: El módulo DEBE exportar extensiones de `Context`, `Dp`, `Px` y utilidades de
  preview comunes (`@PreviewLightDark`, `@PreviewFontScale`).
- **RF-022**: El módulo DEBE exportar `MangoOfflineBanner` como composable observable del
  estado de red.

### Key Entities

- **DomainError**: Jerarquía sealed de errores del dominio; jamás cruza a la capa de
  presentación.
- **UiError**: Representación de error para UI; contiene mensaje localizable, severidad y
  acciones disponibles.
- **AppDispatchers**: Abstracción de dispatchers de coroutines para testabilidad.
- **MangoTheme**: Proveedor de tokens visuales Compose (colores, tipografía, formas).
- **Componentes Mango**: Átomos y moléculas del design system reutilizados en todas las features.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Los cuatro módulos compilan sin errores (`./gradlew build`) tras su implementación.
- **SC-002**: Todos los tests unitarios de `:core:common` y `:core:error` pasan con cobertura
  del 100% en las clases de mapeo (cada rama de `DomainError` cubierta).
- **SC-003**: Los snapshot tests de todos los componentes del design system pasan en CI; un
  cambio accidental de color o tipografía hace fallar el test.
- **SC-004**: La auditoría `validar-arquitectura` reporta cero violaciones de la matriz de
  dependencias en los cuatro módulos.
- **SC-005**: La auditoría `validar-manejo-errores` reporta cero violaciones de las reglas §7.13
  en los cuatro módulos.
- **SC-006**: Ningún módulo de feature puede compilar con un import directo a `DomainError` o
  `Throwable` en sus Composables (regla Detekt activa).
- **SC-007**: El catálogo de previews del design system muestra correctamente cada componente
  en claro y oscuro en Android Studio sin configuración adicional.

## Assumptions

- Los módulos ya tienen su `build.gradle.kts` vacío creado en ETAPA 0; este trabajo implementa
  su contenido.
- La fuente Playfair Display se incorpora como archivo TTF en `core/design-system/src/main/res/font/`
  o se descarga desde Google Fonts vía el plugin de Android Studio (si no hay restricción de red).
- Los golden files de snapshot se generan en la primera ejecución y se commitean; posteriores
  ejecuciones los verifican.
- La cobertura del 100% aplica solo a las clases de mapeo (ErrorMapper); los componentes UI
  se validan con snapshots, no con unit tests de lógica.
- Paparazzi se prefiere sobre Roborazzi por no requerir dispositivo físico ni emulador.
- Los cuatro módulos se implementan secuencialmente (common → error → design-system → ui)
  dado que hay dependencias entre ellos.
