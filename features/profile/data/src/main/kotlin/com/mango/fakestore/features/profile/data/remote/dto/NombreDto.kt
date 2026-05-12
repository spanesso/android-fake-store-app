package com.mango.fakestore.features.profile.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NombreDto(
    val firstname: String,
    val lastname: String,
)
