package com.example.studentmaterialsapp.social

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class DiscussionViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // State: List of messages
    private val _messages = MutableStateFlow<List<DiscussionMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    // Listen for messages in a specific topic (e.g., "Algebra")
    fun loadMessages(topicName: String) {
        // Create a safe collection path name
        val collectionPath = "discussions_${topicName.replace(" ", "_").lowercase()}"

        db.collection(collectionPath)
            .orderBy("timestamp", Query.Direction.ASCENDING) // Oldest first
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Map documents to DiscussionMessage objects
                    val msgList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(DiscussionMessage::class.java)?.copy(id = doc.id)
                    }
                    _messages.value = msgList
                }
            }
    }

    // Send a new message
    fun sendMessage(topicName: String, text: String) {
        val currentUser = auth.currentUser ?: return
        if (text.isBlank()) return

        val collectionPath = "discussions_${topicName.replace(" ", "_").lowercase()}"
        
        // Use the user's display name (set during SignUp) or fall back to email prefix or "Student"
        val senderName = currentUser.displayName 
            ?: currentUser.email?.substringBefore("@") 
            ?: "Student"

        val newMessage = DiscussionMessage(
            senderId = currentUser.uid,
            senderName = senderName,
            text = text.trim(),
            timestamp = System.currentTimeMillis()
        )

        // Add to Firestore (auto-ID)
        db.collection(collectionPath).add(newMessage)
    }

    // Helper to get current user ID for UI styling
    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }
}
