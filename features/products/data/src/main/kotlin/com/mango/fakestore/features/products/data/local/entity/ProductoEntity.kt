package com.mango.fakestore.features.products.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey val id: Int,
    val titulo: String,
    val descripcion: String,
    val precio: Double,
    val categoria: String,
    val imagenUrl: String,
    val valoracionPuntuacion: Double,
    val valoracionNumVotaciones: Int,
    val cachadoEn: Long = System.currentTimeMillis(),
)
