package com.chatflow.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chatflow.app.data.UpdateProfileRequest
import com.chatflow.app.data.User
import com.chatflow.app.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val loading: Boolean = false,
    val saving: Boolean = false,
    val user: User? = null,
    val message: String = ""
)

class ProfileViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        UserRepository()

    private val sessionManager =
        SessionManager(application)

    private val _uiState =
        MutableStateFlow(
            ProfileUiState()
        )

    val uiState: StateFlow<ProfileUiState> =
        _uiState

    fun loadProfile() {

        val token =
            sessionManager.getToken()

        if (token.isNullOrBlank()) {

            _uiState.value =
                ProfileUiState(
                    message =
                        "Login session not found"
                )

            return
        }

        _uiState.value =
            ProfileUiState(
                loading = true
            )

        viewModelScope.launch {

            try {

                val response =
                    repository.getProfile(
                        token
                    )

                _uiState.value =
                    ProfileUiState(
                        user =
                            response.user
                    )

            } catch (error: Exception) {

                _uiState.value =
                    ProfileUiState(
                        message =
                            error.message
                                ?: "Failed to load profile"
                    )
            }
        }
    }

    fun updateProfile(
        displayName: String,
        about: String
    ) {

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
                saving = true,
                message = ""
            )

        viewModelScope.launch {

            try {

                val response =
                    repository.updateProfile(
                        token = token,
                        request =
                            UpdateProfileRequest(
                                displayName =
                                    displayName,
                                about =
                                    about
                            )
                    )

                _uiState.value =
                    ProfileUiState(
                        user =
                            response.user,
                        message =
                            "Profile updated"
                    )

            } catch (error: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        saving = false,
                        message =
                            error.message
                                ?: "Failed to update profile"
                    )
            }
        }
    }
}
