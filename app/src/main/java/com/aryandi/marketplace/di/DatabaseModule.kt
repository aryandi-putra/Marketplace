package com.aryandi.marketplace.di

import android.content.Context
import androidx.room.Room
import com.aryandi.marketplace.data.local.MarketplaceDatabase
import com.aryandi.marketplace.data.local.dao.CartDao
import com.aryandi.marketplace.data.local.dao.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMarketplaceDatabase(
        @ApplicationContext context: Context
    ): MarketplaceDatabase {
        return Room.databaseBuilder(
            context,
            MarketplaceDatabase::class.java,
            MarketplaceDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()  // For development: recreate DB on schema change
            .build()
    }

    @Provides
    @Singleton
    fun provideProductDao(database: MarketplaceDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    @Singleton
    fun provideCartDao(database: MarketplaceDatabase): CartDao {
        return database.cartDao()
    }
}
