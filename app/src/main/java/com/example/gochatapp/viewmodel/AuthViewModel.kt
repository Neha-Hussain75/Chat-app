package com.example.gochatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gochatapp.data.repo.UserRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val usersRef = FirebaseDatabase.getInstance().getReference("users")

    fun register(
        name: String,
        email: String,
        password: String,
        callback: (Boolean, String?, String?) -> Unit
    ) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        if (uid != null) {
                            val userMap = mapOf(
                                "userId" to uid,
                                "name" to name,
                                "email" to email,
                                "profilePic" to ""
                            )
                            usersRef.child(uid).setValue(userMap)

                            // ✅ Save FCM token
                            saveFcmToken(uid)

                            callback(true, uid, null)
                        } else {
                            callback(false, null, "Failed to get UID")
                        }
                    } else {
                        callback(false, null, task.exception?.message)
                    }
                }
        }
    }

    fun login(
        email: String,
        password: String,
        callback: (success: Boolean, uid: String?, message: String?) -> Unit
    ) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        if (uid != null) {
                            // ✅ Update token on login also
                            saveFcmToken(uid)
                        }
                        callback(true, uid, null)
                    } else {
                        callback(false, null, task.exception?.message)
                    }
                }
        }
    }

    fun logout() {
        auth.signOut()
    }

    // ✅ Centralized method for saving token
    private fun saveFcmToken(uid: String) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                viewModelScope.launch {
                    UserRepo().saveFcmToken(uid, token)
                }
            }
    }
}
