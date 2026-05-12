# Observabilidad — Mango Fake Store

**Última actualización**: 2026-05-12
**Rama**: `009-observabilidad-firebase`

---

## Stack activo

| Herramienta | Propósito | Módulo |
|---|---|---|
| **Firebase Crashlytics** | Reporte de errores no fatales + custom keys de contexto | `:core:analytics` |
| **Firebase Analytics** | Eventos de negocio (vistas, favoritos, login) | `:core:analytics` |
| **Firebase Performance Monitoring** | Trazas automáticas HTTP + custom traces críticos | `:core:analytics` |

Todo el stack es **gratuito** en el plan Spark de Firebase (R0.12).

---

## Datos recogidos

### Firebase Crashlytics

| Dato | Descripción | ¿Contiene PII? |
|---|---|---|
| `userId` | SHA-256 del ID de usuario, truncado a 16 chars | ❌ Anonimizado |
| `sessionId` | UUID v4 generado en memoria al arrancar la app, no persistido | ❌ |
| `flavor` | Variante de build (`dev` / `staging` / `prod`) | ❌ |
| `appVersion` | Versión de la app (`BuildConfig.VERSION_NAME`) | ❌ |
| `buildNumber` | Código de versión (`BuildConfig.VERSION_CODE`) | ❌ |
| `device` | Fabricante y modelo del dispositivo | ❌ Dato de dispositivo, no de persona |
| `errorType` | Nombre de la clase `DomainError` | ❌ |
| `errorCode` | Mismo que `errorType` | ❌ |
| `httpCode` | Código HTTP del error de red (solo para `DomainError.Network.Server`) | ❌ |
| `vm` | Nombre del ViewModel que reportó el error | ❌ |
| `accion` | Nombre de la acción que falló | ❌ |

**Jamás se loguea**: tokens de sesión, contraseñas, datos biométricos (huellas, Face ID), nombres, emails, IPs de usuario, ni ningún otro dato de identificación personal (PII). Cumple §7.10 del prompt maestro.

### Firebase Analytics

| Evento | Parámetros | Cuándo se dispara |
|---|---|---|
| `product_viewed` | `product_id: Int` | Al cargar la lista de productos (primer producto) |
| `product_favorited` | `product_id: Int` | Al marcar favorito desde la lista de productos |
| `product_unfavorited` | `product_id: Int` | Al desmarcar favorito desde lista o pantalla de favoritos |
| `profile_viewed` | — | Al cargar el perfil con éxito |
| `login_success` | — | Autenticación biométrica exitosa |
| `login_failure` | `motivo: String` (`cancelado` \| `bloqueado` \| `no_disponible` \| `error_hw`) | Autenticación biométrica fallida |

Los parámetros solo contienen IDs enteros de producto o literales de motivo de fallo. No se envían nombres, emails ni datos personales.

### Firebase Performance

| Traza | Flujo medido | Dónde se inicia |
|---|---|---|
| `cargar_productos` | Carga de lista de productos desde API | `ProductosViewModel.cargarProductos()` |
| `toggle_favorito` | Toggle de favorito (escritura en Room) | `ProductosViewModel.toggleFavorito()` y `FavoritosViewModel.toggleFavorito()` |
| `login_biometrico` | Flujo completo de autenticación biométrica | `AppViewModel.autenticarParaPerfil()` |

Todas las trazas usan `try/finally` para garantizar que `TraceHandle.detener()` siempre se llama, incluso si hay error.

---

## Rate-limiting de errores

`ErrorRateLimiter` limita a **10 reportes por minuto** por `errorCode`. Errores por encima de ese umbral se descartan silenciosamente (se loguea en `Logger.debug` con `[Rate-limited]`). Esto evita floods de Crashlytics ante errores de red persistentes.

---

## Errores excluidos de Crashlytics

Los siguientes errores son flujos esperados y **nunca** se reportan a Crashlytics:

| `DomainError` | Razón de exclusión |
|---|---|
| `DomainError.Validation` | Error de validación de entrada; flujo normal del negocio |
| `DomainError.Security.BiometricLockout` | El sistema bloquea temporalmente la biometría; flujo conocido |

---

## Cómo abrir el dashboard de Firebase

### Paso 1 — Crear el proyecto Firebase

1. Ir a [console.firebase.google.com](https://console.firebase.google.com) e iniciar sesión con la cuenta Google del equipo.
2. Hacer clic en **"Añadir proyecto"**.
3. Nombre del proyecto: `mango-fake-store` (o el nombre acordado con el equipo).
4. Desactivar Google Analytics si no se va a usar Analytics (recomendado dejarlo activo).
5. Aceptar y crear el proyecto.

### Paso 2 — Registrar la app Android

1. En la consola del proyecto, hacer clic en el icono de Android.
2. **Package name**: `com.example.fakestoreapp` (debe coincidir con `applicationId` en `app/build.gradle.kts`).
3. Apodo de la app: `Mango Fake Store Android`.
4. SHA-1 del certificado de debug: obtener con:
   ```bash
   cd repository/android-fake-store-app
   ./gradlew signingReport
   ```
   Copiar el valor `SHA1` del variant `debug`.
5. Descargar el archivo `google-services.json` generado.

### Paso 3 — Colocar `google-services.json`

```bash
# Colocarlo en el módulo :app (NO en el raíz del repositorio)
cp ~/Downloads/google-services.json repository/android-fake-store-app/app/google-services.json
```

> **IMPORTANTE**: `google-services.json` está en `.gitignore`. **Nunca** hacer commit de este archivo — contiene identificadores del proyecto Firebase que deben distribuirse a través de CI secrets o canales seguros del equipo.

### Paso 4 — Activar Crashlytics

1. En la consola Firebase → **Crashlytics** → **Empezar**.
2. Lanzar la app en un dispositivo/emulador con el `google-services.json` correcto.
3. Forzar un crash de prueba (o esperar el primer error no fatal) — aparecerá en el dashboard en ~5 minutos.

### Paso 5 — Activar Performance Monitoring

1. En la consola Firebase → **Performance** → **Empezar**.
2. El SDK ya está integrado; las trazas aparecerán automáticamente tras el primer lanzamiento.
3. Las trazas HTTP automáticas se ven en la pestaña **"Solicitudes de red"**.
4. Las trazas custom (`cargar_productos`, `toggle_favorito`, `login_biometrico`) aparecen en **"Trazas personalizadas"**.

### Paso 6 — Activar Analytics

1. En la consola Firebase → **Analytics** → **Dashboard**.
2. Los eventos de negocio (`product_viewed`, etc.) aparecen con un retraso de hasta 24 h en el dashboard estándar.
3. Para ver eventos en tiempo real: **Analytics → DebugView** (requiere ADB con el flag de debug activo):
   ```bash
   adb shell setprop debug.firebase.analytics.app com.example.fakestoreapp
   ```

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

Estos valores son los predeterminados del plan Spark y pueden ajustarse en **Configuración del proyecto → Retención de datos**.
