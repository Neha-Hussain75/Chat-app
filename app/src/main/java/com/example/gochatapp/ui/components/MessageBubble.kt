package com.example.gochatapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Surface
import androidx. compose. runtime. remember
import androidx. compose. ui. unit. sp
@Composable
fun MessageBubble(
    message: String,
    timestamp: Long,
    isSentByCurrentUser: Boolean,
    sentBubbleColor: Color = Color(0xFF1985F2),
    receivedBubbleColor: Color = Color(0xFFF0F0F0),
    sentTextColor: Color = Color.White,
    receivedTextColor: Color = Color.Black
) {
    val bubbleColor = if (isSentByCurrentUser) sentBubbleColor else receivedBubbleColor
    val textColor = if (isSentByCurrentUser) sentTextColor else receivedTextColor
    val formattedTime = remember(timestamp) {
        java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(timestamp))
    }
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isSentByCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(message, color = textColor)

                Spacer(Modifier.height(2.dp))

                Text(
                    formattedTime,
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.End) // time right align
                )
            }
        }
    }
}
