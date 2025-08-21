package com.example.gochatapp.data.repo

import com.example.gochatapp.data.model.Conversation
import com.example.gochatapp.data.model.Message
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.database.ServerValue

class FirebaseChatRepository {
    private val db = FirebaseDatabase.getInstance().reference

    private fun conversationIdFor(a: String, b: String): String {
        val sorted = listOf(a, b).sorted()
        return "${sorted[0]}_${sorted[1]}"
    }

    // Chat list (per user)
    fun observeUserConversations(meUid: String) = callbackFlow<List<Conversation>> {
        val ref = db.child("userConversations").child(meUid)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { snap ->
                    val id = snap.key ?: return@mapNotNull null
                    val otherUserId = snap.child("otherUserId").getValue(String::class.java) ?: return@mapNotNull null
                    val lastMessage = snap.child("lastMessage").getValue(String::class.java) ?: ""
                    val updatedAt = snap.child("updatedAt").getValue(Long::class.java) ?: 0L
                    Conversation(id = id, otherUserId = otherUserId, lastMessage = lastMessage, updatedAt = updatedAt)
                }.sortedByDescending { it.updatedAt }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // Messages of a conversation
    fun observeMessages(meUid: String, otherUid: String) = callbackFlow<List<Message>> {
        val cid = conversationIdFor(meUid, otherUid)
        val ref = db.child("conversations").child(cid).child("messages")
        val messages = mutableListOf<Message>()

        val listener = object : ChildEventListener {
            override fun onChildAdded(s: DataSnapshot, prev: String?) {
                s.getValue(Message::class.java)?.let { messages.add(it) }
                trySend(messages.sortedBy { it.timestamp })
            }
            override fun onChildChanged(s: DataSnapshot, prev: String?) {
                s.getValue(Message::class.java)?.let { updated ->
                    val idx = messages.indexOfFirst { it.id == updated.id }
                    if (idx >= 0) messages[idx] = updated
                    trySend(messages.sortedBy { it.timestamp })
                }
            }
            override fun onChildRemoved(s: DataSnapshot) {}
            override fun onChildMoved(s: DataSnapshot, prev: String?) {}
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addChildEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // Send message with fan-out
    suspend fun sendMessage(meUid: String, otherUid: String, text: String) {
        val cid = conversationIdFor(meUid, otherUid)
        val msgRef = db.child("conversations").child(cid).child("messages").push()
        val msgId = msgRef.key ?: throw IllegalStateException("No message key")

        val msg = mapOf(
            "id" to msgId,
            "senderId" to meUid,
            "text" to text,
            "timestamp" to ServerValue.TIMESTAMP
        )

        val updates = hashMapOf<String, Any>(
            "/conversations/$cid/participants/$meUid" to true,
            "/conversations/$cid/participants/$otherUid" to true,
            "/conversations/$cid/lastMessage" to text,
            "/conversations/$cid/updatedAt" to ServerValue.TIMESTAMP,
            "/conversations/$cid/messages/$msgId" to msg,

            "/userConversations/$meUid/$cid/otherUserId" to otherUid,
            "/userConversations/$meUid/$cid/lastMessage" to text,
            "/userConversations/$meUid/$cid/updatedAt" to ServerValue.TIMESTAMP,

            "/userConversations/$otherUid/$cid/otherUserId" to meUid,
            "/userConversations/$otherUid/$cid/lastMessage" to text,
            "/userConversations/$otherUid/$cid/updatedAt" to ServerValue.TIMESTAMP
        )

        db.updateChildren(updates).await()
    }

}
