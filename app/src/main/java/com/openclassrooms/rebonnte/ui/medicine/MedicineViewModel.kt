package com.openclassrooms.rebonnte.ui.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.openclassrooms.rebonnte.data.MedicineRepository
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.Medicine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

class MedicineViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    var _medicines = MutableStateFlow<MutableList<Medicine>>(mutableListOf())
    val medicines: StateFlow<List<Medicine>> get() = _medicines
    val repository = MedicineRepository()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()


    init {
        getAllMedicines()
    }

    fun deleteMedicine(documentId: String) {
        viewModelScope.launch {
            try {
                repository.deleteMedicine(documentId)
                // Refresh the list of medicines
                _medicines.value = repository.getAllMedicines().toMutableList()
                println("Medicine deleted and list refreshed") // Debugging
            } catch (e: Exception) {
                println("Failed to delete medicine: ${e.message}")
            }
        }
    }



    fun addNewMedecine(name: String, stock: Int, nameAisle: Aisle) {

    repository.addNewMedicine(name, stock, nameAisle)
}
    fun updateMedicine(updatedMedicine: Medicine) {
        viewModelScope.launch {
            try {
                repository.updateMedicine(updatedMedicine)
                // Refresh the list of medicines
                _medicines.value = repository.getAllMedicines().toMutableList()
            } catch (e: Exception) {
                println("Failed to update medicine: ${e.message}")
            }
        }
    }

    /**
     * Fetches all medicines from Firestore and updates the StateFlow.
     */
    fun getAllMedicines() {
        viewModelScope.launch {
            try {
                _medicines.value = repository.getAllMedicines().toMutableList()

            } catch (e: Exception) {
                println("Failed to fetch medicines: ${e.message}") // Debugging
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

    // Function to sort medicines by name
    fun sortByName() {
        viewModelScope.launch {
            try {
                _medicines.value = repository.getMedicinesSortedByName().toMutableList()
            } catch (e: Exception) {
                println("Failed to sort medicines by name: ${e.message}")
            }
        }
    }

    // Function to sort medicines by stock
    fun sortByStock() {
        viewModelScope.launch {
            try {
                _medicines.value = repository.getMedicinesSortedByStock().toMutableList()
            } catch (e: Exception) {
                println("Failed to sort medicines by stock: ${e.message}")
            }
        }
    }

    // Function to load medicines in default order (no sorting)
    fun sortByNone() {
        viewModelScope.launch {
            try {
                _medicines.value = repository.getAllMedicines().toMutableList()
            } catch (e: Exception) {
                println("Failed to load medicines: ${e.message}")
            }
        }
    }
}

