# Modelo de amenazas — Mango Fake Store

**Versión**: 1.0 · **Fecha**: 2026-05-12
**Metodología**: STRIDE
**Alcance**: Aplicación Android `com.example.fakestoreapp` + API `fakestoreapi.com`

---

## 1. Actores

| ID | Actor | Nivel de confianza |
|----|-------|--------------------|
| A1 | Usuario legítimo | Alto (usuario autenticado en el dispositivo) |
| A2 | Atacante externo | Ninguno (red no confiable, sin acceso al dispositivo) |
| A3 | Atacante con acceso físico al dispositivo | Medio (puede instalar herramientas, modificar APK) |
| A4 | Investigador de seguridad | Medio (entorno controlado, intención legítima) |
| A5 | Malware en el mismo dispositivo | Medio (puede leer memoria de procesos, IPC) |
| A6 | Insider (empleado del equipo) | Alto (acceso a código fuente y CI/CD) |

---

## 2. Activos a proteger

| ID | Activo | Sensibilidad |
|----|--------|--------------|
| ACT1 | Tokens de sesión (DataStore cifrado) | Alta |
| ACT2 | Lista de favoritos del usuario | Media |
| ACT3 | Datos de perfil del usuario | Media |
| ACT4 | Clave de cifrado de la base de datos (Keystore) | Crítica |
| ACT5 | Código fuente y lógica de negocio de la app | Media |
| ACT6 | Tráfico HTTPS hacia `fakestoreapi.com` | Media |

---

## 3. Tabla STRIDE de amenazas

| ID | Categoría | Actor | Descripción | Impacto | Control implementado | Riesgo residual |
|----|-----------|-------|-------------|---------|----------------------|-----------------|
| T01 | **Spoofing** | A2 | Servidor falso de `fakestoreapi.com` mediante DNS poisoning o MitM para inyectar respuestas maliciosas | Alto | Certificate pinning dual (certificado hoja + CA intermedia) en `NetworkConfig.kt` y `network_security_config.xml`; `debug-overrides` solo activo en builds debug | Bajo — requiere comprometer la CA raíz o Let's Encrypt |
| T02 | **Tampering** | A3 | Modificar la APK instalada para eliminar checks de seguridad o inyectar código malicioso | Alto | Verificación de firma APK en `IntegrityCheckerImpl.esFirmaValida()` comparando SHA-256 del certificado con `BuildConfig.EXPECTED_CERT_HASH`; R8 con `-repackageclasses` dificulta ingeniería inversa | Medio — sin `EXPECTED_CERT_HASH` en dev/staging el check se omite |
| T03 | **Tampering** | A3 | Parchear la app en memoria con Frida para bypassear controles de autenticación o integridad | Alto | Detección de Frida en `IntegrityCheckerImpl.esFridaActivo()` mediante escaneo de `/proc/self/maps`; `IntegrityPolicy.BLOCK` en producción | Medio — detección best-effort, atacante motivado puede evadir el escaneo de maps |
| T04 | **Tampering** | A3 | Usar Xposed Framework para hookar métodos críticos (autenticación biométrica, IntegrityChecker) | Alto | Detección de Xposed en `IntegrityCheckerImpl.esXposedActivo()` mediante `Class.forName`; `IntegrityPolicy.BLOCK` en producción | Medio — igual que Frida, best-effort |
| T05 | **Repudiation** | A1 | Usuario niega haber realizado una acción (e.g., añadir a favoritos) sin trazabilidad | Bajo | `Telemetry` (Firebase Analytics) registra eventos de negocio; logs de Timber eliminados en release por `-assumenosideeffects` para evitar PII en logcat | Bajo — los eventos de analytics son la fuente de verdad |
| T06 | **Information Disclosure** | A2 | Interceptar tráfico HTTP para leer tokens o datos de usuario | Alto | `cleartextTrafficPermitted="false"` en `network_security_config.xml`; certificate pinning activo en release | Bajo — HTTPS forzado con pinning |
| T07 | **Information Disclosure** | A3 | Extraer tokens de sesión o claves de cifrado desde el almacenamiento del dispositivo | Alto | DataStore cifrado con Tink AES-256-GCM (`core:datastore`); clave de Room en Android Keystore (`core:database`); `FLAG_SECURE` en pantalla de perfil vía `SecureScreen` | Bajo con root activo → detectado por `IntegrityCheckerImpl.esRoot()` (RootBeer) |
| T08 | **Information Disclosure** | A5 | Malware lee logcat y extrae información sensible del usuario | Medio | `-assumenosideeffects` en `proguard-rules.pro` elimina `Log.d/v/i` y `Timber.d/v/i` en release; `NoOpLogger` activo en producción | Bajo — logs de debug no presentes en release |
| T09 | **Denial of Service** | A2 | Saturar la API `fakestoreapi.com` para degradar la experiencia | Bajo | `RetryInterceptor` con backoff exponencial y `maxRetries=3`; `ConnectivityObserver` muestra banner offline en vez de crashear; `MangoOfflineBanner` da feedback al usuario | Bajo — la app tolera errores de red sin crashear; la API es pública, DoS no está en el scope |
| T10 | **Elevation of Privilege** | A3 | Usar dispositivo rooteado para escalar privilegios y acceder a datos de otras apps | Alto | `IntegrityCheckerImpl.esRoot()` mediante RootBeer; `IntegrityPolicy.BLOCK` en producción detiene la app; SQLCipher + Keystore protegen datos en reposo | Medio — con root y tiempo suficiente, SQLCipher puede ser atacado; mitigado por Keystore hardware |
| T11 | **Spoofing** | A3 | Reempaquetar y redistribuir la app con código malicioso adicional | Alto | Verificación de firma APK (`esFirmaValida()`); R8 con ofuscación agresiva aumenta el costo del reempaquetado | Medio — en dev/staging el hash está vacío; requiere configurar `RELEASE_CERT_HASH` en CI/CD para producción |
| T12 | **Tampering** | A6 | Introducir una dependencia maliciosa o comprometida en el supply chain | Alto | Gradle dependency locking (pendiente); revisión de PRs con 2 aprobadores; dependencias con tier gratuito verificado (R0.12) | Medio — mitigación parcial sin dependency locking habilitado |

---

## 4. Controles implementados

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
| Verificación de firma APK | SHA-256 del certificado vs. `BuildConfig.EXPECTED_CERT_HASH` | `core/security/integrity/IntegrityCheckerImpl.kt` |
| IntegrityPolicy | BLOCK/WARN/LOG por flavor (prod/staging/dev) | `core/security/integrity/IntegrityPolicy.kt`, `app/build.gradle.kts` |
| FLAG_SECURE | Pantalla de perfil protegida contra capturas | `core/security/ui/SecureScreen.kt` |
| Autenticación biométrica | `BiometricAuthenticator` con BIOMETRIC_STRONG | `core/security/biometric/BiometricAuthenticatorImpl.kt` |
| Gestión de secretos | `local.properties` + `BuildConfig`, sin hardcoding en VCS | `app/build.gradle.kts`, sección 5 de este documento |

---

## 5. Gestión de secretos

| Secreto | Dev | Staging | Prod | Dónde vive | Cómo rotar |
|---------|-----|---------|------|-----------|------------|
| `INTEGRITY_POLICY` | `"LOG"` | `"WARN"` | `"BLOCK"` | `buildConfigField` en `app/build.gradle.kts` | Cambiar el valor en el build.gradle y publicar nuevo release |
| `EXPECTED_CERT_HASH` | `""` (skip) | `""` (skip) | SHA-256 del keystore de release | CI/CD secret `RELEASE_CERT_HASH` inyectado en `./gradlew -PRELEASE_CERT_HASH=...` | Obtener nuevo hash con `keytool -exportcert -alias release -keystore release.jks \| openssl sha256 -binary \| openssl base64` |
| Pin primario de certificado | `dSxOWQR+...` | `dSxOWQR+...` | `dSxOWQR+...` | `network_security_config.xml` y `NetworkConfig.kt` | Obtener nuevo pin con openssl (ver sección 6); actualizar ambos archivos; publicar release antes de que expire el certificado hoja (~90 días) |
| Pin backup de certificado | `jQJTbI...` | `jQJTbI...` | `jQJTbI...` | `network_security_config.xml` y `NetworkConfig.kt` | Verificar antes de cada release con el comando openssl de la sección 6 |
| Clave de cifrado Room | Generada en Keystore | Generada en Keystore | Generada en Keystore | Android Keystore hardware | No es necesario rotar manualmente; gestionada por el Keystore del dispositivo |
| Clave Tink de DataStore | Generada en Keystore | Generada en Keystore | Generada en Keystore | Android Keystore hardware | Igual que Room |

> **Regla R0.7**: Ningún secreto, API key, token ni credencial real vive en este repositorio.
> En producción, `RELEASE_CERT_HASH` se inyecta desde el vault de CI/CD como propiedad Gradle.

---

## 6. Proceso de rotación de pines de certificado

Ejecutar antes de cada release (requiere acceso a red):

```bash
# 1. Obtener el pin del certificado hoja (primario)
openssl s_client -connect fakestoreapi.com:443 -showcerts </dev/null 2>/dev/null \
  | openssl x509 -pubkey -noout \
  | openssl pkey -pubin -outform DER \
  | openssl dgst -sha256 -binary \
  | openssl enc -base64

# 2. Obtener el pin de la CA intermedia (backup)
#    Exportar el segundo certificado de la cadena
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

---

## 7. Riesgos aceptados

| Riesgo | Justificación | Propietario |
|--------|---------------|-------------|
| Detección de Frida/Xposed es best-effort | Un atacante motivado con root puede evadir el escaneo de `/proc/maps` usando versiones ofuscadas de Frida. Para una app de demo con API pública, este nivel es suficiente. En producción real se añadiría detección nativa (JNI) y atestación remota (Play Integrity API). | Equipo de seguridad |
| `EXPECTED_CERT_HASH` vacío en dev/staging | El check de firma se omite cuando el hash es vacío. Esto es intencional para no bloquear el desarrollo. En producción el hash viene de CI/CD y el check está activo. | Equipo de seguridad |
| Backup pin provisional (jQJTbI...) | El pin de backup es el de la CA intermedia de Let's Encrypt E5/E6 obtenido de la investigación (research.md). Debe verificarse con openssl antes de cada release. | Equipo de release |
| play-integrity-api no implementada | Play Integrity API ofrece atestación remota de la app (Google firma el veredicto). No incluida por requerir una cuenta de Play Console y un backend para verificar el token (fuera del alcance de esta prueba técnica). | Equipo de seguridad |
| Dependency locking no habilitado | Gradle dependency locking previene supply chain attacks. No implementado en esta versión para mantener la agilidad de desarrollo. | Tech Lead |
