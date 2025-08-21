package com.example.gochatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gochatapp.data.model.Message
import com.example.gochatapp.data.repo.FirebaseChatRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatDetailState(
    val isLoading: Boolean = true,
    val messages: List<Message> = emptyList(),
    val sending: Boolean = false,
    val error: String? = null
)

class ChatDetailViewModel : ViewModel() {
    private val chatRepo = FirebaseChatRepository()
    private val auth get() = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(ChatDetailState())
    val state: StateFlow<ChatDetailState> = _state.asStateFlow()

    private var otherUid: String? = null

    fun start(otherUserId: String) {
        otherUid = otherUserId
        val me = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            chatRepo.observeMessages(me, otherUserId)
                .onStart { _state.update { it.copy(isLoading = true) } }
                .catch { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
                .collect { msgs ->
                    _state.update { it.copy(isLoading = false, messages = msgs, error = null) }
                }
        }
    }

    fun send(text: String) {
        val me = auth.currentUser?.uid ?: return
        val other = otherUid ?: return
        viewModelScope.launch {
            _state.update { it.copy(sending = true) }
            try {
                chatRepo.sendMessage(me, other, text)
                _state.update { it.copy(sending = false) }
            } catch (e: Exception) {
                _state.update { it.copy(sending = false, error = e.message) }
            }
        }
    }
}
