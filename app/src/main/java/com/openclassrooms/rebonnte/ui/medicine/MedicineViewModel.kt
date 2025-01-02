package com.openclassrooms.rebonnte.ui.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.rebonnte.data.MedicineRepository
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.Medicine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class MedicineViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val repository = MedicineRepository()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _medicines = MutableStateFlow<MutableList<Medicine>>(mutableListOf())
    val medicines: StateFlow<List<Medicine>> get() = _medicines

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    init {
        getAllMedicines()
    }

    /**
     * Sets an error message manually.
     *
     * @param message The error message to set.
     */
    fun setError(message: String) {
        _error.value = message
    }

    /**
     * Clears the current error message.
     */
    fun clearError() {
        _error.value = null
    }

    fun deleteMedicine(documentId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                println("Deleting medicine with documentId: $documentId") // Debugging
                repository.deleteMedicine(documentId)
                _medicines.value = repository.getAllMedicines().toMutableList()
            } catch (e: Exception) {
                _error.value = "Failed to delete medicine: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addNewMedicine(name: String, stock: Int, nameAisle: Aisle) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.addNewMedicine(name, stock, nameAisle)
            } catch (e: Exception) {
                _error.value = "Failed to add medicine: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMedicine(updatedMedicine: Medicine) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateMedicine(updatedMedicine)
                _medicines.value = repository.getAllMedicines().toMutableList()
            } catch (e: Exception) {
                _error.value = "Failed to update medicine: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAllMedicines() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _medicines.value = repository.getAllMedicines().toMutableList()
            } catch (e: Exception) {
                _error.value = "Failed to fetch medicines: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByName(name: String) {
        val currentMedicines: List<Medicine> = medicines.value
        val filteredMedicines: MutableList<Medicine> = ArrayList()
        for (medicine in currentMedicines) {
            if (medicine.name.lowercase(Locale.getDefault())
                    .contains(name.lowercase(Locale.getDefault()))
            ) {
                filteredMedicines.add(medicine)
            }
        }
        _medicines.value = filteredMedicines
    }

    fun sortByName() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _medicines.value = repository.getMedicinesSortedByName().toMutableList()
            } catch (e: Exception) {
                _error.value = "Failed to sort medicines by name: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sortByStock() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _medicines.value = repository.getMedicinesSortedByStock().toMutableList()
            } catch (e: Exception) {
                _error.value = "Failed to sort medicines by stock: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sortByNone() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _medicines.value = repository.getAllMedicines().toMutableList()
            } catch (e: Exception) {
                _error.value = "Failed to load medicines: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}