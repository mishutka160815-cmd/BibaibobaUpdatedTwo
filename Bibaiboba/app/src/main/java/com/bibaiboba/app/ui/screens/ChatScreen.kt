package com.bibaiboba.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.bibaiboba.app.ui.ChatViewModel
import com.bibaiboba.app.ui.components.AnimatedBackground
import com.bibaiboba.app.ui.components.GlassCard
import com.bibaiboba.app.ui.components.MessageBubble
import com.bibaiboba.app.ui.components.TypingIndicator
import com.bibaiboba.app.ui.theme.NeonCyan
import com.bibaiboba.app.ui.theme.NeonPink
import com.bibaiboba.app.ui.theme.NeonPurple
import com.bibaiboba.app.ui.theme.StrokeSoft
import com.bibaiboba.app.ui.theme.TextMuted
import com.bibaiboba.app.ui.theme.TextPrimary
import com.bibaiboba.app.ui.theme.TextSecondary
import com.bibaiboba.app.util.DeviceAction

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onOpenSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size, uiState.isLoading) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1 + if (uiState.isLoading) 1 else 0)
        }
    }

    AnimatedBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            TopBar(
                onOpenSettings = onOpenSettings,
                onClear = { viewModel.clearChat() }
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.messages, key = { it.id }) { message ->
                    MessageBubble(message = message)
                }
                if (uiState.isLoading) {
                    item(key = "typing") {
                        TypingIndicator()
                    }
                }
            }

            QuickActions(
                onPrompt = { viewModel.sendQuickPrompt(it) },
                onAction = { viewModel.executeAction(it) }
            )

            InputBar(
                value = uiState.input,
                onValueChange = viewModel::onInputChange,
                onSend = viewModel::sendCurrentInput,
                enabled = !uiState.isLoading
            )
        }
    }
}

@Composable
private fun TopBar(onOpenSettings: () -> Unit, onClear: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(NeonPurple, NeonCyan, NeonPink)))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = Color.White)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Bibaiboba",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Text(
                text = "ИИ-помощник • управление телефоном",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
        }
        IconButton(onClick = onClear) {
            Icon(Icons.Rounded.DeleteSweep, contentDescription = "Очистить", tint = TextSecondary)
        }
        IconButton(onClick = onOpenSettings) {
            Icon(Icons.Rounded.Settings, contentDescription = "Настройки", tint = TextSecondary)
        }
    }
}

@Composable
private fun QuickActions(
    onPrompt: (String) -> Unit,
    onAction: (DeviceAction) -> Unit
) {
    val actions = listOf(
        Triple("🔍 Поиск", null, "Найди интересные новости технологий сегодня"),
        Triple("⏱ Таймер", DeviceAction("TIMER", "300"), null),
        Triple("🗺 Карты", null, "Открой карты и покажи ближайшие кафе"),
        Triple("⚙️ Настройки", DeviceAction("SETTINGS", "main"), null),
        Triple("📺 YouTube", DeviceAction("OPEN_APP", "YouTube"), null),
        Triple("🌐 Браузер", DeviceAction("OPEN_URL", "https://www.google.com"), null),
        Triple("📞 Телефон", DeviceAction("OPEN_APP", "Phone"), null),
        Triple("💬 WhatsApp", DeviceAction("OPEN_APP", "WhatsApp"), null),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        actions.forEach { (label, action, prompt) ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0x22FFFFFF))
                    .border(1.dp, StrokeSoft, RoundedCornerShape(999.dp))
                    .clickable {
                        when {
                            action != null -> onAction(action)
                            prompt != null -> onPrompt(prompt)
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(text = label, style = MaterialTheme.typography.labelLarge, color = TextPrimary)
            }
        }
    }
}

@Composable
private fun InputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "Спроси Bibaiboba или дай команду...",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = NeonCyan,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                maxLines = 5
            )
            Spacer(Modifier.width(6.dp))
            val canSend = value.isNotBlank() && enabled
            Box(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (canSend) Brush.linearGradient(listOf(NeonPurple, NeonCyan))
                        else Brush.linearGradient(listOf(Color(0x33FFFFFF), Color(0x22FFFFFF)))
                    )
                    .clickable(enabled = canSend) { onSend() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = "Отправить",
                    tint = if (canSend) Color.White else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
