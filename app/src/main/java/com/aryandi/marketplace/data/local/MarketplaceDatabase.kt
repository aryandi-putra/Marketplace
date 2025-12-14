package com.aryandi.marketplace.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aryandi.marketplace.data.local.dao.CartDao
import com.aryandi.marketplace.data.local.dao.ProductDao
import com.aryandi.marketplace.data.local.entity.CartEntity
import com.aryandi.marketplace.data.local.entity.CartProductListConverter
import com.aryandi.marketplace.data.local.entity.ProductEntity

@Database(
    entities = [
        ProductEntity::class,
        CartEntity::class
    ],
    version = 2,  // Increment version for schema change
    exportSchema = false
)
@TypeConverters(CartProductListConverter::class)
abstract class MarketplaceDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao

    companion object {
        const val DATABASE_NAME = "marketplace_database"
    }
}
