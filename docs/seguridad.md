# Modelo de amenazas — Mango Fake Store

**Versión**: 1.0 · **Fecha**: 2026-05-12
**Metodología**: STRIDE
**Alcance**: Aplicación Android `com.example.fakestoreapp` + API `fakestoreapi.com`

---

## 1. Activos a proteger

| ID | Activo | Sensibilidad |
|----|--------|--------------|
| ACT1 | Tokens de sesión (DataStore cifrado) | Alta |
| ACT2 | Lista de favoritos del usuario | Media |
| ACT3 | Datos de perfil del usuario | Media |
| ACT4 | Clave de cifrado de la base de datos (Keystore) | Crítica |
| ACT5 | Código fuente y lógica de negocio de la app | Media |
| ACT6 | Tráfico HTTPS hacia `fakestoreapi.com` | Media |

---

## 2. Controles implementados

| Control | Descripción | Archivo de referencia |
|---------|-------------|----------------------|
| R8 agresivo | `-repackageclasses`, `-overloadaggressively`, `-optimizationpasses 5`, eliminación de logs | `app/proguard-rules.pro` |
| consumer-rules.pro por módulo | Cada módulo documenta qué NO ofusca y por qué | `core/*/consumer-rules.pro`, `features/*/data/consumer-rules.pro` |
| Certificate pinning | Pin primario (cert hoja) + backup (CA intermedia Let's Encrypt E5/E6) | `core/network/src/main/res/xml/network_security_config.xml`, `NetworkConfig.kt` |
| Cifrado en reposo | DataStore con Tink AES-256-GCM, Room con SQLCipher, clave en Android Keystore | `core/datastore/`, `core/database/` |
| Detección de root | RootBeer en `IntegrityCheckerImpl` | `core/security/integrity/IntegrityCheckerImpl.kt` |
| Detección de debugger | `Debug.isDebuggerConnected()` | `core/security/integrity/IntegrityCheckerImpl.kt` |
| Detección de Frida | Escaneo de `/proc/self/maps` | `core/security/integrity/IntegrityCheckerImpl.kt` |
| Detección de Xposed | `Class.forName("de.robv.android.xposed.XposedBridge")` | `core/security/integrity/IntegrityCheckerImpl.kt` |
| IntegrityPolicy | BLOCK/WARN/LOG por flavor (prod/staging/dev) | `core/security/integrity/IntegrityPolicy.kt`, `app/build.gradle.kts` |
| FLAG_SECURE | Pantalla de perfil protegida contra capturas | `core/security/ui/SecureScreen.kt` |
| Autenticación biométrica | `BiometricAuthenticator` con BIOMETRIC_STRONG | `core/security/biometric/BiometricAuthenticatorImpl.kt` |

---

## 3. Proceso de rotación de pines de certificado

Ejecutar antes de cada release (requiere acceso a red):

```bash
# 1. Obtener el pin del certificado hoja (primario)
openssl s_client -connect fakestoreapi.com:443 -showcerts </dev/null 2>/dev/null \
  | openssl x509 -pubkey -noout \
  | openssl pkey -pubin -outform DER \
  | openssl dgst -sha256 -binary \
  | openssl enc -base64

# 2. Obtener el pin de la CA intermedia (backup)
openssl s_client -connect fakestoreapi.com:443 -showcerts </dev/null 2>/dev/null \
  | awk 'BEGIN{c=0} /-----BEGIN CERTIFICATE-----/{c++} c==2{print}' \
  | openssl x509 -pubkey -noout \
  | openssl pkey -pubin -outform DER \
  | openssl dgst -sha256 -binary \
  | openssl enc -base64
```

Actualizar ambos valores en:
- `core/network/src/main/res/xml/network_security_config.xml`
- `core/network/src/main/kotlin/com/mango/fakestore/core/network/config/NetworkConfig.kt`

Los dos archivos DEBEN tener pines idénticos; si difieren, OkHttp fallará aunque el XML sea correcto.
