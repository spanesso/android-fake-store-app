package com.mango.fakestore.features.favorites.domain.usecase

import com.mango.fakestore.features.favorites.domain.repository.FavoritosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservarConteoFavoritos @Inject constructor(
    private val repository: FavoritosRepository,
) {
    operator fun invoke(): Flow<Int> = repository.observarConteo()
}
