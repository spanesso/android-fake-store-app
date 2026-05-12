@file:Suppress("MaximumLineLength", "MaxLineLength")

package com.mango.fakestore.features.profile.presentation.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.features.profile.presentation.model.PerfilContenidoUi
import com.mango.fakestore.features.profile.presentation.ui.screens.PerfilScreen
import com.mango.fakestore.features.profile.presentation.ui.state.PerfilUiEvent
import com.mango.fakestore.features.profile.presentation.ui.state.PerfilUiState
import com.mango.fakestore.core.error.R as ErrorR
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PerfilScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val uiErrorPrueba = UiError(
        messageRes = ErrorR.string.error_red_sin_conexion,
        severity = UiError.Severity.Blocking,
        actions = listOf(UiError.UiErrorAction.Retry),
        errorCode = "NET-000",
    )

    private val contenidoDePrueba = PerfilContenidoUi(
        id = 8,
        nombreCompleto = "John Doe",
        nombreUsuario = "johnd",
        email = "john@example.com",
        telefono = "1-570-236-7033",
        ciudad = "kilcoole",
        calle = "new road 7835",
        codigoPostal = "12926-3874",
        contadorFavoritos = 3,
    )

    // -------------------------------------------------------------------------
    // Estado Content
    // -------------------------------------------------------------------------

    @Test
    fun `dado estado Content entonces muestra titulo de pantalla`() {
        composeRule.setContent {
            MangoTheme {
                PerfilScreen(
                    uiState = PerfilUiState.Content(contenidoDePrueba),
                    onEvent = {},
                )
            }
        }

        composeRule.onNodeWithText("Mi Perfil").assertIsDisplayed()
    }

    @Test
    fun `dado estado Content entonces muestra nombre completo del usuario`() {
        composeRule.setContent {
            MangoTheme {
                PerfilScreen(
                    uiState = PerfilUiState.Content(contenidoDePrueba),
                    onEvent = {},
                )
            }
        }

        composeRule.onNodeWithText("John Doe").assertIsDisplayed()
    }

    @Test
    fun `dado estado Content entonces muestra nombre de usuario con arroba`() {
        composeRule.setContent {
            MangoTheme {
                PerfilScreen(
                    uiState = PerfilUiState.Content(contenidoDePrueba),
                    onEvent = {},
                )
            }
        }

        composeRule.onNodeWithText("@johnd").assertIsDisplayed()
    }

    @Test
    fun `dado estado Content con 3 favoritos entonces existe el contador de favoritos`() {
        composeRule.setContent {
            MangoTheme {
                PerfilScreen(
                    uiState = PerfilUiState.Content(contenidoDePrueba),
                    onEvent = {},
                )
            }
        }

        // El contador está dentro de un Column scrollable; assertExists verifica presencia sin requerir visibilidad
        composeRule.onNodeWithText("3 favoritos").assertExists()
    }

    // -------------------------------------------------------------------------
    // Estado Error + Retry
    // -------------------------------------------------------------------------

    @Test
    fun `dado estado Error entonces muestra boton Reintentar`() {
        composeRule.setContent {
            MangoTheme {
                PerfilScreen(
                    uiState = PerfilUiState.Error(uiErrorPrueba),
                    onEvent = {},
                )
            }
        }

        composeRule.onNodeWithText("Reintentar").assertIsDisplayed()
    }

    @Test
    fun `dado estado Error cuando click en Reintentar entonces invoca onEvent Retry`() {
        var eventoCapturado: PerfilUiEvent? = null

        composeRule.setContent {
            MangoTheme {
                PerfilScreen(
                    uiState = PerfilUiState.Error(uiErrorPrueba),
                    onEvent = { eventoCapturado = it },
                )
            }
        }

        composeRule.onNodeWithText("Reintentar").performClick()

        assertTrue(eventoCapturado is PerfilUiEvent.Retry)
    }
}
