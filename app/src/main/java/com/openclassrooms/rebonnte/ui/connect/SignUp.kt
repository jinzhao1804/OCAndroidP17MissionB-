package com.openclassrooms.rebonnte.ui.connect

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.openclassrooms.rebonnte.domain.model.User

@Composable
fun SignUpScreen(navController: NavController) {
    val auth = Firebase.auth
    val firestore = Firebase.firestore
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") } // Optional: Add name input
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }, // Optional: Add name input
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            if (user != null) {
                                // Create a User object
                                val userData = User(
                                    id = user.uid,
                                    email = email,
                                    createdAt = Timestamp.now(),
                                    name = name // Optional: Add name
                                )
                                // Save the User object to Firestore
                                firestore.collection("users")
                                    .document(user.uid)
                                    .set(userData)
                                    .addOnSuccessListener {
                                        // Navigate to the main screen
                                        navController.navigate("main") {
                                            popUpTo("signup") { inclusive = true }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        errorMessage = "Failed to store user data: ${e.message}"
                                    }
                            }
                        } else {
                            errorMessage = "Sign up failed: ${task.exception?.message}"
                        }
                    }
            } else {
                errorMessage = "Email and password cannot be empty"
            }
        }) {
            Text("Sign Up")
        }
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage!!,
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}