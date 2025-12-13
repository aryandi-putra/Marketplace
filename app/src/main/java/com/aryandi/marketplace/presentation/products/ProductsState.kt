package com.aryandi.marketplace.presentation.products

import com.aryandi.marketplace.data.model.Product

data class ProductsState(
    val products: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMorePages: Boolean = true,
    val currentPage: Int = 1
)
