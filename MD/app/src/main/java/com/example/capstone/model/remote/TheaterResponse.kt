package com.example.capstone.model.remote

data class TheaterResponse(
    val location: String,
    val theaters: List<TheaterData>,
    val page: Int
)

data class TheaterData(
    val theatername: String
)

data class Theater(
    val name: String
)

data class TheaterRequest(
    val location: String,
    val page: Int
)
