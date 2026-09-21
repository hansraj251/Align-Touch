package com.chatflow.app

import android.content.Context

class SessionManager(
    context: Context
) {

    private val preferences =
        context.getSharedPreferences(
            "chatflow_session",
            Context.MODE_PRIVATE
        )

    fun saveSession(
        token: String,
        userId: String
    ) {

        preferences.edit()
            .putString(
                "token",
                token
            )
            .putString(
                "user_id",
                userId
            )
            .apply()
    }

    fun getToken(): String? {

        return preferences.getString(
            "token",
            null
        )
    }

    fun getUserId(): String? {

        return preferences.getString(
            "user_id",
            null
        )
    }

    fun isLoggedIn(): Boolean {

        return !getToken().isNullOrBlank()
    }

    fun clearSession() {

        preferences.edit()
            .clear()
            .apply()
    }
}
