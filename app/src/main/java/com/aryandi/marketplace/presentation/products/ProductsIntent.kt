package com.aryandi.marketplace.presentation.products

sealed class ProductsIntent {
    object LoadProducts : ProductsIntent()
    object LoadMoreProducts : ProductsIntent()
    object LoadCategories : ProductsIntent()
    data class SelectCategory(val category: String?) : ProductsIntent()
    object Retry : ProductsIntent()
    object LoadCartCount : ProductsIntent()
}
