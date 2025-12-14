package com.aryandi.marketplace.data.repository

import com.aryandi.marketplace.data.local.dao.CartDao
import com.aryandi.marketplace.data.local.entity.CartEntity
import com.aryandi.marketplace.data.local.entity.CartProductData
import com.aryandi.marketplace.data.local.entity.toCart
import com.aryandi.marketplace.data.local.entity.toEntity
import com.aryandi.marketplace.data.model.Cart
import com.aryandi.marketplace.data.model.CartItem
import com.aryandi.marketplace.data.remote.CartApi
import com.aryandi.marketplace.data.remote.ProductApi
import com.aryandi.marketplace.util.Resource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

    // Time limit for cache in milliseconds (10 minutes)
    private val CACHE_TIMEOUT_MS = 10 * 60 * 1000L

    private suspend fun cartEntityToItems(cart: CartEntity): List<CartItem> =
        cart.products.map { cartProductData ->
            val product = productApi.getProductById(cartProductData.productId)
            CartItem(product = product, quantity = cartProductData.quantity)
        }

    override suspend fun getUserCart(userId: Int): Resource<List<CartItem>> {
        return try {
            val localCart = cartDao.getUserCart(userId)
            val now = System.currentTimeMillis()
            val isStale = localCart == null || (now - localCart.lastUpdated > CACHE_TIMEOUT_MS)

            // Refresh cache from remote if missing or stale
            if (isStale) {
                val remoteCarts = cartApi.getUserCart(userId)
                val latestCart = remoteCarts.maxByOrNull { it.id }
                latestCart?.let {
                    cartDao.insertCart(
                        it.toEntity(needsSync = false).copy(lastUpdated = now)
                    )
                }
                cartDao.cleanupOldCarts(userId)
            }

            val cachedCart = cartDao.getUserCart(userId)
            val items = cachedCart?.let { cartEntityToItems(it) } ?: emptyList()
            Resource.Success(items)
        } catch (e: Exception) {
            // On error, graceful fallback to local cache if exists
            val localCart = cartDao.getUserCart(userId)
            if (localCart != null) {
                Resource.Success(cartEntityToItems(localCart))
            } else {
                Resource.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    override suspend fun addToCart(userId: Int, productId: Int, quantity: Int): Resource<Cart> {
        return try {
            // Update cache first
            val now = System.currentTimeMillis()
            val localCart = cartDao.getUserCart(userId)
            val products = localCart?.products?.toMutableList() ?: mutableListOf()
            val idx = products.indexOfFirst { it.productId == productId }
            if (idx != -1) {
                products[idx] = products[idx].copy(quantity = products[idx].quantity + quantity)
            } else {
                products.add(CartProductData(productId, quantity))
            }

            val updatedCart = CartEntity(
                id = localCart?.id ?: 0,
                userId = userId,
                date = getCurrentDate(),
                products = products,
                lastUpdated = now,
                needsSync = true // flag for unsynced
            )
            cartDao.insertCart(updatedCart)

            // Sync with API (create or update in remote)
            val apiCart = if ((localCart?.id ?: 0) != 0) {
                cartApi.updateCart(updatedCart.id, updatedCart.toCart())
            } else {
                cartApi.addToCart(updatedCart.toCart())
            }
            // Mark as synced in cache and cleanup
            cartDao.markCartAsSynced(apiCart.id)
            cartDao.cleanupOldCarts(userId)
            Resource.Success(apiCart)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add item to cart")
        }
    }

    override suspend fun updateCart(cartId: Int, cart: Cart): Resource<Cart> {
        return try {
            val now = System.currentTimeMillis()
            // Update cache (write-through)
            cartDao.insertCart(cart.toEntity(needsSync = true).copy(lastUpdated = now))
            val updatedCart = cartApi.updateCart(cartId, cart)
            cartDao.markCartAsSynced(updatedCart.id)
            Resource.Success(updatedCart)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update cart")
        }
    }

    override suspend fun deleteCart(cartId: Int): Resource<Cart> {
        return try {
            cartDao.deleteCartById(cartId)
            val deleted = cartApi.deleteCart(cartId)
            Resource.Success(deleted)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete cart")
        }
    }
}
