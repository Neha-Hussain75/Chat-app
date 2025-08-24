package com.example.gochatapp.ui.screens.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gochatapp.viewmodel.ChatUser
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartChatScreen(
    navController: NavController,
    currentUserId: String,
    isDarkTheme: Boolean
) {
    var users by remember { mutableStateOf<List<ChatUser>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // Colors based on theme
    val primaryColor = if (isDarkTheme) Color(0xFF1985F2) else Color(0xFF1985F2)
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color.White
    val cardColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFF7F7F7)
    val textColor = if (isDarkTheme) Color.Black else Color.Black
    val secondaryTextColor = if (isDarkTheme) Color.Gray else Color.Gray
    val searchBarColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFF0F0F0)

    val usersRef = FirebaseDatabase.getInstance().getReference("users")
    val chatsRef = FirebaseDatabase.getInstance().getReference("chats")

    // Fetch users
    LaunchedEffect(Unit) {
        usersRef.get().addOnSuccessListener { snapshot ->
            val allUsers = snapshot.children.mapNotNull { child ->
                val uid = child.key ?: return@mapNotNull null
                if (uid == currentUserId) return@mapNotNull null
                val name = child.child("name").getValue(String::class.java) ?: "User"
                val email = child.child("email").getValue(String::class.java) ?: ""
                val online = child.child("online").getValue(Boolean::class.java) ?: false
                ChatUser(uid, name, email, online)
            }

            chatsRef.get().addOnSuccessListener { chatSnapshot ->
                val chattedUserIds = chatSnapshot.children.mapNotNull { chatChild ->
                    val chatId = chatChild.key ?: return@mapNotNull null
                    val parts = chatId.split("_")
                    if (parts.contains(currentUserId)) parts.first { it != currentUserId } else null
                }
                users = allUsers.filter { it.uid !in chattedUserIds }
                isLoading = false
            }.addOnFailureListener {
                users = allUsers
                isLoading = false
            }
        }.addOnFailureListener {
            isLoading = false
            users = emptyList()
        }
    }

    // Filter users by search query
    val filteredUsers = if (searchQuery.text.isEmpty()) users
    else users.filter {
        it.username.contains(searchQuery.text, ignoreCase = true) ||
                it.email.contains(searchQuery.text, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Start Chat",
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(top = 16.dp)) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = backgroundColor
                ),
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(52.dp)
                    .shadow(4.dp, RoundedCornerShape(24.dp))
                    .background(searchBarColor, RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "Search Icon",
                        tint = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                if (searchQuery.text.isEmpty()) {
                                    Text(
                                        text = "Search users",
                                        color = secondaryTextColor,
                                        fontSize = 14.sp
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }

                    if (searchQuery.text.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = secondaryTextColor,
                            modifier = Modifier
                                .size(22.dp)
                                .clickable { searchQuery = TextFieldValue("") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading -> CircularProgressIndicator(
                        color = primaryColor,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    filteredUsers.isEmpty() -> Text(
                        "No users available",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 16.sp,
                        color = secondaryTextColor
                    )
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredUsers) { user ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(6.dp, RoundedCornerShape(12.dp))
                                    .clickable(
                                        onClick = {
                                            val chatId = if (currentUserId < user.uid) {
                                                "${currentUserId}_${user.uid}"
                                            } else "${user.uid}_${currentUserId}"

                                            val chatRef = chatsRef.child(chatId)
                                            chatRef.child("createdAt").setValue(System.currentTimeMillis())
                                                .addOnCompleteListener {
                                                    navController.navigate("chatdetail/${user.uid}") {
                                                        popUpTo("startchat") { inclusive = true }
                                                    }
                                                }
                                        },
                                        indication = rememberRipple(bounded = true, color = primaryColor),
                                        interactionSource = remember { MutableInteractionSource() }
                                    ),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFC)), // <-- changed here
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(primaryColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            user.username.firstOrNull()?.toString() ?: "U",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 20.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                user.username,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 16.sp,
                                                color = textColor
                                            )
                                            if (user.online) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .clip(CircleShape)
                                                        .background(primaryColor)
                                                )
                                            }
                                        }

                                        Text(
                                            text = user.email,
                                            fontSize = 12.sp,
                                            color = secondaryTextColor,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}
