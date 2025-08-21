package com.example.gochatapp.data.model

data class User(
    val id: String = "",
    val displayName: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val isOnline: Boolean = false,
    val lastMessage: String = ""
)