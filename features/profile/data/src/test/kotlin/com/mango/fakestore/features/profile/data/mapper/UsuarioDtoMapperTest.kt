@file:Suppress("MaximumLineLength", "MaxLineLength")

package com.mango.fakestore.features.profile.data.mapper

import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.features.profile.data.remote.dto.DireccionDto
import com.mango.fakestore.features.profile.data.remote.dto.NombreDto
import com.mango.fakestore.features.profile.data.remote.dto.UsuarioDto
import org.junit.Test

class UsuarioDtoMapperTest {

    private val dtoCompleto = UsuarioDto(
        id = 8,
        username = "johnd",
        email = "john@example.com",
        phone = "1-570-236-7033",
        name = NombreDto(firstname = "John", lastname = "Doe"),
        address = DireccionDto(
            city = "kilcoole",
            street = "new road",
            number = 7835,
            zipcode = "12926-3874",
        ),
    )

    @Test
    fun `dado un UsuarioDto cuando se mapea a dominio entonces el id es correcto`() {
        val resultado = dtoCompleto.toDomain()
        assertThat(resultado.id).isEqualTo(8)
    }

    @Test
    fun `dado un UsuarioDto cuando se mapea a dominio entonces el nombreCompleto concatena firstname y lastname`() {
        val resultado = dtoCompleto.toDomain()
        assertThat(resultado.nombreCompleto).isEqualTo("John Doe")
    }

    @Test
    fun `dado un UsuarioDto cuando se mapea a dominio entonces el nombreUsuario es correcto`() {
        val resultado = dtoCompleto.toDomain()
        assertThat(resultado.nombreUsuario).isEqualTo("johnd")
    }

    @Test
    fun `dado un UsuarioDto cuando se mapea a dominio entonces el email es correcto`() {
        val resultado = dtoCompleto.toDomain()
        assertThat(resultado.email).isEqualTo("john@example.com")
    }

    @Test
    fun `dado un UsuarioDto cuando se mapea a dominio entonces el telefono es correcto`() {
        val resultado = dtoCompleto.toDomain()
        assertThat(resultado.telefono).isEqualTo("1-570-236-7033")
    }

    @Test
    fun `dado un UsuarioDto cuando se mapea a dominio entonces la calle incluye nombre y numero`() {
        val resultado = dtoCompleto.toDomain()
        assertThat(resultado.calle).isEqualTo("new road 7835")
    }

    @Test
    fun `dado un UsuarioDto cuando se mapea a dominio entonces la ciudad es correcta`() {
        val resultado = dtoCompleto.toDomain()
        assertThat(resultado.ciudad).isEqualTo("kilcoole")
    }

    @Test
    fun `dado un UsuarioDto cuando se mapea a dominio entonces el codigoPostal es correcto`() {
        val resultado = dtoCompleto.toDomain()
        assertThat(resultado.codigoPostal).isEqualTo("12926-3874")
    }

    @Test
    fun `dado un UsuarioDto con number cero cuando se mapea a dominio entonces la calle muestra el numero como cero`() {
        val dto = dtoCompleto.copy(address = dtoCompleto.address.copy(number = 0))
        val resultado = dto.toDomain()
        assertThat(resultado.calle).isEqualTo("new road 0")
    }

    @Test
    fun `dado un UsuarioDto con firstname vacio cuando se mapea a dominio entonces nombreCompleto inicia con espacio`() {
        val dto = dtoCompleto.copy(name = NombreDto(firstname = "", lastname = "Doe"))
        val resultado = dto.toDomain()
        assertThat(resultado.nombreCompleto).isEqualTo(" Doe")
    }
}
