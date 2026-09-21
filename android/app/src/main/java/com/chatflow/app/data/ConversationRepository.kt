package com.chatflow.app.data

class ConversationRepository {

    suspend fun getConversations(
        token: String
    ): ConversationResponse {

        return ApiClient.authApi.getConversations(
            authorization =
                "Bearer $token"
        )
    }
}
