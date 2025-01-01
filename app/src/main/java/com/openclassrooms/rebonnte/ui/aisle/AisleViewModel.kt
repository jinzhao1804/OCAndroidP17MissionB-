package com.openclassrooms.rebonnte.ui.aisle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.data.AisleRepository
import com.openclassrooms.rebonnte.domain.model.Aisle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AisleViewModel(
    private val repository: AisleRepository // Pass repository as a dependency
) : ViewModel() {

    // MutableStateFlow for managing the list of aisles
    private val _aisles = MutableStateFlow<List<Aisle>>(emptyList())
    val aisles: StateFlow<List<Aisle>> get() = _aisles


    init {
        // Fetch all aisles when the ViewModel is created
        getAllAisles()
    }


    /**
     * Adds a new aisle to the list and saves it to the repository.
     */
    fun addNewAisle() {

        val currentAisles = _aisles.value.toMutableList()
        val newAisle = Aisle("Aisle ${currentAisles.size + 1}")

        currentAisles.add(newAisle)
        _aisles.value = currentAisles


        // Save the new aisle to the repository using a coroutine
        viewModelScope.launch {
            try {
                repository.addNewAisle(newAisle)
            } catch (e: Exception) {
                // Handle errors (e.g., log or show a message)
                println("Failed to add aisle: ${e.message}")
            }
        }
    }

    // Fetch all aisles from Firestore
    fun getAllAisles() {
        viewModelScope.launch {
            try {
                _aisles.value = repository.getAllAisles()
            } catch (e: Exception) {
                // Handle errors (e.g., log or show a message)
                println("Failed to fetch aisles: ${e.message}")
            }
        }
    }
}