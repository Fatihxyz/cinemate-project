package com.example.capstone.model.remote

data class Movie(
    val movieid: Int,
    val title: String,
    val genres: String
)

data class MovieResponse(
    val page: Int,
    val limit: Int,
    val movies: List<Movie>
)