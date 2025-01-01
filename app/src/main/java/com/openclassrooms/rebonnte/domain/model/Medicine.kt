package com.openclassrooms.rebonnte.domain.model

import java.util.UUID

data class Medicine(
    val documentId: String = UUID.randomUUID().toString(), // Generate a random UUID
    var name: String,
    var stock: Int,
    var nameAisle: Aisle,
    var histories: List<History>
)
