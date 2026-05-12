package com.mango.fakestore.features.favorites.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favoritos")
data class FavoritoEntity(
    @PrimaryKey val productoId: Int,
    val titulo: String,
    val precio: Double,
    val imagenUrl: String,
    val categoria: String,
    val fechaMarcado: Long,
)
