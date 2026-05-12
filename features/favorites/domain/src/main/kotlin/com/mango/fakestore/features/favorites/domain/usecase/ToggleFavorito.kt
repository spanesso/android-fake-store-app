package com.mango.fakestore.features.favorites.domain.usecase

import arrow.core.Either
import com.mango.fakestore.core.error.DomainError
import com.mango.fakestore.features.favorites.domain.model.Favorito
import com.mango.fakestore.features.favorites.domain.repository.FavoritosRepository
import javax.inject.Inject

class ToggleFavorito @Inject constructor(
    private val repository: FavoritosRepository,
) {
    suspend operator fun invoke(favorito: Favorito): Either<DomainError, Unit> =
        repository.toggleFavorito(favorito)
}
