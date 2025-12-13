package com.aryandi.marketplace.data.repository

import com.aryandi.marketplace.data.model.LoginRequest
import com.aryandi.marketplace.data.model.LoginResponse
import com.aryandi.marketplace.data.remote.AuthApi
import com.aryandi.marketplace.util.Resource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun login(username: String, password: String): Resource<LoginResponse> {
        return try {
            val response = authApi.login(LoginRequest(username, password))
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred")
        }
    }
}
