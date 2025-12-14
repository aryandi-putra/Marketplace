package com.aryandi.marketplace.data.repository

import com.aryandi.marketplace.data.model.Cart
import com.aryandi.marketplace.data.model.CartItem
import com.aryandi.marketplace.data.model.CartProduct
import com.aryandi.marketplace.data.remote.CartApi
import com.aryandi.marketplace.data.remote.ProductApi
import com.aryandi.marketplace.util.Resource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartApi: CartApi,
    private val productApi: ProductApi
) : CartRepository {

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

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

    override suspend fun addToCart(userId: Int, productId: Int, quantity: Int): Resource<Cart> {
        return try {
            // Get user's existing cart
            val existingCarts = cartApi.getUserCart(userId)
            val latestCart = existingCarts.maxByOrNull { it.id }

            val cart = if (latestCart != null) {
                // Update existing cart - check if product already exists
                val existingProducts = latestCart.products.toMutableList()
                val existingProductIndex =
                    existingProducts.indexOfFirst { it.productId == productId }

                if (existingProductIndex != -1) {
                    // Product exists, update quantity
                    existingProducts[existingProductIndex] =
                        existingProducts[existingProductIndex].copy(
                            quantity = existingProducts[existingProductIndex].quantity + quantity
                        )
                } else {
                    // Product doesn't exist, add new product
                    existingProducts.add(CartProduct(productId = productId, quantity = quantity))
                }

                // Use PUT to update cart
                val updatedCart = latestCart.copy(
                    date = getCurrentDate(),
                    products = existingProducts
                )
                cartApi.updateCart(latestCart.id, updatedCart)
            } else {
                // Create new cart using POST
                val newCart = Cart(
                    id = 0, // Will be assigned by API
                    userId = userId,
                    date = getCurrentDate(),
                    products = listOf(CartProduct(productId = productId, quantity = quantity))
                )
                cartApi.addToCart(newCart)
            }

            Resource.Success(cart)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add item to cart")
        }
    }

    override suspend fun updateCart(cartId: Int, cart: Cart): Resource<Cart> {
        return try {
            val updatedCart = cartApi.updateCart(cartId, cart)
            Resource.Success(updatedCart)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update cart")
        }
    }

    override suspend fun deleteCart(cartId: Int): Resource<Cart> {
        return try {
            val deletedCart = cartApi.deleteCart(cartId)
            Resource.Success(deletedCart)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete cart")
        }
    }
}
