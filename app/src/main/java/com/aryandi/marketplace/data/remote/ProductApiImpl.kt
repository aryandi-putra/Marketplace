package com.aryandi.marketplace.data.remote

import com.aryandi.marketplace.data.model.Product
import com.aryandi.marketplace.util.ApiConstants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class ProductApiImpl @Inject constructor(
    private val client: HttpClient
) : ProductApi {

    override suspend fun getAllProducts(): List<Product> {
        return client.get("${ApiConstants.BASE_URL}/products").body()
    }

    override suspend fun getAllCategories(): List<String> {
        return client.get("${ApiConstants.BASE_URL}/products/categories").body()
    }

    override suspend fun getProductsByCategory(category: String): List<Product> {
        return client.get("${ApiConstants.BASE_URL}/products/category/$category").body()
    }

    override suspend fun getProductById(id: Int): Product {
        return client.get("${ApiConstants.BASE_URL}/products/$id").body()
    }
}
