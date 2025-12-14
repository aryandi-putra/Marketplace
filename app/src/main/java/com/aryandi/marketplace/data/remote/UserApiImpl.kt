package com.aryandi.marketplace.data.remote

import com.aryandi.marketplace.data.model.User
import com.aryandi.marketplace.util.ApiConstants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class UserApiImpl @Inject constructor(
    private val client: HttpClient
) : UserApi {

    override suspend fun getUser(userId: Int): User {
        return client.get("${ApiConstants.BASE_URL}/users/$userId").body()
    }
}
