package com.example.capstone.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.R
import com.example.capstone.data.remote.ApiService
import com.example.capstone.data.repo.TheaterRepository
import com.example.capstone.databinding.FragmentTheaterBinding
import com.example.capstone.model.remote.Theater
import com.example.capstone.data.remote.ApiClient
import com.example.capstone.viewmodel.TheaterViewModel
import com.example.capstone.viewmodel.TheaterViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TheaterFragment : Fragment() {

    private lateinit var binding: FragmentTheaterBinding
    private var currentLocation: String? = null

    private val initialLocation: String by lazy {
        arguments?.getString("location") ?: "Jakarta"
    }

    private val theaterViewModel: TheaterViewModel by viewModels {
        TheaterViewModelFactory(
            TheaterRepository(ApiClient.retrofit.create(ApiService::class.java)),
            initialLocation
        ) { theater ->
            navigateToOngoingMovies(theater)
        }
    }

    private val selectLocationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val newLocation = result.data?.getStringExtra("location")
            if (newLocation != null && newLocation != currentLocation) {
                currentLocation = newLocation
                theaterViewModel.updateLocation(newLocation)
                binding.btnSelectLocation.text = newLocation
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTheaterBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = theaterViewModel

        currentLocation = initialLocation
        binding.btnSelectLocation.text = currentLocation

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvTheater.layoutManager = layoutManager
        binding.rvTheater.adapter = theaterViewModel.theaterAdapter

        binding.btnSelectLocation.setOnClickListener {
            val intent = Intent(requireContext(), SelectLocationActivity::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(requireContext(), R.anim.fade_in, R.anim.fade_out)
            selectLocationLauncher.launch(intent, options)
        }

        lifecycleScope.launch {
            theaterViewModel.theaterAdapter.loadStateFlow.collectLatest { loadStates ->
                binding.progressBar.isVisible = loadStates.refresh is androidx.paging.LoadState.Loading
                binding.rvTheater.isVisible = loadStates.refresh !is androidx.paging.LoadState.Loading
            }
        }

        lifecycleScope.launch {
            theaterViewModel.theaters.collectLatest { pagingData ->
                theaterViewModel.theaterAdapter.submitData(pagingData)
            }
        }

        return binding.root
    }

    private fun navigateToOngoingMovies(theater: Theater) {
        val intent = Intent(requireContext(), OngoingMoviesActivity::class.java).apply {
            putExtra("THEATER_NAME", theater.name)
        }
        val options = ActivityOptionsCompat.makeCustomAnimation(requireContext(), R.anim.slide_in_right, R.anim.slide_out_left)
        startActivity(intent, options.toBundle())
    }
}
