@file:Suppress("MaximumLineLength", "MaxLineLength")

package com.mango.fakestore.features.products.presentation.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.features.products.presentation.model.ProductoUi
import com.mango.fakestore.features.products.presentation.ui.screens.ProductosScreen
import com.mango.fakestore.features.products.presentation.ui.state.ProductosUiEvent
import com.mango.fakestore.features.products.presentation.ui.state.ProductosUiState
import com.mango.fakestore.core.error.R as ErrorR
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ProductosScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val uiErrorPrueba = UiError(
        messageRes = ErrorR.string.error_red_sin_conexion,
        severity = UiError.Severity.Blocking,
        actions = listOf(UiError.UiErrorAction.Retry),
        errorCode = "NET-000",
    )

    private val productosDePrueba = listOf(
        ProductoUi(
            id = 1,
            titulo = "Camiseta de lino",
            precio = "$49.99",
            precioDouble = 49.99,
            categoria = "ropa",
            imagenUrl = "",
            puntuacion = 4.1f,
            numVotaciones = 259,
            esFavorito = false,
        ),
        ProductoUi(
            id = 2,
            titulo = "Pantalon slim fit",
            precio = "$79.99",
            precioDouble = 79.99,
            categoria = "ropa",
            imagenUrl = "",
            puntuacion = 3.9f,
            numVotaciones = 120,
            esFavorito = true,
        ),
    )

    // -------------------------------------------------------------------------
    // Estado Content
    // -------------------------------------------------------------------------

    @Test
    fun `dado estado Content con productos entonces muestra titulo de pantalla`() {
        composeRule.setContent {
            MangoTheme {
                ProductosScreen(
                    uiState = ProductosUiState.Content(productosDePrueba),
                    onEvent = {},
                )
            }
        }

        composeRule.onNodeWithText("Productos").assertIsDisplayed()
    }

    @Test
    fun `dado estado Content con productos entonces muestra nombre del primer producto`() {
        composeRule.setContent {
            MangoTheme {
                ProductosScreen(
                    uiState = ProductosUiState.Content(productosDePrueba),
                    onEvent = {},
                )
            }
        }

        composeRule.onNodeWithText("Camiseta de lino").assertIsDisplayed()
    }

    // -------------------------------------------------------------------------
    // Estado Empty
    // -------------------------------------------------------------------------

    @Test
    fun `dado estado Empty entonces muestra mensaje de lista vacia`() {
        composeRule.setContent {
            MangoTheme {
                ProductosScreen(
                    uiState = ProductosUiState.Empty,
                    onEvent = {},
                )
            }
        }

        composeRule.onNodeWithText("No hay productos disponibles en este momento.").assertIsDisplayed()
    }

    // -------------------------------------------------------------------------
    // Estado Error + Retry
    // -------------------------------------------------------------------------

    @Test
    fun `dado estado Error entonces muestra boton Reintentar`() {
        composeRule.setContent {
            MangoTheme {
                ProductosScreen(
                    uiState = ProductosUiState.Error(uiErrorPrueba),
                    onEvent = {},
                )
            }
        }

        composeRule.onNodeWithText("Reintentar").assertIsDisplayed()
    }

    @Test
    fun `dado estado Error cuando click en Reintentar entonces invoca onEvent Retry`() {
        var eventoCapturado: ProductosUiEvent? = null

        composeRule.setContent {
            MangoTheme {
                ProductosScreen(
                    uiState = ProductosUiState.Error(uiErrorPrueba),
                    onEvent = { eventoCapturado = it },
                )
            }
        }

        composeRule.onNodeWithText("Reintentar").performClick()

        assertTrue(eventoCapturado is ProductosUiEvent.Retry)
    }
}
