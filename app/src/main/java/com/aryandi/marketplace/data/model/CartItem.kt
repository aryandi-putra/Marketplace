package com.aryandi.marketplace.data.model

// UI model combining cart product with full product details
data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val totalPrice: Double
        get() = product.price * quantity
}
