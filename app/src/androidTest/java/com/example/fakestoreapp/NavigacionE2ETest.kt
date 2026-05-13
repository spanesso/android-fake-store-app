package com.example.fakestoreapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigacionE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun appArrancaYMuestraPantallaProductos() {
        composeRule.onNodeWithText("Productos").assertIsDisplayed()
    }

    @Test
    fun navegarAFavoritosMuestraPantallaFavoritos() {
        composeRule.onNodeWithText("Favoritos").performClick()
        composeRule.onNodeWithText("Favoritos").assertIsDisplayed()
    }

    @Test
    fun navegarAPerfilMuestraPantallaPerfil() {
        composeRule.onNodeWithText("Perfil").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Perfil").assertIsDisplayed()
    }
}
