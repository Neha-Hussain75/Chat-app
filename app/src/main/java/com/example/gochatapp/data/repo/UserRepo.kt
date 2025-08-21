package com.example.gochatapp.data.repo

import com.example.gochatapp.data.model.User
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepo {
    private val db = FirebaseDatabase.getInstance().reference

    fun observeAllUsersExcept(meUid: String) = callbackFlow<List<User>> {
        val ref = db.child("users")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull {
                    it.getValue(User::class.java)?.copy(id = it.key ?: "")
                }.filter { it.id != meUid }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun setOnline(uid: String, online: Boolean) {
        db.child("users").child(uid).child("isOnline").setValue(online).await()
    }

    suspend fun saveFcmToken(uid: String, token: String) {
        db.child("users").child(uid).child("fcmToken").setValue(token).await()
    }
}
