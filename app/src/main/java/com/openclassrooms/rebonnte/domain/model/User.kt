package com.openclassrooms.rebonnte.domain.model

import com.google.firebase.Timestamp

data class User(
    val id: String = "",
    val email: String = "",
    val createdAt: Timestamp? = null,
    val name: String? = null,
    val profilePictureUrl: String? = null
)