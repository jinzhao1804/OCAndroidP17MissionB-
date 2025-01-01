package com.openclassrooms.rebonnte.ui.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.data.MedicineRepository
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.Medicine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class MedicineViewModel : ViewModel() {
    var _medicines = MutableStateFlow<MutableList<Medicine>>(mutableListOf())
    val medicines: StateFlow<List<Medicine>> get() = _medicines
    val repository = MedicineRepository()

    init {
        getAllMedicines()
    }

    fun addNewMedecine(name: String, stock: Int, nameAisle: Aisle) {

    repository.addNewMedicine(name, stock, nameAisle)
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

    fun sortByNone() {
        _medicines.value = medicines.value.toMutableList() // Pas de tri
    }

    fun sortByName() {
        val currentMedicines = ArrayList(medicines.value)
        currentMedicines.sortWith(Comparator.comparing(Medicine::name))
        _medicines.value = currentMedicines
    }

    fun sortByStock() {
        val currentMedicines = ArrayList(medicines.value)
        currentMedicines.sortWith(Comparator.comparingInt(Medicine::stock))
        _medicines.value = currentMedicines
    }
}

