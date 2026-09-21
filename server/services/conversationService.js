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
