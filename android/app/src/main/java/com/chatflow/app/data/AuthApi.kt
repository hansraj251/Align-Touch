package com.chatflow.app.data

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH

import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {

    @POST("api/auth/request-otp")
    suspend fun requestOtp(
        @Body request: OtpRequest
    ): OtpResponse

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: OtpVerifyRequest
    ): OtpResponse

    @GET("api/users/me")
    suspend fun getProfile(
        @Header("Authorization")
        authorization: String
    ): ProfileResponse

    @PATCH("api/users/me")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest,
        @Header("Authorization")
        authorization: String
    ): ProfileResponse

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

    @POST("api/conversations/group")
    suspend fun createGroupConversation(
        @Body request: CreateGroupConversationRequest,
        @Header("Authorization")
        authorization: String
    ): CreateGroupConversationResponse

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

    @PUT("api/contacts/{id}")
    suspend fun updateContact(
        @Path("id")
        contactId: String,
        @Body request: UpdateContactRequest,
        @Header("Authorization")
        authorization: String
    ): CreateContactResponse

    @DELETE("api/contacts/{id}")
    suspend fun deleteContact(
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


@GET("api/conversations/{id}/group")
suspend fun getGroupDetails(

    @Path("id")
    conversationId: String,

    @Header("Authorization")
    authorization: String

): GroupDetailsResponse

@GET("api/conversations/{id}/group/members")
suspend fun getGroupMembers(

    @Path("id")
    conversationId: String,

    @Header("Authorization")
    authorization: String

): GroupMembersResponse



    @PUT("api/conversations/{id}/group")
    suspend fun updateGroup(
        @Path("id") conversationId: String,
        @Body request: UpdateGroupRequest,
        @Header("Authorization") authorization: String
    ): GroupDetailsResponse

    @POST("api/conversations/{id}/group/members")
    suspend fun addGroupMember(
        @Path("id") conversationId: String,
        @Body request: AddGroupMemberRequest,
        @Header("Authorization") authorization: String
    ): AddGroupMemberResponse

    @DELETE("api/conversations/{id}/group/members/{userId}")
    suspend fun removeGroupMember(
        @Path("id") conversationId: String,
        @Path("userId") userId: String,
        @Header("Authorization") authorization: String
    ): DeleteGroupMemberResponse

    @DELETE("api/conversations/{id}/group")
    suspend fun deleteGroup(
        @Path("id") conversationId: String,
        @Header("Authorization") authorization: String
    ): DeleteGroupResponse

}
