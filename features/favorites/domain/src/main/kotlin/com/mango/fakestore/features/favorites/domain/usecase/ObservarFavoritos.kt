package com.mango.fakestore.features.favorites.domain.usecase

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.favorites.domain.model.Favorito
import com.mango.fakestore.features.favorites.domain.repository.FavoritosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservarFavoritos @Inject constructor(
    private val repository: FavoritosRepository,
) {
    operator fun invoke(): Flow<Either<DomainError, List<Favorito>>> =
        repository.observarFavoritos()
}
