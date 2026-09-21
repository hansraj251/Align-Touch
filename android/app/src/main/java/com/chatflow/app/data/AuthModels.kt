package com.chatflow.app.data

data class LoginRequest(
    val phone: String? = null,
    val email: String? = null,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: User?
)

data class User(
    val id: String,
    val phone: String?,
    val email: String?,
    val display_name: String,
    val avatar_url: String?,
    val about: String?,
    val last_seen_at: String?,
    val created_at: String?
)
