package com.chatflow.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatflow.app.data.ConversationRepository
import com.chatflow.app.data.GroupDetails
import com.chatflow.app.data.GroupMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class GroupInfoUiState(

    val loading: Boolean = false,

    val actionLoading: Boolean = false,

    val deleted: Boolean = false,

    val group: GroupDetails? = null,

    val members: List<GroupMember> = emptyList(),

    val message: String = ""

)

class GroupInfoViewModel : ViewModel() {

    private val repository =
        ConversationRepository()

    private val _uiState =
        MutableStateFlow(
            GroupInfoUiState()
        )

    val uiState:
        StateFlow<GroupInfoUiState> =
        _uiState

    fun loadGroupInfo(
        token: String,
        conversationId: String
    ) {

        if (
            token.isBlank() ||
            conversationId.isBlank()
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

                val groupResponse =
                    repository.getGroupDetails(
                        token = token,
                        conversationId =
                            conversationId
                    )

                val membersResponse =
                    repository.getGroupMembers(
                        token = token,
                        conversationId =
                            conversationId
                    )

                _uiState.value =
                    _uiState.value.copy(
                        loading = false,
                        group =
                            groupResponse.group,
                        members =
                            membersResponse.members,
                        message = ""
                    )

            } catch (error: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        loading = false,
                        message =
                            error.message
                                ?: "Failed to load group"
                    )
            }
        }
    }

    fun updateGroup(
        token: String,
        conversationId: String,
        title: String,
        avatarUrl: String? = null
    ) {
        if (
            token.isBlank() ||
            conversationId.isBlank() ||
            title.isBlank()
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    actionLoading = true,
                    message = ""
                )

            try {
                repository.updateGroup(
                    token = token,
                    conversationId = conversationId,
                    title = title,
                    avatarUrl = avatarUrl
                )

                loadGroupInfo(
                    token = token,
                    conversationId = conversationId
                )

                _uiState.value =
                    _uiState.value.copy(
                        actionLoading = false
                    )
            } catch (error: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        actionLoading = false,
                        message =
                            error.message
                                ?: "Failed to update group"
                    )
            }
        }
    }

    fun addGroupMember(
        token: String,
        conversationId: String,
        userId: String
    ) {

        if (
            token.isBlank() ||
            conversationId.isBlank() ||
            userId.isBlank()
        ) {
            return
        }

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    actionLoading = true,
                    message = ""
                )

            try {

                repository.addGroupMember(
                    token = token,
                    conversationId =
                        conversationId,
                    userId = userId
                )

                loadGroupInfo(
                    token = token,
                    conversationId =
                        conversationId
                )

                _uiState.value =
                    _uiState.value.copy(
                        actionLoading = false
                    )

            } catch (error: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        actionLoading = false,
                        message =
                            error.message
                                ?: "Failed to add member"
                    )
            }
        }
    }

    fun removeGroupMember(
        token: String,
        conversationId: String,
        userId: String
    ) {
        if (
            token.isBlank() ||
            conversationId.isBlank() ||
            userId.isBlank()
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    actionLoading = true,
                    message = ""
                )

            try {
                repository.removeGroupMember(
                    token = token,
                    conversationId = conversationId,
                    userId = userId
                )

                loadGroupInfo(
                    token = token,
                    conversationId = conversationId
                )

                _uiState.value =
                    _uiState.value.copy(
                        actionLoading = false
                    )
            } catch (error: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        actionLoading = false,
                        message =
                            error.message
                                ?: "Failed to remove member"
                    )
            }
        }
    }

    fun deleteGroup(
        token: String,
        conversationId: String
    ) {
        if (
            token.isBlank() ||
            conversationId.isBlank()
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    actionLoading = true,
                    message = ""
                )

            try {
                repository.deleteGroup(
                    token = token,
                    conversationId = conversationId
                )

                _uiState.value =
                    _uiState.value.copy(
                        actionLoading = false,
                        deleted = true,
                        message = ""
                    )
            } catch (error: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        actionLoading = false,
                        message =
                            error.message
                                ?: "Failed to delete group"
                    )
            }
        }
    }

}
