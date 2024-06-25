package com.example.capstone.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.capstone.R
import com.example.capstone.data.repo.UserPreferences
import com.example.capstone.data.remote.ApiService
import com.example.capstone.data.repo.MovieRepository
import com.example.capstone.data.repo.TheaterRepository
import com.example.capstone.databinding.ActivityMainBinding
import com.example.capstone.model.remote.Theater
import com.example.capstone.data.remote.ApiClient
import com.example.capstone.viewmodel.MovieViewModel
import com.example.capstone.viewmodel.MovieViewModelFactory
import com.example.capstone.viewmodel.TheaterViewModel
import com.example.capstone.viewmodel.TheaterViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var theaterViewModel: TheaterViewModel
    private lateinit var movieViewModel: MovieViewModel
    private var lastBackPressTime: Long = 0
    private lateinit var backToast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = ""

        userPreferences = UserPreferences.getInstance(this)

        viewPager = binding.viewPager
        bottomNavigationView = binding.bottomNavigation

        val location: String = intent.getStringExtra("location") ?: "Jakarta"

        val theaterRepository = TheaterRepository(ApiClient.retrofit.create(ApiService::class.java))
        val movieRepository = MovieRepository(ApiClient.retrofit.create(ApiService::class.java))
        theaterViewModel = ViewModelProvider(
            this, TheaterViewModelFactory(theaterRepository, location) { theater ->
                navigateToOngoingMovies(theater)
            }
        ).get(TheaterViewModel::class.java)
        movieViewModel = ViewModelProvider(this, MovieViewModelFactory(movieRepository)).get(MovieViewModel::class.java)

        viewPager.adapter = ViewPagerAdapter(this)

        viewPager.setCurrentItem(0, false)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> viewPager.setCurrentItem(0, true)
                R.id.navigation_theater -> viewPager.setCurrentItem(1, true)
                R.id.navigation_ticket -> viewPager.setCurrentItem(2, true)
                else -> false
            }
            true
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> bottomNavigationView.selectedItemId = R.id.navigation_home
                    1 -> bottomNavigationView.selectedItemId = R.id.navigation_theater
                    2 -> bottomNavigationView.selectedItemId = R.id.navigation_ticket
                }
            }
        })

        bottomNavigationView.selectedItemId = R.id.navigation_home

        backToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isTaskRoot) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastBackPressTime < 2000) {
                        backToast.cancel()
                        finish()
                    } else {
                        lastBackPressTime = currentTime
                        backToast.show()
                    }
                } else {
                    super@MainActivity.onBackPressed()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }



    private fun navigateToOngoingMovies(theater: Theater) {
        val intent = Intent(this, OngoingMoviesActivity::class.java).apply {
            putExtra("THEATER_NAME", theater.name)
        }
        startActivity(intent)
    }

    companion object {
        const val REQUEST_CODE_SELECT_LOCATION = 1
    }
}
