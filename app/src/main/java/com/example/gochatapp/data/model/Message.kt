package com.example.gochatapp.data.model

data class Message(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val seenBy: Map<String, Boolean>? = null
)
