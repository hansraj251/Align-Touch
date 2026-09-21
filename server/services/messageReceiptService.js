const conversationRepository =
    require("../repositories/conversationRepository");

const messageRepository =
    require("../repositories/messageRepository");

const messageReceiptRepository =
    require("../repositories/messageReceiptRepository");

const messageReceiptService = {

    async markDelivered(
        messageId,
        userId
    ) {
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

        if (
            String(message.sender_id) ===
            String(userId)
        ) {
            throw new Error(
                "Sender cannot mark own message as delivered"
            );
        }

        return messageReceiptRepository.markDelivered(
            messageId,
            userId
        );
    },

    async markRead(
        messageId,
        userId
    ) {
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

        if (
            String(message.sender_id) ===
            String(userId)
        ) {
            throw new Error(
                "Sender cannot mark own message as read"
            );
        }

        return messageReceiptRepository.markRead(
            messageId,
            userId
        );
    }
};

module.exports =
    messageReceiptService;
