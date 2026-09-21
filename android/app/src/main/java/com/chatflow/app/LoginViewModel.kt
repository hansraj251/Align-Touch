package com.chatflow.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chatflow.app.data.AuthRepository
import com.chatflow.app.data.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val message: String = "",
    val response: LoginResponse? = null
)

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        AuthRepository()

    private val sessionManager =
        SessionManager(application)

    private val _uiState =
        MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> =
        _uiState

    fun login(
        identifier: String,
        password: String
    ) {

        if (
            identifier.isBlank() ||
            password.isBlank()
        ) {

            _uiState.value =
                LoginUiState(
                    message =
                        "Phone/email and password are required"
                )

            return
        }

        _uiState.value =
            LoginUiState(
                loading = true
            )

        viewModelScope.launch {

            try {

                val isEmail =
                    identifier.contains("@")

                val response =
                    if (isEmail) {

                        repository.login(
                            email = identifier,
                            password = password
                        )

                    } else {

                        repository.login(
                            phone = identifier,
                            password = password
                        )
                    }

                if (
                    response.success &&
                    response.token != null &&
                    response.user != null
                ) {

                    sessionManager.saveSession(
                        token =
                            response.token,
                        userId =
                            response.user.id
                    )
                }

                _uiState.value =
                    LoginUiState(
                        success =
                            response.success,
                        message =
                            response.message,
                        response =
                            response
                    )

            } catch (error: Exception) {

                _uiState.value =
                    LoginUiState(
                        message =
                            error.message
                                ?: "Login failed"
                    )
            }
        }
    }
}
