package com.chatflow.app.data

class ConversationRepository {

    suspend fun getConversations(

        token: String

    ): ConversationResponse {

        return ApiClient.authApi.getConversations(

            authorization =
                "Bearer $token"

        )

    }

    suspend fun createDirectConversation(

        token: String,

        otherUserId: String

    ): CreateDirectConversationResponse {

        return ApiClient.authApi.createDirectConversation(

            request =
                CreateDirectConversationRequest(
                    otherUserId =
                        otherUserId
                ),

            authorization =
                "Bearer $token"

        )

    }

    suspend fun updateGroup(
        token: String,
        conversationId: String,
        title: String,
        avatarUrl: String? = null
    ): GroupDetailsResponse {
        return ApiClient.authApi.updateGroup(
            conversationId = conversationId,
            request = UpdateGroupRequest(
                title = title,
                avatarUrl = avatarUrl
            ),
            authorization = "Bearer $token"
        )
    }

    suspend fun addGroupMember(
        token: String,
        conversationId: String,
        userId: String
    ): AddGroupMemberResponse {

        return ApiClient.authApi.addGroupMember(
            conversationId = conversationId,
            request =
                AddGroupMemberRequest(
                    memberUserId = userId
                ),
            authorization = "Bearer $token"
        )
    }

    suspend fun removeGroupMember(
        token: String,
        conversationId: String,
        userId: String
    ): DeleteGroupMemberResponse {
        return ApiClient.authApi.removeGroupMember(
            conversationId = conversationId,
            userId = userId,
            authorization = "Bearer $token"
        )
    }

    suspend fun deleteGroup(
        token: String,
        conversationId: String
    ): DeleteGroupResponse {
        return ApiClient.authApi.deleteGroup(
            conversationId = conversationId,
            authorization = "Bearer $token"
        )
    }

    suspend fun clearChat(

        token: String,

        conversationId: String

    ): ClearChatResponse {

        return ApiClient.authApi.clearChat(

            conversationId = conversationId,

            authorization = "Bearer $token"

        )

    }

    suspend fun getGroupDetails(
        token: String,
        conversationId: String
    ): GroupDetailsResponse {

        return ApiClient.authApi.getGroupDetails(
            conversationId = conversationId,
            authorization = "Bearer $token"
        )
    }

    suspend fun getGroupMembers(
        token: String,
        conversationId: String
    ): GroupMembersResponse {

        return ApiClient.authApi.getGroupMembers(
            conversationId = conversationId,
            authorization = "Bearer $token"
        )
    }

    suspend fun createGroupConversation(

        token: String,

        title: String,

        memberUserIds: List<String>

    ): CreateGroupConversationResponse {

        return ApiClient.authApi.createGroupConversation(

            request =

                CreateGroupConversationRequest(

                    title = title,

                    memberUserIds = memberUserIds

                ),

            authorization =

                "Bearer $token"

        )

    }

}
