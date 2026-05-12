package com.mango.fakestore.features.profile.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DireccionDto(
    val city: String,
    val street: String,
    val number: Int,
    val zipcode: String,
)
