package com.aryandi.marketplace.presentation.products

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryandi.marketplace.domain.usecase.GetCategoriesUseCase
import com.aryandi.marketplace.domain.usecase.GetProductsUseCase
import com.aryandi.marketplace.domain.usecase.GetProductsByCategoryUseCase
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
class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductsState())
    val state: StateFlow<ProductsState> = _state.asStateFlow()

    private val _effect = Channel<ProductsEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    val intentChannel = Channel<ProductsIntent>(Channel.UNLIMITED)

    init {
        handleIntents()
        sendIntent(ProductsIntent.LoadCategories)
        sendIntent(ProductsIntent.LoadProducts)
    }

    private fun handleIntents() {
        viewModelScope.launch {
            intentChannel.consumeAsFlow().collect { intent ->
                when (intent) {
                    is ProductsIntent.LoadProducts -> loadProducts()
                    is ProductsIntent.LoadMoreProducts -> loadMoreProducts()
                    is ProductsIntent.LoadCategories -> loadCategories()
                    is ProductsIntent.SelectCategory -> selectCategory(intent.category)
                    is ProductsIntent.Retry -> retry()
                }
            }
        }
    }

    private fun loadProducts() {
        val category = _state.value.selectedCategory

        if (category == null) {
            getProductsUseCase().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }

                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            products = result.data ?: emptyList(),
                            isLoading = false,
                            currentPage = 1
                        )
                    }

                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false)
                        _effect.send(ProductsEffect.ShowError(result.message ?: "Unknown error"))
                    }
                }
            }.launchIn(viewModelScope)
        } else {
            getProductsByCategoryUseCase(category).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }

                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            products = result.data ?: emptyList(),
                            isLoading = false,
                            currentPage = 1
                        )
                    }

                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false)
                        _effect.send(ProductsEffect.ShowError(result.message ?: "Unknown error"))
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun loadMoreProducts() {
        if (_state.value.isLoadingMore || !_state.value.hasMorePages) return

        _state.value = _state.value.copy(isLoadingMore = true)

        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            _state.value = _state.value.copy(
                isLoadingMore = false,
                hasMorePages = false
            )
        }
    }

    private fun loadCategories() {
        getCategoriesUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        categories = result.data ?: emptyList()
                    )
                }

                is Resource.Error -> {
                }

                is Resource.Loading -> {
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun selectCategory(category: String?) {
        _state.value = _state.value.copy(
            selectedCategory = category,
            products = emptyList(),
            currentPage = 1,
            hasMorePages = true
        )
        loadProducts()
    }

    private fun retry() {
        loadProducts()
    }

    fun sendIntent(intent: ProductsIntent) {
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }
}
