package com.aryandi.marketplace.data.remote

import com.aryandi.marketplace.data.model.Cart

interface CartApi {
    suspend fun getUserCart(userId: Int): List<Cart>

    suspend fun updateCart(cartId: Int, cart: Cart): Cart

    suspend fun deleteCart(cartId: Int): Cart
}
