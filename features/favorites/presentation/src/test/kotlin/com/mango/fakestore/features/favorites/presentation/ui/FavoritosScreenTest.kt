@file:Suppress("MaximumLineLength", "MaxLineLength")

package com.mango.fakestore.features.favorites.presentation.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.features.favorites.presentation.model.FavoritoUi
import com.mango.fakestore.features.favorites.presentation.ui.screens.FavoritosScreen
import com.mango.fakestore.features.favorites.presentation.ui.state.FavoritosUiEvent
import com.mango.fakestore.features.favorites.presentation.ui.state.FavoritosUiState
import com.mango.fakestore.core.error.R as ErrorR
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FavoritosScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val uiErrorPrueba = UiError(
        messageRes = ErrorR.string.error_red_sin_conexion,
        severity = UiError.Severity.Blocking,
        actions = listOf(UiError.UiErrorAction.Retry),
        errorCode = "NET-000",
    )

    private val favoritosDePrueba = listOf(
        FavoritoUi(
            productoId = 1,
            titulo = "Camiseta favorita",
            precio = 49.99,
            imagenUrl = "",
            categoria = "ropa",
        ),
        FavoritoUi(
            productoId = 2,
            titulo = "Pantalon favorito",
            precio = 79.99,
            imagenUrl = "",
            categoria = "ropa",
        ),
    )

    // -------------------------------------------------------------------------
    // Estado Content
    // -------------------------------------------------------------------------

    @Test
    fun `dado estado Content con favoritos entonces muestra titulo de pantalla`() {
        composeRule.setContent {
            MangoTheme {
                FavoritosScreen(
                    uiState = FavoritosUiState.Content(favoritosDePrueba),
                    onEvent = {},
                )
            }
        }

        composeRule.onNodeWithText("Favoritos").assertIsDisplayed()
    }

    @Test
    fun `dado estado Content con favoritos entonces muestra nombre del primer favorito`() {
        composeRule.setContent {
            MangoTheme {
                FavoritosScreen(
                    uiState = FavoritosUiState.Content(favoritosDePrueba),
                    onEvent = {},
                )
            }
        }

        composeRule.onNodeWithText("Camiseta favorita").assertIsDisplayed()
    }

    // -------------------------------------------------------------------------
    // Estado Empty
    // -------------------------------------------------------------------------

    @Test
    fun `dado estado Empty entonces muestra mensaje de lista vacia`() {
        composeRule.setContent {
            MangoTheme {
                FavoritosScreen(
                    uiState = FavoritosUiState.Empty,
                    onEvent = {},
                )
            }
        }

        composeRule.onNodeWithText("Aún no tienes productos favoritos.").assertIsDisplayed()
    }

    // -------------------------------------------------------------------------
    // Estado Error + Retry
    // -------------------------------------------------------------------------

    @Test
    fun `dado estado Error entonces muestra boton Reintentar`() {
        composeRule.setContent {
            MangoTheme {
                FavoritosScreen(
                    uiState = FavoritosUiState.Error(uiErrorPrueba),
                    onEvent = {},
                )
            }
        }

        composeRule.onNodeWithText("Reintentar").assertIsDisplayed()
    }

    @Test
    fun `dado estado Error cuando click en Reintentar entonces invoca onEvent Retry`() {
        var eventoCapturado: FavoritosUiEvent? = null

        composeRule.setContent {
            MangoTheme {
                FavoritosScreen(
                    uiState = FavoritosUiState.Error(uiErrorPrueba),
                    onEvent = { eventoCapturado = it },
                )
            }
        }

        composeRule.onNodeWithText("Reintentar").performClick()

        assertTrue(eventoCapturado is FavoritosUiEvent.Reintentar)
    }
}
