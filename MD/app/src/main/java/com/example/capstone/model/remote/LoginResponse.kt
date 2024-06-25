package com.example.capstone.model.remote

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("userId")
    val userId: Int?,

    @SerializedName("accessToken")
    val accessToken: String?,

    @SerializedName("error")
    val error: Boolean?,

    @SerializedName("message")
    val message: String?
)

data class LoginResult(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("userId")
    val userId: String? = null,

    @SerializedName("token")
    val token: String? = null
)
