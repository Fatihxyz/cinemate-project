package com.example.capstone.data.local

data class Review(
    val userId: Int,
    val movieId: Int,
    val rating: Float,
    val timestamp: Long,
    val username: String
)
