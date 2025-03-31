package com.example.routestakehome.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatSession(
    val id: String,
    val title: String,
    val messages: List<ChatMessage>
)

@Serializable
data class ChatMessage(
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: Long
)
