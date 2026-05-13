package com.mango.fakestore.features.auth.domain.usecase

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.auth.domain.model.SesionUsuario
import com.mango.fakestore.features.auth.domain.repository.SesionRepository
import javax.inject.Inject

class SeleccionarUsuario @Inject constructor(
    private val repository: SesionRepository,
) {
    suspend operator fun invoke(userId: Int): Either<DomainError, SesionUsuario> =
        repository.seleccionarUsuario(userId)
}
