package com.example.capstone.view

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.capstone.R
import com.example.capstone.data.remote.ApiService
import com.example.capstone.model.remote.TicketResponse
import com.example.capstone.data.remote.ApiClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response

class PaymentActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiClient.retrofit.create(ApiService::class.java)
    }

    private var selectedSeat: String? = null
    private var selectedShowtime: String? = null
    private lateinit var seatButtons: List<Button>
    private lateinit var showtimeButtons: List<Button>
    private var purchasedSeats: List<String> = emptyList()
    private val totalSeats = listOf("A1", "A2", "A3", "A4", "A5", "B1", "B2", "B3", "B4", "B5", "C1", "C2", "C3", "C4", "C5", "D1", "D2", "D3", "D4", "D5", "E1", "E2", "E3", "E4", "E5")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val movieId = intent.getIntExtra("MOVIE_ID", -1)
        val movieTitle = intent.getStringExtra("MOVIE_TITLE")
        val movieGenres = intent.getStringExtra("MOVIE_GENRES")
        val userId = intent.getIntExtra("USER_ID", -1)

        Log.d("PaymentActivity", "Received data - movieId: $movieId, movieTitle: $movieTitle, movieGenres: $movieGenres, userId: $userId")

        findViewById<TextView>(R.id.tv_movie_title).text = movieTitle
        findViewById<TextView>(R.id.tv_movie_genres).text = movieGenres

        setupShowtimeSelectionGrid(movieId)

        selectedShowtime = "2024-06-17 10:00:00"
        fetchPurchasedSeats(movieId, selectedShowtime!!)

        findViewById<Button>(R.id.buttonPay).setOnClickListener {
            if (movieId != -1 && userId != -1 && selectedSeat != null && selectedShowtime != null) {
                proceedPayment(movieId, selectedShowtime!!, userId, selectedSeat!!)
            } else {
                Log.e("PaymentActivity", "Invalid data - movieId: $movieId, userId: $userId, selectedSeat: $selectedSeat, selectedShowtime: $selectedShowtime")
                Toast.makeText(this, "Please select a seat and showtime", Toast.LENGTH_SHORT).show()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@PaymentActivity, OngoingMoviesActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                }
                val options = ActivityOptionsCompat.makeCustomAnimation(
                    this@PaymentActivity,
                    R.anim.slide_in_left, R.anim.slide_out_right
                )
                startActivity(intent, options.toBundle())
                finishAfterTransition()
            }
        })
    }

    private fun setupShowtimeSelectionGrid(movieId: Int) {
        val showTimes = listOf("2024-06-17 10:00:00", "2024-06-17 13:00:00", "2024-06-17 16:00:00", "2024-06-17 19:00:00", "2024-06-17 22:00:00")
        val showtimeGrid: GridLayout = findViewById(R.id.grid_showtime_selection)
        showtimeGrid.removeAllViews() // Clear previous showtimes

        showtimeButtons = showTimes.map { showTime ->
            Button(this).apply {
                text = showTime
                textSize = 12f
                setPadding(8, 8, 8, 8)
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                }
                setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))
                setOnClickListener {
                    selectedShowtime = showTime
                    clearShowtimeSelection()
                    setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
                    fetchPurchasedSeats(movieId, showTime)
                    it.isSelected = true
                }
                checkIfShowtimeIsFull(movieId, showTime)
            }.also { showtimeGrid.addView(it) }
        }

        showtimeButtons.find { it.text == "2024-06-17 10:00:00" }?.apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
            isSelected = true
        }
    }

    private fun startCountdownToOngoingMoviesActivity() {
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                findViewById<Button>(R.id.buttonPay).text = "Payment Successful! Redirecting in ${millisUntilFinished / 1000}s"
                findViewById<Button>(R.id.buttonPay).isEnabled = false
            }

            override fun onFinish() {
                val intent = Intent(this@PaymentActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                }
                val options = ActivityOptionsCompat.makeCustomAnimation(this@PaymentActivity, R.anim.slide_in_left, R.anim.slide_out_right)
                startActivity(intent, options.toBundle())
                finishAfterTransition()
            }
        }.start()
    }

    private fun checkIfShowtimeIsFull(movieId: Int, showTime: String) {
        lifecycleScope.launch {
            try {
                val response: Response<List<String>> = apiService.getPurchasedSeats(movieId, showTime)
                if (response.isSuccessful) {
                    val seats = response.body() ?: emptyList()
                    if (seats.size >= totalSeats.size) {
                        showtimeButtons.find { it.text == showTime }?.setBackgroundColor(ContextCompat.getColor(this@PaymentActivity, R.color.red))
                        showtimeButtons.find { it.text == showTime }?.isEnabled = false
                    }
                } else {
                    Log.e("PaymentActivity", "Failed to check if showtime is full: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("PaymentActivity", "Error: ${e.message}")
            }
        }
    }

    private fun clearShowtimeSelection() {
        showtimeButtons.forEach {
            it.isSelected = false
            it.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))
        }
    }

    private fun fetchPurchasedSeats(movieId: Int, showTime: String) {
        lifecycleScope.launch {
            try {
                val response: Response<List<String>> = apiService.getPurchasedSeats(movieId, showTime)
                if (response.isSuccessful) {
                    purchasedSeats = response.body() ?: emptyList()
                    setupSeatSelectionGrid()
                } else {
                    Log.e("PaymentActivity", "Failed to fetch purchased seats: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("PaymentActivity", "Error: ${e.message}")
            }
        }
    }

    private fun setupSeatSelectionGrid() {
        val seatGrid: GridLayout = findViewById(R.id.grid_seat_selection)
        seatGrid.removeAllViews()
        seatButtons = totalSeats.map { seat ->
            Button(this).apply {
                text = seat
                textSize = 12f
                setPadding(8, 8, 8, 8)
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                }
                setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))
                isEnabled = !purchasedSeats.contains(seat)
                if (isEnabled) {
                    setOnClickListener {
                        selectedSeat = seat
                        clearSeatSelection()
                        setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
                        it.isSelected = true
                    }
                } else {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                }
            }.also { seatGrid.addView(it) }
        }
    }

    private fun clearSeatSelection() {
        seatButtons.forEach {
            if (it.isEnabled) {
                it.isSelected = false
                it.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))
            }
        }
    }

    private fun proceedPayment(movieId: Int, showTime: String, userId: Int, seat: String) {
        val jsonObject = JSONObject().apply {
            put("movieid", movieId)
            put("show_time", showTime)
            put("userid", userId)
            put("seat", seat)
        }

        val requestBody = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        lifecycleScope.launch {
            try {
                val response: Response<TicketResponse> = apiService.buyTicket(requestBody)
                if (response.isSuccessful) {
                    runOnUiThread {
                        findViewById<Button>(R.id.buttonPay).isEnabled = false
                    }
                    startCountdownToOngoingMoviesActivity()
                } else {
                    Log.e("PaymentActivity", "Payment failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("PaymentActivity", "Error: ${e.message}")
            }
        }
    }
}
