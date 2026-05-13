# CI/CD — Guía operacional

**Última actualización**: 2026-05-12
**Stack**: GitHub Actions (gratuito para repos públicos) + Firebase Test Lab + Firebase App Distribution + SonarCloud

---

## Stack de CI/CD activo

| Herramienta | Propósito | Tier |
|---|---|---|
| **GitHub Actions** | Pipeline principal (PR, main, release) | Gratuito ilimitado (repo público) |
| **Firebase Test Lab** | Pruebas instrumentadas en dispositivos reales | Cuota diaria gratuita (5 físicos / 10 virtuales) |
| **Firebase App Distribution** | Distribución interna a QA | Gratuito ilimitado |
| **SonarCloud** | Quality gate centralizado en la nube | Gratuito para repos públicos |
| **`act`** | Ejecución local de workflows de GitHub Actions | Open source (gratuito) |

---

## Mapa de workflows

```mermaid
flowchart TD
    PR[Pull Request abierto] -->|trigger| prJob[pr.yml]
    prJob --> lint[Detekt + ktlint]
    prJob --> test[Tests unitarios]
    test --> coverage[Cobertura Kover]
    coverage --> sonar{SONAR_TOKEN?}
    sonar -->|sí| sonarcloud[SonarCloud análisis]
    lint & test --> build[assembleDevDebug]
    build --> artifact1[APK debug artefact]

    MERGE[Merge a develop] -->|trigger| mainJob[main.yml]
    mainJob --> lint2[Lint + Tests + Coverage]
    lint2 --> buildStaging[assembleStagingDebug]
    buildStaging --> testLab[Firebase Test Lab]
    testLab -->|continue-on-error| dist[Firebase App Distribution]
    dist --> qa[Build en qa-internal]

    TAG[Tag v*] -->|trigger| releaseJob[release.yml]
    releaseJob --> bundleProd[bundleProdRelease firmado]
    bundleProd --> uploadMapping[Crashlytics mapping.txt]
    uploadMapping --> ghRelease[GitHub Release + release notes]
```

---

## Secretos requeridos

Configurar en: `GitHub → Settings → Secrets and variables → Actions → New repository secret`

| Nombre del secreto | Descripción | Cómo generarlo | Usado en |
|---|---|---|---|
| `FIREBASE_GOOGLE_SERVICES_JSON` | Contenido JSON de `google-services.json` | Firebase Console → Project settings → Download | pr.yml, main.yml, release.yml |
| `FIREBASE_SERVICE_ACCOUNT_JSON` | Service account de Firebase | Firebase Console → Project settings → Service accounts → Generate private key | main.yml, release.yml |
| `SONAR_TOKEN` | Token de autenticación de SonarCloud | sonarcloud.io → My Account → Security → Generate Tokens | pr.yml, main.yml (opcional) |

### Variables de entorno (no secretos)

Configurar en: `GitHub → Settings → Secrets and variables → Actions → Variables`

| Variable | Descripción | Ejemplo |
|---|---|---|
| `FIREBASE_APP_ID` | ID de la app en Firebase | `1:123456789:android:abc123def456` |
| `FIREBASE_TEST_RESULTS_BUCKET` | Bucket de GCS para resultados de Test Lab | `mi-proyecto-test-results` |

---

## Rotación de secretos

### Rotar `FIREBASE_GOOGLE_SERVICES_JSON`

1. Ir a Firebase Console → Project settings → General → Your apps → Mango Fake Store Android
2. Hacer clic en "Download google-services.json"
3. Actualizar el secret en GitHub: Settings → Secrets → `FIREBASE_GOOGLE_SERVICES_JSON` → Update
4. Verificar: hacer push a una rama con PR y confirmar que el pipeline de PR pasa

### Rotar `FIREBASE_SERVICE_ACCOUNT_JSON`

1. Ir a Firebase Console → Project settings → Service accounts
2. Hacer clic en "Generate new private key" → confirmar
3. Actualizar el secret en GitHub: `FIREBASE_SERVICE_ACCOUNT_JSON` → Update
4. Revocar el service account anterior en Google Cloud Console → IAM → Service accounts

### Rotar `SONAR_TOKEN`

1. sonarcloud.io → My Account → Security → Revocar token existente
2. Generar nuevo token con el mismo nombre
3. Actualizar `SONAR_TOKEN` en GitHub Secrets

---

## Ejecución local con `act`

### Instalar `act`

```bash
# macOS
brew install act

# Linux
curl -s https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash
```

### Crear archivo de secretos locales

```bash
# En repository/android-fake-store-app/
cat > .secrets.local << 'EOF'
FIREBASE_GOOGLE_SERVICES_JSON={"project_info":{"project_id":"..."},...}
SONAR_TOKEN=tu_token_opcional
EOF
```

> ⚠️ `.secrets.local` está en `.gitignore` — **nunca lo commitees**.

### Ejecutar pipelines localmente

```bash
cd repository/android-fake-store-app

# Simular el pipeline de PR (lint + test + build)
act pull_request --secret-file .secrets.local

# Solo el job de lint
act pull_request -j lint --secret-file .secrets.local

# Solo el job de tests
act pull_request -j test --secret-file .secrets.local

# Simular push a develop
act push --secret-file .secrets.local

# Ver todos los workflows disponibles
act --list
```

---

## Troubleshooting

### `No matching client found for package name`

**Causa**: `google-services.json` no incluye la variante de flavor (`.dev`, `.staging`).
**Solución**: Asegurarse de que el secret `FIREBASE_GOOGLE_SERVICES_JSON` incluye entradas para `com.example.fakestoreapp`, `com.example.fakestoreapp.dev` y `com.example.fakestoreapp.staging`.

### `Firebase Test Lab: quota exceeded`

**Causa**: Se agotó la cuota diaria gratuita (5 ejecuciones en dispositivos físicos).
**Solución**: El job usa `continue-on-error: true`, así que el pipeline continúa. Esperar a que la cuota se restablezca al día siguiente (UTC 00:00).

### El pipeline de PR tarda más de 15 minutos

**Causa probable**: Caché de Gradle invalidada (cambio en `libs.versions.toml` o `build.gradle.kts`).
**Solución**: Normal tras un cambio de dependencias. La siguiente ejecución usará el nuevo caché. Si ocurre sistemáticamente, revisar si hay tareas innecesariamente incrementales.

### `SonarCloud: organization not found`

**Causa**: `sonar-project.properties` tiene el `sonar.organization` incorrecto.
**Solución**: Verificar en sonarcloud.io → My Organizations → nombre exacto de la organización. Debe coincidir con el campo `sonar.organization` en `sonar-project.properties`.
