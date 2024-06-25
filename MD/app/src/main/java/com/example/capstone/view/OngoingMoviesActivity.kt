package com.example.capstone.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.data.repo.UserPreferences
import com.example.capstone.data.remote.ApiService
import com.example.capstone.databinding.ActivityOngoingMoviesBinding
import com.example.capstone.data.remote.ApiClient
import com.example.capstone.viewmodel.OngoingMoviesViewModel
import com.example.capstone.viewmodel.OngoingMoviesViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.lifecycle.repeatOnLifecycle
import com.example.capstone.R
import com.example.capstone.data.repo.OngoingMoviesRepository
import com.example.capstone.model.remote.Movie

class OngoingMoviesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOngoingMoviesBinding
    private val ongoingMoviesViewModel: OngoingMoviesViewModel by viewModels {
        val theaterId = intent.getIntExtra("THEATER_ID", -1)
        OngoingMoviesViewModelFactory(OngoingMoviesRepository(ApiClient.retrofit.create(ApiService::class.java)), theaterId) { movie ->
            getUserIdAndNavigate(movie)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOngoingMoviesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val theaterName = intent.getStringExtra("THEATER_NAME")
        supportActionBar?.title = theaterName

        binding.backButton.setOnClickListener {
            finish()
        }

        val movieAdapter = ongoingMoviesViewModel.movieAdapter

        binding.rvMovies.layoutManager = LinearLayoutManager(this)
        binding.rvMovies.adapter = movieAdapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                ongoingMoviesViewModel.movies.collectLatest { pagingData ->
                    movieAdapter.submitData(pagingData)
                    binding.progressBar.visibility = View.GONE
                    binding.rvMovies.visibility = View.VISIBLE
                }
            }
        }

        movieAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is androidx.paging.LoadState.Loading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvMovies.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.rvMovies.visibility = View.VISIBLE
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@OngoingMoviesActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                }
                val options = ActivityOptionsCompat.makeCustomAnimation(
                    this@OngoingMoviesActivity,
                    R.anim.slide_in_left, R.anim.slide_out_right
                )
                startActivity(intent, options.toBundle())
                finishAfterTransition()
            }
        })
    }

    private fun getUserIdAndNavigate(movie: Movie) {
        val userPreferences = UserPreferences.getInstance(this)

        lifecycleScope.launch {
            userPreferences.userId.collectLatest { userId ->
                if (userId != null) {
                    navigateToMovieDetail(userId.toInt(), movie)
                } else {
                    Log.e("OngoingMoviesActivity", "User ID is null")
                }
            }
        }
    }

    private fun navigateToMovieDetail(userId: Int, movie: Movie) {
        val intent = Intent(this, PaymentActivity::class.java).apply {
            putExtra("MOVIE_ID", movie.movieid)
            putExtra("MOVIE_TITLE", movie.title)
            putExtra("MOVIE_GENRES", movie.genres)
            putExtra("USER_ID", userId)
        }
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
        startActivity(intent, options.toBundle())
    }
}
