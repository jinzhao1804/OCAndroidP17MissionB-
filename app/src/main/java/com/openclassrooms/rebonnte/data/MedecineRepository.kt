package com.openclassrooms.rebonnte.data

import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.History
import com.openclassrooms.rebonnte.domain.model.Medicine
import kotlinx.coroutines.tasks.await

class MedicineRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val medicinesCollection = firestore.collection("medicines")


    suspend fun deleteMedicine(documentId: String) {
        try {
            // Check if the document ID is valid
            if (documentId.isBlank()) {
                println("Document ID is empty. Cannot delete medicine.")
                return
            }

            // Delete the document from Firestore
            medicinesCollection.document(documentId)
                .delete()
                .await()

            println("Medicine deleted successfully!")
        } catch (e: Exception) {
            println("Failed to delete medicine: ${e.message}")
        }
    }

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
            "documentId" to medicine.documentId, // Include the documentId
            "name" to medicine.name,
            "stock" to medicine.stock,
            "nameAisle" to mapOf("name" to medicine.nameAisle.name),
            "histories" to medicine.histories.map { history ->
                mapOf(
                    "medicineName" to history.medicineName,
                    "userId" to history.userId,
                    "date" to history.date,
                    "details" to history.details
                )
            }
        )

        // Add the medicine to the "medicines" collection in Firestore
        medicinesCollection
            .add(medicineData)
            .addOnSuccessListener {
                println("Medicine added successfully!")
            }
            .addOnFailureListener { e ->
                println("Failed to add medicine: ${e.message}")
            }
    }
    /**
     * Fetches all medicines from Firestore.
     * @return List of Medicine objects.
     */
    suspend fun getAllMedicines(): List<Medicine> {
        return try {
            // Query the "medicines" collection
            val querySnapshot = medicinesCollection.get().await()
            println("Firestore query successful. Documents: ${querySnapshot.documents.size}") // Debugging

            // Map Firestore documents to Medicine objects
            querySnapshot.documents.mapNotNull { document ->
                val documentId = document.getString("documentId") ?: "" // Retrieve the documentId
                val name = document.getString("name") ?: ""
                val stock = document.getLong("stock")?.toInt() ?: 0
                val nameAisle = document.get("nameAisle") as? Map<String, Any>
                val histories = document.get("histories") as? List<Map<String, Any>> ?: emptyList()

                // Log each document for debugging
                println("Document: $document")
                println("Parsed name: $name, stock: $stock, nameAisle: $nameAisle, histories: $histories")

                // Create Medicine object
                Medicine(
                    documentId = documentId, // Include the documentId
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

    /**
     * Updates an existing medicine in Firestore.
     * @param medicine The updated medicine object.
     */
    suspend fun updateMedicine(medicine: Medicine) {
        try {
            // Check if the document ID is valid
            if (medicine.documentId.isBlank()) {
                println("Document ID is empty. Cannot update medicine.")
                return
            }

            // Convert the Medicine object to a Map
            val medicineData = hashMapOf(
                "documentId" to medicine.documentId, // Include the documentId
                "name" to medicine.name,
                "stock" to medicine.stock,
                "nameAisle" to mapOf("name" to medicine.nameAisle.name),
                "histories" to medicine.histories.map { history ->
                    mapOf(
                        "medicineName" to history.medicineName,
                        "userId" to history.userId,
                        "date" to history.date,
                        "details" to history.details
                    )
                }
            )

            // Update the document in Firestore using the document ID
            medicinesCollection.document(medicine.documentId)
                .set(medicineData)
                .await()

            println("Medicine updated successfully!")
        } catch (e: Exception) {
            println("Failed to update medicine: ${e.message}")
        }
    }
}