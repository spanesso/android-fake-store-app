package com.mango.fakestore.features.profile.data.repository

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.ext.safeApiCall
import com.mango.fakestore.features.profile.data.mapper.toDomain
import com.mango.fakestore.features.profile.data.remote.PerfilApi
import com.mango.fakestore.features.profile.domain.model.Usuario
import com.mango.fakestore.features.profile.domain.repository.PerfilRepository
import javax.inject.Inject

class PerfilRepositoryImpl @Inject constructor(
    private val api: PerfilApi,
) : PerfilRepository {

    override suspend fun obtenerPerfil(userId: Int): Either<DomainError, Usuario> =
        safeApiCall { api.obtenerUsuario(userId).toDomain() }
}
