package com.aryandi.marketplace.presentation.productdetail

sealed class ProductDetailIntent {
    data class LoadProduct(val productId: Int) : ProductDetailIntent()
    data class AddToCart(val productId: Int, val quantity: Int = 1) : ProductDetailIntent()
    object Retry : ProductDetailIntent()
}
