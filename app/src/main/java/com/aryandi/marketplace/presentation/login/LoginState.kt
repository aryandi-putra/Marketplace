package com.aryandi.marketplace.presentation.login

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false
)
