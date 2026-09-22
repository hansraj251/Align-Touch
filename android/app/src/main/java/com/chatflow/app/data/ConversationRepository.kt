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

    suspend fun createDirectConversation(

        token: String,

        otherUserId: String

    ): CreateDirectConversationResponse {

        return ApiClient.authApi.createDirectConversation(

            request =
                CreateDirectConversationRequest(
                    otherUserId =
                        otherUserId
                ),

            authorization =
                "Bearer $token"

        )

    }

}
