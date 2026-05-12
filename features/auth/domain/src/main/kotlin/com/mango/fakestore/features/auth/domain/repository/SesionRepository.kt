package com.mango.fakestore.features.auth.domain.repository

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.auth.domain.model.SesionUsuario
import kotlinx.coroutines.flow.Flow

interface SesionRepository {
    suspend fun seleccionarUsuario(userId: Int): Either<DomainError, SesionUsuario>
    fun obtenerSesionActiva(): Flow<Int?>
    suspend fun cerrarSesion(): Either<DomainError, Unit>
}
