package com.chatflow.app.data

class UserRepository {

    suspend fun getUsers(
        token: String
    ): UserListResponse {

        return ApiClient.authApi.getUsers(
            authorization =
                "Bearer $token"
        )
    }
}
