package com.aryandi.marketplace.presentation.login

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryandi.marketplace.domain.usecase.LoginUseCase
import com.aryandi.marketplace.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    val intentChannel = Channel<LoginIntent>(Channel.UNLIMITED)

    init {
        handleIntents()
    }

    private fun handleIntents() {
        viewModelScope.launch {
            intentChannel.consumeAsFlow().collect { intent ->
                when (intent) {
                    is LoginIntent.Login -> login(intent.username, intent.password)
                    is LoginIntent.UpdateUsername -> updateUsername(intent.username)
                    is LoginIntent.UpdatePassword -> updatePassword(intent.password)
                }
            }
        }
    }

    private fun login(username: String, password: String) {
        loginUseCase(username, password).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }

                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _effect.send(LoginEffect.NavigateToProducts(result.data?.token ?: ""))
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _effect.send(LoginEffect.ShowError(result.message ?: "Unknown error"))
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun updateUsername(username: String) {
        _state.value = _state.value.copy(username = username)
    }

    private fun updatePassword(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun sendIntent(intent: LoginIntent) {
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }
}
