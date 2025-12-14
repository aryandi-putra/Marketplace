package com.aryandi.marketplace.data.repository

import com.aryandi.marketplace.data.model.LoginResponse
import com.aryandi.marketplace.util.Resource

interface AuthRepository {
    suspend fun login(username: String, password: String): Resource<LoginResponse>
}
