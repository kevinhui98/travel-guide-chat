package com.example.routestakehome.ui.session


import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.routestakehome.data.local.ChatStorage
import com.example.routestakehome.model.ChatMessage
import com.example.routestakehome.model.ChatSession
import com.example.routestakehome.ui.chat.ChatViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.example.routestakehome.R

@Composable
fun SessionListScreen(
    viewModel: ChatViewModel,
    onNewChat: () -> Unit,
    onChatSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val chatStorage = remember { ChatStorage(context) }
//    val sessions by viewModel.allSessions.collectAsState()
    val sessions by viewModel.allSessionsFlow.collectAsState()
//    var sessions by remember { mutableStateOf(chatStorage.loadSessions().reversed()) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var selectedSessionId by remember { mutableStateOf<String?>(null) }
    var newTitle by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        viewModel.loadAllSessions()
        val stored = chatStorage.loadSessions()
        stored.forEach { session ->
            Log.d("SessionList", "Session: ${session.id} - ${session.title}")
        }
    }
    Scaffold(
        bottomBar = {
            Button(
                onClick = onNewChat,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(56.dp)

            ) {
                Text(text = "+ CREATE NEW")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "Chat with me",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Log.i("SessionListScreen","$sessions")
                items(sessions) { session ->
                    Log.i("chat session","${session.id}")
                    SessionCard(session = session,
                        onClick = { onChatSelected(session.id) },
                        onLongClick = {
                            selectedSessionId = session.id
                            newTitle = session.title
                            showRenameDialog = true
                        })
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Chat") },
            text = {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("New Title") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedSessionId?.let { id ->
                        viewModel.renameSession(id, newTitle)
                    }
                    showRenameDialog = false
                }) {
                    Text("Rename")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRenameDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SessionCard(session: ChatSession, onClick: () -> Unit,onLongClick: () -> Unit) {
    Card(

        modifier = Modifier.fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatDate(session),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Image(
                painter = painterResource(id = R.drawable.chevron_right_24),
                contentDescription = "Go to chat",
                modifier = Modifier.size(24.dp)
            )

        }
    }
}

fun formatDate(session: ChatSession): String {
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val today = sdf.format(Date())
    val sessionDate = sdf.format(Date(session.messages.firstOrNull()?.timestamp ?: 0L))

    return when {
        today == sessionDate -> "Today"
        wasYesterday(session.messages.firstOrNull()?.timestamp ?: 0L) -> "Yesterday"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(session.messages.firstOrNull()?.timestamp ?: 0L))
    }
}

fun wasYesterday(timestamp: Long): Boolean {
    val cal = Calendar.getInstance()
    cal.time = Date()
    cal.add(Calendar.DATE, -1)
    val yesterday = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
    val messageDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(timestamp))
    return yesterday == messageDate
}

@Preview(showBackground = true)
@Composable
fun PreviewSessionCard() {
    val mockSession = ChatSession(
        id = "123",
        title = "Trip to Tokyo",
        messages = listOf(
            ChatMessage("user", "Let's plan a trip!", System.currentTimeMillis() - 86_400_000)
        )
    )
    MaterialTheme {
        SessionCard(
            session = mockSession,
            onClick = {},
            onLongClick = {}
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewSessionListScreen() {
//    val dummySessions = listOf(
//        ChatSession(
//            id = "1",
//            title = "Trip ideas",
//            messages = listOf(ChatMessage("user", "Let's go to Japan", System.currentTimeMillis()))
//        ),
//        ChatSession(
//            id = "2",
//            title = "Work notes",
//            messages = listOf(ChatMessage("assistant", "Here’s your daily standup summary.", System.currentTimeMillis()))
//        )
//    )
//
//    val fakeViewModel = object : ChatViewModel(ChatStorage(LocalContext.current), LLMApi("fake")) {
//        private val _sessions = MutableStateFlow(dummySessions)
//        override val allSessions: StateFlow<List<ChatSession>> get() = _sessions
//    }
//    MaterialTheme {
//        SessionListScreen(
//            onNewChat = {},
//            onChatSelected = {},
//            viewModel = fakeViewModel
//        )
//    }
//}