package com.mango.fakestore.features.profile.domain.usecase

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.profile.domain.model.Usuario
import com.mango.fakestore.features.profile.domain.repository.PerfilRepository
import javax.inject.Inject

class GuardarPerfilLocal @Inject constructor(
    private val repository: PerfilRepository,
) {
    suspend operator fun invoke(usuario: Usuario): Either<DomainError, Unit> =
        repository.guardarPerfilLocal(usuario)
}
