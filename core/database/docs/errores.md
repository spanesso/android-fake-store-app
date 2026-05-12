# Manejo de errores — `:core:database`

## Mapeo de errores de base de datos

El mapeo de excepciones SQL a `DomainError` es responsabilidad de `DatabaseErrorMapper` en `:core:error`. Los DAOs de feature modules deben envolver sus operaciones con `safeDbCall { }`.

| Excepción capturada | `DomainError` resultante | Condición típica |
|--------------------|--------------------------|-----------------|
| `SQLiteConstraintException` | `DomainError.Database.IntegrityViolation` | Violación de clave única o FK |
| `SQLiteException` (genérico) | `DomainError.Database.WriteFailed` | Error al escribir en disco |
| Cualquier otro `Throwable` | `DomainError.Database.ReadFailed` | Error de lectura inesperado |

## Tabla UiError

| `DomainError` | `UiError.severity` | `R.string` |
|---------------|-------------------|------------|
| `Database.ReadFailed` | `Warning` | `error_bd_lectura` |
| `Database.WriteFailed` | `Warning` | `error_bd_escritura` |
| `Database.NotFound` | `Blocking` | `error_bd_no_encontrado` |
| `Database.IntegrityViolation` | `Warning` | `error_bd_integridad` |

> Los `R.string` están definidos en `app/src/main/res/values/strings.xml` y `values-en/strings.xml`.

## Errores de cifrado SQLCipher

Si la passphrase es incorrecta (por ejemplo, tras llamar a `clearPassphrase()` sin recrearla), SQLCipher lanza `SQLiteException: file is not a database`. Este error se mapea a `DomainError.Database.ReadFailed`.

**Acción recomendada**: al detectar `ReadFailed` con este mensaje, reiniciar la app y solicitar nueva autenticación para recrear la passphrase. Nunca eliminar el fichero de BD sin confirmación del usuario.

## Ejemplo de uso en feature module

```kotlin
// En RepositorioImpl de un feature:
override suspend fun obtenerFavoritos(): Either<DomainError, List<Favorito>> =
    safeDbCall { favoritosDao.getAll() }
        .map { entities -> entities.map(FavoritoEntity::toDomain) }
```

`safeDbCall` está definido en `:core:error/ext/SafeCallExt.kt` y usa `DatabaseErrorMapper` internamente.
