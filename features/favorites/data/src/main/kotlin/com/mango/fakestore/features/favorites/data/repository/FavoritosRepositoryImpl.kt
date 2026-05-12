package com.mango.fakestore.features.favorites.data.repository

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.ext.safeDbCall
import com.mango.fakestore.features.favorites.data.local.FavoritosDao
import com.mango.fakestore.features.favorites.data.mapper.toDomain
import com.mango.fakestore.features.favorites.data.mapper.toEntity
import com.mango.fakestore.features.favorites.domain.model.Favorito
import com.mango.fakestore.features.favorites.domain.repository.FavoritosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritosRepositoryImpl @Inject constructor(
    private val dao: FavoritosDao,
) : FavoritosRepository {

    override fun observarFavoritos(): Flow<Either<DomainError, List<Favorito>>> =
        dao.observarFavoritos().map { entities ->
            Either.Right(entities.map { it.toDomain() })
        }

    override fun observarConteo(): Flow<Int> = dao.observarConteo()

    override suspend fun toggleFavorito(favorito: Favorito): Either<DomainError, Unit> =
        safeDbCall {
            if (dao.esFavorito(favorito.productoId)) {
                dao.borrarFavorito(favorito.productoId)
            } else {
                dao.insertarFavorito(favorito.toEntity())
            }
        }
}
