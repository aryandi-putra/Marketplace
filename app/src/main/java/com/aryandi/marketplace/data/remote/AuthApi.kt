package com.aryandi.marketplace.data.remote

import com.aryandi.marketplace.data.model.LoginRequest
import com.aryandi.marketplace.data.model.LoginResponse

interface AuthApi {
    suspend fun login(loginRequest: LoginRequest): LoginResponse
}
