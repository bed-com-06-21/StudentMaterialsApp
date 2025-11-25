package com.example.studentmaterialsapp.home

import androidx.compose.ui.graphics.Color

data class SubjectProgress(
    val subjectName: String,
    val progressPercentage: Float, // 0.0f to 1.0f (e.g., 0.75 is 75%)
    val color: Color
)

// Mock data generator (In a real app, this comes from a database)
fun getMockProgress(): List<SubjectProgress> {
    return listOf(
        SubjectProgress("Mathematics", 0.75f, Color(0xFF4CAF50)), // Green
        SubjectProgress("English", 0.45f, Color(0xFF2196F3)),     // Blue
        SubjectProgress("Biology", 0.30f, Color(0xFFFF9800)),     // Orange
        SubjectProgress("History", 0.10f, Color(0xFFE91E63))      // Pink
    )
}
