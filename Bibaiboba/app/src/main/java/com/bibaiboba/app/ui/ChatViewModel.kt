package com.bibaiboba.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bibaiboba.app.BibaibobaApp
import com.bibaiboba.app.data.model.AppSettings
import com.bibaiboba.app.data.model.ChatMessage
import com.bibaiboba.app.data.model.ChatRole
import com.bibaiboba.app.util.DeviceAction
import com.bibaiboba.app.util.DeviceActionExecutor
import com.bibaiboba.app.util.LocalCommandParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val input: String = "",
    val errorBanner: String? = null
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as BibaibobaApp
    private val settingsRepository = app.settingsRepository
    private val chatRepository = app.chatRepository

    val settings: StateFlow<AppSettings> = settingsRepository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings(
            apiBaseUrl = com.bibaiboba.app.BuildConfig.DEFAULT_API_BASE_URL,
            apiKey = com.bibaiboba.app.BuildConfig.DEFAULT_API_KEY,
            model = com.bibaiboba.app.BuildConfig.DEFAULT_API_MODEL
        )
    )

    private val _uiState = MutableStateFlow(
        ChatUiState(
            messages = listOf(
                ChatMessage(
                    role = ChatRole.ASSISTANT,
                    content = "Привет! Я Bibaiboba — твой ИИ-помощник ✨\n\n" +
                            "Могу отвечать на вопросы, помогать с задачами и выполнять действия на телефоне: " +
                            "открывать приложения, ставить таймеры, искать информацию и многое другое.\n\n" +
                            "Что сделаем?"
                )
            )
        )
    )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onInputChange(value: String) {
        _uiState.update { it.copy(input = value, errorBanner = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorBanner = null) }
    }

    fun sendCurrentInput() {
        val text = uiState.value.input.trim()
        if (text.isBlank() || uiState.value.isLoading) return
        _uiState.update { it.copy(input = "") }
        sendMessage(text)
    }

    fun sendQuickPrompt(text: String) {
        if (uiState.value.isLoading) return
        sendMessage(text)
    }

    fun executeAction(action: DeviceAction) {
        val result = DeviceActionExecutor.execute(getApplication(), action)
        val message = result.fold(
            onSuccess = { "✅ $it" },
            onFailure = { "⚠️ Не удалось выполнить действие: ${it.message ?: "неизвестная ошибка"}" }
        )
        appendMessage(ChatMessage(role = ChatRole.ASSISTANT, content = message))
    }

    private fun sendMessage(text: String) {
        val userMsg = ChatMessage(role = ChatRole.USER, content = text)
        appendMessage(userMsg)

        // Try local device command first for obvious commands
        val localAction = LocalCommandParser.parse(text)
        if (localAction != null) {
            val result = DeviceActionExecutor.execute(getApplication(), localAction)
            val reply = result.fold(
                onSuccess = { "✅ $it" },
                onFailure = { "⚠️ Не удалось выполнить действие: ${it.message ?: "неизвестная ошибка"}" }
            )
            appendMessage(ChatMessage(role = ChatRole.ASSISTANT, content = reply))
            return
        }

        val pending = ChatMessage(role = ChatRole.ASSISTANT, content = "", isPending = true)
        appendMessage(pending)
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val currentSettings = settings.value
            val history = _uiState.value.messages.filter { it.id != pending.id }
            val result = chatRepository.sendMessage(currentSettings, history, text)

            result.fold(
                onSuccess = { raw ->
                    val (clean, action) = DeviceActionExecutor.extractAction(raw)
                    replacePending(pending.id, ChatMessage(role = ChatRole.ASSISTANT, content = clean.ifBlank { raw }))
                    if (action != null) {
                        val actionResult = DeviceActionExecutor.execute(getApplication(), action)
                        val actionMsg = actionResult.fold(
                            onSuccess = { "⚡ $it" },
                            onFailure = { "⚠️ Действие не выполнено: ${it.message ?: "ошибка"}" }
                        )
                        appendMessage(ChatMessage(role = ChatRole.ASSISTANT, content = actionMsg))
                    }
                },
                onFailure = { e ->
                    val err = e.message?.takeIf { it.isNotBlank() } ?: "Не удалось получить ответ"
                    replacePending(
                        pending.id,
                        ChatMessage(
                            role = ChatRole.ASSISTANT,
                            content = "Не удалось получить ответ от ИИ.\n$err\n\nПроверьте API-ключ, модель и интернет в настройках.",
                            isError = true
                        )
                    )
                }
            )
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun appendMessage(message: ChatMessage) {
        _uiState.update { it.copy(messages = it.messages + message) }
    }

    private fun replacePending(pendingId: String, message: ChatMessage) {
        _uiState.update { state ->
            state.copy(messages = state.messages.map { if (it.id == pendingId) message else it })
        }
    }

    fun clearChat() {
        _uiState.update {
            ChatUiState(
                messages = listOf(
                    ChatMessage(
                        role = ChatRole.ASSISTANT,
                        content = "Чат очищен. Чем могу помочь?"
                    )
                )
            )
        }
    }

    // Settings updates
    fun updateApiBaseUrl(v: String) = viewModelScope.launch { settingsRepository.updateApiBaseUrl(v) }
    fun updateApiKey(v: String) = viewModelScope.launch { settingsRepository.updateApiKey(v) }
    fun updateModel(v: String) = viewModelScope.launch { settingsRepository.updateModel(v) }
    fun updateUserName(v: String) = viewModelScope.launch { settingsRepository.updateUserName(v) }
    fun updatePersona(v: String) = viewModelScope.launch { settingsRepository.updatePersona(v) }
    fun updateHaptics(v: Boolean) = viewModelScope.launch { settingsRepository.updateHaptics(v) }
    fun updateAnimations(v: Boolean) = viewModelScope.launch { settingsRepository.updateAnimations(v) }
}
