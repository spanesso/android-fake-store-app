# Diseño interno — `:core:common`

## Diagrama de componentes

```mermaid
flowchart TB
    subgraph core_common [":core:common"]
        direction TB
        subgraph dispatchers
            AI[AppDispatchers<br/>«interfaz»]
            DA[DefaultAppDispatchers<br/>«implementación»]
            DM[DispatchersModule<br/>«Hilt»]
        end
        subgraph ext
            EE[EitherExt<br/>flatMapRight, fold]
            FE[FlowEitherExt<br/>mapRight, filterRight]
            KE[KotlinExt<br/>isNotNullOrBlank, truncate…]
        end
        DA -->|implementa| AI
        DM -->|provee| AI
    end

    external_arrow["arrow.core<br/>Either&lt;L,R&gt;"]
    external_coroutines["kotlinx.coroutines<br/>Flow, Dispatcher"]

    EE --> external_arrow
    FE --> external_arrow
    FE --> external_coroutines
    dispatchers --> external_coroutines
```

## Decisiones de diseño

### AppDispatchers como interfaz

Los `CoroutineDispatcher` se abstraen en la interfaz `AppDispatchers` para permitir su sustitución en tests con `StandardTestDispatcher`. El módulo Hilt `DispatchersModule` provee `DefaultAppDispatchers` en producción.

### Arrow Either en vez de Result<T>

Se eligió Arrow `Either<L, R>` sobre `kotlin.Result<T>` porque:
- El tipo del error es explícito en la firma (`L = DomainError`)
- Las extensiones `flatMapRight`/`fold` mantienen el código libre de `try/catch`
- Arrow `mapLeft` y `getOrElse` están disponibles de fábrica; solo se añaden las que no existen

### Extensiones de flujo separadas

`FlowEitherExt.kt` está separado de `EitherExt.kt` para no arrastrar la dependencia de `kotlinx.coroutines` en contextos donde solo se necesitan operaciones síncronas sobre `Either`.

## Puntos de extensión

- Añadir nuevas extensiones de `Either` (p. ej. `zipRight`) en `EitherExt.kt`
- Añadir nuevas extensiones de `Flow<Either>` en `FlowEitherExt.kt`
- No añadir lógica Android en este módulo; permanece puro Kotlin/JVM
