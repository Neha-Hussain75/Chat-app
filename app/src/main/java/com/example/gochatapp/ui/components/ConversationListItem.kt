package com.example.gochatapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gochatapp.viewmodel.ChatUser
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ConversationListItem(
    conv: ChatUser,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedTime = if (conv.updatedAt > 0) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.format(Date(conv.updatedAt))
    } else ""
    println("DEBUG updatedAt: ${conv.updatedAt}")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(conv.uid) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF1985F2)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = conv.username.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = conv.username,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF000307)
            )

            if (conv.lastMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = conv.lastMessage,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }

        if (formattedTime.isNotEmpty()) {
            Text(
                text = formattedTime,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
