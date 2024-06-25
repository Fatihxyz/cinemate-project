package com.example.capstone.model.remote


data class OngoingMoviesResponse(
    val results: List<Movie>,
    val totalPages: Int,
    val totalResults: Int
)
