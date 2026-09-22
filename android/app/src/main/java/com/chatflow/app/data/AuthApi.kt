package com.chatflow.app.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("api/auth/request-otp")
    suspend fun requestOtp(
        @Body request: OtpRequest
    ): OtpResponse

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: OtpVerifyRequest
    ): OtpResponse

    @GET("api/users")
    suspend fun getUsers(
        @Header("Authorization")
        authorization: String
    ): UserListResponse

    @GET("api/conversations")
    suspend fun getConversations(
        @Header("Authorization")
        authorization: String
    ): ConversationResponse

    @POST("api/conversations/direct")
    suspend fun createDirectConversation(
        @Body request: CreateDirectConversationRequest,
        @Header("Authorization")
        authorization: String
    ): CreateDirectConversationResponse

    @GET("api/messages/{conversationId}")
    suspend fun getMessages(
        @Path("conversationId")
        conversationId: String,

        @Header("Authorization")
        authorization: String
    ): MessageResponse

    @POST("api/messages")
    suspend fun sendMessage(
        @Body request: SendMessageRequest,

        @Header("Authorization")
        authorization: String
    ): SendMessageResponse
    @GET("api/contacts")
    suspend fun getContacts(
        @Header("Authorization")
        authorization: String
    ): ContactListResponse

    @GET("api/contacts/{id}")
    suspend fun getContact(
        @Path("id")
        contactId: String,
        @Header("Authorization")
        authorization: String
    ): CreateContactResponse

    @POST("api/contacts")
    suspend fun createContact(
        @Body request: CreateContactRequest,
        @Header("Authorization")
        authorization: String
    ): CreateContactResponse

}
