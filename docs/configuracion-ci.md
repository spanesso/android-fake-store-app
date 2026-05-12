# Guía de configuración para revisores

Esta guía explica qué configuraciones adicionales son necesarias para ejecutar el pipeline de CI/CD completo y para distribuir la app internamente.

---

## Estado por defecto al clonar

Al clonar el repositorio y ejecutar los comandos básicos, funcionan sin configuración adicional:

```bash
./gradlew build           # ✅ compila sin configuración extra
./gradlew test            # ✅ tests unitarios (incluye UI con Robolectric)
./gradlew lint            # ✅ análisis estático
./gradlew assembleDevDebug  # ✅ APK de debug
```

El archivo `app/google-services.json` está incluido en el repositorio. Corresponde al proyecto Firebase `mango-fake-store`, creado exclusivamente para esta prueba técnica (sin billing activo, sin datos personales).

---

## GitHub Secrets requeridos para CI completo

Los secrets se configuran en: **Settings → Secrets and variables → Actions → New repository secret**

### Obligatorios para jobs principales (tests, lint, cobertura, build)

| Secret | Descripción | Cómo obtener |
|---|---|---|
| `FIREBASE_GOOGLE_SERVICES_JSON` | Contenido completo del `app/google-services.json` | Copiar el JSON del archivo directamente |

> **Nota**: Este secret es redundante si el `google-services.json` ya está en el repo. Está configurado por compatibilidad con el workflow, que lo escribe en CI antes de compilar.

### Opcionales para análisis de calidad

| Secret | Descripción | Cómo obtener |
|---|---|---|
| `SONAR_TOKEN` | Token de autenticación para SonarCloud | [sonarcloud.io](https://sonarcloud.io) → Mi Cuenta → Seguridad → Tokens |

> El job de SonarCloud tiene `continue-on-error: true` — si el secret no está configurado, el job falla silenciosamente y los demás 5 jobs siguen verdes.

### Opcionales para tests instrumentados y distribución

| Secret | Descripción | Cómo obtener |
|---|---|---|
| `GOOGLE_CREDENTIALS_JSON` | Service account de GCP con permisos Firebase Test Lab | GCP Console → IAM → Cuentas de servicio → Crear clave JSON |
| `FIREBASE_APP_ID` | ID de la app en Firebase (formato: `1:xxx:android:yyy`) | Firebase Console → Configuración del proyecto → Tus apps |
| `FIREBASE_TESTERS` | Lista de emails separados por coma para recibir la app | Ej: `revisor1@empresa.com,revisor2@empresa.com` |

> Los jobs de Firebase Test Lab y Firebase App Distribution requieren estos tres secrets. Sin ellos, los jobs fallan con error de credenciales GCP — esto es esperado y no afecta la calidad del código.

---

## Qué jobs requieren cada secret

| Job CI | Secret requerido | Estado sin secret |
|---|---|---|
| Análisis estático (Detekt + ktlint) | Ninguno | ✅ Verde |
| Tests unitarios | `FIREBASE_GOOGLE_SERVICES_JSON` | ✅ Verde (el JSON ya está en repo) |
| Cobertura (Kover) | `FIREBASE_GOOGLE_SERVICES_JSON` | ✅ Verde |
| Compilar APK staging debug | `FIREBASE_GOOGLE_SERVICES_JSON` | ✅ Verde |
| Análisis SonarCloud | `SONAR_TOKEN` | ⚠️ Falla con `continue-on-error: true` |
| Tests instrumentados (Firebase Test Lab) | `GOOGLE_CREDENTIALS_JSON` | ❌ Falla (opcional) |
| Distribuir a QA (Firebase App Distribution) | `GOOGLE_CREDENTIALS_JSON` + `FIREBASE_APP_ID` + `FIREBASE_TESTERS` | ❌ Falla (opcional) |

---

## Acceso al proyecto Firebase

El proyecto Firebase `mango-fake-store` es el backend de la app para esta prueba técnica:

- **Consola**: [console.firebase.google.com](https://console.firebase.google.com)
- **Proyecto ID**: `mango-fake-store`
- **Crashlytics**: reportes de errores en producción
- **Analytics**: eventos de usuario (`ProductoVisto`, `ProductoFavoritado`, etc.)
- **Performance Monitoring**: trazas de `cargar_productos` y `toggle_favorito`

Para acceder al proyecto Firebase contactar al autor de la prueba técnica.

---

## Ejecutar CI localmente (act)

Para simular el pipeline de GitHub Actions en local:

```bash
# Instalar act: https://github.com/nektos/act
brew install act

# Crear archivo de secrets locales (no commitear)
cat > .secrets.local << 'EOF'
FIREBASE_GOOGLE_SERVICES_JSON=$(cat app/google-services.json)
SONAR_TOKEN=tu_token_sonarcloud
EOF

# Ejecutar el workflow de PR
act pull_request --secret-file .secrets.local --job lint
act pull_request --secret-file .secrets.local --job test
act pull_request --secret-file .secrets.local --job coverage
```

> El archivo `.secrets.local` ya está en `.gitignore`.
