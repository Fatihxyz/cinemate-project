package com.example.capstone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.capstone.data.repo.MovieRepository
import com.example.capstone.model.remote.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    private val queryFlow = MutableStateFlow("")

    val movies: Flow<PagingData<Movie>> = queryFlow
        .debounce(200)
        .flatMapLatest { query ->
            repository.searchMovies(query).cachedIn(viewModelScope)
        }

    fun setSearchQuery(query: String) {
        queryFlow.value = query
    }
}
