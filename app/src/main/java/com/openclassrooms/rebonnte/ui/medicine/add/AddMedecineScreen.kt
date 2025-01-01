package com.openclassrooms.rebonnte.ui.medicine.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.util.copy
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel

@Composable
fun AddMedecineScreen(
    navController: NavController,
    viewModel: MedicineViewModel = viewModel()) {
    // State variables to hold the input values
    var name by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var aisle by remember { mutableStateOf(Aisle("")) }
    var textFieldValue by remember { mutableStateOf(aisle.name) }


    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // Name TextField
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Stock TextField (for integer input)
        TextField(
            value = stock,
            onValueChange = { newValue ->
                // Validate that the input is an integer
                if (newValue.isEmpty() || newValue.toIntOrNull() != null) {
                    stock = newValue
                }
            },
            label = { Text("Stock") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = textFieldValue,
            onValueChange = { newName ->
                textFieldValue = newName // Directly update the name property
            },
            label = { Text("Aisle Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Handle the save action here
                val stockValue = stock.toIntOrNull() ?: 0 // Convert stock to Int, default to 0 if invalid
                viewModel.addNewMedecine(name, stockValue, aisle) // Call ViewModel function
                navController.popBackStack() // Navigate back
            },
            modifier = Modifier.align(Alignment.End) // Align the button to the end of its container
        ) {
            Text("Save") // Button text
        }
    }
}