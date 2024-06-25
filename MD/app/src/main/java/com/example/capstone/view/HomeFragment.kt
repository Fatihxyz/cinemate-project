package com.example.capstone.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.adapter.LoadingStateAdapter
import com.example.capstone.adapter.MovieAdapter
import com.example.capstone.adapter.RecommendationAdapter
import com.example.capstone.data.repo.UserPreferences
import com.example.capstone.data.local.Movie
import com.example.capstone.data.remote.ApiService
import com.example.capstone.data.repo.MovieRepository
import com.example.capstone.databinding.FragmentHomeBinding
import com.example.capstone.data.remote.ApiClient
import com.example.capstone.util.RecommendationSystem
import com.example.capstone.util.DatasetLoader
import com.example.capstone.viewmodel.HomeViewModel
import com.example.capstone.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(MovieRepository(ApiClient.retrofit.create(ApiService::class.java)))
    }
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var recommendationAdapter: RecommendationAdapter
    private lateinit var userPreferences: UserPreferences
    private lateinit var recommendationSystem: RecommendationSystem
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        userPreferences = UserPreferences.getInstance(requireContext())
        recommendationSystem = RecommendationSystem(requireContext(), "model.tflite")

        movieAdapter = MovieAdapter { movie ->
            val intent = Intent(context, DetailMovieActivity::class.java)
            intent.putExtra("MOVIE_ID", movie.movieid)
            startActivity(intent)
        }
        binding.rvMovie.layoutManager = LinearLayoutManager(context)
        binding.rvMovie.adapter = movieAdapter.withLoadStateHeaderAndFooter(
            header = LoadingStateAdapter { movieAdapter.retry() },
            footer = LoadingStateAdapter { movieAdapter.retry() }
        )

        recommendationAdapter = RecommendationAdapter()
        binding.rvRecommendation.layoutManager = LinearLayoutManager(context)
        binding.rvRecommendation.adapter = recommendationAdapter

        lifecycleScope.launch {
            movieAdapter.loadStateFlow.collectLatest { loadStates ->
                binding.progressBar.isVisible = loadStates.refresh is androidx.paging.LoadState.Loading
                binding.rvMovie.isVisible = loadStates.refresh !is androidx.paging.LoadState.Loading
            }
        }

        lifecycleScope.launch {
            homeViewModel.movies.collectLatest { pagingData ->
                movieAdapter.submitData(pagingData)
            }
        }

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    homeViewModel.setSearchQuery(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        lifecycleScope.launch {
            fetchRecommendations()
        }

        return binding.root
    }

    private fun fetchRecommendations() {
        lifecycleScope.launch {
            val userId = userPreferences.userId.firstOrNull()?.toIntOrNull()
            if (userId != null) {
                try {
                    val recommendations = getRecommendationsForUser(userId)
                    recommendationAdapter.submitList(recommendations)
                    binding.rvRecommendation.isVisible = true
                } catch (e: Exception) {
                    binding.rvRecommendation.isVisible = false
                    e.printStackTrace()
                    Toast.makeText(context, "Error fetching recommendations", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.rvRecommendation.isVisible = false
                Toast.makeText(context, "User ID not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getRecommendationsForUser(userId: Int): List<Movie> {
        val movies = DatasetLoader.loadMovies(requireContext())
        val recommendations = mutableListOf<Movie>()
        for (movie in movies) {
            val predictedRating = recommendationSystem.predict(userId, movie.movieId)
            movie.predictedRating = predictedRating
            recommendations.add(movie)
        }
        val sortedRecommendations = recommendations.sortedByDescending { it.predictedRating }
        return sortedRecommendations
    }
}
