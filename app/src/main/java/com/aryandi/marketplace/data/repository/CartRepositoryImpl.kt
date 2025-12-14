package com.aryandi.marketplace.data.repository

import com.aryandi.marketplace.data.model.Cart
import com.aryandi.marketplace.data.model.CartItem
import com.aryandi.marketplace.data.model.CartProduct
import com.aryandi.marketplace.data.remote.CartApi
import com.aryandi.marketplace.data.remote.ProductApi
import com.aryandi.marketplace.data.local.dao.CartDao
import com.aryandi.marketplace.data.local.entity.toCart
import com.aryandi.marketplace.data.local.entity.toEntity
import com.aryandi.marketplace.util.Resource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartApi: CartApi,
    private val productApi: ProductApi,
    private val cartDao: CartDao
) : CartRepository {

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    companion object {
        // set cache duration to 10 minutes
        private val CACHE_VALIDITY_MS = TimeUnit.MINUTES.toMillis(10)
    }

    private fun isCacheValid(cacheTimestamp: Long): Boolean {
        return System.currentTimeMillis() - cacheTimestamp < CACHE_VALIDITY_MS
    }

    override suspend fun getUserCart(userId: Int): Resource<List<CartItem>> {
        return try {
            val cachedEntities = cartDao.getAllCarts().filter { it.userId == userId }
            val latestCachedEntity = cachedEntities.maxByOrNull { it.id }
            if (latestCachedEntity != null && cachedEntities.isNotEmpty() && isCacheValid(
                    latestCachedEntity.lastUpdated
                )
            ) {
                val cachedCart = latestCachedEntity.toCart()
                val cartItems = cachedCart.products.map {
                    val product = productApi.getProductById(it.productId)
                    CartItem(product = product, quantity = it.quantity)
                }
                return Resource.Success(cartItems)
            }
            // Only fetch cart from API if the cache is not exist/expired
            val carts = cartApi.getUserCart(userId)
            val latestCart = carts.maxByOrNull { it.id }
            if (latestCart == null) {
                return Resource.Success(emptyList())
            }
            cartDao.insertAll(carts.map { it.toEntity() })
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
            val cachedEntity = cartDao.getAllCarts()
                .filter { it.userId == userId }
                .maxByOrNull { it.id }
            val existingCarts = cartApi.getUserCart(userId)
            if (cachedEntity != null && isCacheValid(cachedEntity.lastUpdated)) {
                return Resource.Success(cachedEntity.toCart())
            }
            val latestCart = existingCarts.maxByOrNull { it.id }
            val cart = if (latestCart != null) {
                val existingProducts = latestCart.products.toMutableList()
                val existingProductIndex =
                    existingProducts.indexOfFirst { it.productId == productId }
                if (existingProductIndex != -1) {
                    existingProducts[existingProductIndex] =
                        existingProducts[existingProductIndex].copy(
                            quantity = existingProducts[existingProductIndex].quantity + quantity
                        )
                } else {
                    existingProducts.add(CartProduct(productId = productId, quantity = quantity))
                }
                val updatedCart = latestCart.copy(
                    date = getCurrentDate(),
                    products = existingProducts
                )
                cartApi.updateCart(latestCart.id, updatedCart)
            } else {
                val newCart = Cart(
                    id = 1,
                    userId = userId,
                    date = getCurrentDate(),
                    products = listOf(CartProduct(productId = productId, quantity = quantity))
                )
                cartApi.addToCart(newCart)
            }
            cartDao.insertCart(cart.toEntity())
            Resource.Success(cart)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add item to cart")
        }
    }

    override suspend fun updateCart(cartId: Int, cart: Cart): Resource<Cart> {
        return try {
            val cachedEntity = cartDao.getCartById(cartId)
            cartDao.updateCart(cart.toEntity())
            val updatedCart = cartApi.updateCart(cartId, cart)
            if (cachedEntity != null) {
                return Resource.Success(cachedEntity.toCart())
            }
            Resource.Success(updatedCart)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update cart")
        }
    }

    override suspend fun deleteCart(cartId: Int): Resource<Cart> {
        return try {
            val cachedEntity = cartDao.getCartById(cartId)
            if (cachedEntity != null) {
                cartDao.deleteCart(cachedEntity)
            }
            val deletedCart = cartApi.deleteCart(cartId)
            Resource.Success(deletedCart)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete cart")
        }
    }
}
