# Módulo `:features:profile:data`

**Propósito**: Implementa el acceso a datos remotos del perfil de usuario. Consume el endpoint `/users/{id}` de Fake Store API y mapea la respuesta a la entidad `Usuario` del dominio.

## Contratos implementados

| Símbolo | Descripción | Retorno |
|---------|-------------|---------|
| `PerfilRepositoryImpl.obtenerPerfil(userId)` | Llama a `PerfilApi.obtenerUsuario(id)` y mapea el DTO al dominio; envuelto en `safeApiCall` | `Either<DomainError, Usuario>` |

## Dependencias

- `:features:profile:domain` — `PerfilRepository`, `Usuario`
- `:core:error` — `DomainError`, `safeApiCall`
- `:core:common` — utilidades Kotlin
- `retrofit-core` — cliente HTTP
- `retrofit.kotlinx.serialization` — conversor JSON
- `kotlinx.serialization.json` — parseo de JSON
- `arrow-core` — `Either`

## Ejemplos de uso

```kotlin
// En PerfilDataModule (inyección de dependencias)
@Binds
abstract fun bindPerfilRepository(
    impl: PerfilRepositoryImpl,
): PerfilRepository

@Provides
fun providePerfilApi(retrofit: Retrofit): PerfilApi =
    retrofit.create(PerfilApi::class.java)
```

## Estructura interna

```
features/profile/data/
└── src/main/kotlin/.../data/
    ├── di/
    │   └── PerfilDataModule.kt    módulo Hilt (@Binds + @Provides)
    ├── mapper/
    │   └── UsuarioDtoMapper.kt    extensión UsuarioDto.toDomain()
    ├── remote/
    │   ├── PerfilApi.kt           interfaz Retrofit @GET("users/{id}")
    │   └── dto/
    │       ├── NombreDto.kt       @Serializable (firstname, lastname)
    │       ├── DireccionDto.kt    @Serializable (city, street, number, zipcode)
    │       └── UsuarioDto.kt      @Serializable raíz (compone NombreDto + DireccionDto)
    └── repository/
        └── PerfilRepositoryImpl.kt implementa PerfilRepository
```

## Cómo regenerar esta documentación

```bash
/documentar-modulo features:profile
```
