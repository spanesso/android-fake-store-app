# Observabilidad — Mango Fake Store

**Última actualización**: 2026-05-12

---

## Stack activo

| Herramienta | Propósito | Módulo |
|---|---|---|
| **Firebase Crashlytics** | Reporte de errores no fatales + custom keys de contexto | `:core:analytics` |
| **Firebase Analytics** | Eventos de negocio (vistas, favoritos, login) | `:core:analytics` |
| **Firebase Performance Monitoring** | Trazas automáticas HTTP + custom traces críticos | `:core:analytics` |

Todo el stack es **gratuito** en el plan Spark de Firebase.

---

## Datos recogidos

### Firebase Analytics

| Evento | Parámetros | Cuándo se dispara |
|---|---|---|
| `product_viewed` | `product_id: Int` | Al cargar la lista de productos (primer producto) |
| `product_favorited` | `product_id: Int` | Al marcar favorito desde la lista de productos |
| `product_unfavorited` | `product_id: Int` | Al desmarcar favorito desde lista o pantalla de favoritos |
| `profile_viewed` | — | Al cargar el perfil con éxito |
| `login_success` | — | Autenticación exitosa |
| `login_failure` | `motivo: String` | Autenticación fallida |

Los parámetros solo contienen IDs enteros de producto o literales de motivo de fallo. No se envían nombres, emails ni datos personales.

### Firebase Performance

| Traza | Flujo medido | Dónde se inicia |
|---|---|---|
| `cargar_productos` | Carga de lista de productos desde API | `ProductosViewModel.cargarProductos()` |
| `toggle_favorito` | Toggle de favorito (escritura en Room) | `ProductosViewModel.toggleFavorito()` y `FavoritosViewModel.toggleFavorito()` |
Todas las trazas usan `try/finally` para garantizar que `TraceHandle.detener()` siempre se llama, incluso si hay error.

---

## Rate-limiting de errores

`ErrorRateLimiter` limita a **10 reportes por minuto** por `errorCode`. Errores por encima de ese umbral se descartan silenciosamente (se loguea en `Logger.debug` con `[Rate-limited]`). Esto evita floods de Crashlytics ante errores de red persistentes.

---

## Política PII

**Jamás se loguea**: tokens de sesión, contraseñas, nombres, emails ni ningún otro dato de identificación personal.

En Crashlytics, el `userId` se almacena como SHA-256 truncado a 16 caracteres (no reversible). El `sessionId` es un UUID v4 generado en memoria al arrancar — no se persiste.

---

## Rotación de credenciales Firebase

Si el `google-services.json` se expone accidentalmente:

1. Ir a la consola Firebase → **Configuración del proyecto → General → Tus apps**.
2. Hacer clic en **"Actualizar clave API"** o regenerar el certificado SHA-1.
3. Descargar el nuevo `google-services.json` y distribuirlo a través de CI secrets.
4. Revocar el token comprometido en [console.cloud.google.com](https://console.cloud.google.com) → **APIs & Services → Credentials**.

---

## Política de retención de datos

- Firebase Crashlytics: 90 días (configurable en la consola).
- Firebase Analytics: 14 meses por defecto (configurable en la consola).
- Firebase Performance: 30 días.
