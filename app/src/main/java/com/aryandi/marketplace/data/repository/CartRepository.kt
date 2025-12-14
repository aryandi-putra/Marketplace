package com.aryandi.marketplace.data.repository

import com.aryandi.marketplace.data.model.CartItem
import com.aryandi.marketplace.util.Resource

interface CartRepository {
    suspend fun getUserCart(userId: Int): Resource<List<CartItem>>
}
