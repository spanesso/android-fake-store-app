# ADR 0003 — `ObservarConteoFavoritos` retorna `Flow<Int>` en vez de `Flow<Either<DomainError, Int>>`

**Estado**: Aceptado  
**Fecha**: 2026-05-12  
**Autores**: Equipo Mango Fake Store

---

## Contexto

La regla ARQ-008 del skill `validar-arquitectura` exige que todos los casos de uso devuelvan `Either<DomainError, T>` o `Flow<Either<DomainError, T>>`. El caso de uso `ObservarConteoFavoritos` devuelve `Flow<Int>`.

Este caso de uso se consume en `AppViewModel.contadorFavoritos` (badge de navegación) y en `PerfilViewModel` (contador reactivo en pantalla de perfil). En ambos contextos, el valor es un indicador visual secundario: si falla, simplemente muestra `0`.

---

## Decisión

Mantener `ObservarConteoFavoritos.invoke(): Flow<Int>` como excepción documentada a la regla ARQ-008 / ERR-002.

---

## Justificación

1. **Semántica de Room Flow**: La capa `data` implementa `observarConteo()` con un `DAO.conteo()` de Room. Los `Flow<T>` de Room nunca emiten errores en el flujo normal; los errores de BD se manifiestan como excepciones en el `CoroutineScope` suscriptor, que ya está protegido por `CoroutineExceptionHandler`.

2. **Efecto nulo ante fallo**: El único efecto de un error en el conteo es que el badge muestra `0`. No bloquea ninguna funcionalidad del usuario. Forzar `Either` agregaría complejidad de manejo de errores donde el resultado erróneo y el resultado vacío son indistinguibles para la UI.

3. **Alcance de la prueba técnica**: Cambiar la firma requeriría modificaciones en cascada: `FavoritosRepository`, `FavoritosRepositoryImpl`, `AppViewModel.contadorFavoritos` (que es un `StateFlow<Int>` consumido por la BottomBar), `PerfilViewModel`, y sus respectivos tests. El coste supera el beneficio en el contexto de esta prueba técnica.

---

## Consecuencias

- **Positivo**: Código más sencillo para un contador que nunca bloquea la UI.
- **Negativo**: Excepción a la regla ARQ-008/ERR-002 que el skill `validar-arquitectura` reportará. Este ADR actúa como supresión documentada.
- **Deuda técnica**: En una versión productiva, se recomendaría envolver con `catch` en el repositorio para emitir `0` ante errores de BD, y documentarlo en el mapper de errores.
