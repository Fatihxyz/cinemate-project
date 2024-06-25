package com.example.capstone.model.remote

import com.google.gson.annotations.SerializedName

data class RatingResponse(
    @SerializedName("userid") val userid: Int,
    @SerializedName("movieid") val movieid: Int,
    @SerializedName("rating") val rating: Float,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("username") val username: String
)