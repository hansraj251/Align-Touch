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
    val otp: String? = null,

    val verified: Boolean? = null,

    val token: String? = null,

    val user: User? = null
)



data class User(
    val id: String,
    val phone: String?,
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

data class CreateGroupConversationRequest(

    val title: String,

    val memberUserIds: List<String>

)

data class CreateGroupConversationResponse(

    val success: Boolean,

    val conversation: Conversation

)

