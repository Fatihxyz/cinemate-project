package com.example.capstone.view

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.core.app.ActivityOptionsCompat
import com.example.capstone.R
import com.example.capstone.adapter.TicketAdapter
import com.example.capstone.data.repo.UserPreferences
import com.example.capstone.data.remote.ApiService
import com.example.capstone.model.remote.TicketRequest
import com.example.capstone.data.remote.ApiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private val apiService: ApiService by lazy {
        ApiClient.retrofit.create(ApiService::class.java)
    }

    private lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreferences = UserPreferences.getInstance(requireContext())

        val navigateToUpdateUserButton = view.findViewById<Button>(R.id.navigateToUpdateUserButton)
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

        startFetchingTickets()

        navigateToUpdateUserButton.setOnClickListener {
            val intent = Intent(requireContext(), UpdateUserActivity::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(
                requireContext(), R.anim.slide_in_right, R.anim.slide_out_left
            )
            startActivity(intent, options.toBundle())
        }

        logoutButton.setOnClickListener {
            logout()
        }
        swipeRefreshLayout.setOnRefreshListener {
            fetchTickets()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun startFetchingTickets() {
        lifecycleScope.launch {
            while (isActive) {
                fetchTickets()
                delay(30000)
            }
        }
    }

    private fun fetchTickets() {
        lifecycleScope.launch {
            val userId = userPreferences.userId.firstOrNull()

            userId?.let {
                try {
                    val requestBody = mapOf("userid" to it.toInt())
                    val response = apiService.getTickets(requestBody)

                    if (response.isSuccessful && response.body() != null) {
                        val tickets = response.body()!!
                        displayTickets(tickets)
                    } else {
                        Log.e(TAG, "Failed to fetch tickets: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching tickets", e)
                }
            } ?: Log.e(TAG, "User ID is null, unable to fetch tickets")
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            userPreferences.clearLoginStatus()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val options = ActivityOptionsCompat.makeCustomAnimation(
                requireContext(), R.anim.slide_in_left, R.anim.slide_out_right
            )
            startActivity(intent, options.toBundle())
            requireActivity().finish()
        }
    }

    private fun displayTickets(tickets: List<TicketRequest>) {
        val ticketRecyclerView = view?.findViewById<RecyclerView>(R.id.ticketRecyclerView)
        ticketRecyclerView?.layoutManager = LinearLayoutManager(context)
        ticketRecyclerView?.adapter = TicketAdapter(tickets)
    }
}
