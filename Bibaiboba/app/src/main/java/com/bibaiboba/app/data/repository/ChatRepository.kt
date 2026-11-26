package com.bibaiboba.app.data.repository

import com.bibaiboba.app.data.api.NetworkModule
import com.bibaiboba.app.data.model.AppSettings
import com.bibaiboba.app.data.model.ChatMessage
import com.bibaiboba.app.data.model.ChatMessageDto
import com.bibaiboba.app.data.model.ChatRequest
import com.bibaiboba.app.data.model.ChatRole

class ChatRepository {

    suspend fun sendMessage(
        settings: AppSettings,
        history: List<ChatMessage>,
        userMessage: String
    ): Result<String> {
        return runCatching {
            val base = settings.apiBaseUrl.trim().trimEnd('/')
            val url = when {
                base.endsWith("/chat/completions") -> base
                base.endsWith("/v1") -> "$base/chat/completions"
                else -> "$base/v1/chat/completions"
            }

            val systemPrompt = buildString {
                append(settings.assistantPersona.trim())
                if (settings.userName.isNotBlank()) {
                    append("\nИмя пользователя: ${settings.userName.trim()}.")
                }
                append(
                    "\n\nТы работаешь в мобильном приложении Bibaiboba на Android. " +
                    "Если пользователь просит выполнить действие на телефоне, ответь кратко и добавь в конце ответа " +
                    "строку с тегом действия в формате [ACTION:TYPE:PAYLOAD]. " +
                    "Поддерживаемые действия: " +
                    "OPEN_URL:<url>, OPEN_APP:<package_or_name>, CALL:<number>, DIAL:<number>, " +
                    "SEARCH:<query>, MAPS:<query>, ALARM:<HH:MM>, TIMER:<seconds>, " +
                    "SETTINGS:<wifi|bluetooth|display|sound|apps|accessibility|main>, " +
                    "SHARE:<text>, COPY:<text>. " +
                    "Если действие не нужно, не добавляй тег."
                )
            }

            val messages = buildList {
                add(ChatMessageDto(role = "system", content = systemPrompt))
                history
                    .filter { !it.isPending && !it.isError }
                    .takeLast(20)
                    .forEach { msg ->
                        val role = when (msg.role) {
                            ChatRole.USER -> "user"
                            ChatRole.ASSISTANT -> "assistant"
                            ChatRole.SYSTEM -> "system"
                        }
                        add(ChatMessageDto(role = role, content = msg.content))
                    }
                add(ChatMessageDto(role = "user", content = userMessage))
            }

            val request = ChatRequest(
                model = settings.model.ifBlank { "gpt-4o-mini" },
                messages = messages
            )

            val auth = settings.apiKey.trim().let { key ->
                if (key.startsWith("Bearer ", ignoreCase = true)) key else "Bearer $key"
            }

            val response = NetworkModule.chatApi.chatCompletions(
                url = url,
                authorization = auth,
                request = request
            )

            if (response.error != null) {
                error(response.error.message ?: "Ошибка API")
            }

            response.choices.firstOrNull()?.message?.content?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: error("Пустой ответ от модели")
        }
    }
}
