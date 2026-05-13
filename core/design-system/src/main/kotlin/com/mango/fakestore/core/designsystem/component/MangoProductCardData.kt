package com.mango.fakestore.core.designsystem.component

data class MangoProductCardData(
    val title: String,
    val price: String,
    val imagenUrl: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val puntuacion: Float = 0f,
    val numVotaciones: Int = 0,
)
