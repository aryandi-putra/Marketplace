package com.aryandi.marketplace.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aryandi.marketplace.data.local.entity.CartEntity

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertCart(cart: CartEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(carts: List<CartEntity>)

    @Update
    suspend fun updateCart(cart: CartEntity)

    @Delete
    suspend fun deleteCart(cart: CartEntity)

    @Query("SELECT * FROM carts")
    suspend fun getAllCarts(): List<CartEntity>

    @Query("SELECT * FROM carts WHERE id = :cartId LIMIT 1")
    suspend fun getCartById(cartId: Int): CartEntity?

    @Query("DELETE FROM carts")
    suspend fun clearCarts()
}