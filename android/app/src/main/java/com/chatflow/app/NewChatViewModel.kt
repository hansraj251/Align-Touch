package com.chatflow.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.chatflow.app.data.User

import com.chatflow.app.data.Contact
import com.chatflow.app.data.UserRepository

import com.chatflow.app.data.ContactRepository
import com.chatflow.app.data.ConversationRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class NewChatUiState(

    val loading: Boolean = false,

    val creating: Boolean = false,

    val users: List<User> = emptyList(),

    val contacts: List<Contact> = emptyList(),

    val message: String = ""

)

class NewChatViewModel : ViewModel() {

    private val userRepository =
        UserRepository()

    private val contactRepository =
        ContactRepository()

    private val conversationRepository =
        ConversationRepository()

    private val _uiState =
        MutableStateFlow(
            NewChatUiState()
        )

    val uiState:
        StateFlow<NewChatUiState> =
        _uiState

    fun loadUsers(
        token: String
    ) {

        _uiState.value =
            NewChatUiState(
                loading = true
            )

        viewModelScope.launch {

            try {

                val response =
                    userRepository.getUsers(
                        token
                    )

                _uiState.value =
                    NewChatUiState(
                        users =
                            response.users
                    )

            } catch (error: Exception) {

                _uiState.value =
                    NewChatUiState(
                        message =
                            error.message
                                ?: "Failed to load users"
                    )

            }

        }

    }

    fun loadContacts(
        token: String
    ) {
        viewModelScope.launch {
            try {
                val response =
                    contactRepository.getContacts(
                        token
                    )

                _uiState.value =
                    _uiState.value.copy(
                        contacts =
                            response.contacts
                    )

            } catch (error: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        message =
                            error.message
                                ?: "Failed to load contacts"
                    )
            }
        }
    }

    fun createContact(
        token: String,
        request: com.chatflow.app.data.CreateContactRequest,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            try {

                contactRepository.createContact(
                    token = token,
                    request = request
                )

                onSuccess()

            } catch (error: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        message =
                            error.message
                                ?: "Failed to create contact"
                    )
            }
        }
    }

    fun createConversation(

        token: String,

        userId: String,

        onSuccess: (com.chatflow.app.data.Conversation) -> Unit

    ) {

        _uiState.value =

            _uiState.value.copy(

                creating = true,

                message = ""

            )

        viewModelScope.launch {

            try {

                val response =

                    conversationRepository

                        .createDirectConversation(

                            token = token,

                            otherUserId = userId

                        )

                _uiState.value =

                    _uiState.value.copy(

                        creating = false

                    )

                onSuccess(

                    response.conversation

                )

            } catch (error: Exception) {

                _uiState.value =

                    _uiState.value.copy(

                        creating = false,

                        message =

                            error.message

                                ?: "Failed to create conversation"

                    )

            }

        }

    }


}
