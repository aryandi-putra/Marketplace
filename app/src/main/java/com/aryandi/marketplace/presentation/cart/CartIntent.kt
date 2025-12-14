package com.aryandi.marketplace.presentation.cart

sealed class CartIntent {
    data class LoadCart(val userId: Int) : CartIntent()
    object Retry : CartIntent()
    data class UpdateQuantity(val productId: Int, val quantity: Int) : CartIntent()
    data class RemoveItem(val productId: Int) : CartIntent()
}
