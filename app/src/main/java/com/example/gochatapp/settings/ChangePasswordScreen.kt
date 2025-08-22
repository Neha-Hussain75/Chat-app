package com.example.gochatapp.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    isDarkTheme: Boolean
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val colors = MaterialTheme.colorScheme
    val textColor = colors.onBackground
    val cardColor = colors.surface
    val buttonColor = Color(0xFF1985F2)


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Change Password", color = textColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = colors.background)
            )
        },
        containerColor = colors.background,
        modifier = Modifier.statusBarsPadding()

    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = cardColor,
                    focusedBorderColor = buttonColor,
                    unfocusedBorderColor = colors.onSurface.copy(alpha = 0.5f),
                    cursorColor = buttonColor
                ),
                textStyle = LocalTextStyle.current.copy(color = textColor)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = cardColor,
                    focusedBorderColor = buttonColor,
                    unfocusedBorderColor = colors.onSurface.copy(alpha = 0.5f),
                    cursorColor = buttonColor
                ),
                textStyle = LocalTextStyle.current.copy(color = textColor)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = cardColor,
                    focusedBorderColor = buttonColor,
                    unfocusedBorderColor = colors.onSurface.copy(alpha = 0.5f),
                    cursorColor = buttonColor
                ),
                textStyle = LocalTextStyle.current.copy(color = textColor)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (newPassword != confirmPassword) {
                        message = "Passwords do not match!"
                        return@Button
                    }
                    if (currentUser?.email != null) {
                        val credential = EmailAuthProvider.getCredential(currentUser.email!!, currentPassword)
                        currentUser.reauthenticate(credential).addOnCompleteListener { authResult ->
                            if (authResult.isSuccessful) {
                                currentUser.updatePassword(newPassword).addOnCompleteListener { update ->
                                    message = if (update.isSuccessful) "Password updated successfully" else "Failed to update password"
                                }
                            } else message = "Current password is incorrect"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor, contentColor = Color.White)
            ) {
                Text("Update Password")
            }

            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(message, color = Color.Red)
            }
        }
    }
}
