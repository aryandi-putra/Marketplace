package com.aryandi.marketplace.presentation.products

sealed class ProductsEffect {
    data class ShowError(val message: String) : ProductsEffect()
}
