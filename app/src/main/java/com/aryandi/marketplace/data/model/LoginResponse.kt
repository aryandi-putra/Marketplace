package com.aryandi.marketplace.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String
)
