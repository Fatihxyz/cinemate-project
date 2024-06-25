package com.example.capstone.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.core.app.ActivityOptionsCompat
import com.example.capstone.R
import com.example.capstone.data.repo.UserPreferences
import com.example.capstone.data.remote.ApiService
import com.example.capstone.data.remote.ApiClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Response

class UpdateUserActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiClient.retrofit.create(ApiService::class.java)
    }

    private lateinit var updateUsernameEditText: EditText
    private lateinit var updateEmailEditText: EditText
    private lateinit var currentPasswordEditText: EditText
    private lateinit var updatePasswordEditText: EditText
    private lateinit var updateUserButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user)

        updateUsernameEditText = findViewById(R.id.updateUsernameEditText)
        updateEmailEditText = findViewById(R.id.updateEmailEditText)
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText)
        updatePasswordEditText = findViewById(R.id.updatePasswordEditText)
        updateUserButton = findViewById(R.id.updateUserButton)

        lifecycleScope.launch {
            val userId = UserPreferences.getInstance(this@UpdateUserActivity).userId.first()
            if (userId != null) {
                updateUserButton.setOnClickListener {
                    if (validateInputs()) {
                        updateUser(userId.toInt())
                    }
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@UpdateUserActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                }
                val options = ActivityOptionsCompat.makeCustomAnimation(
                    this@UpdateUserActivity,
                    R.anim.slide_in_left, R.anim.slide_out_right
                )
                startActivity(intent, options.toBundle())
                finishAfterTransition()
            }
        })
    }

    private fun validateInputs(): Boolean {
        val email = updateEmailEditText.text.toString()
        return if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun updateUser(userId: Int) {
        val username = updateUsernameEditText.text.toString()
        val email = updateEmailEditText.text.toString()
        val currentPassword = currentPasswordEditText.text.toString()
        val newPassword = updatePasswordEditText.text.toString()

        lifecycleScope.launch {
            try {
                val requestBody = mapOf(
                    "username" to username,
                    "email" to email,
                    "currentPassword" to currentPassword,
                    "password" to newPassword
                )
                val response: Response<Void> = apiService.updateUser(userId, requestBody)

                if (response.isSuccessful) {
                    Toast.makeText(this@UpdateUserActivity, "User updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@UpdateUserActivity, "Update failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@UpdateUserActivity, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
