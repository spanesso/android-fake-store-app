# Decisiones técnicas — Mango Fake Store

Este documento explica las decisiones de diseño que no son evidentes a primera vista en el código, con justificación, referencias exactas y el path hacia la solución final en un entorno de producción.

---

## §1 — `PERFIL_USER_ID = 8` en `PerfilViewModel`

**Referencia**: `features/profile/presentation/src/main/kotlin/.../viewmodel/PerfilViewModel.kt:111`

```kotlin
companion object {
    private const val PERFIL_USER_ID = 8
}
```

**Por qué existe**: La Fake Store API (`fakestoreapi.com`) es una API pública de pruebas con usuarios pre-existentes del id 1 al 10. El id 8 corresponde al usuario "John Doe" con datos completos. Esta constante existe exclusivamente porque la app es un prototipo monousuario sobre una API de pruebas sin autenticación real.

**No es**: Un dato hardcoded por descuido. Es una decisión explícita de prototipo.

**Path hacia producción**: En un sistema real con autenticación, el id del usuario se obtendría de la sesión activa:

```kotlin
// Solución final con SessionManager
class PerfilViewModel @Inject constructor(
    private val obtenerPerfil: ObtenerPerfil,
    private val sessionManager: SessionManager,  // ← inyectado
    ...
) : ViewModel() {
    private fun cargarPerfil() {
        viewModelScope.launch {
            val userId = sessionManager.currentUserId()  // ← dinámico
            obtenerPerfil(userId)
        }
    }
}
```

---

## §2 — Favoritos monousuario: `FavoritoEntity` sin `userId`

**Referencia**: `features/favorites/data/src/main/kotlin/.../entity/FavoritoEntity.kt`

```kotlin
@Entity(tableName = "favoritos")
data class FavoritoEntity(
    @PrimaryKey val productoId: Int,
    val titulo: String,
    val precio: Double,
    val imagenUrl: String,
    val categoria: String,
    val fechaMarcado: Long,
)
```

**Por qué no hay `userId`**: El prototipo es monousuario. La Fake Store API no tiene autenticación real; hay un único "usuario" en la sesión en todo momento. Añadir `userId` sin una capa de autenticación funcional sería ingeniería prematura.

**Las queries Room** no filtran por usuario:

```kotlin
@Query("SELECT * FROM favoritos ORDER BY fechaMarcado DESC")
fun observarTodos(): Flow<List<FavoritoEntity>>
```

**Path hacia producción**: En un sistema multiusuario:

1. Añadir campo `userId: String` a `FavoritoEntity`.
2. Actualizar clave primaria a compuesta: `@PrimaryKey` → `@Entity(primaryKeys = ["productoId", "userId"])`.
3. Actualizar todas las queries Room: `WHERE userId = :userId`.
4. Actualizar `FavoritosRepositoryImpl` para obtener el `userId` de `SessionManager`.

---

## §3 — UseCases dependen de interfaces, nunca de clases concretas de `data`

**Referencia**: todos los archivos en `features/*/domain/src/main/kotlin/.../casosdeuso/`

Todos los UseCases del proyecto inyectan **interfaces de repositorio** definidas en `domain`, no implementaciones concretas de `data`:

| UseCase | Repositorio inyectado | Tipo |
|---|---|---|
| `ObtenerPerfil` | `PerfilRepository` | Interface en `domain` |
| `ObservarFavoritos` | `FavoritosRepository` | Interface en `domain` |
| `ToggleFavorito` | `FavoritosRepository` | Interface en `domain` |
| `ObservarConteoFavoritos` | `FavoritosRepository` | Interface en `domain` |
| `ObtenerProductos` | `ProductosRepository` | Interface en `domain` |

**Por qué importa**: Este es el principio de inversión de dependencias (Dependency Inversion Principle — DIP). `domain` nunca importa de `data`; es `data` quien implementa las interfaces de `domain`. Esto permite:
- Testar UseCases con mocks sin necesitar Room ni Retrofit.
- Cambiar la implementación de `data` sin tocar `domain`.

**Verificado automáticamente**: La clase `UseCaseLayerKonsistTest` en `core/design-system/src/test/` verifica en tiempo de compilación que ningún UseCase importa clases de la capa `data`.

---

## §4 — Hilt en UseCases: `@Inject constructor`, sin `@Provides`

**Referencia**: todos los UseCases en `features/*/domain/`

```kotlin
class ObtenerProductos @Inject constructor(
    private val repositorio: ProductosRepository,
) { ... }
```

**Por qué `@Inject` y no `@Provides`**: La guía oficial de Hilt recomienda `@Inject constructor` para clases que tú controlas. `@Provides` se usa solo cuando:
- La clase viene de una librería externa (Retrofit, Room, etc.).
- Se necesitan múltiples instancias de la misma interfaz.
- La construcción requiere lógica especial.

Los UseCases no cumplen ninguna de estas condiciones: son clases Kotlin puras que Hilt puede construir automáticamente. `@Inject constructor` es más simple, más idiomático y más fácil de testear.

---

## §5 — UseCases compartidos entre ViewModels: decisión deliberada

**Referencia**: `ProductosViewModel.kt`, `FavoritosViewModel.kt`, `AppViewModel.kt`, `PerfilViewModel.kt`

Dos UseCases son usados por más de un ViewModel:

| UseCase | ViewModels que lo usan | Motivo |
|---|---|---|
| `ObservarFavoritos` | `ProductosViewModel`, `FavoritosViewModel` | Products necesita mostrar el corazón activo en cada card; Favorites muestra la lista completa |
| `ToggleFavorito` | `ProductosViewModel`, `FavoritosViewModel` | El toggle puede ejecutarse desde el catálogo o desde la pantalla de favoritos |
| `ObservarConteoFavoritos` | `AppViewModel`, `PerfilViewModel` | AppVM alimenta el badge del BottomBar; PerfilVM muestra el contador en la tarjeta de perfil |

**Esto no es redundancia**: cada ViewModel tiene una responsabilidad diferente. Compartir UseCases es precisamente el beneficio de extraerlos: la lógica de negocio vive una sola vez en `domain` y múltiples capas de presentación la reutilizan.

**Lo que sería redundancia** (y no existe aquí): dos ViewModels que hacen exactamente lo mismo sin diferencia de contexto, o un UseCase que duplica la lógica de otro.

---

## §6 — `ObservarConteoFavoritos` devuelve `Flow<Int>`, no `Flow<Either<DomainError, Int>>`

**Referencia**: `features/favorites/domain/src/main/kotlin/.../usecase/ObservarConteoFavoritos.kt`

```kotlin
operator fun invoke(): Flow<Int> = repository.observarConteo()
```

**Por qué `Flow<Int>` y no `Flow<Either<DomainError, Int>>`**: La operación `SELECT COUNT(*) FROM favoritos` de Room es una consulta de lectura local que **nunca falla** en condiciones normales de operación:
- Si la tabla está vacía, Room emite `0` (no lanza excepción).
- Si hay N elementos, Room emite `N`.
- Room no puede "no encontrar" la tabla porque es creada al inicializar la base de datos.

Envolver el conteo en `Either<DomainError, Int>` añadiría complejidad sintáctica en todos los consumidores (`AppViewModel`, `PerfilViewModel`) sin aportar valor real. El patrón `Either` se reserva para operaciones que genuinamente pueden fallar: llamadas a red, lecturas de entidades que podrían no existir o escrituras que podrían ser rechazadas.

**Consistencia**: `FavoritosRepository.observarConteo()` tiene la misma firma (`Flow<Int>`) por el mismo motivo.

---

## §7 — `:app` depende de `:features:*:domain` y `:features:*:data` (Hilt wiring)

**Referencia**: `app/build.gradle.kts`, líneas de dependencias de features

**Por qué `:app` declara dependencias en módulos `domain` y `data`**: Este es el comportamiento estándar de Hilt en proyectos Android multi-módulo. El módulo `:app` actúa como **compositor del grafo de inyección de dependencias**:

1. **`AppModule.kt`** (en `:app:di`) provee `ProductosDao` y `FavoritosDao` — necesita importar clases de `:features:products:data` y `:features:favorites:data`.
2. **Hilt ksp processor** (`ksp(hilt.compiler)`) necesita ver todos los `@Module`/`@InstallIn` al momento de generar el `ApplicationComponent`. Aunque los módulos Hilt de `data` y `domain` están anotados con `@InstallIn(SingletonComponent::class)`, el procesador de Hilt en `:app` los descubre solo si están en el classpath de compilación de `:app`.
3. Las dependencias en `domain` son transitivamente necesarias para que el compilador Kotlin resuelva los tipos referenciados en los `@Binds` de los módulos `data`.

**Esto es la excepción documentada en ARQ-010**: "Excepto para wiring de Hilt, `:app` debe depender solo de `:features:*:presentation` y `:core:*`." Todas las dependencias en `data` y `domain` desde `:app` son exclusivamente para el grafo DI de Hilt.

**Path hacia producción**: Con Hilt 2.52+ y el plugin de agregación habilitado (`hilt.enableAggregatingTask = true`), los módulos de sub-proyectos se agregan automáticamente y estas dependencias explícitas pueden eliminarse. Ver la [guía oficial de Hilt multi-módulo](https://dagger.dev/hilt/gradle-setup.html#using-hilt-with-multiple-gradle-modules).
