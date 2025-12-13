package com.aryandi.marketplace.presentation.login

sealed class LoginIntent {
    data class Login(val username: String, val password: String) : LoginIntent()
    data class UpdateUsername(val username: String) : LoginIntent()
    data class UpdatePassword(val password: String) : LoginIntent()
    object ClearError : LoginIntent()
}
