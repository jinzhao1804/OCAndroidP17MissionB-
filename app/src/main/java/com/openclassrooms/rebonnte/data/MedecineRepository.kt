package com.openclassrooms.rebonnte.data

import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.Medicine

class MedicineRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun addNewMedicine(
        name: String,
        stock: Int,
        nameAisle: Aisle
    ) {
        val medicine = Medicine(
            name = name,
            stock = stock,
            nameAisle = nameAisle,
            histories = emptyList()
        )

        // Convert the Medicine object to a Map
        val medicineData = hashMapOf(
            "name" to medicine.name,
            "stock" to medicine.stock,
            "nameAisle" to medicine.nameAisle,
            "histories" to medicine.histories
        )

        // Add the medicine to the "medicines" collection in Firestore
        firestore.collection("medicines")
            .add(medicineData)
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->

            }
    }
}