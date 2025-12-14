package com.aryandi.marketplace.data.repository

import com.aryandi.marketplace.data.model.User
import com.aryandi.marketplace.util.Resource

interface UserRepository {
    suspend fun getUser(userId: Int): Resource<User>
}
