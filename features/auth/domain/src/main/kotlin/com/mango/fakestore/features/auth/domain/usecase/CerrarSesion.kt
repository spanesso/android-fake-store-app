package com.mango.fakestore.features.auth.domain.usecase

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.auth.domain.repository.SesionRepository
import javax.inject.Inject

class CerrarSesion @Inject constructor(
    private val repository: SesionRepository,
) {
    suspend operator fun invoke(): Either<DomainError, Unit> =
        repository.cerrarSesion()
}
