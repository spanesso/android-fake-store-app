package com.mango.fakestore.features.profile.domain.repository

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.profile.domain.model.Usuario

interface PerfilRepository {
    suspend fun obtenerPerfil(userId: Int): Either<DomainError, Usuario>
}
