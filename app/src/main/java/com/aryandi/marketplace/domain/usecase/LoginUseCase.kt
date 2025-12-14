package com.aryandi.marketplace.domain.usecase

import com.aryandi.marketplace.data.model.LoginResponse
import com.aryandi.marketplace.data.repository.AuthRepository
import com.aryandi.marketplace.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(username: String, password: String): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading())
        val result = repository.login(username, password)
        emit(result)
    }
}
