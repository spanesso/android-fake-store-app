package com.mango.fakestore.features.profile.data.repository

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.ext.safeApiCall
import com.mango.fakestore.core.error.ext.safeDbCall
import com.mango.fakestore.features.profile.data.local.PerfilDao
import com.mango.fakestore.features.profile.data.mapper.toDomain
import com.mango.fakestore.features.profile.data.mapper.toEntity
import com.mango.fakestore.features.profile.data.remote.PerfilApi
import com.mango.fakestore.features.profile.domain.model.Usuario
import com.mango.fakestore.features.profile.domain.repository.PerfilRepository
import javax.inject.Inject

class PerfilRepositoryImpl @Inject constructor(
    private val api: PerfilApi,
    private val dao: PerfilDao,
) : PerfilRepository {

    override suspend fun obtenerPerfil(userId: Int): Either<DomainError, Usuario> {
        val local = dao.obtenerPerfil(userId)
        return if (local != null) {
            Either.Right(local.toDomain())
        } else {
            safeApiCall { api.obtenerUsuario(userId).toDomain() }
        }
    }

    override suspend fun guardarPerfilLocal(usuario: Usuario): Either<DomainError, Unit> =
        safeDbCall { dao.insertarPerfil(usuario.toEntity()) }
}
