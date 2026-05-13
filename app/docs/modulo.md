# Módulo `:app`

**Propósito**: Punto de ensamblaje de la aplicación Mango Fake Store; conecta todos los módulos `:features:*` y `:core:*` en un NavHost raíz con BottomBar, banner offline global y handler de excepciones de corrutinas.

---

## Contratos públicos

El módulo `:app` es el punto de entrada de la aplicación y no expone contratos a otros módulos. Consume los siguientes contratos de los módulos de features:

| Símbolo consumido | Módulo origen | Descripción |
|---|---|---|
| `ProductosRoute` | `:features:products:presentation` | Composable de destino NavHost — listado de productos |
| `FavoritosRoute` | `:features:favorites:presentation` | Composable de destino NavHost — pantalla de favoritos |
| `PerfilRoute` | `:features:profile:presentation` | Composable de destino NavHost — perfil del usuario |
| `ObservarConteoFavoritos` | `:features:favorites:domain` | UseCase reactivo — contador de favoritos para el badge de la BottomBar |
| `ConnectivityObserver` | `:core:network` | Observable de estado de red para `isOffline` |
| `Telemetry` | `:core:analytics` | Reporte de errores no fatales al sistema de telemetría |
| `DomainErrorToUiErrorMapper` | `:core:error` | Mapeo de excepciones → `UiError` localizado para el Snackbar global |

---

## Dependencias

```
:app
├── :core:design-system    (MangoTheme, MangoNavigationBar, MangoNavItem)
├── :core:ui               (MangoOfflineBanner)
├── :core:network          (ConnectivityObserver)
├── :core:analytics        (Telemetry)
├── :core:error            (DomainError, UiError, DomainErrorToUiErrorMapper)
├── :core:common           (AppDispatchers)
├── :core:logging          (Logger / TimberLogger)
├── :core:database         (AppDatabase)
├── :core:datastore        (SessionData, UserPreferences)
├── :features:products:domain        (wiring Hilt)
├── :features:products:data          (wiring Hilt)
├── :features:products:presentation  (ProductosRoute)
├── :features:favorites:domain       (ObservarConteoFavoritos, wiring Hilt)
├── :features:favorites:data         (wiring Hilt)
├── :features:favorites:presentation (FavoritosRoute)
├── :features:profile:domain         (wiring Hilt)
├── :features:profile:data           (wiring Hilt)
└── :features:profile:presentation   (PerfilRoute)
```

---

## Ejemplos de uso

### Arrancar la app (MainActivity)

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        appViewModel.reportarErrorGlobal(throwable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch(errorHandler) { }
        setContent { MainContent() }
    }
}
```

### Observar estado offline desde AppViewModel

```kotlin
val isOffline: StateFlow<Boolean> = connectivityObserver.statusFlow
    .map { it != ConnectivityStatus.Connected }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
```

### Deep links disponibles

| URI | Destino |
|---|---|
| `mango://fakestore/productos` | `AppRoute.Productos` |
| `mango://fakestore/favoritos` | `AppRoute.Favoritos` |
| `mango://fakestore/perfil` | `AppRoute.Perfil` |

---

## Estructura interna

```
app/src/main/java/com/example/fakestoreapp/
├── MainActivity.kt                    — Activity raíz con CoroutineExceptionHandler
├── MangoApp.kt                        — Application con @HiltAndroidApp
├── database/
│   └── AppDatabase.kt                 — Room database (products + favorites)
├── di/
│   └── AppModule.kt                   — Provides DAOs y AppDatabase
└── ui/
    ├── AppUiEffect.kt                 — sealed interface AppUiEffect (MostrarErrorGlobal)
    ├── AppViewModel.kt                — ViewModel raíz: conectividad, errores, favoritos
    ├── MainContent.kt                 — Composable raíz: MangoTheme + MangoNavHost + SnackbarHost
    └── navigation/
        ├── AppRoutes.kt               — sealed interface AppRoute (@Serializable)
        └── MangoNavHost.kt            — NavHost + BottomBar

app/src/test/
└── AppViewModelTest.kt                — tests unitarios (conectividad, handler global)

app/src/androidTest/
├── NavigacionE2ETest.kt               — Tests E2E instrumentados (HiltAndroidTest)
└── di/
```

---

## Cómo regenerar esta documentación

```bash
/documentar-modulo app
```
