package com.aryandi.marketplace.presentation.productdetail

sealed class ProductDetailIntent {
    data class LoadProduct(val productId: Int) : ProductDetailIntent()
    object Retry : ProductDetailIntent()
}
