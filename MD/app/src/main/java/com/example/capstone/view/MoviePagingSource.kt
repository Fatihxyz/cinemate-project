package com.example.capstone.view

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.capstone.data.remote.ApiService
import com.example.capstone.model.remote.Movie
import retrofit2.HttpException
import java.io.IOException
import android.util.Log

class MoviePagingSource(
    private val apiService: ApiService,
    private val query: String
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val page = params.key ?: 1
            val limit = params.loadSize
            Log.d("MoviePagingSource", "Requesting page: $page with limit: $limit")
            val response = apiService.searchMovies(mapOf("title" to query), page, limit)
            if (response.isSuccessful) {
                val movieResponse = response.body()
                val movies = movieResponse?.movies ?: emptyList()
                Log.d("MoviePagingSource", "Movies on page $page: ${movies.size}")
                LoadResult.Page(
                    data = movies,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (movies.isEmpty()) null else page + 1
                )
            } else {
                Log.e("MoviePagingSource", "Failed to load data: ${response.message()}")
                LoadResult.Error(HttpException(response))
            }
        } catch (exception: IOException) {
            Log.e("MoviePagingSource", "IOException", exception)
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            Log.e("MoviePagingSource", "HttpException", exception)
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
