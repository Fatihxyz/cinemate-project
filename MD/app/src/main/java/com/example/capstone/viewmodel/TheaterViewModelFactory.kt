package com.example.capstone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.capstone.data.repo.TheaterRepository
import com.example.capstone.model.remote.Theater

class TheaterViewModelFactory(
    private val repository: TheaterRepository,
    private val initialLocation: String?,
    private val onClick: (Theater) -> Unit
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TheaterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TheaterViewModel(repository, initialLocation, onClick) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
