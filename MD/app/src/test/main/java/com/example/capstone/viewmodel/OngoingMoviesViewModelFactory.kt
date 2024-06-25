package com.example.capstone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.capstone.data.repo.OngoingMoviesRepository
import com.example.capstone.model.remote.Movie

class OngoingMoviesViewModelFactory(
    private val repository: OngoingMoviesRepository,
    private val theaterId: Int,
    private val onClick: (Movie) -> Unit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OngoingMoviesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OngoingMoviesViewModel(repository, theaterId, onClick) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}