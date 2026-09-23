const userRepository =
    require("../repositories/userRepository");

const conversationRepository =
    require("../repositories/conversationRepository");

const conversationService = {

    async isMember(
        conversationId,
        userId
    ) {

        return conversationRepository.isMember(
            conversationId,
            userId
        );
    },

    async getConversationsForUser(
        userId
    ) {

        return conversationRepository
            .getConversationsForUser(
                userId
            );
    },

    
async updateGroup(
    userId,
    conversationId,
    title,
    avatarUrl = null
) {
    const member =
        await conversationRepository.getGroupMemberRole(
            conversationId,
            userId
        );

    if (!member) {
        throw new Error("Group not found");
    }

    if (member.role !== "admin") {
        throw new Error(
            "Only group admin can edit the group"
        );
    }

    if (!title || !title.trim()) {
        throw new Error(
            "Group name is required"
        );
    }

    return conversationRepository.updateGroup(
        conversationId,
        title.trim(),
        avatarUrl
    );
},

async addGroupMember(
    userId,
    conversationId,
    memberUserId
) {
    const member =
        await conversationRepository.getGroupMemberRole(
            conversationId,
            userId
        );

    if (!member) {
        throw new Error("Group not found");
    }

    if (member.role !== "admin") {
        throw new Error(
            "Only group admin can add members"
        );
    }

    if (!memberUserId) {
        throw new Error(
            "Member user id is required"
        );
    }

    if (
        String(memberUserId) ===
        String(userId)
    ) {
        throw new Error(
            "Admin cannot add self"
        );
    }

    const user =
        await userRepository.getById(
            memberUserId
        );

    if (!user) {
        throw new Error(
            "User not found"
        );
    }

    const added =
        await conversationRepository.addGroupMember(
            conversationId,
            memberUserId
        );

    if (!added) {
        throw new Error(
            "User is already a group member"
        );
    }

    return added;
},

async removeGroupMember(
    userId,
    conversationId,
    memberUserId
) {
    const member =
        await conversationRepository.getGroupMemberRole(
            conversationId,
            userId
        );

    if (!member) {
        throw new Error("Group not found");
    }

    if (member.role !== "admin") {
        throw new Error(
            "Only group admin can remove members"
        );
    }

    if (!memberUserId) {
        throw new Error(
            "Member user id is required"
        );
    }

    if (
        String(memberUserId) ===
        String(userId)
    ) {
        throw new Error(
            "Admin cannot remove self"
        );
    }

    const removed =
        await conversationRepository.removeGroupMember(
            conversationId,
            memberUserId
        );

    if (!removed) {
        throw new Error(
            "Group member not found"
        );
    }

    return removed;
},

async deleteGroup(
    userId,
    conversationId
) {
    const member =
        await conversationRepository.getGroupMemberRole(
            conversationId,
            userId
        );

    if (!member) {
        throw new Error("Group not found");
    }

    if (member.role !== "admin") {
        throw new Error(
            "Only group admin can delete the group"
        );
    }

    const deleted =
        await conversationRepository.deleteGroup(
            conversationId
        );

    if (!deleted) {
        throw new Error("Group not found");
    }

    return deleted;
},

async getGroupDetails(
        userId,
        conversationId
    ) {

        const group =
            await conversationRepository
                .getGroupDetails(
                    conversationId,
                    userId
                );

        if (!group) {
            throw new Error(
                "Group not found"
            );
        }

        return group;
    },

    async getGroupMembers(
        userId,
        conversationId
    ) {

        const group =
            await conversationRepository
                .getGroupDetails(
                    conversationId,
                    userId
                );

        if (!group) {
            throw new Error(
                "Group not found"
            );
        }

        return conversationRepository
            .getGroupMembers(
                conversationId
            );
    },

    async createGroupConversation(
        userId,
        title,
        memberUserIds
    ) {
        const groupTitle =
            String(title || "").trim();

        if (!groupTitle) {
            throw new Error(
                "Group title is required"
            );
        }

        if (groupTitle.length > 255) {
            throw new Error(
                "Group title cannot exceed 255 characters"
            );
        }

        if (!Array.isArray(memberUserIds)) {
            throw new Error(
                "Group members are required"
            );
        }

        const uniqueMemberIds = [
            ...new Set(
                memberUserIds
                    .map(
                        (id) =>
                            String(id).trim()
                    )
                    .filter(Boolean)
            )
        ].filter(
            (id) =>
                id !==
                String(userId)
        );

        if (uniqueMemberIds.length < 1) {
            throw new Error(
                "Select at least one group member"
            );
        }

        for (
            const memberId
            of uniqueMemberIds
        ) {
            const member =
                await userRepository.getById(
                    memberId
                );

            if (!member) {
                throw new Error(
                    "Group member not found"
                );
            }
        }

        return conversationRepository.createGroupConversation(
            userId,
            groupTitle,
            uniqueMemberIds
        );
    },

    async createDirectConversation(
        userId,
        otherUserId
    ) {

        if (!otherUserId) {
            throw new Error(
                "Other user is required"
            );
        }

        if (
            String(userId) ===
            String(otherUserId)
        ) {
            throw new Error(
                "Cannot create conversation with yourself"
            );
        }

        const otherUser =
            await userRepository.getById(
                otherUserId
            );

        if (!otherUser) {
            throw new Error(
                "User not found"
            );
        }

        const existingConversation =
            await conversationRepository.findDirectConversation(
                userId,
                otherUserId
            );

        if (existingConversation) {
            return existingConversation;
        }

        return conversationRepository.createDirectConversation(
            userId,
            otherUserId
        );
    }
};

module.exports =
    conversationService;
