package com.chatflow.app.data

class AuthRepository {

    suspend fun requestOtp(
        identifier: String,
        identifierType: String
    ): OtpResponse {

        return ApiClient.authApi.requestOtp(
            OtpRequest(
                identifier =
                    identifier,
                identifierType =
                    identifierType
            )
        )
    }

    suspend fun verifyOtp(
        identifier: String,
        otp: String
    ): OtpResponse {

        return ApiClient.authApi.verifyOtp(
            OtpVerifyRequest(
                identifier =
                    identifier,
                otp = otp
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
