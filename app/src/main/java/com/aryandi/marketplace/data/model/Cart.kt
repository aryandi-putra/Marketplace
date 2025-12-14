package com.aryandi.marketplace.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Cart(
    val id: Int,
    val userId: Int,
    val date: String,
    val products: List<CartProduct>
)

@Serializable
data class CartProduct(
    val productId: Int,
    val quantity: Int
)
