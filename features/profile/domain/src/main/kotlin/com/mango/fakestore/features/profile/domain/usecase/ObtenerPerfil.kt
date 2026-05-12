package com.mango.fakestore.features.profile.domain.usecase

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.profile.domain.model.Usuario
import com.mango.fakestore.features.profile.domain.repository.PerfilRepository
import javax.inject.Inject

class ObtenerPerfil @Inject constructor(
    private val repository: PerfilRepository,
) {
    suspend operator fun invoke(userId: Int): Either<DomainError, Usuario> {
        require(userId > 0) { "userId debe ser mayor que 0" }
        return repository.obtenerPerfil(userId)
    }
}
