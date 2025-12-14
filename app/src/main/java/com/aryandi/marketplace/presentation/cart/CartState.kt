package com.aryandi.marketplace.presentation.cart

import com.aryandi.marketplace.data.model.CartItem

data class CartState(
    val items: List<CartItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val subtotal: Double
        get() = items.sumOf { it.totalPrice }

    val tax: Double
        get() = subtotal * 0.1 // 10% tax

    val total: Double
        get() = subtotal + tax

    val itemCount: Int
        get() = items.sumOf { it.quantity }
}
