package com.chatflow.app.data

data class OtpRequest(

    val identifier: String,

    val identifierType: String
)

data class OtpVerifyRequest(

    val identifier: String,

    val purpose: String = "login",

    val otp: String
)

data class OtpResponse(

    val success: Boolean,

    val message: String,

    val expiresAt: String? = null,

    val verified: Boolean? = null,

    val token: String? = null,

    val user: User? = null
)



data class LoginRequest(
    val phone: String? = null,
    val email: String? = null,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: User?
)

data class User(
    val id: String,
    val phone: String?,
    val email: String?,
    val display_name: String,
    val avatar_url: String?,
    val about: String?,
    val last_seen_at: String?,
    val created_at: String?
)

data class UpdateProfileRequest(
    val displayName: String,
    val about: String? = null,
    val avatarUrl: String? = null
)

data class ProfileResponse(
    val success: Boolean,
    val user: User?
)

data class UserListResponse(
    val success: Boolean,
    val users: List<User>
)

data class CreateDirectConversationRequest(
    val otherUserId: String
)

data class CreateDirectConversationResponse(
    val success: Boolean,
    val conversation: Conversation
)
