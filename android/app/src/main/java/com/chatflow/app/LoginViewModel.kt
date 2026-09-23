package com.chatflow.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chatflow.app.data.AuthRepository
import com.chatflow.app.data.OtpResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(

    val loading: Boolean = false,

    val otpLoading: Boolean = false,

    val otpSent: Boolean = false,

    val success: Boolean = false,

    val message: String = "",


    val otpResponse: OtpResponse? = null

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

    fun requestOtp(
        identifier: String
    ) {

        if (identifier.isBlank()) {
            _uiState.value =
                LoginUiState(
                    message =
                        "Mobile number is required"
                )
            return
        }

        _uiState.value =
            LoginUiState(
                otpLoading = true
            )

        viewModelScope.launch {
            try {

                val response =
                    repository.requestOtp(
                        identifier =
                            identifier,
                        identifierType =
                            "phone"
                    )

                _uiState.value =
                    LoginUiState(
                        otpSent =
                            response.success,
                        message =
                            response.message,
                        otpResponse =
                            response
                    )

            } catch (error: Exception) {

                _uiState.value =
                    LoginUiState(
                        message =
                            error.message
                                ?: "Failed to send OTP"
                    )
            }
        }
    }

    fun verifyOtp(
        identifier: String,
        otp: String
    ) {

        if (
            identifier.isBlank() ||
            otp.isBlank()
        ) {
            _uiState.value =
                LoginUiState(
                    message =
                        "Mobile number and OTP are required"
                )
            return
        }

        _uiState.value =
            _uiState.value.copy(
                loading = true,
                message = ""
            )

        viewModelScope.launch {
            try {

                val response =
                    repository.verifyOtp(
                        identifier =
                            identifier,
                        otp = otp
                    )

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
                        otpResponse =
                            response
                    )

            } catch (error: Exception) {

                _uiState.value =
                    LoginUiState(
                        message =
                            error.message
                                ?: "OTP verification failed"
                    )
            }
        }
    }

}
