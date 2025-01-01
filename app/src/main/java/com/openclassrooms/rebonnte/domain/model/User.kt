package com.openclassrooms.rebonnte.domain.model

import com.google.firebase.Timestamp

data class User(
    val id: String = "", // User ID (UID from Firebase Authentication)
    val email: String = "", // User's email
    val createdAt: Timestamp? = null, // Timestamp when the user was created
    val name: String? = null, // Optional: User's name
    val profilePictureUrl: String? = null // Optional: URL to the user's profile picture
)