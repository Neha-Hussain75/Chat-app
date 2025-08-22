package com.example.gochatapp.ui.screens.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
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
import com.example.gochatapp.ui.components.ConversationListItem
import com.example.gochatapp.viewmodel.ChatListViewModel
import com. example. gochatapp. ui. components. BaseScaffold
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavController,
    currentUserId: String,
    isDarkTheme: Boolean,
    viewModel: ChatListViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var expandedMenu by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { viewModel.start() }

    val themeColor = Color(0xFF1985F2)
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF9F9F9)
    val searchBgColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFF0F0F0)
    val secondaryTextColor = if (isDarkTheme) Color.LightGray else Color.Gray

    val filteredConversations = state.conversations.filter { convo ->
        val displayName = convo.username ?: "User"
        displayName.contains(searchQuery, ignoreCase = true)
    }

    BaseScaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Chats",
                        color = if (isDarkTheme) Color.White else Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = backgroundColor),
                actions = {
                    Box {
                        IconButton(onClick = { expandedMenu = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = if (isDarkTheme) Color.White else Color.Black
                            )
                        }
                        DropdownMenu(
                            expanded = expandedMenu,
                            onDismissRequest = { expandedMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = {
                                    expandedMenu = false
                                    navController.navigate("settings")
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("startchat/$currentUserId") },
                containerColor = themeColor,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Default.ChatBubble,
                    contentDescription = "Start Chat",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(52.dp)
                    .shadow(4.dp, RoundedCornerShape(24.dp))
                    .background(searchBgColor, RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ChatBubble,
                        contentDescription = null,
                        tint = themeColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { inner ->
                                if (searchQuery.isEmpty())
                                    Text("Search chats", color = secondaryTextColor, fontSize = 16.sp)
                                inner()
                            }
                        )
                    }
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = secondaryTextColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = themeColor
                    )

                    filteredConversations.isEmpty() -> Text(
                        "No chats found",
                        modifier = Modifier.align(Alignment.Center),
                        color = secondaryTextColor
                    )

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredConversations) { convo ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFC)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                ConversationListItem(
                                    conv = convo,
                                    onClick = { otherUserId ->
                                        navController.navigate("chatdetail/$otherUserId")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp, horizontal = 12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
