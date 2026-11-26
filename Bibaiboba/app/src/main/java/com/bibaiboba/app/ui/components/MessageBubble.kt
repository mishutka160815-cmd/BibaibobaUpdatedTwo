package com.bibaiboba.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bibaiboba.app.data.model.ChatMessage
import com.bibaiboba.app.data.model.ChatRole
import com.bibaiboba.app.ui.theme.AiBubble
import com.bibaiboba.app.ui.theme.ErrorRed
import com.bibaiboba.app.ui.theme.NeonCyan
import com.bibaiboba.app.ui.theme.NeonPink
import com.bibaiboba.app.ui.theme.NeonPurple
import com.bibaiboba.app.ui.theme.StrokeSoft
import com.bibaiboba.app.ui.theme.TextPrimary
import com.bibaiboba.app.ui.theme.TextSecondary
import com.bibaiboba.app.ui.theme.UserBubble
import com.bibaiboba.app.ui.theme.UserBubble2

@Composable
fun MessageBubble(
    message: ChatMessage,
    animate: Boolean = true,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == ChatRole.USER
    val shape = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = if (isUser) 20.dp else 6.dp,
        bottomEnd = if (isUser) 6.dp else 20.dp
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(NeonPurple, NeonCyan)))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(shape)
                .then(
                    if (isUser) {
                        Modifier.background(Brush.linearGradient(listOf(UserBubble, UserBubble2, NeonPink.copy(alpha = 0.9f))))
                    } else if (message.isError) {
                        Modifier
                            .background(ErrorRed.copy(alpha = 0.12f))
                            .border(1.dp, ErrorRed.copy(alpha = 0.4f), shape)
                    } else {
                        Modifier
                            .background(AiBubble)
                            .border(1.dp, Brush.linearGradient(listOf(StrokeSoft, NeonPurple.copy(alpha = 0.2f))), shape)
                    }
                )
                .padding(horizontal = 14.dp, vertical = 11.dp)
        ) {
            Column {
                if (!isUser) {
                    Text(
                        text = "Bibaiboba",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (message.isError) ErrorRed else NeonCyan
                    )
                }
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isUser) TextPrimary else if (message.isError) Color(0xFFFFCDD8) else TextPrimary
                )
            }
        }

        if (isUser) {
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
