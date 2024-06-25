package com.example.capstone.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.capstone.view.OngoingMoviesPagingSource
import com.example.capstone.data.remote.ApiService
import com.example.capstone.model.remote.Movie
import kotlinx.coroutines.flow.Flow

class OngoingMoviesRepository(private val apiService: ApiService) {

    fun getOngoingMoviesPagingSource(theaterId: Int): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { OngoingMoviesPagingSource(apiService, theaterId) }
        ).flow
    }
}
