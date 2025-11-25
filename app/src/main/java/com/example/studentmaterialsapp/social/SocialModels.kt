package com.example.studentmaterialsapp.social

import java.util.UUID

// Represents a student's question (Legacy/Alternative)
data class DiscussionPost(
    val id: String = UUID.randomUUID().toString(),
    val authorName: String,
    val topicName: String, // e.g., "Algebra"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val likes: Int = 0
)

// Represents a reply to a question (Legacy/Alternative)
data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val postId: String, // Links this comment to a specific post
    val authorName: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

// Represents a chat message in the Discussion Room
data class DiscussionMessage(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val timestamp: Long = 0L
)
