# Calidad y mejoras — Mango Fake Store

Este documento resume las fortalezas arquitectónicas de la solución y las mejoras de completitud incorporadas durante la etapa de pulido final.

---

## Fortalezas de la solución

### 1. Arquitectura Clean + MVVM verificada con análisis estático

La solución aplica Clean Architecture de forma rigurosa: las capas `domain`, `data` y `presentation` están separadas en módulos Gradle independientes con dependencias unidireccionales. Esto se verifica automáticamente mediante:

- **Konsist** (`UseCaseLayerKonsistTest`): comprueba en tiempo de compilación que ningún UseCase importa clases de `data` y que todos tienen `@Inject constructor`.
- **Detekt + ktlint**: análisis estático de estilo y calidad que se ejecuta en cada PR.
- **Validación manual**: cero violaciones ARQ-001..ARQ-010 y ERR-001..ERR-010 (ver informes en `specs/012-mejoras-calidad/`).

### 2. Manejo de errores tipado de extremo a extremo

La estrategia de errores usa `Either<DomainError, T>` como contrato entre capas:

- `safeApiCall` y `safeDbCall` capturan excepciones en la frontera `data` y las convierten a `DomainError`.
- Los UseCases propagan `Either` sin conocer detalles de red o base de datos.
- Los ViewModels traducen `DomainError` a `UiError` con `messageRes` localizado; la UI nunca lee `.message` directamente.
- Todas las corrutinas críticas están protegidas con `CoroutineExceptionHandler` que reporta errores a Crashlytics y actualiza el estado de error en la UI.

### 3. Tests con Robolectric: Compose UI sin emulador

Los tres módulos de presentación cuentan con tests de UI que usan `createComposeRule()` y `RobolectricTestRunner`. Estos tests verifican estados `Content`, `Empty`, `Error` y el flujo de `Retry`, y se ejecutan como tests unitarios en JVM (sin necesidad de dispositivo ni emulador):

| Módulo | Clase de test | Tests |
|--------|---------------|-------|
| `:features:products:presentation` | `ProductosScreenTest` | 5 |
| `:features:favorites:presentation` | `FavoritosScreenTest` | 5 |
| `:features:profile:presentation` | `PerfilScreenTest` | 6 |

### 4. Seguridad en capas

La app implementa defensa en profundidad:

- **Cifrado en reposo**: Room con SQLCipher, DataStore con Tink AES-256-GCM.
- **Certificate pinning** con pin de backup en `network_security_config.xml`.
- **R8 agresivo** con reglas por módulo.
- **`IntegrityChecker`**: detecta root, depurador adjunto, Frida/Xposed, firma APK incorrecta.

### 5. Observabilidad con Firebase

La app está instrumentada para producción:

- **Crashlytics**: errores no fatales con `DomainError` tipado, rate-limiting, userId anónimo (SHA-256).
- **Analytics**: eventos tipados (`ProductoVisto`, `ProductoFavoritado`, `PerfilVisto`, etc.) sin PII.
- **Performance Monitoring**: trazas en `cargar_productos` y `toggle_favorito`.

### 6. CI/CD completo con GitHub Actions

El pipeline tiene 6 jobs que se ejecutan en cada PR:

| Job | Herramienta |
|-----|-------------|
| Análisis estático | Detekt + ktlint |
| Tests unitarios | JUnit + Robolectric |
| Cobertura | Kover |
| Build APK | Gradle `assembleDevDebug` |
| Análisis de calidad | SonarCloud (`continue-on-error: true`) |
| Tests instrumentados | Firebase Test Lab (opcional) |

---

## Mejoras incorporadas en la etapa de pulido

### Mejora 1 — Documentación de decisiones técnicas

Se creó `docs/decisiones-tecnicas.md` con explicación detallada de:
- `PERFIL_USER_ID = 8`: constante de prototipo con path hacia `SessionManager` en producción.
- Favoritos monousuario: `FavoritoEntity` sin `userId` es una decisión válida para prototipo, con path hacia esquema multiusuario.
- Inversión de dependencias: tabla de los 5 UseCases con sus interfaces de repositorio.
- Hilt `@Inject constructor` vs `@Provides`: guía de cuándo usar cada patrón.
- `ObservarConteoFavoritos` con `Flow<Int>`: justificación de por qué no se usa `Either` en este caso.
- Dependencias de `:app` en módulos `data`/`domain`: la excepción de Hilt wiring documentada.

### Mejora 2 — Tests de UI con Compose + Robolectric

Se añadieron tests de Compose para las tres pantallas principales. Antes no existía ningún test de UI. Ahora cada pantalla tiene al menos un test para estado `Content` y un test para `Error + Retry`, ejecutables sin emulador.

### Mejora 3 — Regla Konsist para inversión de dependencias

Se añadió `UseCaseLayerKonsistTest` que verifica automáticamente dos invariantes en cada compilación:
1. Ningún UseCase importa clases de la capa `data`.
2. Todos los UseCases tienen `@Inject constructor`.

Esto convierte las decisiones arquitectónicas de §3 y §4 en reglas ejecutables.

### Mejora 4 — `google-services.json` accesible para revisores

El archivo `app/google-services.json` del proyecto Firebase `mango-fake-store` (proyecto de demo sin billing ni datos de producción) se incluyó en el repositorio y se creó `docs/configuracion-ci.md` explicando qué secrets de GitHub son opcionales y cuáles son necesarios para ejecutar el CI completo.

### Mejora 5 — Comentarios explicativos en `app/build.gradle.kts`

Las dependencias de `:app` en módulos `data` y `domain` (patrón estándar de Hilt multi-módulo) ahora tienen un comentario que explica que son la excepción documentada de wiring de Hilt, evitando confusión al revisar el código.
