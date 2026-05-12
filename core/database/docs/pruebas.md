# Pruebas — `:core:database`

## Tests unitarios (6 tests totales)

| Clase de test | Tests | Descripción |
|---------------|-------|-------------|
| `MangoDatabaseIntegrationTest` | 2 | Abre BD Room in-memory (Robolectric); versión = 1 |
| `DatabaseKeyManagerTest` | 4 | Crea passphrase, misma passphrase en llamadas sucesivas, longitud 32 bytes, `clearPassphrase` permite regeneración |

## Herramientas de test

- **Room in-memory** (`Room.inMemoryDatabaseBuilder`) — sin fichero de disco, sin SQLCipher en tests unitarios
- **Robolectric** — Android SDK en JVM para `MangoDatabaseIntegrationTest`
- **FakeDatabaseKeyManager** — implementación in-memory del contrato `DatabaseKeyManager` (evita Android Keystore en JVM)

## `TestMangoDatabase`

Clase de ayuda declarada en `src/test/`:

```kotlin
@Entity(tableName = "schema_version")
internal data class SchemaVersionEntity(@PrimaryKey val version: Int = 1)

@Database(entities = [SchemaVersionEntity::class], version = 1, exportSchema = false)
abstract class TestMangoDatabase : MangoDatabase()
```

Permite verificar que la clase base `MangoDatabase` es extensible con la anotación `@Database` y que Room abre correctamente una BD in-memory con ella.

## Comandos Gradle

```bash
# Desde la raíz del repositorio (https://github.com/spanesso/android-fake-store-app)

./gradlew :core:database:testDebugUnitTest
```

## Umbrales de cobertura

| Componente | Estado |
|------------|--------|
| Contrato `DatabaseKeyManager` | ✅ 4/4 ramas del contrato |
| `MangoDatabase` (apertura/versión) | ✅ cubierto vía integración |
| `AndroidKeystoreDatabaseKeyManager` | _(requiere test instrumentado — Android Keystore)_ |

## Tests de integración (pendiente — ETAPA 7)

`AndroidKeystoreDatabaseKeyManager` requiere dispositivo real o emulador con Keystore activo. Añadir como `@LargeTest` instrumentado en ETAPA 7 (hardening de seguridad).
