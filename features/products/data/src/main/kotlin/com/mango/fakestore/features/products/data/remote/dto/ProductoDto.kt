package com.mango.fakestore.features.products.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductoDto(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val image: String,
    val rating: RatingDto,
)
