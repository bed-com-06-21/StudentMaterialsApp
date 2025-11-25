package com.example.studentmaterialsapp.timetable

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class TimeTableViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // State: List of timetable entries
    private val _timeTable = MutableStateFlow<List<TimeTableEntry>>(emptyList())
    val timeTable = _timeTable.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage = _statusMessage.asStateFlow()

    // Load data when screen opens
    fun loadTimeTable() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("timetable")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _statusMessage.value = "Error loading timetable"
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val entries = snapshot.toObjects(TimeTableEntry::class.java)
                    // Sort slightly by Day/Time if needed, or handle in UI
                    _timeTable.value = entries
                }
            }
    }

    // Add a new class entry
    fun addEntry(day: String, time: String, subject: String) {
        val userId = auth.currentUser?.uid ?: return
        if (day.isBlank() || time.isBlank() || subject.isBlank()) return

        val id = UUID.randomUUID().toString()
        val entry = TimeTableEntry(id, day, time, subject)

        db.collection("users").document(userId)
            .collection("timetable").document(id)
            .set(entry)
            .addOnSuccessListener {
                _statusMessage.value = "Class added!"
            }
            .addOnFailureListener {
                _statusMessage.value = "Failed to add class."
            }
    }

    // Delete an entry
    fun deleteEntry(id: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("timetable").document(id)
            .delete()
            .addOnSuccessListener {
                _statusMessage.value = "Class removed"
            }
    }

    fun clearStatus() {
        _statusMessage.value = null
    }
}
