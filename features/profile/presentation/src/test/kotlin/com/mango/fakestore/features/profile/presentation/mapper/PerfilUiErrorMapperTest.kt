package com.mango.fakestore.features.profile.presentation.mapper

import com.google.common.truth.Truth.assertThat
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.core.error.mapper.DomainErrorToUiErrorMapper
import com.mango.fakestore.features.profile.presentation.R
import org.junit.Before
import org.junit.Test

class PerfilUiErrorMapperTest {

    private lateinit var mapper: PerfilUiErrorMapper

    @Before
    fun setUp() {
        mapper = PerfilUiErrorMapper(DomainErrorToUiErrorMapper())
    }

    @Test
    fun `dado Network NotFound cuando se mapea entonces retorna error con messageRes de perfil y errorCode NET-404`() {
        val resultado = mapper.map(DomainError.Network.NotFound())

        assertThat(resultado.messageRes).isEqualTo(R.string.error_perfil_no_encontrado)
        assertThat(resultado.errorCode).isEqualTo("NET-404")
    }

    @Test
    fun `dado Network NotFound cuando se mapea entonces las acciones incluyen Retry`() {
        val resultado = mapper.map(DomainError.Network.NotFound())

        assertThat(resultado.actions).contains(UiError.UiErrorAction.Retry)
    }

    @Test
    fun `dado Network NotFound cuando se mapea entonces las acciones incluyen Dismiss`() {
        val resultado = mapper.map(DomainError.Network.NotFound())

        assertThat(resultado.actions).contains(UiError.UiErrorAction.Dismiss)
    }

    @Test
    fun `dado Network NotFound cuando se mapea entonces la severidad es Info`() {
        val resultado = mapper.map(DomainError.Network.NotFound())

        assertThat(resultado.severity).isEqualTo(UiError.Severity.Info)
    }

    @Test
    fun `dado Network NoConnection cuando se mapea entonces delega al mapper base con errorCode NET-000`() {
        val resultado = mapper.map(DomainError.Network.NoConnection())

        assertThat(resultado.errorCode).isEqualTo("NET-000")
        assertThat(resultado.actions).contains(UiError.UiErrorAction.Retry)
    }

    @Test
    fun `dado Network Timeout cuando se mapea entonces delega al mapper base con errorCode NET-001`() {
        val resultado = mapper.map(DomainError.Network.Timeout())

        assertThat(resultado.errorCode).isEqualTo("NET-001")
    }

    @Test
    fun `dado Network Server cuando se mapea entonces delega al mapper base con errorCode NET-500`() {
        val resultado = mapper.map(DomainError.Network.Server(500))

        assertThat(resultado.errorCode).isEqualTo("NET-500")
    }

    @Test
    fun `dado Network Parsing cuando se mapea entonces delega al mapper base con errorCode NET-002`() {
        val resultado = mapper.map(DomainError.Network.Parsing())

        assertThat(resultado.errorCode).isEqualTo("NET-002")
    }

    @Test
    fun `dado Network Unauthorized cuando se mapea entonces delega al mapper base con errorCode NET-401`() {
        val resultado = mapper.map(DomainError.Network.Unauthorized())

        assertThat(resultado.errorCode).isEqualTo("NET-401")
    }

    @Test
    fun `dado Network Forbidden cuando se mapea entonces delega al mapper base con errorCode NET-403`() {
        val resultado = mapper.map(DomainError.Network.Forbidden())

        assertThat(resultado.errorCode).isEqualTo("NET-403")
    }

    @Test
    fun `dado Unknown cuando se mapea entonces delega al mapper base con errorCode UNK-000`() {
        val resultado = mapper.map(DomainError.Unknown())

        assertThat(resultado.errorCode).isEqualTo("UNK-000")
        assertThat(resultado.actions).contains(UiError.UiErrorAction.Retry)
    }

    @Test
    fun `dado Database ReadFailed cuando se mapea entonces delega al mapper base con errorCode DB-001`() {
        val resultado = mapper.map(DomainError.Database.ReadFailed())

        assertThat(resultado.errorCode).isEqualTo("DB-001")
    }
}
