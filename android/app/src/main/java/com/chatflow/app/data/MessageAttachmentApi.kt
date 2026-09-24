package com.chatflow.app.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface MessageAttachmentApi {

    @Multipart
    @POST("api/message-uploads")
    suspend fun uploadAttachment(
        @Part("conversationId")
        conversationId: RequestBody,
        @Part file: MultipartBody.Part,
        @Header("Authorization")
        authorization: String
    ): MessageAttachmentUploadResponse

    @GET("api/message-uploads/{attachmentId}")
    suspend fun downloadAttachment(
        @Path("attachmentId")
        attachmentId: String,
        @Header("Authorization")
        authorization: String
    ): ResponseBody
}
