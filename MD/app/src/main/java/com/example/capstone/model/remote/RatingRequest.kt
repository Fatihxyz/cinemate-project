package com.example.capstone.model.remote

data class RatingRequest(
    val userid: Int,
    val movieid: Int,
    val rating: Float
)
