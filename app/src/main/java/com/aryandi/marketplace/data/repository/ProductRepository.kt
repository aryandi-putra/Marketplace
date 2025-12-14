package com.aryandi.marketplace.data.repository

import com.aryandi.marketplace.data.model.Product
import com.aryandi.marketplace.util.Resource

interface ProductRepository {
    suspend fun getAllProducts(): Resource<List<Product>>

    suspend fun getAllCategories(): Resource<List<String>>

    suspend fun getProductsByCategory(category: String): Resource<List<Product>>

    suspend fun getProductById(id: Int): Resource<Product>
}
