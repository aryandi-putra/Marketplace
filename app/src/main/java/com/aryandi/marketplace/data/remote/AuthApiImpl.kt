package com.aryandi.marketplace.data.remote

import com.aryandi.marketplace.data.model.LoginRequest
import com.aryandi.marketplace.data.model.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class AuthApiImpl @Inject constructor(
    private val client: HttpClient
) : AuthApi {

    companion object {
        private const val BASE_URL = "https://fakestoreapi.com"
    }

    override suspend fun login(loginRequest: LoginRequest): LoginResponse {
        return client.post("$BASE_URL/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(loginRequest)
        }.body()
    }
}
