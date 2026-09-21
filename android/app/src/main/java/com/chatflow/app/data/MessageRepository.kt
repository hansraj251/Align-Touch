package com.chatflow.app.data

class MessageRepository {

    suspend fun getMessages(
        conversationId: String,
        token: String
    ): MessageResponse {

        return ApiClient.authApi.getMessages(
            conversationId = conversationId,
            authorization =
                "Bearer $token"
        )
    }

    suspend fun sendMessage(
        request: SendMessageRequest,
        token: String
    ): SendMessageResponse {

        return ApiClient.authApi.sendMessage(
            request = request,
            authorization =
                "Bearer $token"
        )
    }
}
