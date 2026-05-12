# Módulo `:features:profile:domain`

**Propósito**: Contiene las reglas de negocio del perfil de usuario — entidades de dominio, contratos de repositorio y casos de uso.

## Contratos públicos

| Símbolo | Descripción | Retorno |
|---------|-------------|---------|
| `ObtenerPerfil(userId: Int)` | Carga los datos del usuario desde el repositorio; precondición: `userId > 0` | `Either<DomainError, Usuario>` |
| `PerfilRepository.obtenerPerfil(userId: Int)` | Interfaz de repositorio para obtener el perfil remoto | `Either<DomainError, Usuario>` |

## Entidad principal

```kotlin
data class Usuario(
    val id: Int,
    val nombreCompleto: String,   // "${firstname} ${lastname}"
    val nombreUsuario: String,
    val email: String,
    val telefono: String,
    val ciudad: String,
    val calle: String,            // "${street} ${number}"
    val codigoPostal: String,
)
```

## Dependencias

- `:core:common` — `AppDispatchers` (si se necesita en el futuro)
- `:core:error` — `DomainError`, `Either`
- `arrow-core` — tipos funcionales `Either`
- `javax.inject` — inyección de dependencias (via Hilt)

## Ejemplos de uso

```kotlin
class PerfilViewModel @Inject constructor(
    private val obtenerPerfil: ObtenerPerfil,
) : ViewModel() {
    fun cargar() = viewModelScope.launch {
        obtenerPerfil(userId = 8).fold(
            ifLeft  = { /* manejar error */ },
            ifRight = { usuario -> /* actualizar UI */ },
        )
    }
}
```

## Estructura interna

```
features/profile/domain/
└── src/main/kotlin/.../domain/
    ├── model/
    │   └── Usuario.kt          entidad de dominio
    ├── repository/
    │   └── PerfilRepository.kt interfaz (contrato de datos)
    └── usecase/
        └── ObtenerPerfil.kt    caso de uso con precondición userId > 0
```

## Cómo regenerar esta documentación

```bash
# Desde la raíz del proyecto
/documentar-modulo features:profile
```
