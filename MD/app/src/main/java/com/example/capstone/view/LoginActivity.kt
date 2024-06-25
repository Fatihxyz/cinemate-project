package com.example.capstone.view

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.capstone.data.repo.UserPreferences
import com.example.capstone.databinding.ActivityLoginBinding
import com.example.capstone.data.remote.ApiClient
import com.example.capstone.data.remote.UserService
import com.example.capstone.model.remote.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val TAG = "LoginActivity"
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences.getInstance(this)

        binding.signInButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            Log.d(TAG, "Attempting to login with username: $username")
            loginUserWithUsername(username, password)
        }

        binding.registerTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        lifecycleScope.launch {
            val userId = userPreferences.userId.firstOrNull()
            if (userId != null) {
                Log.d(TAG, "Current logged in userId: $userId")
            } else {
                Log.d(TAG, "No userId found in preferences")
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToWelcomeActivity()
            }
        })
    }

    private fun loginUserWithUsername(username: String, password: String) {
        val userService = ApiClient.retrofit.create(UserService::class.java)
        Log.d(TAG, "API call to login user: $username")
        lifecycleScope.launch {
            try {
                val response = userService.login(username, password)
                Log.d(TAG, "Received response for login: $response")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d(TAG, "Response body: $responseBody")

                    if (responseBody != null && responseBody.userId != null && responseBody.accessToken != null) {
                        Log.d(TAG, "Login successful: $responseBody")
                        Toast.makeText(this@LoginActivity, responseBody.message ?: "Login successful", Toast.LENGTH_SHORT).show()
                        userPreferences.saveLoginStatus(true)
                        userPreferences.saveUserId(responseBody.userId.toString())
                        Log.d(TAG, "Logged in userId: ${responseBody.userId}")
                        startCountdownToMainActivity()
                    } else {
                        val message = responseBody?.message ?: "Unknown error"
                        Log.e(TAG, message)
                        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    Log.e(TAG, errorResponse.message ?: "Unknown error")
                    Toast.makeText(this@LoginActivity, errorResponse.message ?: "Unknown error", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message ?: "Login failed")
                Toast.makeText(this@LoginActivity, e.message ?: "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startCountdownToMainActivity() {
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.signInButton.text = "Redirecting in ${millisUntilFinished / 1000}s"
                binding.signInButton.isEnabled = false
            }

            override fun onFinish() {
                navigateToMainActivity()
            }
        }.start()
    }


    companion object {
        private const val TAG = "LoginActivity"
    }
}
