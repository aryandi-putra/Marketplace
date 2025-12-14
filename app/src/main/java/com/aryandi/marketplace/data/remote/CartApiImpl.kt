package com.aryandi.marketplace.data.remote

import com.aryandi.marketplace.data.model.Cart
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
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

    override suspend fun updateCart(cartId: Int, cart: Cart): Cart {
        return client.put("$BASE_URL/carts/$cartId") {
            contentType(ContentType.Application.Json)
            setBody(cart)
        }.body()
    }

    override suspend fun deleteCart(cartId: Int): Cart {
        return client.delete("$BASE_URL/carts/$cartId").body()
    }
}
