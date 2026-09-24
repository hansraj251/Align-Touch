package com.chatflow.app.data

data class MessageResponse(
    val success: Boolean,
    val messages: List<Message>
)

data class SendMessageRequest(
    val conversationId: String,
    val content: String,
    val replyToMessageId: String? = null
)

data class SendMessageResponse(
    val success: Boolean,
    val message: Message
)

data class Message(
    val id: String,
    val conversation_id: String,
    val sender_id: String,
    val message_type: String,
    val content: String?,
    val reply_to_message_id: String?,
    val created_at: String,
    val edited_at: String?,
    val deleted_at: String?,
    val expires_at: String?
)

data class MessageReactionResponse(
    val success: Boolean,
    val reactions: List<MessageReaction>
)

data class MessageReaction(
    val id: String,
    val message_id: String,
    val user_id: String,
    val reaction: String,
    val created_at: String
)

data class MessageReceipt(
    val id: String,
    val message_id: String,
    val user_id: String,
    val delivered_at: String?,
    val read_at: String?,
    val created_at: String
)
