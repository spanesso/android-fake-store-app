# Configuración Konsist

Konsist se usa para validar las reglas de arquitectura definidas en §3 y §4 del prompt
maestro como tests JUnit que corren en CI. Los tests viven en
`core/testing/src/test/kotlin/com/mango/fakestore/core/testing/konsist/` y se ejecutan con:

```bash
./gradlew :core:testing:test --tests "*Konsist*"
```

Reglas implementadas (replican las reglas ARQ-NNN del skill `validar-arquitectura`):

- `dataNoConoceAPresentation`: ningún archivo bajo `*/data/` importa nada bajo `*/presentation/`.
- `presentationNoConoceAData`: ningún archivo bajo `*/presentation/` importa nada bajo `*/data/`.
- `domainSoloDependeDeCoreCommonYError`: archivos bajo `*/domain/` solo importan de `:core:common`,
  `:core:error`, o del mismo módulo.
- `composablesDeFeatureNoImportanHiltViewModel`: ningún Composable fuera de `ui/route/` importa
  `androidx.hilt.navigation.compose.hiltViewModel`.
- `material3FueraDeDesignSystemProhibido`: imports de `androidx.compose.material3.*` solo se
  permiten en `:core:design-system` (excepto Surface, Scaffold, Snackbar que son contenedores
  permitidos en `:core:ui`).
- `usecasesRetornanEither`: cada función `operator fun invoke` en `*/domain/casosdeuso/` retorna
  `Either<DomainError, T>` o `Flow<Either<DomainError, T>>`.
- `composablesNoImportanDomainErrorNiThrowable`: refuerzo de R0.3 y R0.11.

Los tests Konsist se implementarán en ETAPA 1 (cuando exista `:core:testing`).
