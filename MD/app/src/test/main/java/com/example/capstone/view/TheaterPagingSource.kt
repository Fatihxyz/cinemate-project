package com.example.capstone.data.source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.capstone.data.remote.ApiService
import com.example.capstone.model.remote.Theater
import com.example.capstone.model.remote.TheaterRequest
import com.example.capstone.model.remote.TheaterResponse
import retrofit2.HttpException
import java.io.IOException

class TheaterPagingSource(
    private val apiService: ApiService,
    private val location: String? = null
) : PagingSource<Int, Theater>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Theater> {
        return try {
            val page = params.key ?: 1
            val response = if (location == null) {
                apiService.getTheaters(page)
            } else {
                apiService.getTheatersLocation(TheaterRequest(location, page))
            }

            Log.d(TAG, "Loading page: $page, response code: ${response.code()}")

            val theaterResponse: TheaterResponse? = response.body()
            val theaters: List<Theater> = theaterResponse?.theaters?.map { theaterData ->
                Theater(
                    name = theaterData.theatername
                )
            } ?: emptyList()

            LoadResult.Page(
                data = theaters,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (theaters.isEmpty()) null else page + 1
            )
        } catch (exception: IOException) {
            Log.e(TAG, "IOException occurred: ${exception.message}", exception)
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            Log.e(TAG, "HttpException occurred: HTTP ${exception.code()}, ${exception.message}", exception)
            LoadResult.Error(exception)
        } catch (exception: Exception) {
            Log.e(TAG, "Unexpected error occurred: ${exception.message}", exception)
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Theater>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val TAG = "TheaterPagingSource"
    }
}
