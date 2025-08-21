package com.example.gochatapp.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun register(email: String, password: String, displayName: String, avatarUrl: String? = null) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = auth.currentUser
        user?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(avatarUrl?.let { android.net.Uri.parse(it) }) // 🔹 avatar set
                .build()
        )?.await()

        // 🔹 Realtime Database me save
        result.user?.uid?.let { uid ->
            createOrUpdateUserProfile(uid, displayName, email, avatarUrl)
        }
    }

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun createOrUpdateUserProfile(
        uid: String,
        displayName: String,
        email: String,
        avatarUrl: String? = null
    ) {
        val db = FirebaseDatabase.getInstance().reference
        val map = mutableMapOf<String, Any?>(
            "username" to displayName,
            "email" to email,
            "avatarUrl" to avatarUrl,
            "isOnline" to true
        )
        db.child("users").child(uid).updateChildren(map)
            .addOnFailureListener { throw it }.await()
    }

    fun logout() {
        auth.signOut()
    }

    fun currentUserId(): String? = auth.currentUser?.uid
}
