package com.example.studentmaterialsapp.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SummaryViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Holds the text the student writes
    private val _noteContent = MutableStateFlow("")
    val noteContent: StateFlow<String> = _noteContent.asStateFlow()

    // Holds the last updated timestamp string
    private val _lastUpdated = MutableStateFlow<String?>(null)
    val lastUpdated: StateFlow<String?> = _lastUpdated.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Load existing notes for this specific topic
    fun loadSummary(topic: String) {
        val userId = auth.currentUser?.uid ?: return
        _isLoading.value = true

        // Sanitize topic name for document ID (replace / with _)
        val safeDocId = topic.replace("/", "_")

        // Path: users/{userId}/summaries/{safeDocId}
        db.collection("users").document(userId)
            .collection("summaries").document(safeDocId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val content = document.getString("content") ?: ""
                    val timestamp = document.getLong("lastUpdated") ?: 0L
                    
                    _noteContent.value = content
                    if (timestamp > 0) {
                        _lastUpdated.value = formatTime(timestamp)
                    }
                } else {
                    _noteContent.value = "" // No notes yet
                    _lastUpdated.value = null
                }
                _isLoading.value = false
            }
            .addOnFailureListener {
                _statusMessage.value = "Failed to load notes"
                _isLoading.value = false
            }
    }

    // Update text state as user types
    fun updateContent(newText: String) {
        _noteContent.value = newText
    }

    // Save to Firebase
    fun saveSummary(topic: String) {
        val userId = auth.currentUser?.uid ?: return
        _isLoading.value = true

        val currentTime = System.currentTimeMillis()
        val safeDocId = topic.replace("/", "_")

        val data = hashMapOf(
            "content" to _noteContent.value,
            "topic" to topic,
            "lastUpdated" to currentTime
        )

        // Save using the Topic Name as the ID so it's easy to find later
        db.collection("users").document(userId)
            .collection("summaries").document(safeDocId)
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                _statusMessage.value = "Summary saved successfully!"
                _lastUpdated.value = formatTime(currentTime)
                _isLoading.value = false
            }
            .addOnFailureListener { e ->
                _statusMessage.value = "Error saving: ${e.message}"
                _isLoading.value = false
            }
    }

    fun clearStatus() {
        _statusMessage.value = null
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
