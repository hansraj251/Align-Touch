package com.chatflow.app.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("api/conversations")
    suspend fun getConversations(
        @Header("Authorization")
        authorization: String
    ): ConversationResponse

    @GET("api/messages/{conversationId}")
    suspend fun getMessages(
        @Path("conversationId")
        conversationId: String,

        @Header("Authorization")
        authorization: String
    ): MessageResponse

    @POST("api/messages")
    suspend fun sendMessage(
        @Body request: SendMessageRequest,

        @Header("Authorization")
        authorization: String
    ): SendMessageResponse
}
