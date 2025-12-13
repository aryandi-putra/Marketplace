package com.aryandi.marketplace.di

import com.aryandi.marketplace.data.remote.AuthApi
import com.aryandi.marketplace.data.remote.AuthApiImpl
import com.aryandi.marketplace.data.repository.AuthRepository
import com.aryandi.marketplace.data.repository.AuthRepositoryImpl
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
}
