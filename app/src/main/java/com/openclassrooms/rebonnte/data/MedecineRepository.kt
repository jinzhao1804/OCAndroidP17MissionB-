package com.openclassrooms.rebonnte.data

import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.History
import com.openclassrooms.rebonnte.domain.model.Medicine
import kotlinx.coroutines.tasks.await

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

    /**
     * Fetches all medicines from Firestore.
     * @return List of Medicine objects.
     */
    suspend fun getAllMedicines(): List<Medicine> {
        return try {
            // Query the "medicines" collection
            val querySnapshot = firestore.collection("medicines").get().await()
            println("Firestore query successful. Documents: ${querySnapshot.documents.size}") // Debugging

            // Map Firestore documents to Medicine objects
            querySnapshot.documents.mapNotNull { document ->
                val name = document.getString("name") ?: ""
                val stock = document.getLong("stock")?.toInt() ?: 0
                val nameAisle = document.get("nameAisle") as? Map<String, Any>
                val histories = document.get("histories") as? List<Map<String, Any>> ?: emptyList()

                // Log each document for debugging
                println("Document: $document")
                println("Parsed name: $name, stock: $stock, nameAisle: $nameAisle, histories: $histories")

                // Create Medicine object
                Medicine(
                    name = name,
                    stock = stock,
                    nameAisle = nameAisle?.let { Aisle(it["name"] as? String ?: "Unknown Aisle") } ?: Aisle("Unknown Aisle"),
                    histories = histories.map { historyMap ->
                        History(
                            medicineName = historyMap["medicineName"] as? String ?: "",
                            userId = historyMap["userId"] as? String ?: "",
                            date = historyMap["date"] as? String ?: "",
                            details = historyMap["details"] as? String ?: ""
                        )
                    }
                )
            }.also {
                println("Fetched ${it.size} medicines from Firestore") // Debugging
            }
        } catch (e: Exception) {
            println("Failed to fetch medicines: ${e.message}") // Debugging
            emptyList()
        }
    }
}