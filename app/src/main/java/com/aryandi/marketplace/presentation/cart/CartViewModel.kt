package com.aryandi.marketplace.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryandi.marketplace.domain.usecase.GetCartUseCase
import com.aryandi.marketplace.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state = _state.asStateFlow()

    val intentChannel = Channel<CartIntent>(Channel.UNLIMITED)

    private var currentUserId: Int? = null

    init {
        handleIntents()
    }

    private fun handleIntents() {
        viewModelScope.launch {
            intentChannel.consumeAsFlow().collect { intent ->
                when (intent) {
                    is CartIntent.LoadCart -> loadCart(intent.userId)
                    is CartIntent.Retry -> currentUserId?.let { loadCart(it) }
                    is CartIntent.UpdateQuantity -> updateQuantity(
                        intent.productId,
                        intent.quantity
                    )

                    is CartIntent.RemoveItem -> removeItem(intent.productId)
                }
            }
        }
    }

    private fun loadCart(userId: Int) {
        currentUserId = userId
        viewModelScope.launch {
            getCartUseCase(userId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }

                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            items = resource.data ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = resource.message
                        )
                    }
                }
            }
        }
    }

    private fun updateQuantity(productId: Int, quantity: Int) {
        val updatedItems = _state.value.items.map { item ->
            if (item.product.id == productId) {
                item.copy(quantity = quantity)
            } else {
                item
            }
        }
        _state.value = _state.value.copy(items = updatedItems)
    }

    private fun removeItem(productId: Int) {
        val updatedItems = _state.value.items.filter { it.product.id != productId }
        _state.value = _state.value.copy(items = updatedItems)
    }

    fun sendIntent(intent: CartIntent) {
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }
}
