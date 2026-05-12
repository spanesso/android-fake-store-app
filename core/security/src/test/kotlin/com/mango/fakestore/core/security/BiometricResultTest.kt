package com.mango.fakestore.core.security

import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.core.security.biometric.BiometricResult
import org.junit.Test

class BiometricResultTest {

    @Test
    fun `Exito es instancia de BiometricResult`() {
        assertThat(BiometricResult.Exito).isInstanceOf(BiometricResult::class.java)
    }

    @Test
    fun `Cancelado es instancia de BiometricResult`() {
        assertThat(BiometricResult.Cancelado).isInstanceOf(BiometricResult::class.java)
    }

    @Test
    fun `BloqueadoTemporalmente es instancia de BiometricResult`() {
        assertThat(BiometricResult.BloqueadoTemporalmente).isInstanceOf(BiometricResult::class.java)
    }

    @Test
    fun `NoDisponible es instancia de BiometricResult`() {
        assertThat(BiometricResult.NoDisponible).isInstanceOf(BiometricResult::class.java)
    }

    @Test
    fun `Error contiene mensaje`() {
        val error = BiometricResult.Error("Hardware no disponible")
        assertThat(error.mensaje).isEqualTo("Hardware no disponible")
    }

    @Test
    fun `when exhaustivo sobre todos los casos`() {
        val resultados: List<BiometricResult> = listOf(
            BiometricResult.Exito,
            BiometricResult.Cancelado,
            BiometricResult.BloqueadoTemporalmente,
            BiometricResult.NoDisponible,
            BiometricResult.Error("msg"),
        )
        resultados.forEach { resultado ->
            val descripcion = when (resultado) {
                BiometricResult.Exito -> "exito"
                BiometricResult.Cancelado -> "cancelado"
                BiometricResult.BloqueadoTemporalmente -> "bloqueado"
                BiometricResult.NoDisponible -> "no_disponible"
                is BiometricResult.Error -> "error: ${resultado.mensaje}"
            }
            assertThat(descripcion).isNotEmpty()
        }
    }
}
