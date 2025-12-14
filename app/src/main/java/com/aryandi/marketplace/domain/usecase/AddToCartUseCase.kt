package com.aryandi.marketplace.domain.usecase

import com.aryandi.marketplace.data.model.Cart
import com.aryandi.marketplace.data.repository.CartRepository
import com.aryandi.marketplace.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    operator fun invoke(userId: Int, productId: Int, quantity: Int = 1): Flow<Resource<Cart>> =
        flow {
            emit(Resource.Loading())
            val result = repository.addToCart(userId, productId, quantity)
            emit(result)
        }
}
