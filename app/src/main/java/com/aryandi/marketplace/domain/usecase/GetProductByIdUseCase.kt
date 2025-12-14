package com.aryandi.marketplace.domain.usecase

import com.aryandi.marketplace.data.model.Product
import com.aryandi.marketplace.data.repository.ProductRepository
import com.aryandi.marketplace.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(productId: Int): Flow<Resource<Product>> = flow {
        emit(Resource.Loading())
        val result = repository.getProductById(productId)
        emit(result)
    }
}
