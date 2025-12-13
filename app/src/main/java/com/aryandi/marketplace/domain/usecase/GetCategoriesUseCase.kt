package com.aryandi.marketplace.domain.usecase

import com.aryandi.marketplace.data.repository.ProductRepository
import com.aryandi.marketplace.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<Resource<List<String>>> = flow {
        emit(Resource.Loading())
        val result = repository.getAllCategories()
        emit(result)
    }
}
