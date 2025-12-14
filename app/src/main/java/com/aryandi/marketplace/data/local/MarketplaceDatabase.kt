package com.aryandi.marketplace.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aryandi.marketplace.data.local.dao.ProductDao
import com.aryandi.marketplace.data.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MarketplaceDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        const val DATABASE_NAME = "marketplace_database"
    }
}
