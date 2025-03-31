package com.example.routestakehome

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.routestakehome.data.local.ChatStorage
import com.example.routestakehome.data.remote.LLMApi
import com.example.routestakehome.BuildConfig.API_KEY
import com.example.routestakehome.ui.chat.ChatScreen
import com.example.routestakehome.ui.chat.ChatViewModel
import com.example.routestakehome.ui.session.SessionListScreen
@Composable
fun ChatAIApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel = remember {
        ChatViewModel(
            chatStorage = ChatStorage(context),
            llmApi = LLMApi(apiKey = BuildConfig.API_KEY),
        )
    }
    LaunchedEffect(Unit) {
        viewModel.loadAllSessions()
        viewModel.logAllStoredSessions("ChatAIApp")
    }
    NavHost(navController, startDestination = "sessions") {
        composable("sessions") {
            SessionListScreen(
                viewModel = viewModel,
                onNewChat = { navController.navigate("chat/new") },
                onChatSelected = { sessionId ->
                    navController.navigate("chat/$sessionId")
                }
            )
        }
        composable("chat/new") {
            Log.i("ChatAIApp","$viewModel")
            ChatScreen(isNewChat = true,navController = navController)
        }
        composable("chat/{sessionId}") { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId")
            ChatScreen(isNewChat = false, existingSessionId = sessionId,navController = navController)
        }
    }
}

