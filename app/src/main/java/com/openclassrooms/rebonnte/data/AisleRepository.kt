package com.openclassrooms.rebonnte.data


import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.openclassrooms.rebonnte.domain.model.Aisle
import kotlinx.coroutines.tasks.await

class AisleRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val aislesCollection = firestore.collection("aisles")

    /**
     * Adds a new aisle to Firestore.
     * @param aisle The aisle to add.
     */
    suspend fun addNewAisle(aisle: Aisle) {
        try {
            // Add the aisle to the "aisles" collection
            aislesCollection.add(aisle).await()
        } catch (e: Exception) {
            // Handle errors (e.g., log or throw)
            throw e
        }
    }

    /**
     * Retrieves all aisles from Firestore.
     * @return List of Aisle objects.
     */
    suspend fun getAllAisles(): List<Aisle> {
        return try {
            // Query the "aisles" collection
            val querySnapshot = aislesCollection.get().await()

            // Convert each document to an Aisle object
            querySnapshot.documents.map { document ->
                document.toObject<Aisle>() ?: throw IllegalStateException("Failed to parse document: $document")
            }
        } catch (e: Exception) {
            // Handle errors (e.g., log or throw)
            throw e
        }
    }
}