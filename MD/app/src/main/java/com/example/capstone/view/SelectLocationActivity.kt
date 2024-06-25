package com.example.capstone.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.capstone.R
import com.example.capstone.model.remote.TheaterRequest
import com.example.capstone.data.remote.ApiService
import com.example.capstone.databinding.ActivitySelectLocationBinding
import com.example.capstone.data.remote.ApiClient
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SelectLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectLocationBinding
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.retrofit.create(ApiService::class.java)

        binding.buttonJakarta.setOnClickListener { submitLocation("JAKARTA") }
        binding.buttonAmbon.setOnClickListener { submitLocation("AMBON") }
        binding.buttonPalembang.setOnClickListener { submitLocation("PALEMBANG") }
        binding.buttonBandung.setOnClickListener { submitLocation("BANDUNG") }
        binding.buttonSurabaya.setOnClickListener { submitLocation("SURABAYA") }
        val buttonClose = findViewById<ImageButton>(R.id.buttonClose)
        buttonClose.setOnClickListener {
            finishWithFade()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishWithFade()
            }
        })
    }

    private fun submitLocation(location: String) {
        lifecycleScope.launch {
            try {
                val response = apiService.getTheatersLocation(TheaterRequest(location, 1))
                if (response.isSuccessful) {
                    val resultIntent = Intent().apply {
                        putExtra("location", location)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finishWithFade()
                } else {
                    Toast.makeText(this@SelectLocationActivity, "Failed to submit location", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                Toast.makeText(this@SelectLocationActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@SelectLocationActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun finishWithFade() {
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
