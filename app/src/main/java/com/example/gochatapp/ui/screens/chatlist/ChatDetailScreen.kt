package com.example.gochatapp.ui.screens.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gochatapp.data.model.Message
import com.example.gochatapp.ui.components.MessageBubble
import com.example.gochatapp.viewmodel.ChatDetailViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    userId: String,
    onBack: () -> Unit,
    isDarkTheme: Boolean,
    vmFactory: (() -> ChatDetailViewModel)? = null
) {
    val vm: ChatDetailViewModel = vmFactory?.invoke() ?: viewModel(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ChatDetailViewModel() as T
    })
    val state by vm.state.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    LaunchedEffect(userId) { vm.start(userId) }

    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val secondaryTextColor = if (isDarkTheme) Color.LightGray else Color.Gray
    val themeBlue = Color(0xFF2C5DEC)

    var otherUserName by remember { mutableStateOf(userId) }
    LaunchedEffect(userId) {
        val ref = FirebaseDatabase.getInstance().getReference("users/$userId/name")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(String::class.java)?.let { otherUserName = it }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Circle avatar with initial
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(themeBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = otherUserName.firstOrNull()?.uppercase() ?: "U",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(otherUserName, color = textColor, fontWeight = FontWeight.SemiBold)
                            Text("Online", color = secondaryTextColor, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        bottomBar = {
            // Custom bottom bar with border blue
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFF9F9F9))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var text by remember { mutableStateOf("") }
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...", color = secondaryTextColor) },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            unfocusedIndicatorColor = themeBlue,
                            focusedIndicatorColor = themeBlue,
                            cursorColor = themeBlue,
                            focusedTextColor = textColor,    // ✅ yeh use karo
                            unfocusedTextColor = textColor   // ✅ yeh bhi
                        )
                    )

                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { if (text.isNotBlank()) { vm.send(text); text = "" } },
                        colors = ButtonDefaults.buttonColors(containerColor = themeBlue),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Send", color = Color.White)
                    }
                }
            }
        },
        containerColor = backgroundColor
    ) { inner ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = themeBlue)
            }
        } else {
            MessagesList(
                messages = state.messages,
                currentUserId = currentUserId,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .padding(horizontal = 36.dp, vertical = 4.dp), // 👈 gap from edges
                isDarkTheme = isDarkTheme
            )
        }
    }
}

@Composable
private fun MessagesList(
    messages: List<Message>,
    currentUserId: String,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean
) {
    val ordered = messages.sortedBy { it.timestamp }
    val bubbleColorSent = Color(0xFF2C5DEC)
    val bubbleColorReceived = if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFF0F0F0)
    val textColorSent = Color.White
    val textColorReceived = if (isDarkTheme) Color.White else Color.Black

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(ordered, key = { it.id }) { msg ->
            MessageBubble(
                message = msg.text,
                isSentByCurrentUser = msg.senderId == currentUserId,
                sentBubbleColor = bubbleColorSent,
                receivedBubbleColor = bubbleColorReceived,
                sentTextColor = textColorSent,
                receivedTextColor = textColorReceived
            )
            Spacer(Modifier.height(6.dp))
        }
    }
}
