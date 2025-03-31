package com.example.routestakehome.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

import com.example.routestakehome.data.remote.models.GPTMessage
import com.example.routestakehome.data.remote.models.GPTRequest
import com.example.routestakehome.data.remote.models.GPTResponse
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import android.util.Log
import io.ktor.client.statement.bodyAsText

class LLMApi(private val apiKey: String) {

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }

        defaultRequest {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun getResponse(messages: List<GPTMessage>): GPTResponse? {
        Log.i("LLLMApi", "calling get response")
         try {
            val response: GPTResponse = client.post("https://openrouter.ai/api/v1/chat/completions") {
                setBody(
                    GPTRequest(
                        model = "google/gemini-2.0-flash-exp:free",
                        messages = messages
                    )
                )
            }.body()
//            Log.d("LLMApi", "Response: ${response.choices.firstOrNull()?.message?.content ?: "No content"}")
            return response
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
