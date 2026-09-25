package com.chatflow.app.data

data class ConversationResponse(
    val success: Boolean,
    val conversations: List<Conversation>
)

data class Conversation(
    val id: String,
    val type: String,
    val title: String?,
    val avatar_url: String?,
    val created_by: String,
    val created_at: String,
    val updated_at: String,
    val other_user_id: String,
    val contact_id: String?,
    val other_user_phone: String?,
    val other_user_display_name: String,
    val other_user_avatar_url: String?,
    val other_user_about: String?,
    val other_user_last_seen_at: String?
)

data class GroupDetailsResponse(

    val success: Boolean,

    val group: GroupDetails

)

data class GroupDetails(

    val id: String,

    val type: String,

    val title: String?,

    val avatar_url: String?,

    val created_by: String,

    val created_at: String,

    val updated_at: String,

    val current_user_role: String

)

data class GroupMembersResponse(

    val success: Boolean,

    val members: List<GroupMember>

)

data class GroupMember(

    val id: String,

    val user_id: String,

    val role: String,

    val joined_at: String,

    val phone: String?,

    val display_name: String?,

    val avatar_url: String?,

    val about: String?

)

data class UpdateGroupRequest(
    val title: String,
    val avatarUrl: String? = null
)

data class AddGroupMemberRequest(
    val memberUserId: String
)

data class AddGroupMemberResponse(
    val success: Boolean,
    val member: GroupMember?
)

data class DeleteGroupMemberResponse(
    val success: Boolean,
    val member: GroupMember?
)

data class DeleteGroupResponse(
    val success: Boolean,
    val groupId: String
)

data class ClearChatResponse(

    val success: Boolean,

    val conversationId: String,

    val clearedAt: String

)
