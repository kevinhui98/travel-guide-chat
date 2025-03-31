package com.example.routestakehome.ui.chat


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routestakehome.data.remote.models.GPTMessage
import com.example.routestakehome.model.ChatMessage
import com.example.routestakehome.model.ChatSession
import com.example.routestakehome.data.local.ChatStorage
import com.example.routestakehome.data.remote.LLMApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class ChatViewModel(
    private val chatStorage: ChatStorage,
    private val llmApi: LLMApi
) : ViewModel() {
    // The currently active chat
    private val _session = MutableStateFlow<ChatSession?>(null)
    val session: StateFlow<ChatSession?> = _session

    // All chats
    private val allSessions: MutableList<ChatSession> = mutableListOf()
    private val _allSessions = MutableStateFlow<List<ChatSession>>(emptyList())
    val allSessionsFlow: StateFlow<List<ChatSession>> = _allSessions

    fun loadAllSessions() {
        chatStorage.logRawFile()
        val stored = chatStorage.loadSessions()
        allSessions.clear()
        allSessions.addAll(stored)
        _allSessions.value = allSessions.toList()
    }
    fun startNewSession(): ChatSession {
        val welcomeMessages = listOf(
            ChatMessage("assistant", "Welcome! I'm your travel assistant.", System.currentTimeMillis()),
            ChatMessage("assistant", "Where would you like to travel to?", System.currentTimeMillis() + 1)
        )
        // Only load once if allSessions is empty
        if (allSessions.isEmpty()) {
            val stored = chatStorage.loadSessions()
            allSessions.clear()
            allSessions.addAll(stored)
            _allSessions.value = allSessions.toList()
        }

        val newSession = ChatSession(
            id = UUID.randomUUID().toString(),
            title = "New Chat",
            messages = welcomeMessages
        )
        _session.value = newSession
        allSessions.add(newSession)
        _allSessions.value = allSessions.toList()
        chatStorage.saveSessions(allSessions)
        return newSession
    }

    fun loadSession(sessionId: String)  {
        // Only load once if allSessions is empty
        if (allSessions.isEmpty()) {
            val stored = chatStorage.loadSessions()
            allSessions.clear()
            allSessions.addAll(stored)
            _allSessions.value = allSessions.toList()
        }
        val session = allSessions.find { it.id == sessionId }
        _session.value = session
    }
    fun logAllStoredSessions(tag: String = "ChatStorage log all") {
        val sessions = chatStorage.loadSessions()
        Log.i(tag, "Logging all sessions (${sessions.size}):")
        sessions.forEach { session ->
            Log.i(tag, "Session: ${session.id} - ${session.title}")
            session.messages.forEach { msg ->
                Log.i(tag, "  ${msg.role}: ${msg.content}")
            }
        }
    }
     fun sendMessage(userMessage: String) {
        val current = _session.value ?: return
        val userMsg = ChatMessage("user", userMessage, System.currentTimeMillis())
        Log.i("current val", "$current")
        val updated = current.copy(messages = current.messages + userMsg)
        _session.value = updated
        Log.i("sendMessage ", "$allSessions")
        viewModelScope.launch {
            val systemPrompt = GPTMessage("system",
                "You are a friendly and knowledgeable travel assistant AI. " +
                "Your job is to help users plan their trips. " +
                "When a user tells you where they want to go, ask follow-up questions to understand their travel preferences, such as dates, budget, interests " +
                "(e.g., sightseeing, food, adventure), number of travelers, and preferred accommodations or transportation. " +
                "Use this information to provide helpful suggestions like itineraries, flight and hotel options, places to visit, and travel tips. " +
                "Always keep the conversation clear, engaging, and easy to follow.\n" +
                "\n" +
                "Wait for the user to give more details after they mention where they want to go. " +
                "Be proactive but not overwhelming. Make sure to ask clarifying questions if anything is unclear.")
            val assistantResponse = llmApi.getResponse(
                messages = listOf(systemPrompt) + updated.messages.map {
                    GPTMessage(it.role, it.content)
                }
            )
            assistantResponse?.let {
                val assistantMsg = ChatMessage("assistant", it.choices.firstOrNull()?.message?.content.orEmpty(), System.currentTimeMillis())
                val newSession = _session.value?.copy(messages = _session.value!!.messages + assistantMsg)
                _session.value = newSession
                if (!newSession?.title.isNullOrBlank() && newSession.title == "New Chat") {
                    // Ask the LLM to title the chat
                    generateTitleForSession(newSession)
                }
                val index = allSessions.indexOfFirst { it.id == newSession?.id }
                if (index >= 0 && newSession != null) {
                    allSessions[index] = newSession
                } else if (newSession != null) {
                    allSessions.add(newSession)
                }
                _allSessions.value = allSessions.toList()
                chatStorage.saveSessions(allSessions)
            }
        }
    }
    fun renameSession(sessionId: String, newTitle: String) {
        val index = allSessions.indexOfFirst { it.id == sessionId }
        if (index >= 0) {
            val updated = allSessions[index].copy(title = newTitle)
            allSessions[index] = updated
            _allSessions.value = allSessions.toList()

            if (_session.value?.id == sessionId) {
                _session.value = updated
            }

            chatStorage.saveSessions(allSessions)
        }
    }

    private fun generateTitleForSession(session: ChatSession) {
        viewModelScope.launch {
            val titlePrompt = listOf(
                GPTMessage("system", "You are an assistant that generates a short, relevant title for a chat session."),
                GPTMessage("user", "Here is the conversation:\n" +
                        session.messages.joinToString("\n") { "${it.role}: ${it.content}" } +
                        "\n\nGive a concise title for this chat.")
            )

            val response = llmApi.getResponse(titlePrompt)
            val titleSuggestion = response?.choices?.firstOrNull()?.message?.content?.trim()

            if (!titleSuggestion.isNullOrBlank()) {
                val updatedSession = session.copy(title = titleSuggestion)

                // Update the session in the list
                val index = allSessions.indexOfFirst { it.id == session.id }
                if (index >= 0) {
                    allSessions[index] = updatedSession
                    _allSessions.value = allSessions.toList()
                    chatStorage.saveSessions(allSessions)

                    // Also update the current session shown on screen
                    _session.value = updatedSession
                }
            }
        }
    }

}

