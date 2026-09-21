package com.chatflow.app.data

class AuthRepository {

    suspend fun login(
        phone: String? = null,
        email: String? = null,
        password: String
    ): LoginResponse {

        return ApiClient.authApi.login(
            LoginRequest(
                phone = phone,
                email = email,
                password = password
            )
        )
    }

    suspend fun getConversations(
        token: String
    ): ConversationResponse {

        return ApiClient.authApi.getConversations(
            authorization =
                "Bearer $token"
        )
    }
}
