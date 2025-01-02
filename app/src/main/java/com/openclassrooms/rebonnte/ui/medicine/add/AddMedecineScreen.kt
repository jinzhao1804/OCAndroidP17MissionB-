package com.openclassrooms.rebonnte.ui.medicine.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.util.copy
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel


fun isValidAisleName(aisle: String): Boolean {
    return aisle.isNotBlank() && aisle.matches(Regex("^[a-zA-Z0-9 ]+\$"))
}
fun isValidStock(stock: String): Boolean {
    return stock.toIntOrNull()?.let { it >= 0 } ?: false
}
fun isValidMedicineName(name: String): Boolean {
    return name.isNotBlank() && name.matches(Regex("^[a-zA-Z0-9 ]+\$"))
}
@Composable
fun AddMedecineScreen(
    navController: NavController,
    viewModel: MedicineViewModel = viewModel()
) {
    // State variables to hold the input values
    var name by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var aisle by remember { mutableStateOf(Aisle("")) }
    var textFieldValue by remember { mutableStateOf(aisle.name) }

    // Error messages
    var nameError by remember { mutableStateOf<String?>(null) }
    var stockError by remember { mutableStateOf<String?>(null) }
    var aisleError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // Name TextField
        TextField(
            value = name,
            onValueChange = {
                name = it
                nameError = if (isValidMedicineName(it)) null else "Invalid medicine name"
            },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            isError = nameError != null
        )
        if (nameError != null) {
            Text(text = nameError!!, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Stock TextField (for integer input)
        TextField(
            value = stock,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.toIntOrNull() != null) {
                    stock = newValue
                    stockError = if (isValidStock(newValue)) null else "Stock must be a positive integer"
                }
            },
            label = { Text("Stock") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            isError = stockError != null
        )
        if (stockError != null) {
            Text(text = stockError!!, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Aisle TextField
        TextField(
            value = textFieldValue,
            onValueChange = { newName ->
                textFieldValue = newName
                aisleError = if (isValidAisleName(newName)) null else "Invalid aisle name"
            },
            label = { Text("Aisle Name") },
            modifier = Modifier.fillMaxWidth(),
            isError = aisleError != null
        )
        if (aisleError != null) {
            Text(text = aisleError!!, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                // Validate all fields before saving
                val isNameValid = isValidMedicineName(name)
                val isStockValid = isValidStock(stock)
                val isAisleValid = isValidAisleName(textFieldValue)

                nameError = if (isNameValid) null else "Invalid medicine name"
                stockError = if (isStockValid) null else "Stock must be a positive integer"
                aisleError = if (isAisleValid) null else "Invalid aisle name"

                if (isNameValid && isStockValid && isAisleValid) {
                    val stockValue = stock.toIntOrNull() ?: 0
                    val updatedAisle = Aisle(textFieldValue)
                    viewModel.addNewMedicine(name, stockValue, updatedAisle)
                    navController.popBackStack()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}