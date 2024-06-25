package com.example.capstone.data.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.capstone.model.remote.ErrorResponse
import com.example.capstone.model.remote.LoginResponse
import com.example.capstone.model.remote.LoginResult
import com.example.capstone.data.remote.UserService
import com.example.capstone.util.wrapEspressoIdlingResource
import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.Response

class UserRepository private constructor(
    private var userService: UserService,
    private val pref: UserPreferences,
) {
    fun loginUser(username: String, password: String): LiveData<Result<LoginResult>> = liveData {
        emit(Result.Loading)
        Log.d("UserRepository", "Login request started for username: $username")

        wrapEspressoIdlingResource {
            try {
                val response: Response<LoginResponse> = userService.login(username, password)
                Log.d("UserRepository", "Received response for login: $response")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("UserRepository", "Response body: $responseBody")

                    if (responseBody != null && responseBody.error == false) {
                        val loginResult = LoginResult(
                            userId = responseBody.userId.toString(),
                            token = responseBody.accessToken
                        )
                        Log.d("UserRepository", "Login successful for username: $username")
                        emit(Result.Success(loginResult))
                    } else {
                        val message = responseBody?.message ?: "Unknown error"
                        Log.e("UserRepository", "Login failed: $message")
                        emit(Result.Error(message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    Log.e("UserRepository", "HttpException: ${errorResponse.message}")
                    emit(Result.Error(errorResponse.message.toString()))
                }
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                Log.e("UserRepository", "HttpException: ${errorBody.message}")
                emit(Result.Error(errorBody.message.toString()))
            } catch (e: Exception) {
                Log.e("UserRepository", "Exception: ${e.message}")
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun registerUser(name: String, email: String, password: String): LiveData<Result<LoginResult>> = liveData {
        emit(Result.Loading)
        Log.d("UserRepository", "Register request started for email: $email")

        wrapEspressoIdlingResource {
            try {
                val response: Response<LoginResponse> = userService.register(name, email, password)
                Log.d("UserRepository", "Received response for register: $response")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("UserRepository", "Response body: $responseBody")

                    if (responseBody != null && responseBody.error == false) {
                        val loginResult = LoginResult(
                            userId = responseBody.userId.toString(),
                            token = responseBody.accessToken
                        )
                        Log.d("UserRepository", "Register successful for email: $email")
                        emit(Result.Success(loginResult))
                    } else {
                        val message = responseBody?.message ?: "Unknown error"
                        Log.e("UserRepository", "Register failed: $message")
                        emit(Result.Error(message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    Log.e("UserRepository", "HttpException: ${errorResponse.message}")
                    emit(Result.Error(errorResponse.message.toString()))
                }
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                Log.e("UserRepository", "HttpException: ${errorBody.message}")
                emit(Result.Error(errorBody.message.toString()))
            } catch (e: Exception) {
                Log.e("UserRepository", "Exception: ${e.message}")
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(userService: UserService, pref: UserPreferences): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userService, pref)
            }.also { instance = it }
    }
}
