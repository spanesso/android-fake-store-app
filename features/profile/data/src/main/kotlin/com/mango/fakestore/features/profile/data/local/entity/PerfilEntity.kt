package com.mango.fakestore.features.profile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perfiles")
data class PerfilEntity(
    @PrimaryKey val id: Int,
    val nombreCompleto: String,
    val nombreUsuario: String,
    val email: String,
    val telefono: String,
    val ciudad: String,
    val calle: String,
    val numeroCalle: Int,
    val codigoPostal: String,
    val cachadoEn: Long,
)
