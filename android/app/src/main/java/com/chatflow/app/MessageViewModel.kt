package com.chatflow.app

import android.util.Log

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chatflow.app.data.Message
import com.chatflow.app.data.MessageRepository
import com.chatflow.app.data.MessageReceipt
import com.chatflow.app.data.SendMessageRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MessageUiState(
    val loading: Boolean = false,
    val sending: Boolean = false,
    val messages: List<Message> = emptyList(),

    val messageReceipts: Map<String, MessageReceipt> = emptyMap(),
    val message: String = ""
)

class MessageViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        MessageRepository()

    private val sessionManager =
        SessionManager(application)

    private val socketManager =
        SocketManager()

    private val _uiState =
        MutableStateFlow(
            MessageUiState()
        )

    val uiState: StateFlow<MessageUiState> =
        _uiState

    fun loadMessages(
        conversationId: String
    ) {

        val token =
            sessionManager.getToken()
        val currentUserId = sessionManager.getUserId()

        if (token.isNullOrBlank()) {

            _uiState.value =
                MessageUiState(
                    message =
                        "Login session not found"
                )

            return
        }

        _uiState.value =
            _uiState.value.copy(
                loading = true,
                message = ""
            )

        Log.d(
            "ChatFlowMessage",
            "loadMessages called for conversation: $conversationId"
        )

        socketManager.connect(
            token = token,
            onConnected = {
                socketManager.listenForMessageDelivered { data ->
                    try {
                        val receipt =
                            MessageReceipt(
                                id =
                                    data.getString(
                                        "id"
                                    ),
                                message_id =
                                    data.getString(
                                        "message_id"
                                    ),
                                user_id =
                                    data.getString(
                                        "user_id"
                                    ),
                                delivered_at =
                                    if (
                                        data.isNull(
                                            "delivered_at"
                                        )
                                    ) {
                                        null
                                    } else {
                                        data.getString(
                                            "delivered_at"
                                        )
                                    },
                                read_at =
                                    if (
                                        data.isNull(
                                            "read_at"
                                        )
                                    ) {
                                        null
                                    } else {
                                        data.getString(
                                            "read_at"
                                        )
                                    },
                                created_at =
                                    data.getString(
                                        "created_at"
                                    )
                            )

                        _uiState.value =
                            _uiState.value.copy(
                                messageReceipts =
                                    _uiState.value.messageReceipts +
                                        (
                                            receipt.message_id to
                                                receipt
                                        )
                            )
                    } catch (error: Exception) {
                        Log.e(
                            "ChatFlowMessage",
                            "Delivery receipt error",
                            error
                        )
                    }
                }

                socketManager.listenForMessageRead { data ->
                    try {
                        val receipt =
                            MessageReceipt(
                                id =
                                    data.getString(
                                        "id"
                                    ),
                                message_id =
                                    data.getString(
                                        "message_id"
                                    ),
                                user_id =
                                    data.getString(
                                        "user_id"
                                    ),
                                delivered_at =
                                    if (
                                        data.isNull(
                                            "delivered_at"
                                        )
                                    ) {
                                        null
                                    } else {
                                        data.getString(
                                            "delivered_at"
                                        )
                                    },
                                read_at =
                                    if (
                                        data.isNull(
                                            "read_at"
                                        )
                                    ) {
                                        null
                                    } else {
                                        data.getString(
                                            "read_at"
                                        )
                                    },
                                created_at =
                                    data.getString(
                                        "created_at"
                                    )
                            )

                        _uiState.value =
                            _uiState.value.copy(
                                messageReceipts =
                                    _uiState.value.messageReceipts +
                                        (
                                            receipt.message_id to
                                                receipt
                                        )
                            )
                    } catch (error: Exception) {
                        Log.e(
                            "ChatFlowMessage",
                            "Read receipt error",
                            error
                        )
                    }
                }

                socketManager.listenForNewMessages { data ->
                    try {
                        val message =
                            Message(
                                id =
                                    data.getString(
                                        "id"
                                    ),
                                conversation_id =
                                    data.getString(
                                        "conversation_id"
                                    ),
                                sender_id =
                                    data.getString(
                                        "sender_id"
                                    ),
                                message_type =
                                    data.getString(
                                        "message_type"
                                    ),
                                content =
                                    if (
                                        data.isNull(
                                            "content"
                                        )
                                    ) {
                                        null
                                    } else {
                                        data.getString(
                                            "content"
                                        )
                                    },
                                reply_to_message_id =
                                    if (
                                        data.isNull(
                                            "reply_to_message_id"
                                        )
                                    ) {
                                        null
                                    } else {
                                        data.getString(
                                            "reply_to_message_id"
                                        )
                                    },
                                created_at =
                                    data.getString(
                                        "created_at"
                                    ),
                                edited_at =
                                    if (
                                        data.isNull(
                                            "edited_at"
                                        )
                                    ) {
                                        null
                                    } else {
                                        data.getString(
                                            "edited_at"
                                        )
                                    },
                                deleted_at =
                                    if (
                                        data.isNull(
                                            "deleted_at"
                                        )
                                    ) {
                                        null
                                    } else {
                                        data.getString(
                                            "deleted_at"
                                        )
                                    }
                            )

                        if (
                            message.conversation_id ==
                                conversationId &&
                            _uiState.value.messages.none {
                                it.id == message.id
                            }
                        ) {
                            _uiState.value =
                                _uiState.value.copy(
                                    messages =
                                        _uiState.value.messages +
                                            message
                                )

                            if (
                                message.sender_id != currentUserId &&
                                message.conversation_id == conversationId
                            ) {
                                socketManager.markDelivered(
                                    message.id
                                )
                            }
                        }
                    } catch (error: Exception) {
                        _uiState.value =
                            _uiState.value.copy(
                                message =
                                    "Socket message error: " +
                                        (
                                            error.message
                                                ?: "Invalid message"
                                        )
                            )
                    }
                }

                socketManager.joinConversation(
                    conversationId
                )
            },
            onError = { error ->
                _uiState.value =
                    _uiState.value.copy(
                        message =
                            "Socket error: $error"
                    )
            }
        )

        viewModelScope.launch {

            try {

                val response =
                    repository.getMessages(
                        conversationId =
                            conversationId,
                        token =
                            token
                    )

                _uiState.value =
                    _uiState.value.copy(
                        loading = false,
                        messages =
                            response.messages,
                        message = ""
                    )

            } catch (error: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        loading = false,
                        message =
                            error.message
                                ?: "Failed to load messages"
                    )
            }
        }
    }

    fun markMessageRead(
        messageId: String
    ) {
        socketManager.markRead(
            messageId
        )
    }

    fun sendMessage(
        conversationId: String,
        content: String
    ) {

        if (content.isBlank()) {
            return
        }

        val token =
            sessionManager.getToken()

        if (token.isNullOrBlank()) {

            _uiState.value =
                _uiState.value.copy(
                    message =
                        "Login session not found"
                )

            return
        }

        _uiState.value =
            _uiState.value.copy(
                sending = true,
                message = ""
            )

        Log.d(
            "ChatFlowMessage",
            "Sending message via socket: conversation=$conversationId"
        )

        try {

            socketManager.sendMessage(
                conversationId = conversationId,
                content = content
            )

            _uiState.value =
                _uiState.value.copy(
                    sending = false,
                    message = ""
                )

        } catch (error: Exception) {

            Log.e(
                "ChatFlowMessage",
                "Socket send error",
                error
            )

            _uiState.value =
                _uiState.value.copy(
                    sending = false,
                    message =
                        error.message
                            ?: "Failed to send message"
                )
        }
    }
}
