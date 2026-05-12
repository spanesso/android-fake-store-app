# Manejo de errores — `:core:datastore`

## Estrategia

`:core:datastore` no expone `Either<DomainError, T>` en su interfaz pública porque los errores de DataStore son situaciones de recuperación automática, no errores de negocio que el usuario necesita ver. Los fallos se tratan con fallbacks silenciosos y logging.

## Tabla de fallos y tratamiento

| Situación | Tratamiento | Log |
|-----------|-------------|-----|
| Fichero de preferencias corrupto | `corruptionHandler` emite `emptyPreferences()` — la app arranca sin sesión | `Timber.e` |
| Fallo de descifrado Tink (`accessToken`) | `decryptOrNull()` devuelve `null` — sesión vacía | `Timber.e("Tink decrypt failed for 'accessToken'")` |
| Fallo de descifrado Tink (`refreshToken`) | `decryptOrNull()` devuelve `null` | `Timber.e` |
| Fallo de descifrado Tink (`userId`) | `decryptOrNull()` devuelve `null` | `Timber.e` |
| Valor de `AppTheme` desconocido en preferencias | Fallback a `AppTheme.SYSTEM` | `Timber.e("Unknown AppTheme value: $raw")` |
| Excepción en `dataStore.edit { }` | Se propaga como excepción suspendida (rethrow) | _(caller debe manejar)_ |

## Relación con `DomainError`

`:core:datastore` **no** produce `DomainError` directamente. La tradución ocurre en la capa `data` de los feature modules:

```kotlin
// En RepositorioImpl de :features:auth:data
override suspend fun cerrarSesion(): Either<DomainError, Unit> =
    try {
        dataStore.clearSession()
        Either.Right(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Either.Left(DomainError.Database.WriteFailed(e)) // o Security.SessionExpired
    }
```

## Notas de seguridad

- **Los tokens nunca se almacenan en texto plano.** `MangoDataStoreImpl.saveSession` siempre llama a `tink.encrypt(token)` antes de escribir.
- **Los logs de Timber nunca incluyen el valor del token.** Solo se loguea el nombre del campo (`"accessToken"`) y el mensaje de excepción de Tink.
- El fichero `mango_prefs.preferences_pb` está en el almacenamiento interno de la app (no accesible sin root en dispositivos no rooteados).
