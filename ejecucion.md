# Guía de ejecución — Prompts por sesión

Este documento contiene los **prompts exactos** que debes pegarle a Claude Code, en orden, para construir la app paso a paso siguiendo `prompt.txt`. Cada bloque está pensado para una sesión nueva (conversación nueva en Claude Code) salvo que se indique lo contrario.

## Reglas de uso

1. **Una sesión = una etapa**. Si te quedas sin contexto a mitad, cierra con commit parcial y reabre con el mismo prompt + "continúa desde el punto X".
2. Antes de empezar, asegúrate de estar en el directorio raíz del proyecto: `/Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica`.
3. Antes de la ETAPA 0, decide y comunica:
   - Si fusionas el repo `repository/android-fake-store-app/` con este repo o lo mantienes separado.
   - Rama base (`master` o renombrar a `main`).
4. Tras cada sesión, espera a que abra el PR y revísalo antes de pasar a la siguiente.
5. Si una sesión deja algo a medias, anótalo aquí en `## Estado actual` al final del documento.

---

## SESIÓN 1 — ETAPA 0 · Preparación y skills

```
Lee /Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/prompt.txt
y ejecuta la ETAPA 0 completa (§14, puntos 0.1 a 0.8).

Empieza por 0.1: crea los 7 skills de §13 usando el skill
example-skills:skill-creator, uno por uno, validando cada uno con un
caso de prueba antes de pasar al siguiente. No avances a 0.2 hasta
que los 7 skills estén creados y commiteados.

Después continúa con 0.2 (/speckit-constitution con los principios
listados), 0.3 (build-logic con convention plugins), 0.4 (Detekt +
ktlint + Konsist + Kover con las reglas de §7.13), 0.5 (estructura
vacía de módulos según §4), 0.6 (docs/arquitectura.md), 0.7
(docs/adr/0001-manejo-errores.md) y 0.8 (docs/adr/0002-stack-tier-
gratuito.md).

Al cerrar la etapa, abre PR contra la rama base con título y
descripción en español.

Pregúntame si tienes dudas antes de asumir.
```

---

## SESIÓN 2 — ETAPA 1 (parte 1/3) · Núcleo de errores y diseño

```
Lee /Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/prompt.txt
y ejecuta la ETAPA 1, sub-etapas 1.1 a 1.4 (§14):

  1.1 :core:common
  1.2 :core:error
  1.3 :core:design-system
  1.4 :core:ui

Por cada módulo:
  · Usa el skill `crear-modulo` para el scaffolding.
  · Sigue el ciclo Spec Kit completo:
      /speckit-specify → /speckit-clarify → /speckit-plan
      → /speckit-tasks → /speckit-analyze → /speckit-implement
  · Al terminar, ejecuta los skills `validar-arquitectura`,
    `validar-manejo-errores` y `crear-pruebas-unitarias`.
  · Genera la documentación del módulo con `documentar-modulo`.

En :core:design-system implementa todos los tokens y componentes
listados en §5, con previews para cada estado (claro/oscuro) y
snapshot tests con Paparazzi o Roborazzi.

Compila el proyecto Android tras cada módulo
(cd repository/android-fake-store-app && ./gradlew build).

Al cerrar, abre PR en español. modifica el CLAUDE.md y README.md con estos nuevos modulos

Pregúntame si tienes dudas antes de asumir.
```

---

## SESIÓN 3 — ETAPA 1 (parte 2/3) · Núcleo de red y persistencia

```
Lee /Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/prompt.txt
y ejecuta la ETAPA 1, sub-etapas 1.5 a 1.7 (§14):

  1.5 :core:network  (Retrofit, OkHttp, certificate pinning,
                     NetworkErrorMapper, interceptor de retry con
                     backoff §7.7, ConnectivityObserver, BuildConfig
                     por flavor dev/staging/prod)
  1.6 :core:database (Room base + SQLCipher + DatabaseErrorMapper)
  1.7 :core:datastore (DataStore encriptado para tokens y prefs)

Por cada módulo:
  · skill `crear-modulo` → ciclo Spec Kit completo → implement →
    skills `validar-arquitectura`, `validar-manejo-errores`,
    `crear-pruebas-unitarias` y `documentar-modulo`.
  · Tests obligatorios por cada rama de los mappers (§7.4, §7.14).
  · MockWebServer para network, InMemoryRoom para database.

Compila tras cada módulo. Al cerrar, abre PR en español.

instrucciones de como debo implementar del dashboard en la pagina de Firebase 

Pregúntame si tienes dudas antes de asumir.
```

---

## SESIÓN 4 — ETAPA 1 (parte 3/3) · Núcleo de analytics, seguridad y testing

```
Lee /Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/prompt.txt
y ejecuta la ETAPA 1, sub-etapas 1.8 a 1.10 (§14):

  1.8  :core:analytics (interfaces Telemetry y EventTracker;
                        impls Firebase obligatoria + Console debug;
                        Datadog/Sentry como impls opcionales detrás
                        de flag — R0.12; receptor de DomainError
                        no fatales → Crashlytics)
  1.9  :core:security  (BiometricPrompt con BIOMETRIC_STRONG,
                        IntegrityChecker con RootBeer, SecureScreen
                        Composable con FLAG_SECURE)
  1.10 :core:testing   (utilidades de test, fakes, dispatchers test,
                        builders de DomainError y UiError)

Por cada módulo:
  · skill `crear-modulo` → ciclo Spec Kit → implement → skills de
    validación → `documentar-modulo`.

Compila tras cada módulo. Al cerrar, abre PR en español y deja
ETAPA 1 cerrada.

Pregúntame si tienes dudas antes de asumir.
```

---

## SESIÓN 5 — ETAPA 2 · Autenticación biométrica

```
Lee /Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/prompt.txt
y ejecuta la ETAPA 2 (§14): :features:auth.

Sigue el ciclo Spec Kit completo:
  /speckit-specify "login biométrico con BiometricPrompt"
  → /speckit-clarify → /speckit-plan → /speckit-tasks
  → /speckit-analyze → /speckit-implement

Requisitos clave:
  · Usa skill `crear-modulo` con la estructura
    api/domain/data/presentation.
  · Pantalla de login biométrico (§6.4): BIOMETRIC_STRONG con
    fallback a credencial del dispositivo, Keystore vinculado a
    auth biométrica reciente.
  · Mapeo de errores biométricos a DomainError.Security.* (§7.9):
    BiometricUnavailable, BiometricLockout, IntegrityFailed.
  · Re-autenticación tras inactividad configurable.
  · Vista construida con skill `crear-vista`: Composable Route con
    ViewModel + Composable Screen puro + previews por cada estado
    (Idle, Authenticating, Lockout, Unavailable, Error).
  · Tests con casos de lockout y unavailable.
  · Genera prompts de imagen con skill `prompts-de-diseno`.
  · Documenta con skill `documentar-modulo` (incluye errores.md).

Al cerrar, ejecuta `validar-arquitectura` y `validar-manejo-errores`,
compila el proyecto, y abre PR en español.

Pregúntame si tienes dudas antes de asumir.
```

---

## SESIÓN 6 — ETAPA 3 · Listado de productos

```
Lee /Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/prompt.txt
y ejecuta la ETAPA 3 (§14): :features:products.

Sigue el ciclo Spec Kit completo:
  /speckit-specify "listado de productos consumiendo
                    https://fakestoreapi.com/products"
  → /speckit-clarify → /speckit-plan → /speckit-tasks
  → /speckit-analyze → /speckit-implement

Requisitos clave:
  · Skill `crear-modulo` → estructura api/domain/data/presentation.
  · Domain: entidad Producto + usecase ObtenerProductos devolviendo
    Either<DomainError, List<Producto>>.
  · Data: Retrofit ProductsApi + ProductoDto + Room cache + mappers
    explícitos + repositorio con Single Source of Truth +
    safeApiCall (§7.4).
  · Presentation: ViewModel + UiState sealed (Loading/Empty/Error/
    Content) + UiEvent (incluye Retry) + UiEffect.
  · Vista con skill `crear-vista`: Composable Route con ViewModel +
    Composable Screen puro recibiendo state + onEvent. Previews
    obligatorios para CADA estado en claro y oscuro.
  · Toda la UI usa componentes de :core:design-system
    (MangoErrorState con onRetry, MangoLoadingIndicator,
    MangoProductCard).
  · Paging 3 preparado por interfaces aunque el endpoint sea pequeño.
  · Tests por cada rama de DomainError + ViewModel con Turbine.
  · Skill `prompts-de-diseno` para generar el prompt de imagen de
    cada estado de la pantalla.
  · Skill `documentar-modulo` (incluye errores.md del módulo).

Al cerrar, ejecuta `validar-arquitectura` y `validar-manejo-errores`,
compila, y abre PR en español.

Pregúntame si tienes dudas antes de asumir.
```

---

## SESIÓN 7 — ETAPA 4 · Favoritos persistidos

```
Lee /Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/prompt.txt
y ejecuta la ETAPA 4 (§14): :features:favorites.

Sigue el ciclo Spec Kit completo:
  /speckit-specify "favoritos persistidos en Room con toggle desde
                    productos y pantalla dedicada"
  → /speckit-clarify → /speckit-plan → /speckit-tasks
  → /speckit-analyze → /speckit-implement

Requisitos clave:
  · Skill `crear-modulo` → misma estructura que productos.
  · Persistencia en Room (tabla favoritos vinculada al producto).
  · Toggle invocable desde la pantalla de productos y desde
    favoritos. Errores no bloqueantes notificados con
    UiEffect.ShowSnackbar(uiError).
  · Repo expone Flow<List<Favorito>> (Single Source of Truth) para
    que el contador del perfil reaccione automáticamente.
  · Tests obligatorios:
      - persistencia tras cierre/apertura,
      - ramas de error de BD (IntegrityViolation, WriteFailed),
      - ViewModel con Turbine cubriendo Loading/Error/Content/Retry.
  · Vista con skill `crear-vista` (Route + Screen puro + previews
    por estado, claro y oscuro).
  · Skill `prompts-de-diseno` y skill `documentar-modulo`.

Al cerrar, ejecuta `validar-arquitectura` y `validar-manejo-errores`,
compila, y abre PR en español.

Pregúntame si tienes dudas antes de asumir.
```

---

## SESIÓN 8 — ETAPA 5 · Perfil de usuario

```
Lee /Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/prompt.txt
y ejecuta la ETAPA 5 (§14): :features:profile.

Sigue el ciclo Spec Kit completo:
  /speckit-specify "perfil de usuario con datos de
                    https://fakestoreapi.com/users/<id> y contador
                    de favoritos reactivo"
  → /speckit-clarify → /speckit-plan → /speckit-tasks
  → /speckit-analyze → /speckit-implement

Requisitos clave:
  · Skill `crear-modulo` → estructura api/domain/data/presentation.
  · Endpoint /users/<id> → entidad Usuario.
  · Mostrar info del usuario + contador de favoritos OBSERVADO por
    Flow del repo de :features:favorites (no llamada puntual).
  · Pantalla envuelta en `SecureScreen { ... }` (FLAG_SECURE, §6.3).
  · Manejo explícito de DomainError.Network.NotFound con UiState.
    Error específico (§7).
  · Vista con skill `crear-vista`: Route + Screen puro + previews
    por cada estado (Loading/Error/NotFound/Content) en claro y
    oscuro.
  · Tests por cada rama de DomainError + integración del Flow de
    favoritos con MockWebServer y InMemoryRoom.
  · Skill `prompts-de-diseno` y `documentar-modulo`.

Al cerrar, ejecuta `validar-arquitectura` y `validar-manejo-errores`,
compila, y abre PR en español.

Pregúntame si tienes dudas antes de asumir.
```

---

## SESIÓN 9 — ETAPA 6 · Navegación raíz y app

```
Lee /Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/prompt.txt
y ejecuta la ETAPA 6 (§14): ensamblaje en :app.

Sigue el ciclo Spec Kit completo:
  /speckit-specify "NavHost raíz con BottomBar (Productos,
                    Favoritos, Perfil), gateway biométrico,
                    banner offline y handler global de excepciones"
  → /speckit-clarify → /speckit-plan → /speckit-tasks
  → /speckit-analyze → /speckit-implement

Requisitos clave:
  · NavHost en :app con type-safe routes
    (kotlinx.serialization).
  · BottomBar con Productos, Favoritos y Perfil (componente del
    design system MangoNavigationBar).
  · Login biométrico como gateway antes de acceder a Perfil.
  · Deep linking básico documentado.
  · MangoOfflineBanner global escuchando ConnectivityObserver de
    :core:network.
  · CoroutineExceptionHandler raíz en MainActivity → reporte a
    telemetría (§8) + UiEffect.
  · Tests instrumentados end-to-end con Hilt test runner y
    Espresso: abrir app → listar productos → marcar favorito →
    cerrar y reabrir → favorito persiste → ver contador en perfil.
  · Skill `documentar-modulo` para :app.

Al cerrar, ejecuta `validar-arquitectura` y `validar-manejo-errores`,
compila, y abre PR en español.

Pregúntame si tienes dudas antes de asumir.
```

---

## SESIÓN 10 — ETAPA 7 · Hardening de seguridad

```
Lee /Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/prompt.txt
y ejecuta la ETAPA 7 (§14): seguridad reforzada.

Tareas:
  · 7.1 Reglas R8/ProGuard agresivas (§6.1) por módulo en
    consumer-rules.pro. Documentar lo que NO se ofusca y por qué.
  · 7.2 Ofuscación de strings sensibles (StringFog/Paranoid o NDK)
    y movimiento de secrets fuera del binario (§6.5, R0.7) usando
    local.properties + BuildConfig + flavors.
  · 7.3 Network Security Config + Certificate Pinning real con
    pines primario y backup (§6.5).
  · 7.4 Threat model completo en docs/seguridad.md.

Adicional:
  · Hardening: validación de firma APK, detección de debugger en
    release, hooks anti-Frida/Xposed (§6.7).
  · IntegrityChecker integrado con política configurable (block/
    warn/log) decidida en IntegrityPolicy.

Compila release y verifica que la app sigue funcionando con R8
activo. Tests instrumentados verdes.

Al cerrar, abre PR en español.

Pregúntame si tienes dudas antes de asumir.
```

---

## SESIÓN 11 — ETAPA 8 · Observabilidad (Firebase, gratis)

```
Lee /Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/prompt.txt
y ejecuta la ETAPA 8 (§14): observabilidad con stack gratuito.

Stack obligatorio (R0.12, §8): Firebase Crashlytics + Firebase
Analytics + Firebase Performance Monitoring. Datadog/Sentry
permanecen como impls opcionales detrás de flag.

Tareas:
  · 8.1 Firebase Crashlytics: inicialización, setUserId con hash,
    custom keys (flavor, appVersion, buildNumber, device,
    sessionId). Receptor de DomainError no fatales (§7.10) →
    FirebaseCrashlytics.recordException con tags estándar.
  · 8.2 Firebase Analytics: implementar EventTracker con
    FirebaseAnalyticsTracker. Eventos clave (§8.4):
    product_viewed, product_favorited, product_unfavorited,
    profile_viewed, login_success, login_failure.
  · 8.3 Firebase Performance: HttpMetric automático y custom
    traces para flujos críticos (carga de productos, toggle de
    favorito, login biométrico).
  · 8.4 Rate-limiting de errores ruidosos por errorCode en el
    receptor de DomainError.
  · 8.5 Documentar en docs/observabilidad.md qué está activo, qué
    es opcional y cómo activar Datadog/Sentry si la empresa los
    provee.

Asegúrate de que NUNCA se loguean tokens, contraseñas, datos
biométricos ni PII (§7.10).

Al cerrar, abre PR en español.

Pregúntame si tienes dudas antes de asumir.
```

---

## SESIÓN 12 — ETAPA 9 · CI/CD (GitHub Actions, gratis)

```
Lee /Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/prompt.txt
y ejecuta la ETAPA 9 (§14): pipelines de CI/CD.

Pipeline principal: GitHub Actions (gratis). Espejo opcional:
Azure Pipelines. Bitrise queda opcional.

Tareas:
  · 9.1 Crear .github/workflows/pr.yml: detekt, ktlint, unit tests,
    Kover, snapshot tests, build debug. Subir cobertura como
    artifact. SonarCloud SOLO si el repo es público.
  · 9.1 Crear .github/workflows/main.yml: todo lo de pr +
    instrumentation tests en Firebase Test Lab + build staging +
    deploy a Firebase App Distribution.
  · 9.1 Crear .github/workflows/release.yml: build signed release,
    subida a Google Play Internal Testing, subida de mapping files
    a Crashlytics, crear tag y release notes.
  · 9.2 Espejo opcional azure-pipelines.yml.
  · 9.3 Firebase App Distribution para QA interno.
  · 9.4 SonarCloud opcional (solo público).
  · 9.5 docs/ci-cd.md con: cómo regenerar y rotar cada secreto,
    cómo correr workflows localmente con `act`, mapeo Bitrise →
    GitHub Actions por si la empresa exige Bitrise.

Secretos de GitHub Actions necesarios (a configurar manualmente
por el usuario tras el PR): KEYSTORE_B64, KEYSTORE_PASSWORD,
KEY_ALIAS, KEY_PASSWORD, FIREBASE_SERVICE_ACCOUNT_JSON,
GOOGLE_PLAY_SERVICE_ACCOUNT_JSON, SONAR_TOKEN (opcional).

Al cerrar, abre PR en español. NO subas secretos al repo.

Pregúntame si tienes dudas antes de asumir.
```

---

## SESIÓN 13 — ETAPA 10 · Pulido y entrega final

```
Lee /Users/spanesso/Documents/PROYECTOS/EZSOLUTION/prueba_tecnica/prompt.txt
y ejecuta la ETAPA 10 (§14): pulido y entrega.

Tareas:
  · 10.1 Ejecutar `validar-arquitectura` y `validar-manejo-errores`
    sobre el repo completo. Cero violaciones es requisito de
    cierre.
  · 10.2 Verificar cobertura agregada Kover >= 80%. Si algún
    módulo está por debajo, generar tests adicionales con el skill
    `crear-pruebas-unitarias` hasta cumplir umbrales (§11.5):
    domain >=90%, data >=80%, presentation >=70%.
  · 10.3 README.md raíz en español con: visión del producto,
    arquitectura resumida con diagrama, cómo correr en local, cómo
    testear, cómo desplegar, cómo activar herramientas opcionales.
  · 10.4 (Opcional) Capturar GIFs o vídeo corto de los flujos.
    Guardar en docs/media/.
  · 10.5 Crear tag v1.0.0 y generar release notes en español
    listando módulos, features y decisiones de arquitectura.

Comprobaciones finales antes del tag:
  · CI verde en main.
  · Definition of Done de §15 cumplida en cada etapa.
  · Toda la documentación en español (CLAUDE.md, README.md, docs/*,
    documentos por módulo).
  · Ningún Composable de vista importa ViewModel, DomainError ni
    Throwable.

Al terminar, abre PR final, mergea, crea el tag y comparte la URL
del repo lista para entregar a Mango.

Pregúntame si tienes dudas antes de asumir.
```

---

## Estado actual

- **Sesión actual**: 4 — ETAPA 1 (parte 3/3) · sub-etapas 1.8–1.10 (analytics, security, testing)
- **Última etapa completada**: SESIÓN 3 — ETAPA 1.5–1.7 (network, database, datastore) · PR abierto
- **Pendientes / decisiones**:
  - `MangoDatabase` sin `@Database` (Room requiere ≥1 entidad) — ensamblaje concreto en `:app`
  - Certificate pin de backup en `network_security_config.xml` es placeholder; reemplazar en producción
  - `AndroidKeystoreDatabaseKeyManager` y `TinkEncryption` requieren tests instrumentados (ETAPA 7)
  - Próxima sesión: ETAPA 1.8–1.10 usando SESIÓN 4 prompt de `ejecucion.md`
