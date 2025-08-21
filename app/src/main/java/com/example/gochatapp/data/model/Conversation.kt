// Conversation.kt
package com.example.gochatapp.data.model

data class Conversation(
    val id: String = "",
    val title: String = "",
    val lastMessage: String? = null,
    val lastMessageTime: Long = 0L,
    val updatedAt: Long = 0L,
    val otherUserId: String = ""
)
