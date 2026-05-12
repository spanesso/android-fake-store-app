# Módulo `:core:database`

**Propósito**: Define la clase base abstracta de Room cifrada con SQLCipher, el gestor de clave de base de datos con Android Keystore, y la infraestructura de migraciones. Los feature modules definen sus propias `@Entity` y DAOs; `:app` ensambla la base de datos concreta.

## Contratos públicos

| Símbolo | Descripción | Tipo |
|---------|-------------|------|
| `MangoDatabase` | Clase base abstracta (extiende `RoomDatabase`) | `abstract class` |
| `MangoDatabase.DATABASE_NAME` | Nombre del fichero de base de datos | `const val = "mango_store.db"` |
| `MangoDatabase.DATABASE_VERSION` | Versión actual de esquema | `const val = 1` |
| `DatabaseKeyManager` | Interfaz para obtener y destruir la passphrase de cifrado | `interface` |
| `DatabaseKeyManager.getOrCreatePassphrase()` | Devuelve 32 bytes de passphrase (crea si no existe) | `ByteArray` |
| `DatabaseKeyManager.clearPassphrase()` | Elimina la passphrase del Keystore | `Unit` |
| `DatabaseMigrations.all` | Array de migraciones Room registradas | `Array<Migration>` |

## Patrón de ensamblaje en `:app`

`:core:database` **no** declara `@Database` porque Room KSP exige al menos una `@Entity`. El módulo `:app` declara la clase concreta:

```kotlin
// En :app — AppDatabase.kt
@Database(
    entities = [
        ProductEntity::class,
        FavoriteEntity::class,
        // ...todas las entidades de los feature modules
    ],
    version = MangoDatabase.DATABASE_VERSION,
    exportSchema = true
)
abstract class AppDatabase : MangoDatabase()

// En :app — DatabaseProvider.kt (módulo Hilt)
@Module @InstallIn(SingletonComponent::class)
object DatabaseProvider {
    @Provides @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        keyManager: DatabaseKeyManager
    ): AppDatabase {
        val passphrase = keyManager.getOrCreatePassphrase()
        val factory = SupportFactory(passphrase)
        return Room.databaseBuilder(context, AppDatabase::class.java, MangoDatabase.DATABASE_NAME)
            .openHelperFactory(factory)
            .addMigrations(*DatabaseMigrations.all)
            .build()
    }
}
```

## Dependencias

- `:core:error` — `DomainError.Database`, `DatabaseErrorMapper`
- `:core:common` — `AppDispatchers`
- `androidx.room:room-runtime` + `room-ktx`
- `net.zetetic:sqlcipher-android` — cifrado SQLCipher
- `androidx.security:security-crypto` — `EncryptedSharedPreferences` para la passphrase

## Estructura interna

```
core/database/
├── src/main/kotlin/com/mango/fakestore/core/database/
│   ├── MangoDatabase.kt                clase base abstracta de Room
│   ├── di/
│   │   └── DatabaseModule.kt          @Binds DatabaseKeyManager
│   ├── key/
│   │   ├── DatabaseKeyManager.kt      interfaz de gestión de passphrase
│   │   └── AndroidKeystoreDatabaseKeyManager.kt  impl con EncryptedSharedPreferences
│   └── migration/
│       └── DatabaseMigrations.kt      array de migraciones (vacío en v1)
└── src/test/kotlin/...
    ├── MangoDatabaseIntegrationTest.kt integración con Room in-memory
    ├── TestMangoDatabase.kt           BD de prueba con @Entity placeholder
    └── key/DatabaseKeyManagerTest.kt  contrato de DatabaseKeyManager con FakeDatabaseKeyManager
```

## Cómo regenerar esta documentación

```
/documentar-modulo modulo=core:database
```
