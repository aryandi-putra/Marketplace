package com.aryandi.marketplace.domain.usecase

import com.aryandi.marketplace.data.model.CartItem
import com.aryandi.marketplace.data.repository.CartRepository
import com.aryandi.marketplace.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    operator fun invoke(userId: Int): Flow<Resource<List<CartItem>>> = flow {
        emit(Resource.Loading())
        val result = repository.getUserCart(userId)
        emit(result)
    }
}
