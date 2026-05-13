package com.mango.fakestore.features.auth.data.mapper

import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.features.auth.data.remote.dto.AuthDireccionDto
import com.mango.fakestore.features.auth.data.remote.dto.AuthNombreDto
import com.mango.fakestore.features.auth.data.remote.dto.AuthUsuarioDto
import org.junit.Test

class AuthUsuarioDtoMapperTest {

    private val dto = AuthUsuarioDto(
        id = 10,
        username = "jimmie_k",
        email = "jimmie@gmail.com",
        phone = "1-104-001-4567",
        name = AuthNombreDto(firstname = "jimmie", lastname = "klein"),
        address = AuthDireccionDto(city = "fort wayne", street = "oak lawn ave", number = 526, zipcode = "10256-4532"),
    )

    @Test
    fun `dado dto completo cuando se mapea a domain entonces todos los campos son correctos`() {
        val usuario = dto.toDomain()

        assertThat(usuario.id).isEqualTo(10)
        assertThat(usuario.nombreUsuario).isEqualTo("jimmie_k")
        assertThat(usuario.email).isEqualTo("jimmie@gmail.com")
        assertThat(usuario.telefono).isEqualTo("1-104-001-4567")
        assertThat(usuario.nombreCompleto).isEqualTo("jimmie klein")
        assertThat(usuario.ciudad).isEqualTo("fort wayne")
        assertThat(usuario.calle).isEqualTo("oak lawn ave")
        assertThat(usuario.numeroCalle).isEqualTo(526)
        assertThat(usuario.codigoPostal).isEqualTo("10256-4532")
    }

    @Test
    fun `dado dto cuando se mapea a domain entonces la contrasena NO aparece en el dominio`() {
        val usuario = dto.toDomain()

        // El modelo de dominio Usuario no tiene campo password — esta es una validación explícita de seguridad
        val campos = usuario.javaClass.declaredFields.map { it.name }
        assertThat(campos).doesNotContain("password")
    }

    @Test
    fun `dado dto cuando se mapea a entity entonces cachadoEn se establece`() {
        val antes = System.currentTimeMillis()
        val entity = dto.toEntity()
        val despues = System.currentTimeMillis()

        assertThat(entity.id).isEqualTo(10)
        assertThat(entity.nombreUsuario).isEqualTo("jimmie_k")
        assertThat(entity.cachadoEn).isAtLeast(antes)
        assertThat(entity.cachadoEn).isAtMost(despues)
    }
}
