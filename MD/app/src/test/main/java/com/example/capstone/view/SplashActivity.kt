package com.example.capstone.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.example.capstone.R
import com.example.capstone.databinding.ActivitySplashBinding
import com.example.capstone.data.repo.UserPreferences
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        userPreferences = UserPreferences.getInstance(this)

        lifecycleScope.launch {
            userPreferences.isOnboardingCompleted.collect { isOnboardingCompleted ->
                if (isOnboardingCompleted) {
                    userPreferences.isLoggedIn.collect { isLoggedIn ->
                        val intent = if (isLoggedIn) {
                            Intent(this@SplashActivity, MainActivity::class.java)
                        } else {
                            Intent(this@SplashActivity, WelcomeActivity::class.java)
                        }
                        startActivity(intent, ActivityOptionsCompat.makeCustomAnimation(this@SplashActivity, R.anim.slide_in_right, R.anim.slide_out_left).toBundle())
                        finish()
                    }
                } else {
                    val intent = Intent(this@SplashActivity, OnboardingActivity::class.java)
                    startActivity(intent, ActivityOptionsCompat.makeCustomAnimation(this@SplashActivity, R.anim.slide_in_right, R.anim.slide_out_left).toBundle())
                    finish()
                }
            }
        }
    }
}
