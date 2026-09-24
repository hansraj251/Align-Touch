const conversationRepository =
    require("../repositories/conversationRepository");

const messageRepository =
    require("../repositories/messageRepository");

const messageReactionRepository =
    require("../repositories/messageReactionRepository");

const allowedReactions = [
    "👍",
    "❤️",
    "😂",
    "😮",
    "😢",
    "😡"
];

const messageReactionService = {

    async addReaction({
        messageId,
        userId,
        reaction
    }) {

        if (!messageId) {

            throw new Error(
                "Message is required"
            );

        }

        if (!reaction) {

            throw new Error(
                "Reaction is required"
            );

        }

        if (!allowedReactions.includes(reaction)) {

            throw new Error(
                "Invalid reaction"
            );

        }

        const message =
            await messageRepository.getById(
                messageId
            );

        if (!message) {

            throw new Error(
                "Message not found"
            );

        }

        const isMember =
            await conversationRepository.isMember(
                message.conversation_id,
                userId
            );

        if (!isMember) {

            throw new Error(
                "You are not a member of this conversation"
            );

        }

        const existingReaction =
            await messageReactionRepository
                .getByMessageAndUser(
                    messageId,
                    userId
                );

        if (existingReaction) {

            if (
                existingReaction.reaction ===
                reaction
            ) {

                return existingReaction;

            }

            await messageReactionRepository
                .deleteReaction(
                    messageId,
                    userId
                );

        }

        return messageReactionRepository
            .createReaction({
                messageId,
                userId,
                reaction
            });

    },

    async removeReaction({
        messageId,
        userId
    }) {

        if (!messageId) {

            throw new Error(
                "Message is required"
            );

        }

        const message =
            await messageRepository.getById(
                messageId
            );

        if (!message) {

            throw new Error(
                "Message not found"
            );

        }

        const isMember =
            await conversationRepository.isMember(
                message.conversation_id,
                userId
            );

        if (!isMember) {

            throw new Error(
                "You are not a member of this conversation"
            );

        }

        return messageReactionRepository
            .deleteReaction(
                messageId,
                userId
            );

    },

    async listReactions({
        messageId,
        userId
    }) {

        if (!messageId) {

            throw new Error(
                "Message is required"
            );

        }

        const message =
            await messageRepository.getById(
                messageId
            );

        if (!message) {

            throw new Error(
                "Message not found"
            );

        }

        const isMember =
            await conversationRepository.isMember(
                message.conversation_id,
                userId
            );

        if (!isMember) {

            throw new Error(
                "You are not a member of this conversation"
            );

        }

        return messageReactionRepository
            .listByMessage(
                messageId
            );

    }

};

module.exports =
    messageReactionService;
