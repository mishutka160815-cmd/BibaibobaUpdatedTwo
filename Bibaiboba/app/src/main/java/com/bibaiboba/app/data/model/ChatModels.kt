package com.bibaiboba.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ChatMessageDto(
    val role: String,
    val content: String
)

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessageDto>,
    val temperature: Double = 0.7,
    @SerialName("max_tokens") val maxTokens: Int = 2048
)

@Serializable
data class ChatResponse(
    val choices: List<Choice> = emptyList(),
    val error: ApiError? = null
)

@Serializable
data class Choice(
    val index: Int = 0,
    val message: ChatMessageDto? = null,
    @SerialName("finish_reason") val finishReason: String? = null
)

@Serializable
data class ApiError(
    val message: String? = null,
    val type: String? = null,
    val code: String? = null
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: ChatRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false,
    val isPending: Boolean = false
)

enum class ChatRole { USER, ASSISTANT, SYSTEM }
