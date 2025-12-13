package com.aryandi.marketplace.presentation.login

sealed class LoginEffect {
    data class NavigateToProducts(val token: String) : LoginEffect()
    data class ShowError(val message: String) : LoginEffect()
}
