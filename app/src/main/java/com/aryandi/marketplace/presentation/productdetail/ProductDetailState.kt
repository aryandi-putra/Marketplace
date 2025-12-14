package com.aryandi.marketplace.presentation.productdetail

import com.aryandi.marketplace.data.model.Product

data class ProductDetailState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
