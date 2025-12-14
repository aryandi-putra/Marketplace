package com.aryandi.marketplace.data.remote

import com.aryandi.marketplace.data.model.Cart

interface CartApi {
    suspend fun getUserCart(userId: Int): List<Cart>
}
