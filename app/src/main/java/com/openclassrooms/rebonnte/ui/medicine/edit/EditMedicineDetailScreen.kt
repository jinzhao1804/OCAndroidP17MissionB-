package com.openclassrooms.rebonnte.ui.medicine.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.Medicine

@Composable
fun EditMedicineDetailScreen(
    medicine: Medicine,
    onSave: (Medicine) -> Unit,
    modifier: Modifier = Modifier
) {
    var editedName by remember { mutableStateOf(medicine.name) }
    var editedStock by remember { mutableStateOf(medicine.stock.toString()) }
    var editedAisle by remember { mutableStateOf(medicine.nameAisle.name) }

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
                        if (newValue.isEmpty() || newValue.toIntOrNull() != null) {
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
                    val updatedMedicine = medicine.copy(
                        name = editedName,
                        stock = editedStock.toIntOrNull() ?: 0,
                        nameAisle = Aisle(editedAisle)
                    )
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