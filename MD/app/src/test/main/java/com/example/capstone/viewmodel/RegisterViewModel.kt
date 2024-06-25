package com.example.capstone.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.capstone.data.repo.Result
import com.example.capstone.data.repo.UserRepository
import com.example.capstone.model.remote.LoginResult

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun registerUser(name: String, email: String, password: String): LiveData<Result<LoginResult>> {
        return userRepository.registerUser(name, email, password)
    }
}
