package com.mango.fakestore.features.favorites.data.local.entity

import androidx.room.Entity

@Entity(tableName = "favoritos", primaryKeys = ["productoId", "userId"])
data class FavoritoEntity(
    val productoId: Int,
    val userId: Int = 0,
    val titulo: String,
    val precio: Double,
    val imagenUrl: String,
    val categoria: String,
    val fechaMarcado: Long,
)
