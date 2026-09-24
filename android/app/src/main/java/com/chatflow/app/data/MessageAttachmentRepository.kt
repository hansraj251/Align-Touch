package com.chatflow.app.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody

class MessageAttachmentRepository {

    suspend fun uploadAttachment(
        conversationId: RequestBody,
        file: MultipartBody.Part,
        token: String
    ): MessageAttachmentUploadResponse {

        return ApiClient
            .messageAttachmentApi
            .uploadAttachment(
                conversationId =
                    conversationId,
                file =
                    file,
                authorization =
                    "Bearer $token"
            )
    }

    suspend fun downloadAttachment(
        attachmentId: String,
        token: String
    ): ResponseBody {

        return ApiClient
            .messageAttachmentApi
            .downloadAttachment(
                attachmentId =
                    attachmentId,
                authorization =
                    "Bearer $token"
            )
    }
}
