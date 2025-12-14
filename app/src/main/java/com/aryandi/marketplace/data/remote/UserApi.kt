package com.aryandi.marketplace.data.remote

import com.aryandi.marketplace.data.model.User

interface UserApi {
    suspend fun getUser(userId: Int): User
}
