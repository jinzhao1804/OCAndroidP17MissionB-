package com.openclassrooms.rebonnte.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.History
import com.openclassrooms.rebonnte.domain.model.Medicine
import kotlinx.coroutines.tasks.await
import java.util.*

class MedicineRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val medicinesCollection = firestore.collection("medicines")
    private val firebaseAuth = FirebaseAuth.getInstance()

    // Function to get the current user's email
    fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    suspend fun deleteMedicine(documentId: String) {
        try {
            if (documentId.isBlank()) {
                println("Document ID is empty. Cannot delete medicine.")
                return
            }

            medicinesCollection.document(documentId).delete().await()
            println("Medicine deleted successfully!")
        } catch (e: Exception) {
            println("Failed to delete medicine: ${e.message}")
        }
    }

    fun addNewMedicine(name: String, stock: Int, nameAisle: Aisle) {
        val documentId = UUID.randomUUID().toString() // Generate a unique documentId
        val medicine = Medicine(
            documentId = documentId,
            name = name,
            stock = stock,
            nameAisle = nameAisle,
            histories = emptyList()
        )

        val medicineData = hashMapOf(
            "documentId" to medicine.documentId,
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

        medicinesCollection.add(medicineData)
            .addOnSuccessListener { println("Medicine added successfully!") }
            .addOnFailureListener { e -> println("Failed to add medicine: ${e.message}") }
    }

    suspend fun getAllMedicines(query: Query? = null): List<Medicine> {
        val userEmail = getCurrentUserEmail()

        return try {
            val querySnapshot = query?.get()?.await() ?: medicinesCollection.get().await()
            println("Firestore query successful. Documents: ${querySnapshot.documents.size}")

            querySnapshot.documents.mapNotNull { document ->
                val documentId = document.getString("documentId") ?: ""
                val name = document.getString("name") ?: ""
                val stock = document.getLong("stock")?.toInt() ?: 0
                val nameAisle = document.get("nameAisle") as? Map<String, Any>
                val histories = document.get("histories") as? List<Map<String, Any>> ?: emptyList()

                Medicine(
                    documentId = documentId,
                    name = name,
                    stock = stock,
                    nameAisle = nameAisle?.let { Aisle(it["name"] as? String ?: "Unknown Aisle") } ?: Aisle("Unknown Aisle"),
                    histories = histories.map { historyMap ->
                        History(
                            medicineName = historyMap["medicineName"] as? String ?: "",
                            userId = historyMap["userId"] as? String ?: "",
                            date = historyMap["date"] as? String ?: "",
                            details = historyMap["details"] as? String ?: "",
                            userEmail = userEmail ?: "Unknown Email"
                        )
                    }
                )
            }.also { println("Fetched ${it.size} medicines from Firestore") }
        } catch (e: Exception) {
            println("Failed to fetch medicines: ${e.message}")
            emptyList()
        }
    }

    suspend fun updateMedicine(medicine: Medicine) {
        try {
            if (medicine.documentId.isBlank()) {
                println("Document ID is empty. Cannot update medicine.")
                return
            }

            val querySnapshot = medicinesCollection
                .whereEqualTo("documentId", medicine.documentId)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                println("Document with documentId ${medicine.documentId} does not exist. Cannot update medicine.")
                return
            }

            val firestoreDocumentId = querySnapshot.documents.firstOrNull()?.id
            if (firestoreDocumentId.isNullOrBlank()) {
                println("Firestore document ID is null or blank. Cannot update medicine.")
                return
            }

            val medicineData = hashMapOf(
                "name" to medicine.name,
                "stock" to medicine.stock,
                "nameAisle" to mapOf("name" to medicine.nameAisle.name),
                "histories" to medicine.histories.map { history ->
                    mapOf(
                        "medicineName" to history.medicineName,
                        "userId" to history.userId,
                        "date" to history.date,
                        "details" to history.details,
                        "userEmail" to history.userEmail
                    )
                }
            )

            println("Updating document with Firestore ID: $firestoreDocumentId")
            medicinesCollection.document(firestoreDocumentId).update(medicineData as Map<String, Any>).await()
            println("Medicine updated successfully!")
        } catch (e: Exception) {
            println("Failed to update medicine: ${e.message}")
        }
    }

    suspend fun getMedicinesSortedByName(): List<Medicine> {
        return getAllMedicines(medicinesCollection.orderBy("name", Query.Direction.ASCENDING))
    }

    suspend fun getMedicinesSortedByStock(): List<Medicine> {
        return getAllMedicines(medicinesCollection.orderBy("stock", Query.Direction.ASCENDING))
    }
}