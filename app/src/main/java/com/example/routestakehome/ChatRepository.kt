package com.example.routestakehome

import com.example.routestakehome.data.local.ChatStorage
import com.example.routestakehome.data.remote.LLMApi
import com.example.routestakehome.data.remote.models.GPTMessage
import com.example.routestakehome.data.remote.models.GPTResponse
import com.example.routestakehome.model.ChatMessage
import com.example.routestakehome.model.ChatSession

//class ChatRepository(
//    private val storage: ChatStorage,
//    private val api: LLMApi
//) {
//    fun loadSessions() = storage.loadSessions()
//
//    fun saveSessions(sessions: List<ChatSession>) = storage.saveSessions(sessions)
//
//    suspend fun sendMessage(session: ChatSession, message: ChatMessage): GPTResponse? {
//        val updatedMessages = session.messages + message
//        return api.getResponse(updatedMessages.map {
//            GPTMessage(role = it.role, content = it.content)
//        })
//    }
//}
