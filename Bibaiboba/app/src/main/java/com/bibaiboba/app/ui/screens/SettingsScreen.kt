package com.bibaiboba.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.bibaiboba.app.ui.ChatViewModel
import com.bibaiboba.app.ui.components.AnimatedBackground
import com.bibaiboba.app.ui.components.GlassCard
import com.bibaiboba.app.ui.theme.NeonCyan
import com.bibaiboba.app.ui.theme.NeonPink
import com.bibaiboba.app.ui.theme.NeonPurple
import com.bibaiboba.app.ui.theme.StrokeSoft
import com.bibaiboba.app.ui.theme.TextMuted
import com.bibaiboba.app.ui.theme.TextPrimary
import com.bibaiboba.app.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    viewModel: ChatViewModel,
    onBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()

    var apiBaseUrl by remember { mutableStateOf(settings.apiBaseUrl) }
    var apiKey by remember { mutableStateOf(settings.apiKey) }
    var model by remember { mutableStateOf(settings.model) }
    var userName by remember { mutableStateOf(settings.userName) }
    var persona by remember { mutableStateOf(settings.assistantPersona) }
    var showKey by remember { mutableStateOf(false) }
    var lastSettings by remember { mutableStateOf<com.bibaiboba.app.data.model.AppSettings?>(null) }

    LaunchedEffect(settings) {
        val prev = lastSettings
        val userDirty = prev != null && (
            apiBaseUrl != prev.apiBaseUrl ||
            apiKey != prev.apiKey ||
            model != prev.model ||
            userName != prev.userName ||
            persona != prev.assistantPersona
        )
        if (!userDirty) {
            apiBaseUrl = settings.apiBaseUrl
            apiKey = settings.apiKey
            model = settings.model
            userName = settings.userName
            persona = settings.assistantPersona
        }
        lastSettings = settings
    }

    AnimatedBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Назад", tint = TextPrimary)
                }
                Spacer(Modifier.width(4.dp))
                Column {
                    Text("Настройки", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                    Text("Bibaiboba под твоё устройство", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }

            Spacer(Modifier.height(16.dp))

            SettingsSection(title = "ИИ-подключение", icon = { Icon(Icons.Rounded.Key, null, tint = NeonCyan, modifier = Modifier.size(18.dp)) }) {
                SettingsField(label = "Base URL API", value = apiBaseUrl, onValueChange = { apiBaseUrl = it }, placeholder = "https://api.openai.com/v1/")
                Spacer(Modifier.height(10.dp))
                SettingsField(
                    label = "API Key",
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    placeholder = "Введите ключ",
                    visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                    trailing = {
                        Text(
                            text = if (showKey) "Скрыть" else "Показать",
                            color = NeonCyan,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x22FFFFFF))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .clickableNoRipple { showKey = !showKey }
                        )
                    }
                )
                Spacer(Modifier.height(10.dp))
                SettingsField(label = "Модель", value = model, onValueChange = { model = it }, placeholder = "gpt-4o-mini")
                Spacer(Modifier.height(12.dp))
                SaveButton(text = "Сохранить ИИ-настройки") {
                    viewModel.updateApiBaseUrl(apiBaseUrl.trim())
                    viewModel.updateApiKey(apiKey.trim())
                    viewModel.updateModel(model.trim())
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Ключ, который вы указали при создании приложения, уже подставлен по умолчанию. Можно заменить его в этом поле.",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
            }

            Spacer(Modifier.height(14.dp))

            SettingsSection(title = "Персонализация", icon = { Icon(Icons.Rounded.Tune, null, tint = NeonPink, modifier = Modifier.size(18.dp)) }) {
                SettingsField(label = "Ваше имя", value = userName, onValueChange = { userName = it }, placeholder = "Как к вам обращаться?")
                Spacer(Modifier.height(10.dp))
                SettingsField(label = "Персона ассистента", value = persona, onValueChange = { persona = it }, placeholder = "Системный промпт", singleLine = false, minLines = 3)
                Spacer(Modifier.height(12.dp))
                SaveButton(text = "Сохранить персонализацию") {
                    viewModel.updateUserName(userName.trim())
                    viewModel.updatePersona(persona.trim())
                }
            }

            Spacer(Modifier.height(14.dp))

            SettingsSection(title = "Поведение", icon = { Icon(Icons.Rounded.AutoAwesome, null, tint = NeonPurple, modifier = Modifier.size(18.dp)) }) {
                ToggleRow("Виброотклик", "Тактильный отклик на действия", settings.hapticsEnabled) { viewModel.updateHaptics(it) }
                Spacer(Modifier.height(8.dp))
                ToggleRow("Анимации", "Эффекты и плавные переходы", settings.animationsEnabled) { viewModel.updateAnimations(it) }
            }

            Spacer(Modifier.height(14.dp))

            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Управление телефоном", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Для расширенной автоматизации (нажатия, прокрутка, действия внутри других приложений) включите службу доступности Bibaiboba в системных настройках Android. Базовые действия (открыть приложение, таймер, поиск, настройки, карты) работают без неё.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF)),
                    contentAlignment = Alignment.Center
                ) { icon() }
                Spacer(Modifier.width(10.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            }
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun SettingsField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: @Composable (() -> Unit)? = null
) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(Modifier.height(6.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, StrokeSoft, RoundedCornerShape(14.dp)),
            placeholder = { Text(placeholder, color = TextMuted) },
            singleLine = singleLine,
            minLines = minLines,
            visualTransformation = visualTransformation,
            trailingIcon = trailing,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0x18FFFFFF),
                unfocusedContainerColor = Color(0x10FFFFFF),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = NeonCyan,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )
    }
}

@Composable
private fun SaveButton(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(listOf(NeonPurple.copy(alpha = 0.9f), NeonCyan.copy(alpha = 0.9f))))
            .clickableNoRipple(onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, color = Color.White, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun ToggleRow(title: String, subtitle: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
            Text(subtitle, style = MaterialTheme.typography.labelMedium, color = TextMuted)
        }
        Switch(
            checked = checked,
            onCheckedChange = onChecked,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = NeonPurple,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = Color(0x33FFFFFF)
            )
        )
    }
}

@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    this.clickable(
        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
