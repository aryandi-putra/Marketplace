package com.aryandi.marketplace.domain.usecase

import com.aryandi.marketplace.data.model.Product
import com.aryandi.marketplace.data.repository.ProductRepository
import com.aryandi.marketplace.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetProductsByCategoryUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(category: String): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading())
        val result = repository.getProductsByCategory(category)
        emit(result)
    }
}
