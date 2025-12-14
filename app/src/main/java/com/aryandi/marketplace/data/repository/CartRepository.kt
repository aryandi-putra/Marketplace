package com.aryandi.marketplace.data.repository

import com.aryandi.marketplace.data.model.Cart
import com.aryandi.marketplace.data.model.CartItem
import com.aryandi.marketplace.util.Resource

interface CartRepository {
    suspend fun getUserCart(userId: Int): Resource<List<CartItem>>

    suspend fun addToCart(userId: Int, productId: Int, quantity: Int = 1): Resource<Cart>

    suspend fun updateCart(cartId: Int, cart: Cart): Resource<Cart>

    suspend fun deleteCart(cartId: Int): Resource<Cart>
}
