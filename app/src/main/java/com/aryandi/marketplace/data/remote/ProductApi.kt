package com.aryandi.marketplace.data.remote

import com.aryandi.marketplace.data.model.Product

interface ProductApi {
    suspend fun getAllProducts(): List<Product>

    suspend fun getAllCategories(): List<String>

    suspend fun getProductsByCategory(category: String): List<Product>

    suspend fun getProductById(id: Int): Product
}
