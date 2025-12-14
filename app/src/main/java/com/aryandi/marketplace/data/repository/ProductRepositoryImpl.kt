package com.aryandi.marketplace.data.repository

import com.aryandi.marketplace.data.model.Product
import com.aryandi.marketplace.data.remote.ProductApi
import com.aryandi.marketplace.util.Resource
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productApi: ProductApi
) : ProductRepository {

    override suspend fun getAllProducts(): Resource<List<Product>> {
        return try {
            val products = productApi.getAllProducts()
            Resource.Success(products)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred")
        }
    }

    override suspend fun getAllCategories(): Resource<List<String>> {
        return try {
            val categories = productApi.getAllCategories()
            Resource.Success(categories)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred")
        }
    }

    override suspend fun getProductsByCategory(category: String): Resource<List<Product>> {
        return try {
            val products = productApi.getProductsByCategory(category)
            Resource.Success(products)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred")
        }
    }

    override suspend fun getProductById(id: Int): Resource<Product> {
        return try {
            val product = productApi.getProductById(id)
            Resource.Success(product)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred")
        }
    }
}
