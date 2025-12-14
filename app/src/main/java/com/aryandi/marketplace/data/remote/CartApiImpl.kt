package com.aryandi.marketplace.data.remote

import com.aryandi.marketplace.data.model.Cart
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class CartApiImpl @Inject constructor(
    private val client: HttpClient
) : CartApi {

    companion object {
        private const val BASE_URL = "https://fakestoreapi.com"
    }

    override suspend fun getUserCart(userId: Int): List<Cart> {
        return client.get("$BASE_URL/carts/user/$userId").body()
    }
}
