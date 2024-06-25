package com.example.capstone.data.remote

import com.example.capstone.model.remote.*
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("movies/ongoing")
    suspend fun getOngoingMovies(
        @Query("theaterId") theaterId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): OngoingMoviesResponse

    @POST("search-film")
    suspend fun searchMovies(
        @Body query: Map<String, String>,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<MovieResponse>

    @GET("theater")
    suspend fun getTheaters(
        @Query("page") page: Int
    ): Response<TheaterResponse>

    @POST("theater")
    suspend fun getTheatersLocation(
        @Body request: TheaterRequest
    ): Response<TheaterResponse>

    @GET("detail")
    suspend fun getMovieDetail(
        @Query("movieid") movieid: String
    ): Response<MovieDetailResponse>

    @POST("insert-rating")
    suspend fun insertRating(
        @Body ratingRequest: RatingRequest
    ): Response<RatingResponse>

    @GET("get-rating/{movieid}")
    suspend fun getRatings(
        @Path("movieid") movieId: Int
    ): Response<List<RatingResponse>>

    @GET("average-rating/{movieid}")
    suspend fun getAverageRating(
        @Path("movieid") movieid: Int
    ): Response<AverageRatingResponse>

    @POST("buyticket")
    suspend fun buyTicket(
        @Body ticketRequest: RequestBody
    ): Response<TicketResponse>

    @POST("/getTicket")
    suspend fun getTickets(@Body requestBody: Map<String, Int>): Response<List<TicketRequest>>

    @PUT("update/{id}")
    suspend fun updateUser(@Path("id") userId: Int, @Body requestBody: Map<String, String>): Response<Void>

    @GET("getPurchasedSeats")
    suspend fun getPurchasedSeats(
        @Query("movieid") movieId: Int,
        @Query("show_time") showTime: String
    ): Response<List<String>>

}
