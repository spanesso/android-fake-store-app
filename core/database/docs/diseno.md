# Diseño interno — `:core:database`

## Diagrama de ensamblaje

```mermaid
flowchart TB
    subgraph app[":app"]
        AppDB["AppDatabase\n(@Database entities=[...])"]
        DBProvider["DatabaseProvider\n(Hilt @Module)"]
    end

    subgraph coreDB[":core:database"]
        MangoDb["MangoDatabase\n(abstract : RoomDatabase)"]
        KeyMgr["AndroidKeystoreDatabaseKeyManager"]
        KeyI["DatabaseKeyManager (interface)"]
        Migrations["DatabaseMigrations.all"]
    end

    subgraph keystore["Android Keystore"]
        ESharedPrefs["EncryptedSharedPreferences\n(passphrase de 32 bytes)"]
    end

    subgraph feature[":features:*:data"]
        EntityX["*Entity (@Entity)"]
        DaoX["*Dao (@Dao)"]
    end

    AppDB -->|extiende| MangoDb
    DBProvider --> AppDB
    DBProvider -->|SupportFactory(passphrase)| KeyMgr
    KeyMgr --> KeyI
    KeyMgr --> ESharedPrefs
    DBProvider -->|addMigrations| Migrations
    AppDB -->|entities| EntityX
    AppDB -->|abstract fun| DaoX
    DaoX -.accedido vía AppDatabase.-> DBProvider
```

## Decisiones de diseño

### `MangoDatabase` sin `@Database`

Room KSP exige que `entities = [...]` no esté vacío. Como `:core:database` no conoce las entidades de los feature modules, la anotación `@Database` se declara exclusivamente en `:app`. Esto permite que la capa core sea estable e independiente del grafo de features.

### Passphrase en Android Keystore

La passphrase de SQLCipher (32 bytes aleatorios de `SecureRandom`) se almacena en `EncryptedSharedPreferences` protegida por una clave AES-256-GCM en el Android Hardware Keystore. La passphrase nunca se escribe en texto plano ni aparece en logs.

### Borrado de passphrase — `clearPassphrase()`

`clearPassphrase()` elimina la passphrase del Keystore pero **no destruye** el fichero de base de datos. Tras llamarlo, la BD existente queda inaccesible (no descifrable) hasta que se recrea la passphrase. Este comportamiento es intencional: sirve como mecanismo de "borrado lógico" ante un evento de seguridad, por ejemplo detección de root.

### Migraciones vacías en v1

`DatabaseMigrations.all` devuelve un array vacío. Al añadir la primera migración en ETAPA futura, se insertará aquí manteniendo compatibilidad con todos los módulos que consumen `MangoDatabase`.

## Puntos de extensión

- Añadir `fallbackToDestructiveMigration()` en el builder de `:app` solo durante desarrollo.
- Implementar `Migration(1, 2)` cuando cambien las entidades en v2.
- Para limpieza de passphrase tras logout, inyectar `DatabaseKeyManager` en el caso de uso de cierre de sesión.
