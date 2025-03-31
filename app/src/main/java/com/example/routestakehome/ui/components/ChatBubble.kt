package com.example.routestakehome.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.routestakehome.model.ChatMessage
import com.example.routestakehome.ui.theme.UserColor
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import androidx.core.text.HtmlCompat
@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    val bubbleColor = if (isUser) UserColor else Color.Black
    val textColor = if (isUser) Color.Black else Color.White
    val alignment = if (isUser) Arrangement.End else Arrangement.Start
    val formattedTime = remember(message.timestamp) {
        val sdf = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
        sdf.format(java.util.Date(message.timestamp))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = alignment
    ) {
        Column (
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(bubbleColor)
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            val flavour = CommonMarkFlavourDescriptor()
            val parsdTree = MarkdownParser(flavour).buildMarkdownTreeFromString(message.content)
            val html = HtmlGenerator(message.content,parsdTree,flavour).generateHtml()
            val cleanText = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            Text(
                text = if (isUser) message.content else cleanText,
                color = textColor
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isUser) Color.Black else Color.LightGray
                )
            }
        }
    }
}

@Preview
@Composable
private fun ChatBubblePreview() {
    ChatBubble(ChatMessage("user","Hello",1111))

}