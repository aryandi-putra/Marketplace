package com.aryandi.marketplace.data.repository

import com.aryandi.marketplace.data.model.CartItem
import com.aryandi.marketplace.data.remote.CartApi
import com.aryandi.marketplace.data.remote.ProductApi
import com.aryandi.marketplace.util.Resource
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartApi: CartApi,
    private val productApi: ProductApi
) : CartRepository {

    override suspend fun getUserCart(userId: Int): Resource<List<CartItem>> {
        return try {
            val carts = cartApi.getUserCart(userId)

            // Get the most recent cart
            val latestCart = carts.maxByOrNull { it.id }

            if (latestCart == null) {
                return Resource.Success(emptyList())
            }

            // Fetch full product details for each cart item
            val cartItems = latestCart.products.map { cartProduct ->
                val product = productApi.getProductById(cartProduct.productId)
                CartItem(
                    product = product,
                    quantity = cartProduct.quantity
                )
            }

            Resource.Success(cartItems)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred")
        }
    }
}
