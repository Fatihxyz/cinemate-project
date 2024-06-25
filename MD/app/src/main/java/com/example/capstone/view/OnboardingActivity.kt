package com.example.capstone.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.capstone.data.repo.UserPreferences
import com.example.capstone.databinding.ActivityOnboardingBinding
import kotlinx.coroutines.launch

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        userPreferences = UserPreferences.getInstance(this)

        binding.btnSkip.setOnClickListener {
            lifecycleScope.launch {
                userPreferences.saveOnboardingStatus(true)
                startActivity(Intent(this@OnboardingActivity, WelcomeActivity::class.java))
                finish()
            }
        }

        binding.btnStart.setOnClickListener {
            lifecycleScope.launch {
                userPreferences.saveOnboardingStatus(true)
                startActivity(Intent(this@OnboardingActivity, WelcomeActivity::class.java))
                finish()
            }
        }
    }
}
