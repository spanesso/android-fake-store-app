package com.mango.fakestore.features.profile.presentation.mapper

import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.UiError
import com.mango.fakestore.core.error.UiError.Severity
import com.mango.fakestore.core.error.UiError.UiErrorAction
import com.mango.fakestore.core.error.mapper.DomainErrorToUiErrorMapper
import com.mango.fakestore.features.profile.presentation.R
import javax.inject.Inject

class PerfilUiErrorMapper @Inject constructor(
    private val baseMapper: DomainErrorToUiErrorMapper,
) {
    fun map(error: DomainError): UiError = when (error) {
        is DomainError.Network.NotFound -> UiError(
            messageRes = R.string.error_perfil_no_encontrado,
            severity = Severity.Info,
            actions = listOf(UiErrorAction.Retry, UiErrorAction.Dismiss),
            errorCode = "NET-404",
        )
        else -> baseMapper.map(error)
    }
}
