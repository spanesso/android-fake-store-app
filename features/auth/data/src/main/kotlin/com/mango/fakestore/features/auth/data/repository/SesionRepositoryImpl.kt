package com.mango.fakestore.features.auth.data.repository

import arrow.core.Either
import arrow.core.flatMap
import com.mango.fakestore.core.datastore.MangoDataStore
import com.mango.fakestore.core.datastore.model.SessionData
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.ext.safeApiCall
import com.mango.fakestore.core.error.ext.safeDbCall
import com.mango.fakestore.features.auth.data.mapper.toEntity
import com.mango.fakestore.features.auth.data.remote.AuthApiService
import com.mango.fakestore.features.auth.domain.model.SesionUsuario
import com.mango.fakestore.features.auth.domain.repository.SesionRepository
import com.mango.fakestore.features.profile.data.local.PerfilDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SesionRepositoryImpl @Inject constructor(
    private val authApi: AuthApiService,
    private val perfilDao: PerfilDao,
    private val dataStore: MangoDataStore,
) : SesionRepository {

    override suspend fun seleccionarUsuario(userId: Int): Either<DomainError, SesionUsuario> =
        safeApiCall { authApi.obtenerUsuario(userId) }.flatMap { dto ->
            safeDbCall { perfilDao.insertarPerfil(dto.toEntity()) }.flatMap {
                safeDbCall {
                    dataStore.saveSession(SessionData(accessToken = null, refreshToken = null, userId = userId.toString()))
                    SesionUsuario(userId = userId, activa = true)
                }
            }
        }

    override fun obtenerSesionActiva(): Flow<Int?> =
        dataStore.sessionFlow.map { it.userId?.toIntOrNull() }

    override suspend fun cerrarSesion(): Either<DomainError, Unit> =
        safeDbCall { dataStore.clearSession() }
}
