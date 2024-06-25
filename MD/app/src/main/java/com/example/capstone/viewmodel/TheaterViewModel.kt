package com.example.capstone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.capstone.adapter.TheaterAdapter
import com.example.capstone.model.remote.Theater
import com.example.capstone.data.repo.TheaterRepository
import kotlinx.coroutines.flow.*

class TheaterViewModel(
    private val repository: TheaterRepository,
    initialLocation: String?,
    onClick: (Theater) -> Unit
) : ViewModel() {

    private val locationFlow = MutableStateFlow(initialLocation)

    val theaters: Flow<PagingData<Theater>> = locationFlow.flatMapLatest { location ->
        if (location == null) {
            repository.getTheaters().cachedIn(viewModelScope)
        } else {
            repository.getTheatersLocation(location).cachedIn(viewModelScope)
        }
    }

    val theaterAdapter: TheaterAdapter = TheaterAdapter(onClick)

    fun updateLocation(newLocation: String) {
        locationFlow.value = newLocation
    }
}
