package com.example.capstone.view

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.capstone.databinding.ActivityRegisterBinding
import com.example.capstone.model.remote.ErrorResponse
import com.example.capstone.model.remote.LoginResponse
import com.example.capstone.data.remote.ApiClient
import com.example.capstone.data.remote.UserService
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val userService: UserService by lazy {
        ApiClient.retrofit.create(UserService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (isValidEmail(email)) {
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    registerUser(username, email, password)
                } else {
                    Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToWelcome()
            }
        })
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun registerUser(username: String, email: String, password: String) {
        lifecycleScope.launch {
            try {
                val response: Response<LoginResponse> = userService.register(username, email, password)
                Log.d("RegisterActivity", "Response received: $response")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val message = responseBody?.message ?: "Unknown error"
                    Log.d("RegisterActivity", "Response body: $responseBody")

                    if (response.code() == 201) {
                        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
                        startCountdownToLogin()
                    } else {
                        Log.e("RegisterActivity", message)
                        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    val message = errorResponse.message ?: "Unknown error"
                    Log.e("RegisterActivity", message)
                    Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val message = errorBody.message ?: "Unknown error"
                Log.e("RegisterActivity", message)
                Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                val message = e.message ?: "Unknown error"
                Log.e("RegisterActivity", message)
                Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCountdownToLogin() {
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.registerButton.text = "Redirecting in ${millisUntilFinished / 1000}s"
                binding.registerButton.isEnabled = false
            }

            override fun onFinish() {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.start()
    }

    private fun navigateToWelcome() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
