package com.example.gochatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.example.gochatapp.settings.SettingsScreen
import com.example.gochatapp.settings.EditProfileScreen
import com.example.gochatapp.settings.ChangePasswordScreen
import com.example.gochatapp.ui.screens.SplashScreen
import com.example.gochatapp.ui.screens.auth.LoginScreen
import com.example.gochatapp.ui.screens.auth.RegisterScreen
import com.example.gochatapp.ui.screens.chatlist.ChatDetailScreen
import com.example.gochatapp.ui.screens.chatlist.ChatListScreen
import com.example.gochatapp.ui.screens.chatlist.StartChatScreen
import com.example.gochatapp.ui.theme.ChatTheme
import com.example.gochatapp.viewmodel.AuthViewModel
import com.example.gochatapp.viewmodel.ChatListViewModel
import androidx.navigation.navArgument
import com.google.firebase.messaging.FirebaseMessaging
import android.content.pm.PackageManager
import android.os.Build

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_GoChatApp)
        super.onCreate(savedInstanceState)

        // ✅ Notification permission Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        // ✅ FCM Token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                println("FCM Token: $token")
            }
        }

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            var showSplash by remember { mutableStateOf(true) }

            ChatTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val authVm: AuthViewModel = viewModel()

                if (showSplash) {
                    SplashScreen {
                        showSplash = false
                    }
                } else {
                    NavHost(navController = navController, startDestination = "login") {

                        composable("login") {
                            LoginScreen(
                                vm = authVm,
                                onLoginSuccess = { uid, _name ->
                                    navController.navigate("chatlist/$uid") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onRegisterClick = { navController.navigate("register") }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                vm = authVm,
                                onRegisterSuccess = { uid, _name ->
                                    navController.navigate("chatlist/$uid") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                onLoginClick = {
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(
                            "chatlist/{currentUserId}",
                            arguments = listOf(navArgument("currentUserId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val currentUserId = backStackEntry.arguments?.getString("currentUserId") ?: ""
                            val chatListVm: ChatListViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                        return ChatListViewModel(currentUserId) as T
                                    }
                                }
                            )
                            ChatListScreen(
                                navController = navController,
                                viewModel = chatListVm,
                                currentUserId = currentUserId,
                                isDarkTheme = isDarkTheme
                            )
                        }

                        composable(
                            "chatdetail/{otherUserId}",
                            arguments = listOf(navArgument("otherUserId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val otherUserId = backStackEntry.arguments?.getString("otherUserId") ?: ""
                            ChatDetailScreen(
                                userId = otherUserId,
                                onBack = { navController.popBackStack() },
                                isDarkTheme = isDarkTheme
                            )
                        }

                        composable(
                            "startchat/{currentUserId}",
                            arguments = listOf(navArgument("currentUserId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val currentUserId = backStackEntry.arguments?.getString("currentUserId") ?: ""
                            StartChatScreen(navController, currentUserId, isDarkTheme = isDarkTheme)
                        }

                        composable("settings") {
                            SettingsScreen(
                                navController = navController,
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { dark -> isDarkTheme = dark }
                            )
                        }

                        composable("edit_profile") {
                            EditProfileScreen(
                                navController = navController,
                                isDarkTheme = isDarkTheme
                            )
                        }

                        composable("change_password") {
                            ChangePasswordScreen(
                                navController = navController,
                                isDarkTheme = isDarkTheme
                            )
                        }
                    }
                }
            }
        }
    }
}
