package com.example.capstone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.capstone.adapter.OngoingMoviesAdapter
import com.example.capstone.data.repo.OngoingMoviesRepository
import com.example.capstone.model.remote.Movie
import kotlinx.coroutines.flow.*

class OngoingMoviesViewModel(
    private val repository: OngoingMoviesRepository,
    theaterId: Int,
    private val onClick: (Movie) -> Unit
) : ViewModel() {

    val movies: Flow<PagingData<Movie>> = repository.getOngoingMoviesPagingSource(theaterId)
        .cachedIn(viewModelScope)

    val movieAdapter: OngoingMoviesAdapter = OngoingMoviesAdapter(onClick)
}
