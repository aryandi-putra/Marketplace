package com.aryandi.marketplace.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.aryandi.marketplace.data.local.entity.CartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM carts WHERE userId = :userId ORDER BY lastUpdated DESC LIMIT 1")
    fun getUserCartFlow(userId: Int): Flow<CartEntity?>

    @Query("SELECT * FROM carts WHERE userId = :userId ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getUserCart(userId: Int): CartEntity?

    @Query("SELECT * FROM carts WHERE id = :cartId")
    suspend fun getCartById(cartId: Int): CartEntity?

    @Query("SELECT * FROM carts WHERE needsSync = 1")
    suspend fun getCartsNeedingSync(): List<CartEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(cart: CartEntity): Long

    @Update
    suspend fun updateCart(cart: CartEntity)

    @Query("DELETE FROM carts WHERE id = :cartId")
    suspend fun deleteCartById(cartId: Int)

    @Query("DELETE FROM carts WHERE userId = :userId")
    suspend fun deleteUserCarts(userId: Int)

    @Query("DELETE FROM carts")
    suspend fun deleteAllCarts()

    @Query("UPDATE carts SET needsSync = 0 WHERE id = :cartId")
    suspend fun markCartAsSynced(cartId: Int)

    @Query("UPDATE carts SET needsSync = 1 WHERE id = :cartId")
    suspend fun markCartAsNeedingSync(cartId: Int)

    @Query("SELECT COUNT(*) FROM carts WHERE userId = :userId")
    suspend fun getCartCount(userId: Int): Int

    @Transaction
    suspend fun cleanupOldCarts(userId: Int) {
        val latestCart = getUserCart(userId)
        if (latestCart != null) {
            // Delete all carts except the latest one
            deleteUserCarts(userId)
            insertCart(latestCart)
        }
    }
}
