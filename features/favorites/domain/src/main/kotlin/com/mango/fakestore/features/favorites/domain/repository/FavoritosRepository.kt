package com.mango.fakestore.features.favorites.domain.repository

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.favorites.domain.model.Favorito
import kotlinx.coroutines.flow.Flow

interface FavoritosRepository {
    fun observarFavoritos(): Flow<Either<DomainError, List<Favorito>>>
    fun observarConteo(): Flow<Int>
    suspend fun toggleFavorito(favorito: Favorito): Either<DomainError, Unit>
}
