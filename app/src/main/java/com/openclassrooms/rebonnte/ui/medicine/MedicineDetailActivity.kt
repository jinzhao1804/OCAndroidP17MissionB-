package com.openclassrooms.rebonnte.ui.medicine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.rebonnte.MainActivity
import com.openclassrooms.rebonnte.domain.model.History
import com.openclassrooms.rebonnte.ui.medicine.edit.EditMedicineDetailScreen
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import java.util.Date

class MedicineDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("nameMedicine") ?: "Unknown"
        val viewModel = ViewModelProvider(MainActivity.mainActivity)[MedicineViewModel::class.java]

        setContent {
            RebonnteTheme {
                val navController = rememberNavController()

                MedicineDetailScreen(name, viewModel, navController)
            }
        }
    }
}

@Composable
fun MedicineDetailScreen(
    name: String,
    viewModel: MedicineViewModel,
    navController: NavController
) {
    val medicines by viewModel.medicines.collectAsState(initial = emptyList())
    val medicine = medicines.find { it.name == name } ?: return
    var stock by remember { mutableStateOf(medicine.stock) }
    var isEditing by remember { mutableStateOf(false) } // State to toggle edit mode

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (isEditing) {
                // Show Edit Screen
                EditMedicineDetailScreen(
                    medicine = medicine,
                    onSave = { updatedMedicine ->
                        viewModel.updateMedicine(updatedMedicine)
                        isEditing = false // Exit edit mode after saving
                    }
                )
            } else {
                // Show Detail Screen
                Column {
                    Row {
                        Button(
                            onClick = { isEditing = true }, // Enter edit mode
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text("Edit Medicine")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.deleteMedicine(medicine.documentId)
                                navController.navigate("medicine") // Navigate to medicine list
                                navController.popBackStack()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text("Delete")
                        }
                    }
                    Spacer(modifier = Modifier.padding(16.dp))
                    TextField(
                        value = medicine.name,
                        onValueChange = {},
                        label = { Text("Name") },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = medicine.nameAisle.name,
                        onValueChange = {},
                        label = { Text("Aisle") },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = {
                            if (stock > 0) {
                                medicines[medicines.size].histories.toMutableList().add(
                                    History(
                                        medicine.name,
                                        "efeza56f1e65f",
                                        Date().toString(),
                                        "Updated medicine details"
                                    )
                                )
                                stock--
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Minus One"
                            )
                        }
                        TextField(
                            value = stock.toString(),
                            onValueChange = {},
                            label = { Text("Stock") },
                            enabled = false,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            medicines[medicines.size].histories.toMutableList().add(
                                History(
                                    medicine.name,
                                    "efeza56f1e65f",
                                    Date().toString(),
                                    "Updated medicine details"
                                )
                            )
                            stock++
                        }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Plus One"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "History", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(medicine.histories) { history ->
                            HistoryItem(history = history)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(history: History) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = history.medicineName, fontWeight = FontWeight.Bold)
            Text(text = "User: ${history.userId}")
            Text(text = "Date: ${history.date}")
            Text(text = "Details: ${history.details}")
        }
    }
}