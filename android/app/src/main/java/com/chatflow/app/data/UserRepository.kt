package com.chatflow.app.data

import okhttp3.MultipartBody
import okhttp3.ResponseBody

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


    suspend fun uploadAvatar(
        file: MultipartBody.Part,
        token: String
    ): ProfileResponse {

        return ApiClient
            .userAvatarApi
            .uploadAvatar(
                file = file,
                authorization = "Bearer $token"
            )
    }

    suspend fun downloadAvatar(
        userId: String,
        token: String
    ): ResponseBody {

        return ApiClient
            .userAvatarApi
            .downloadAvatar(
                userId = userId,
                authorization = "Bearer $token"
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
