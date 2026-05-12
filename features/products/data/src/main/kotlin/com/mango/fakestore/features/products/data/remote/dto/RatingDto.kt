package com.mango.fakestore.features.products.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RatingDto(
    val rate: Double,
    val count: Int,
)
