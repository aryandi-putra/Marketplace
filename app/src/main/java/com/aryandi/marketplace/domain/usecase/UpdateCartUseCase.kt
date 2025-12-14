package com.aryandi.marketplace.domain.usecase

import com.aryandi.marketplace.data.model.Cart
import com.aryandi.marketplace.data.repository.CartRepository
import com.aryandi.marketplace.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    operator fun invoke(cartId: Int, cart: Cart): Flow<Resource<Cart>> = flow {
        emit(Resource.Loading())
        val result = repository.updateCart(cartId, cart)
        emit(result)
    }
}
