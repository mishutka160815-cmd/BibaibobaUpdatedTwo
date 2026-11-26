package com.bibaiboba.app.data.model

data class AppSettings(
    val apiBaseUrl: String,
    val apiKey: String,
    val model: String,
    val userName: String = "",
    val assistantPersona: String = "Ты Bibaiboba — умный, дружелюбный и стильный ИИ-помощник на телефоне. Ты помогаешь с вопросами, задачами и управлением устройством. Отвечай ясно, полезно и с лёгкой харизмой. Если пользователь просит действие на устройстве, объясни, что именно нужно сделать, и предложи команду.",
    val hapticsEnabled: Boolean = true,
    val animationsEnabled: Boolean = true
)
