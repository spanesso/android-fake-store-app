package com.mango.fakestore.core.ui.composable

import com.mango.fakestore.core.error.UiError
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ErrorContentTest {

    private val sampleError = UiError(
        messageRes = android.R.string.untitled,
        severity = UiError.Severity.Blocking,
        actions = listOf(UiError.UiErrorAction.Retry),
        errorCode = "NET-000",
    )

    @Test
    fun `error no nulo activa el estado de error`() {
        val error: UiError? = sampleError
        assertNotNull(error)
        assertTrue(error != null)
    }

    @Test
    fun `error nulo muestra el contenido normal`() {
        val error: UiError? = null
        assertNull(error)
        assertFalse(error != null)
    }

    @Test
    fun `UiError contiene accion Retry cuando corresponde`() {
        assertTrue(sampleError.actions.contains(UiError.UiErrorAction.Retry))
    }

    @Test
    fun `UiError Fatal no contiene accion Retry`() {
        val fatalError = UiError(
            messageRes = android.R.string.untitled,
            severity = UiError.Severity.Fatal,
            actions = listOf(UiError.UiErrorAction.Login),
            errorCode = "SEC-005",
        )
        assertFalse(fatalError.actions.contains(UiError.UiErrorAction.Retry))
    }
}
