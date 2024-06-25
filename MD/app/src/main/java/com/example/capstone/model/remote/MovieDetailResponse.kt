package com.example.capstone.model.remote

data class MovieDetailResponse(
    val movie: List<List<String>>,
    val average_rating: Float?,
    val rating: List<List<String>>
)

data class UserRating(
    val username: String,
    val rating: Float
)