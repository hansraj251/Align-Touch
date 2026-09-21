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
    val message: String = ""
)

class ConversationViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        ConversationRepository()

    private val sessionManager =
        SessionManager(application)

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

                _uiState.value =
                    ConversationUiState(
                        conversations =
                            response.conversations
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
