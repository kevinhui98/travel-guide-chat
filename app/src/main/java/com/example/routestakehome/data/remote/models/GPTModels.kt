package com.example.routestakehome.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class GPTMessage(
    val role: String,
    val content: String
)

@Serializable
data class GPTRequest(
    val model: String,
    val messages: List<GPTMessage>
)

@Serializable
data class GPTChoice(
    val message: GPTMessage
)

@Serializable
data class GPTResponse(
    val choices: List<GPTChoice>
)