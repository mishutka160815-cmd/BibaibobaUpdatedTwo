package com.bibaiboba.app.data.api

import com.bibaiboba.app.data.model.ChatRequest
import com.bibaiboba.app.data.model.ChatResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface ChatApi {
    @POST
    suspend fun chatCompletions(
        @Url url: String,
        @Header("Authorization") authorization: String,
        @Body request: ChatRequest
    ): ChatResponse
}
