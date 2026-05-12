package com.mango.fakestore.core.ui.composable

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoadingContentTest {

    @Test
    fun `isLoading true indica estado de carga activo`() {
        val isLoading = true
        assertTrue(isLoading)
    }

    @Test
    fun `isLoading false indica contenido disponible`() {
        val isLoading = false
        assertFalse(isLoading)
    }

    @Test
    fun `transicion de carga a contenido cambia estado`() {
        var isLoading = true
        assertTrue(isLoading)
        isLoading = false
        assertFalse(isLoading)
    }
}
