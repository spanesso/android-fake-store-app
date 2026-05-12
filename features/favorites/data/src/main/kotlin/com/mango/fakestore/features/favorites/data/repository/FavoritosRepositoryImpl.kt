package com.mango.fakestore.features.favorites.data.repository

import arrow.core.Either
import arrow.core.right
import com.mango.fakestore.core.datastore.MangoDataStore
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.core.error.ext.safeDbCall
import com.mango.fakestore.features.favorites.data.local.FavoritosDao
import com.mango.fakestore.features.favorites.data.mapper.toDomain
import com.mango.fakestore.features.favorites.data.mapper.toEntity
import com.mango.fakestore.features.favorites.domain.model.Favorito
import com.mango.fakestore.features.favorites.domain.repository.FavoritosRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)

class FavoritosRepositoryImpl @Inject constructor(
    private val dao: FavoritosDao,
    private val dataStore: MangoDataStore,
) : FavoritosRepository {

    private val userIdFlow: Flow<Int> =
        dataStore.sessionFlow.map { it.userId?.toIntOrNull() ?: 0 }

    override fun observarFavoritos(): Flow<Either<DomainError, List<Favorito>>> =
        userIdFlow.flatMapLatest { userId ->
            dao.observarFavoritos(userId).map { entities ->
                entities.map { it.toDomain() }.right()
            }
        }

    override fun observarConteo(): Flow<Int> =
        userIdFlow.flatMapLatest { userId -> dao.observarConteo(userId) }

    override suspend fun toggleFavorito(favorito: Favorito): Either<DomainError, Unit> =
        safeDbCall {
            val userId = dataStore.sessionFlow.first().userId?.toIntOrNull() ?: 0
            if (dao.esFavorito(favorito.productoId, userId)) {
                dao.borrarFavorito(favorito.productoId, userId)
            } else {
                dao.insertarFavorito(favorito.toEntity(userId))
            }
        }
}
