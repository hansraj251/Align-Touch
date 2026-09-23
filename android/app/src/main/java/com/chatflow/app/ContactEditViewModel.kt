package com.chatflow.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatflow.app.data.Contact
import com.chatflow.app.data.ContactRepository
import com.chatflow.app.data.UpdateContactRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ContactEditUiState(
    val loading: Boolean = false,
    val saving: Boolean = false,

    val deleting: Boolean = false,
    val contact: Contact? = null,
    val message: String = ""
)

class ContactEditViewModel : ViewModel() {

    private val repository =
        ContactRepository()

    private val _uiState =
        MutableStateFlow(
            ContactEditUiState()
        )

    val uiState: StateFlow<ContactEditUiState> =
        _uiState

    fun loadContact(
        token: String,
        contactId: String
    ) {

        if (
            token.isBlank() ||
            contactId.isBlank()
        ) {
            return
        }

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    loading = true,
                    message = ""
                )

            try {

                val response =
                    repository.getContact(
                        token = token,
                        contactId = contactId
                    )

                _uiState.value =
                    _uiState.value.copy(
                        loading = false,
                        contact = response.contact,
                        message = ""
                    )

            } catch (error: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        loading = false,
                        message =
                            error.message
                                ?: "Failed to load contact"
                    )
            }
        }
    }

    fun deleteContact(
        token: String,
        contactId: String,
        onSuccess: () -> Unit
    ) {
        if (
            token.isBlank() ||
            contactId.isBlank()
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    deleting = true,
                    message = ""
                )

            try {
                repository.deleteContact(
                    token = token,
                    contactId = contactId
                )

                _uiState.value =
                    _uiState.value.copy(
                        deleting = false,
                        contact = null,
                        message = ""
                    )

                onSuccess()
            } catch (error: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        deleting = false,
                        message =
                            error.message
                                ?: "Failed to delete contact"
                    )
            }
        }
    }

    fun createContact(
        token: String,
        request: com.chatflow.app.data.CreateContactRequest,
        onSuccess: (Contact) -> Unit
    ) {
        if (
            token.isBlank()
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    saving = true,
                    message = ""
                )

            try {
                val response =
                    repository.createContact(
                        token = token,
                        request = request
                    )

                _uiState.value =
                    _uiState.value.copy(
                        saving = false,
                        contact =
                            response.contact,
                        message = ""
                    )

                onSuccess(
                    response.contact
                )

            } catch (error: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        saving = false,
                        message =
                            error.message
                                ?: "Failed to add contact"
                    )
            }
        }
    }

    fun updateContact(
        token: String,
        contactId: String,
        request: UpdateContactRequest,
        onSuccess: (Contact) -> Unit
    ) {

        if (
            token.isBlank() ||
            contactId.isBlank()
        ) {
            return
        }

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    saving = true,
                    message = ""
                )

            try {

                val response =
                    repository.updateContact(
                        token = token,
                        contactId = contactId,
                        request = request
                    )

                _uiState.value =
                    _uiState.value.copy(
                        saving = false,
                        contact =
                            response.contact,
                        message = ""
                    )

                onSuccess(
                    response.contact
                )

            } catch (error: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        saving = false,
                        message =
                            error.message
                                ?: "Failed to update contact"
                    )
            }
        }
    }
}
