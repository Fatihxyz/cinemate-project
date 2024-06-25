package com.example.capstone.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.capstone.model.remote.Theater
import com.example.capstone.data.remote.ApiService
import com.example.capstone.data.source.TheaterPagingSource
import kotlinx.coroutines.flow.Flow

class TheaterRepository(private val apiService: ApiService) {

    fun getTheaters(): Flow<PagingData<Theater>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TheaterPagingSource(apiService) }
        ).flow
    }

    fun getTheatersLocation(location: String): Flow<PagingData<Theater>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TheaterPagingSource(apiService, location) }
        ).flow
    }
}
