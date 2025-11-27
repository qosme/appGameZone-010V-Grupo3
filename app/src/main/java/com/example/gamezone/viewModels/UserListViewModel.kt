package com.example.gamezone.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamezone.data.User
import com.example.gamezone.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val users: StateFlow<List<User>> = userRepository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            userRepository.fetchAndSaveApiUsers()
        }
    }


    fun setAdminStatus(email: String, isAdmin: Boolean) {
        viewModelScope.launch {
            val result = if (isAdmin) {
                userRepository.makeUserAdmin(email)
            } else {
                userRepository.removeUserAdmin(email)
            }

            if (result) {

                userRepository.fetchAndSaveApiUsers()
            }
        }
    }

}