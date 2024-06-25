package com.example.capstone.data.local

data class Movie(
    val movieId: Int,
    val title: String,
    val genres: String,
    var predictedRating: Float = 0f
)

