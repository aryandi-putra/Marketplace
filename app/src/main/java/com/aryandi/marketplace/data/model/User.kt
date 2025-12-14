package com.aryandi.marketplace.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val email: String,
    val username: String,
    val password: String? = null,
    val name: Name,
    val address: Address,
    val phone: String
)

@Serializable
data class Name(
    val firstname: String,
    val lastname: String
)

@Serializable
data class Address(
    val city: String,
    val street: String,
    val number: Int,
    val zipcode: String,
    val geolocation: Geolocation
)

@Serializable
data class Geolocation(
    val lat: String,
    val long: String
)
