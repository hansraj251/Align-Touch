package com.chatflow.app.data

data class ConversationResponse(
    val success: Boolean,
    val conversations: List<Conversation>
)

data class Conversation(
    val id: String,
    val type: String,
    val title: String?,
    val avatar_url: String?,
    val created_by: String,
    val created_at: String,
    val updated_at: String,
    val other_user_id: String,
    val other_user_phone: String?,
    val other_user_email: String?,
    val other_user_display_name: String,
    val other_user_avatar_url: String?,
    val other_user_about: String?,
    val other_user_last_seen_at: String?
)
