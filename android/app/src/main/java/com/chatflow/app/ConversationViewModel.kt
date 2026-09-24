package com.chatflow.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chatflow.app.data.Conversation
import com.chatflow.app.data.ConversationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ConversationUiState(
    val loading: Boolean = false,
    val conversations: List<Conversation> = emptyList(),
    val onlineUserIds: Set<String> = emptySet(),
    val message: String = ""
)

class ConversationViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        ConversationRepository()

    private val sessionManager =
        SessionManager(application)

    private val socketManager =
        SocketManager()

    private val _uiState =
        MutableStateFlow(
            ConversationUiState()
        )

    val uiState: StateFlow<ConversationUiState> =
        _uiState

    fun loadConversations() {

        val token =
            sessionManager.getToken()

        if (token.isNullOrBlank()) {

            _uiState.value =
                ConversationUiState(
                    message =
                        "Login session not found"
                )

            return
        }

        _uiState.value =
            ConversationUiState(
                loading = true
            )

        viewModelScope.launch {

            try {

                val response =
                    repository.getConversations(
                        token
                    )

                android.util.Log.d(
                    "ChatFlowConversations",
                    "API conversations count: ${response.conversations.size}"
                )

                android.util.Log.d(
                    "ChatFlowConversations",
                    "Groups: ${
                        response.conversations.count {
                            it.type == "group"
                        }
                    }"
                )

                android.util.Log.d(
                    "ChatFlowConversations",
                    "Conversation types: ${
                        response.conversations.map {
                            "${it.id}:${it.type}:${it.other_user_display_name}"
                        }
                    }"
                )

                android.util.Log.d(
                    "ChatFlowConversations",
                    "Contact IDs: ${
                        response.conversations.map {
                            "${it.id}:${it.contact_id}"
                        }
                    }"
                )

                _uiState.value =
                    ConversationUiState(
                        conversations =
                            response.conversations
                    )

                socketManager.connect(
                    token = token,
                    onConnected = {
                        socketManager.listenForUserOnline { data ->
                            try {
                                val userId =
                                    data.getString(
                                        "userId"
                                    )

                                _uiState.value =
                                    _uiState.value.copy(
                                        onlineUserIds =
                                            _uiState.value.onlineUserIds +
                                                userId
                                    )
                            } catch (error: Exception) {
                                android.util.Log.e(
                                    "ChatFlowConversations",
                                    "User online event error",
                                    error
                                )
                            }
                        }

                        socketManager.listenForUserOffline { data ->
                            try {
                                val userId =
                                    data.getString(
                                        "userId"
                                    )

                                _uiState.value =
                                    _uiState.value.copy(
                                        onlineUserIds =
                                            _uiState.value.onlineUserIds
                                                .filterNot {
                                                    it == userId
                                                }
                                                .toSet()
                                    )
                            } catch (error: Exception) {
                                android.util.Log.e(
                                    "ChatFlowConversations",
                                    "User offline event error",
                                    error
                                )
                            }
                        }
                    }
                )

            } catch (error: Exception) {

                _uiState.value =
                    ConversationUiState(
                        message =
                            error.message
                                ?: "Failed to load conversations"
                    )
            }
        }
    }
}
