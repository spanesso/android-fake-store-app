package com.example.fakestoreapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fakestoreapp.di.FakeBiometricAuthenticator
import com.mango.fakestore.core.security.biometric.BiometricAuthenticator
import com.mango.fakestore.core.security.biometric.BiometricResult
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigacionE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var biometricAuthenticator: BiometricAuthenticator

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun app_arranca_y_muestra_pantalla_productos() {
        composeRule.onNodeWithText("Productos").assertIsDisplayed()
    }

    @Test
    fun navegar_a_favoritos_muestra_pantalla_favoritos() {
        composeRule.onNodeWithText("Favoritos").performClick()
        composeRule.onNodeWithText("Favoritos").assertIsDisplayed()
    }

    @Test
    fun navegar_a_perfil_con_biometria_exitosa_muestra_pantalla_perfil() {
        (biometricAuthenticator as? FakeBiometricAuthenticator)?.defaultResult =
            BiometricResult.Exito

        composeRule.onNodeWithText("Perfil").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Perfil").assertIsDisplayed()
    }

    @Test
    fun navegar_a_perfil_sin_biometria_permanece_en_pantalla_anterior() {
        (biometricAuthenticator as? FakeBiometricAuthenticator)?.defaultResult =
            BiometricResult.Cancelado

        composeRule.onNodeWithText("Perfil").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Productos").assertIsDisplayed()
    }
}
