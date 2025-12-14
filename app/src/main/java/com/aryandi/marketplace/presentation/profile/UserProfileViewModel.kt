package com.aryandi.marketplace.presentation.profile

import androidx.lifecycle.ViewModel
import com.aryandi.marketplace.data.model.User
import com.aryandi.marketplace.data.repository.UserRepository
import com.aryandi.marketplace.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    suspend fun getUser(userId: Int): Resource<User> {
        return userRepository.getUser(userId)
    }
}
