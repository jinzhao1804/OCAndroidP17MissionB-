package com.openclassrooms.rebonnte.ui.medicine

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.openclassrooms.rebonnte.MainActivity
import com.openclassrooms.rebonnte.domain.model.History
import com.openclassrooms.rebonnte.domain.model.User
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import java.util.Date

class MedicineDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("nameMedicine") ?: "Unknown"
        val viewModel = ViewModelProvider(MainActivity.mainActivity)[MedicineViewModel::class.java]

        setContent {
            RebonnteTheme {
                MedicineDetailScreen(name, viewModel) { // Remove NavController
                    // Navigate back to MainActivity
                    val intent = Intent(this@MedicineDetailActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Close the current activity
                }
            }
        }
    }
}

@Composable
fun MedicineDetailScreen(
    name: String,
    viewModel: MedicineViewModel,
    onNavigateBack: () -> Unit
) {
    val medicines by viewModel.medicines.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val medicine = medicines.find { it.name == name } ?: return
    var stock by remember { mutableStateOf(medicine.stock) }
    var isEditing by remember { mutableStateOf(false) }

    var userId by remember { mutableStateOf("unknown") }
    var userEmail by remember { mutableStateOf("unknown") }

    LaunchedEffect(Unit) {
        val db = Firebase.firestore
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val user = document.toObject(User::class.java)
                    userId = document.id
                    userEmail = user.email
                }
            }
            .addOnFailureListener { exception ->
                viewModel.setError("Error fetching users: ${exception.message}")
            }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error!!, color = MaterialTheme.colorScheme.error)
                }
            } else {
                if (isEditing) {
                    EditMedicineDetailScreen(
                        medicine = medicine,
                        viewModel = viewModel,
                        onSave = { updatedMedicine ->
                            viewModel.updateMedicine(updatedMedicine)
                            isEditing = false
                            onNavigateBack()
                        }
                    )
                } else {
                    Column {
                        Row {
                            Button(
                                onClick = { isEditing = true },
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
                                    onNavigateBack()
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
                                    val newHistory = History(
                                        medicineName = medicine.name,
                                        userId = userId,
                                        date = Date().toString(),
                                        details = "Decreased stock to ${stock - 1}",
                                        userEmail = userEmail
                                    )
                                    val updatedMedicine = medicine.copy(
                                        stock = stock - 1,
                                        histories = medicine.histories + newHistory
                                    )
                                    viewModel.updateMedicine(updatedMedicine)
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
                                val newHistory = History(
                                    medicineName = medicine.name,
                                    userId = userId,
                                    date = Date().toString(),
                                    details = "Increased stock to ${stock + 1}",
                                    userEmail = userEmail
                                )
                                val updatedMedicine = medicine.copy(
                                    stock = stock + 1,
                                    histories = medicine.histories + newHistory
                                )
                                viewModel.updateMedicine(updatedMedicine)
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
            Text(text = "Email: ${history.userEmail}")
        }
    }
}