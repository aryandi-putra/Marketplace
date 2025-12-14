package com.aryandi.marketplace.di

import com.aryandi.marketplace.data.remote.AuthApi
import com.aryandi.marketplace.data.remote.AuthApiImpl
import com.aryandi.marketplace.data.remote.CartApi
import com.aryandi.marketplace.data.remote.CartApiImpl
import com.aryandi.marketplace.data.remote.ProductApi
import com.aryandi.marketplace.data.remote.ProductApiImpl
import com.aryandi.marketplace.data.remote.UserApi
import com.aryandi.marketplace.data.remote.UserApiImpl
import com.aryandi.marketplace.data.repository.AuthRepository
import com.aryandi.marketplace.data.repository.AuthRepositoryImpl
import com.aryandi.marketplace.data.repository.CartRepository
import com.aryandi.marketplace.data.repository.CartRepositoryImpl
import com.aryandi.marketplace.data.repository.ProductRepository
import com.aryandi.marketplace.data.repository.ProductRepositoryImpl
import com.aryandi.marketplace.data.repository.UserRepository
import com.aryandi.marketplace.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthApi(
        authApiImpl: AuthApiImpl
    ): AuthApi

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProductApi(
        productApiImpl: ProductApiImpl
    ): ProductApi

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCartApi(
        cartApiImpl: CartApiImpl
    ): CartApi

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindUserApi(
        userApiImpl: UserApiImpl
    ): UserApi

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}
