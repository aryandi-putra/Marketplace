package com.aryandi.marketplace.presentation.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryandi.marketplace.domain.usecase.GetProductByIdUseCase
import com.aryandi.marketplace.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailState())
    val state = _state.asStateFlow()

    val intentChannel = Channel<ProductDetailIntent>(Channel.UNLIMITED)

    private var currentProductId: Int? = null

    init {
        handleIntents()
    }

    private fun handleIntents() {
        viewModelScope.launch {
            intentChannel.consumeAsFlow().collect { intent ->
                when (intent) {
                    is ProductDetailIntent.LoadProduct -> loadProduct(intent.productId)
                    is ProductDetailIntent.Retry -> currentProductId?.let { loadProduct(it) }
                }
            }
        }
    }

    private fun loadProduct(productId: Int) {
        currentProductId = productId
        viewModelScope.launch {
            getProductByIdUseCase(productId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }

                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            product = resource.data,
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

    fun sendIntent(intent: ProductDetailIntent) {
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }
}
