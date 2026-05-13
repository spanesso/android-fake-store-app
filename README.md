# Mango Fake Store

[![CI](https://github.com/spanesso/android-fake-store-app/actions/workflows/sonar-main.yml/badge.svg?branch=main)](https://github.com/spanesso/android-fake-store-app/actions/workflows/sonar-main.yml)
[![SonarCloud](https://sonarcloud.io/api/project_badges/measure?project=spanesso_android-fake-store-app&metric=alert_status)](https://sonarcloud.io/project/overview?id=spanesso_android-fake-store-app)
[![Android](https://img.shields.io/badge/Android-24%2B-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org/)

Aplicación Android de catálogo de moda construida con las mismas exigencias de calidad de una app de producción real. Consume la [Fake Store API](https://fakestoreapi.com/) y está diseñada para demostrar arquitectura profesional, seguridad, diseño y calidad de código en Android moderno.

---

## Qué hace la aplicación

La app permite a cualquiera de los 10 usuarios disponibles iniciar sesión, explorar el catálogo de productos, guardar sus favoritos y consultar su perfil personal.

- **Inicio de sesión** — el usuario elige su cuenta entre los 10 disponibles. La sesión se mantiene al cerrar y reabrir la app.
- **Catálogo de productos** — lista completa de artículos con imagen, precio, descripción, categoría y puntuación. Las imágenes se descargan una sola vez y quedan guardadas en caché.
- **Favoritos por usuario** — cada usuario tiene su propia lista de favoritos. Al cambiar de cuenta, los favoritos cambian con ella.
- **Perfil** — muestra los datos completos del usuario (nombre, email, dirección, teléfono) y permite cerrar sesión.

---

## Arquitectura

La app sigue **Clean Architecture + MVVM** organizada en módulos Gradle independientes. Esto significa que cada parte de la aplicación tiene una responsabilidad única y no depende de las demás más de lo necesario.

Hay dos tipos de módulos:

**Funcionalidades** — cada pantalla tiene su propio conjunto de módulos separados:

| Módulo | Qué contiene |
|--------|-------------|
| `:features:auth` | Inicio de sesión y gestión de sesión |
| `:features:products` | Catálogo de productos |
| `:features:favorites` | Lista de favoritos por usuario |
| `:features:profile` | Perfil del usuario y cierre de sesión |

**Módulos compartidos** — herramientas que usan todas las funcionalidades:

| Módulo | Para qué sirve |
|--------|---------------|
| `:core:design-system` | Sistema de diseño Mango: colores, tipografía y 19 componentes visuales |
| `:core:ui` | Pantallas de carga, error y estado vacío |
| `:core:network` | Conexión HTTP con reintentos y seguridad TLS |
| `:core:database` | Base de datos local cifrada |
| `:core:datastore` | Almacenamiento seguro de la sesión |
| `:core:error` | Sistema tipado de errores para toda la app |
| `:core:analytics` | Registro de eventos y errores en Firebase |
| `:core:security` | Detección de dispositivos comprometidos y autenticación biométrica |
| `:core:logging` | Registro de mensajes de depuración |
| `:core:common` | Utilidades compartidas de Kotlin |
| `:core:testing` | Herramientas para tests automáticos |

Más detalles en [docs/arquitectura.md](docs/arquitectura.md).

---

## Seguridad

La aplicación implementa múltiples capas de protección:

- **Tráfico cifrado** — certificate pinning sobre HTTPS: si el certificado del servidor no coincide con el pin esperado, la conexión se rechaza.
- **Datos en reposo cifrados** — la base de datos usa SQLCipher y la sesión del usuario se guarda con cifrado AES-256-GCM. Las claves viven en el Android Keystore del dispositivo.
- **Detección de dispositivos comprometidos** — la app detecta si el dispositivo tiene root, si hay un depurador conectado o si herramientas de análisis como Frida o Xposed están activas.
- **Protección de pantalla** — la pantalla de perfil bloquea las capturas de pantalla y la previsualización en el selector de apps.
- **Ofuscación de código** — en producción el código se ofusca con R8 para dificultar la ingeniería inversa.

Más detalles en [docs/seguridad.md](docs/seguridad.md).

---

## Gestión de vistas

Las pantallas están construidas con **Jetpack Compose**, el sistema de UI declarativa moderno de Android. En lugar de describir cómo se ve la pantalla paso a paso, se declara el estado que debe mostrar y Compose se encarga de actualizarla automáticamente cuando ese estado cambia.

Cada pantalla sigue un patrón de tres piezas:

- **UiState** — describe qué debe mostrar la pantalla en cada momento: cargando, contenido listo o error. La pantalla simplemente lee este estado y se dibuja en consecuencia.
- **UiEvent** — representa las acciones del usuario: pulsar un botón, hacer scroll, pedir reintentar. La pantalla envía eventos al ViewModel y nunca toma decisiones por su cuenta.
- **UiEffect** — efectos puntuales que ocurren una sola vez: navegar a otra pantalla, mostrar un mensaje de error breve. Se separan del estado para evitar que se repitan al girar el dispositivo.

Esto garantiza que las pantallas sean completamente pasivas: no deciden nada, solo muestran lo que el ViewModel les dice y reportan lo que hace el usuario.

---

## Gestión de imágenes

Las imágenes del catálogo se cargan con **Coil**, una librería de carga de imágenes optimizada para Kotlin y Compose.

- **Caché en dos niveles** — la imagen se descarga una vez y se guarda primero en memoria (rápida, se pierde al cerrar la app) y luego en disco (persiste entre sesiones). Las siguientes veces que se muestra el producto, la imagen aparece al instante sin consumir datos.
- **Placeholder con efecto shimmer** — mientras la imagen se carga, la tarjeta muestra una animación de brillo que indica actividad sin mostrar espacios vacíos.
- **Liberación automática de memoria** — cuando una tarjeta sale de pantalla, Coil libera la imagen de memoria para que la app no consuma más recursos de los necesarios.

---

## Sistema de diseño

La interfaz está construida sobre un sistema de diseño propio llamado **Mango Design System** que unifica la identidad visual en toda la app:

- Paleta de colores, tipografía y formas definidas como tokens reutilizables
- 19 componentes Compose listos para usar: botones, tarjetas de producto, barras de navegación, diálogos, snackbars, chips, campos de texto y estados de carga con efecto shimmer
- Soporte para modo claro y modo oscuro
- Todos los componentes son accesibles y siguen las guías de Material 3

---

## Observabilidad

La app registra de forma automática lo que ocurre en producción:

- **Firebase Crashlytics** — captura errores inesperados con contexto suficiente para reproducirlos, sin exponer datos personales de los usuarios.
- **Firebase Analytics** — registra eventos de negocio: vistas de producto, favoritos añadidos o quitados, inicios de sesión.
- **Firebase Performance** — mide los tiempos de carga del catálogo y de las operaciones de favoritos.

Más detalles en [docs/observabilidad.md](docs/observabilidad.md).

---

## Calidad

La calidad se verifica de forma automática en cada cambio:

- **334 tests unitarios** que cubren casos de uso, repositorios, ViewModels y mappers de error
- **Detekt** — análisis estático de código Kotlin que detecta problemas de estilo y complejidad
- **Kover** — medición de cobertura de tests por módulo
- **SonarCloud** — informe centralizado de calidad y seguridad en cada PR
- **Konsist** — reglas de arquitectura verificadas automáticamente: si algún módulo viola las dependencias definidas, la build falla

---

## Integración continua

Cada cambio en el repositorio pasa por un pipeline automático en GitHub Actions:

| Pipeline | Se activa | Qué verifica |
|----------|-----------|-------------|
| `pr.yml` | Al abrir un Pull Request | Lint · Tests · Cobertura · Build · SonarCloud |
| `sonar-main.yml` | Al hacer push a `main` | Lint · Tests · Cobertura · Build · SonarCloud |
| `main.yml` | Al hacer push a `develop` | Tests · Build · Firebase Test Lab · Distribución QA |
| `release.yml` | Al crear un tag `v*` | Build de release firmado |

Más detalles en [docs/ci-cd.md](docs/ci-cd.md).

---

## Documentación

| Documento | Contenido |
|-----------|-----------|
| [docs/arquitectura.md](docs/arquitectura.md) | Cómo está organizado el código y por qué |
| [docs/seguridad.md](docs/seguridad.md) | Controles de seguridad implementados |
| [docs/observabilidad.md](docs/observabilidad.md) | Qué se registra en Firebase y cómo |
| [docs/ci-cd.md](docs/ci-cd.md) | Pipelines de integración continua |
