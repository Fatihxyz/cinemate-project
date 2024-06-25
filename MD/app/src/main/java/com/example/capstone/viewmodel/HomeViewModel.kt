package com.example.capstone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.capstone.data.repo.MovieRepository
import com.example.capstone.data.local.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class HomeViewModel(private val repository: MovieRepository) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    val movies: Flow<PagingData<com.example.capstone.model.remote.Movie>> = searchQuery.flatMapLatest { query ->
        repository.searchMovies(query).cachedIn(viewModelScope)
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }
}