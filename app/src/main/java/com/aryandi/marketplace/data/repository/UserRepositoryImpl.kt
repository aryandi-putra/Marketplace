package com.aryandi.marketplace.data.repository

import com.aryandi.marketplace.data.model.User
import com.aryandi.marketplace.data.remote.UserApi
import com.aryandi.marketplace.util.Resource
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {

    override suspend fun getUser(userId: Int): Resource<User> {
        return try {
            val user = userApi.getUser(userId)
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch user profile")
        }
    }
}
