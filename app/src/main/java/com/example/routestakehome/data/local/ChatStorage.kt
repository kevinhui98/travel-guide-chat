package com.example.routestakehome.data.local


import android.content.Context
import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.example.routestakehome.model.ChatSession
import java.io.File

class ChatStorage(private val context: Context) {

    private val fileName = "chat_sessions.json"

    fun saveSessions(sessions: List<ChatSession>) {
        try {
            val json = Json.encodeToString(sessions)
            File(context.filesDir, fileName).writeText(json)
            Log.i("ChatStorage", "$json")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadSessions(): List<ChatSession> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return emptyList()

        return try {
            val json = file.readText()
            Json.decodeFromString(json)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    fun logRawFile() {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            Log.i("ChatStorage", "File content:\n${file.readText()}")
        } else {
            Log.i("ChatStorage", "No file found")
        }
    }

}
