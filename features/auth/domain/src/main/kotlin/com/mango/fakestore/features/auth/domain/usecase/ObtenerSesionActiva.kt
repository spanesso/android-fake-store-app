package com.mango.fakestore.features.auth.domain.usecase

import com.mango.fakestore.features.auth.domain.repository.SesionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerSesionActiva @Inject constructor(
    private val repository: SesionRepository,
) {
    operator fun invoke(): Flow<Int?> = repository.obtenerSesionActiva()
}
