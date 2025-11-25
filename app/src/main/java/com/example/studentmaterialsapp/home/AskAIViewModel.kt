package com.example.studentmaterialsapp.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// --- Data Model for Chat Messages ---
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isError: Boolean = false
)

// --- ViewModel for AI Logic ---
class AskAIViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Initialize Gemini Model
    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        // Using the API Key found in the previous version of this file
        apiKey = "AIzaSyAzvMzaOkHje3cNUHEIlhqQqBwkPPjbAz0 "
    )

    fun sendMessage(userInput: String) {
        if (userInput.isBlank()) return

        // Add user message to list
        val userMessage = ChatMessage(text = userInput, isUser = true)
        _messages.value = _messages.value + userMessage
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // Send prompt to Gemini
                val response = generativeModel.generateContent(userInput)
                val aiResponseText = response.text ?: "I couldn't generate a response."

                // Add AI response to list
                val aiMessage = ChatMessage(text = aiResponseText, isUser = false)
                _messages.value = _messages.value + aiMessage
            } catch (e: Exception) {
                // Handle Errors
                val errorMessage = ChatMessage(
                    text = "Error: ${e.localizedMessage ?: "Unknown error occurred"}",
                    isUser = false,
                    isError = true
                )
                _messages.value = _messages.value + errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }
}
