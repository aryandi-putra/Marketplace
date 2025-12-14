package com.aryandi.marketplace.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryandi.marketplace.data.model.Cart
import com.aryandi.marketplace.data.model.CartProduct
import com.aryandi.marketplace.domain.usecase.DeleteCartUseCase
import com.aryandi.marketplace.domain.usecase.GetCartUseCase
import com.aryandi.marketplace.domain.usecase.UpdateCartUseCase
import com.aryandi.marketplace.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val updateCartUseCase: UpdateCartUseCase,
    private val deleteCartUseCase: DeleteCartUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state = _state.asStateFlow()

    val intentChannel = Channel<CartIntent>(Channel.UNLIMITED)

    private var currentUserId: Int? = null
    private var currentCartId: Int? = null

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
                        // Store cart ID for future updates (using userId as cartId for simplicity)
                        currentCartId = userId
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
        // Update local state first for instant feedback
        val updatedItems = _state.value.items.map { item ->
            if (item.product.id == productId) {
                item.copy(quantity = quantity)
            } else {
                item
            }
        }
        _state.value = _state.value.copy(items = updatedItems)

        // Sync with API
        syncCartToApi()
    }

    private fun removeItem(productId: Int) {
        // Update local state first for instant feedback
        val updatedItems = _state.value.items.filter { it.product.id != productId }
        _state.value = _state.value.copy(items = updatedItems)

        // If cart is now empty, delete it via API
        if (updatedItems.isEmpty()) {
            deleteCartViaApi()
        } else {
            // Otherwise sync remaining items to API
            syncCartToApi()
        }
    }

    private fun syncCartToApi() {
        val cartId = currentCartId ?: return
        val userId = currentUserId ?: return

        // Convert current CartItems to Cart format for API
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cart = Cart(
            id = cartId,
            userId = userId,
            date = dateFormat.format(Date()),
            products = _state.value.items.map { item ->
                CartProduct(
                    productId = item.product.id,
                    quantity = item.quantity
                )
            }
        )

        viewModelScope.launch {
            updateCartUseCase(cartId, cart).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // Cart successfully updated on server
                        // Local state is already updated, so no UI change needed
                    }

                    is Resource.Error -> {
                        // Silently handle error - local state already updated
                        // Could show a snackbar message here if desired
                    }

                    is Resource.Loading -> {
                        // Optional: show a subtle loading indicator
                    }
                }
            }
        }
    }

    private fun deleteCartViaApi() {
        val cartId = currentCartId ?: return

        viewModelScope.launch {
            deleteCartUseCase(cartId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // Cart successfully deleted on server
                        currentCartId = null
                    }

                    is Resource.Error -> {
                        // Silently handle error
                    }

                    is Resource.Loading -> {
                        // Optional: show loading indicator
                    }
                }
            }
        }
    }

    fun sendIntent(intent: CartIntent) {
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }
}
