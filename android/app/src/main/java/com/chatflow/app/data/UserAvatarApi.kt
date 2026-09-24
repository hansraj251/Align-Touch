package com.chatflow.app.data

import okhttp3.MultipartBody
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.GET
import retrofit2.http.Path
import okhttp3.ResponseBody

interface UserAvatarApi {

    @Multipart
    @POST("api/users/me/avatar")
    suspend fun uploadAvatar(
        @Part file: MultipartBody.Part,
        @Header("Authorization")
        authorization: String
    ): ProfileResponse

    @GET("api/users/{userId}/avatar")
    suspend fun downloadAvatar(
        @Path("userId")
        userId: String,
        @Header("Authorization")
        authorization: String
    ): ResponseBody
}
