package com.aryandi.marketplace.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aryandi.marketplace.data.local.dao.CartDao
import com.aryandi.marketplace.data.local.entity.CartEntity
import com.aryandi.marketplace.data.local.entity.CartProductListConverter

@Database(entities = [CartEntity::class], version = 1, exportSchema = false)
@TypeConverters(CartProductListConverter::class)
abstract class MarketplaceDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao

    companion object {
        const val DATABASE_NAME = "marketplace_database"
    }
}
