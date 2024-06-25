package com.example.capstone.model.remote


data class TicketRequest(
    val movieid: Int,
    val show_time: String,
    val purchase_time: String,
    val userid: Int,
    val ticketid: Int,
    val movie_title: String
)

data class TicketResponse(
    val message: String
)