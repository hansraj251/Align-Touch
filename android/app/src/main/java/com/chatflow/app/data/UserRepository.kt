package com.chatflow.app.data

class UserRepository {

    suspend fun getProfile(
        token: String
    ): ProfileResponse {

        return ApiClient.authApi.getProfile(
            authorization =
                "Bearer $token"
        )
    }

    suspend fun updateProfile(
        token: String,
        request: UpdateProfileRequest
    ): ProfileResponse {

        return ApiClient.authApi.updateProfile(
            request = request,
            authorization =
                "Bearer $token"
        )
    }

    suspend fun getUsers(
        token: String
    ): UserListResponse {

        return ApiClient.authApi.getUsers(
            authorization =
                "Bearer $token"
        )
    }
}
