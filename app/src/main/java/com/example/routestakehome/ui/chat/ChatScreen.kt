package com.example.routestakehome.ui.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.routestakehome.data.local.ChatStorage
import com.example.routestakehome.data.remote.LLMApi
import com.example.routestakehome.model.ChatMessage
import com.example.routestakehome.ui.components.ChatBubble
import com.example.routestakehome.BuildConfig
import com.example.routestakehome.BuildConfig.API_KEY
import androidx.compose.material.icons.filled.ArrowBack
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.rememberLazyListState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    isNewChat: Boolean,
    existingSessionId: String? = null,
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel = remember {
        ChatViewModel(
            chatStorage = ChatStorage(context),
            llmApi = LLMApi(apiKey = API_KEY) // Replace with your real key
        )
    }

    val session by viewModel.session.collectAsState()
    var input by remember { mutableStateOf(TextFieldValue("")) }

    // Initialize session
    LaunchedEffect(Unit) {
        if (isNewChat) {
            viewModel.startNewSession()
        }
        else existingSessionId?.let { viewModel.loadSession(it) }
    }

    val listState = rememberLazyListState()
    val messageCount = session?.messages?.size ?: 0

    LaunchedEffect(messageCount) {
        if (messageCount > 0) {
            listState.animateScrollToItem(messageCount - 1)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(session?.title ?: "Chat") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .background(Color.White),
                verticalAlignment = Alignment.CenterVertically,

            ) {
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Type here...") },
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                )
                IconButton(
                    onClick = {
                        val message = input.text.trim()
                        if (message.isNotEmpty()) {
                            viewModel.sendMessage(userMessage = message)
                            input = TextFieldValue("")
                        }
                    },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    ) { padding ->
        Column {
            LazyColumn(
                state = listState,
                contentPadding = padding,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                session?.messages?.let { messages ->
                    items(messages) { msg ->
                        ChatBubble(message = msg)
                    }
                }
            }

        }

    }
}
