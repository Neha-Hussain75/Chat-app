package com.example.gochatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ChatUser(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val online: Boolean = false,
    val lastMessage: String = "",
    val updatedAt: Long = 0L,
)

data class ChatListState(
    val isLoading: Boolean = true,
    val conversations: List<ChatUser> = emptyList()
)

class ChatListViewModel(private val currentUserId: String) : ViewModel() {

    private val _state = MutableStateFlow(ChatListState())
    val state: StateFlow<ChatListState> = _state

    private val usersRef = FirebaseDatabase.getInstance().getReference("users")
    private val chatsRef = FirebaseDatabase.getInstance().getReference("chats")

    fun start() {
        _state.value = _state.value.copy(isLoading = true)

        // Listen for any changes under "chats"
        chatsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chattedUserIds = snapshot.children.mapNotNull { chatChild ->
                    val chatId = chatChild.key ?: return@mapNotNull null
                    val parts = chatId.split("_")
                    if (parts.contains(currentUserId)) {
                        parts.first { it != currentUserId }
                    } else null
                }.toSet()

                // Fetch only users we have chatted with
                usersRef.get().addOnSuccessListener { usersSnapshot ->
                    val userList = usersSnapshot.children.mapNotNull { userChild ->
                        val uid = userChild.key ?: return@mapNotNull null
                        if (uid !in chattedUserIds) return@mapNotNull null
                        val name = userChild.child("name").getValue(String::class.java) ?: "User"
                        val email = userChild.child("email").getValue(String::class.java) ?: ""
                        ChatUser(uid, name, email)
                    }

                    // Fetch last message for each user
                    userList.forEach { user ->
                        val chatId = if (currentUserId < user.uid) {
                            "${currentUserId}_${user.uid}"
                        } else {
                            "${user.uid}_${currentUserId}"
                        }

                        chatsRef.child(chatId).orderByChild("updatedAt").limitToLast(1)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(msgSnapshot: DataSnapshot) {
                                    val lastMsg = msgSnapshot.children.firstOrNull()
                                        ?.child("lastMessage")?.getValue(String::class.java) ?: ""

                                    val updatedAt = msgSnapshot.children.firstOrNull()
                                        ?.child("updatedAt")?.getValue(Any::class.java)
                                        ?.let { value ->
                                            when (value) {
                                                is Long -> value
                                                is Double -> value.toLong()
                                                else -> 0L
                                            }
                                        } ?: 0L


                                    viewModelScope.launch {
                                        _state.value = _state.value.copy(
                                            conversations = _state.value.conversations.map {
                                                if (it.uid == user.uid) it.copy(
                                                    lastMessage = lastMsg,
                                                    updatedAt = updatedAt
                                                )
                                                else it
                                            }.ifEmpty { userList }
                                        )
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {}
                            })
                    }

                    // Initial state
                    viewModelScope.launch {
                        _state.value = ChatListState(isLoading = false, conversations = userList)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}
