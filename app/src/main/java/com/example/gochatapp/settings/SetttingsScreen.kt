package com.example.gochatapp.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    var darkMode by remember { mutableStateOf(isDarkTheme) }
    val themeColor = Color(0xFF2C5DEC)

    // Dynamic colors for light/dark mode
    val backgroundColor = if (darkMode) Color(0xFF121212) else Color(0xFFF9F9F9)
    val cardColor = if (darkMode) Color(0xFF1E1E1E) else Color.White
    val textColor = if (darkMode) Color.White else Color.Black
    val secondaryTextColor = if (darkMode) Color.LightGray else Color.Gray

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold, color = textColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Top
        ) {

            // Edit Profile
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("edit_profile") },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Edit Profile", color = textColor, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Change Password
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("change_password") },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Change Password", color = textColor, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Theme toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Light / Dark Mode", color = textColor, fontSize = 16.sp)
                    Switch(
                        checked = darkMode,
                        onCheckedChange = {
                            darkMode = it
                            onToggleTheme(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = themeColor,
                            uncheckedThumbColor = Color.LightGray,
                            checkedTrackColor = themeColor.copy(alpha = 0.4f),
                            uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Logout
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        auth.signOut()
                        navController.navigate("login") {
                            popUpTo("settings") { inclusive = true }
                        }
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Logout", color = Color.Red, fontSize = 16.sp)
                }
            }
        }
    }
}
