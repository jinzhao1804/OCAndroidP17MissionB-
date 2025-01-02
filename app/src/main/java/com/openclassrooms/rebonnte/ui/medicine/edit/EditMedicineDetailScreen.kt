package com.openclassrooms.rebonnte.ui.medicine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.History
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.domain.model.User
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import java.util.Date

@Composable
fun EditMedicineDetailScreen(
    medicine: Medicine,
    viewModel: MedicineViewModel,
    onSave: (Medicine) -> Unit,
    modifier: Modifier = Modifier
) {
    var editedName by remember { mutableStateOf(medicine.name) }
    var editedStock by remember { mutableStateOf(medicine.stock.toString()) }
    var editedAisle by remember { mutableStateOf(medicine.nameAisle.name) }

    var userId by remember { mutableStateOf("unknown") }
    var userEmail by remember { mutableStateOf("unknown") }

    // Fetch user data from Firestore
    LaunchedEffect(Unit) {
        val db = Firebase.firestore

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    println("Document ID: ${document.id}, Data: ${document.data}")
                    val user = document.toObject(User::class.java)
                    userId = document.id
                    userEmail = user.email
                    println("User: ${user.name}, Email: ${user.email}")
                }
            }
            .addOnFailureListener { exception ->
                println("Error fetching users: ${exception.message}")
            }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Edit Medicine Name
            TextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Edit Aisle Name
            TextField(
                value = editedAisle,
                onValueChange = { editedAisle = it },
                label = { Text("Aisle") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Edit Stock
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    val currentStock = editedStock.toIntOrNull() ?: 0
                    if (currentStock > 0) {
                        editedStock = (currentStock - 1).toString()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Decrease Stock"
                    )
                }
                TextField(
                    value = editedStock,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.toIntOrNull()?.let { it >= 0 } == true) {
                            editedStock = newValue
                        }
                    },
                    label = { Text("Stock") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    val currentStock = editedStock.toIntOrNull() ?: 0
                    editedStock = (currentStock + 1).toString()
                }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Increase Stock"
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    val updatedMedicine = createUpdatedMedicine(
                        medicine,
                        editedName,
                        editedStock,
                        editedAisle,
                        userId,
                        userEmail
                    )
                    viewModel.updateMedicine(updatedMedicine)
                    onSave(updatedMedicine)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Save Changes")
            }
        }
    }
}

private fun createUpdatedMedicine(
    medicine: Medicine,
    editedName: String,
    editedStock: String,
    editedAisle: String,
    userId: String,
    userEmail: String
): Medicine {
    return medicine.copy(
        name = editedName,
        stock = editedStock.toIntOrNull() ?: 0,
        nameAisle = Aisle(editedAisle),
        histories = medicine.histories + History(
            medicineName = medicine.name,
            userId = userId,
            date = Date().toString(),
            details = "Changed stock to $editedStock, changed name to $editedName.",
            userEmail = userEmail
        )
    )
}