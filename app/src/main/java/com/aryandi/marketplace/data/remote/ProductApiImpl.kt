package com.aryandi.marketplace.data.remote

import com.aryandi.marketplace.data.model.Product
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class ProductApiImpl @Inject constructor(
    private val client: HttpClient
) : ProductApi {

    companion object {
        private const val BASE_URL = "https://fakestoreapi.com"
    }

    override suspend fun getAllProducts(): List<Product> {
        return client.get("$BASE_URL/products").body()
    }

    override suspend fun getAllCategories(): List<String> {
        return client.get("$BASE_URL/products/categories").body()
    }

    override suspend fun getProductsByCategory(category: String): List<Product> {
        return client.get("$BASE_URL/products/category/$category").body()
    }
}
