const conversationRepository =
    require("../repositories/conversationRepository");

const messageRepository =
    require("../repositories/messageRepository");

const messageService = {

    async sendTextMessage({
        conversationId,
        senderId,
        content,
        replyToMessageId = null,
        expiresAt = null
    }) {

        if (!conversationId) {
            throw new Error(
                "Conversation is required"
            );
        }

        if (!content || !content.trim()) {
            throw new Error(
                "Message content is required"
            );
        }

        if (expiresAt !== null) {
            const expiryDate =
                new Date(expiresAt);

            if (
                Number.isNaN(
                    expiryDate.getTime()
                )
            ) {
                throw new Error(
                    "Invalid message expiry"
                );
            }

            if (
                expiryDate.getTime() <=
                Date.now()
            ) {
                throw new Error(
                    "Message expiry must be in the future"
                );
            }

            expiresAt =
                expiryDate;
        }

        const isMember =
            await conversationRepository.isMember(
                conversationId,
                senderId
            );

        if (!isMember) {
            throw new Error(
                "You are not a member of this conversation"
            );
        }

        if (replyToMessageId) {

            const replyMessage =
                await messageRepository.getById(
                    replyToMessageId
                );

            if (!replyMessage) {
                throw new Error(
                    "Reply message not found"
                );
            }

            if (
                String(
                    replyMessage.conversation_id
                ) !==
                String(conversationId)
            ) {
                throw new Error(
                    "Reply message belongs to another conversation"
                );
            }
        }

        return messageRepository.createMessage({
            conversationId,
            senderId,
            messageType: "text",
            content: content.trim(),
            replyToMessageId,
            expiresAt
        });
    },

    async deleteMessageForEveryone(
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
            String(message.sender_id) !==
            String(userId)
        ) {
            throw new Error(
                "Only the sender can delete this message"
            );
        }

        if (message.deleted_at) {
            throw new Error(
                "Message is already deleted"
            );
        }

        const deletedMessage =
            await messageRepository.deleteForEveryone(
                messageId,
                userId
            );

        if (!deletedMessage) {
            throw new Error(
                "Message could not be deleted"
            );
        }

        return deletedMessage;
    },

    async listMessages(
        conversationId,
        userId
    ) {

        const isMember =
            await conversationRepository.isMember(
                conversationId,
                userId
            );

        if (!isMember) {
            throw new Error(
                "You are not a member of this conversation"
            );
        }

        return messageRepository.listByConversation(
            conversationId
        );
    }
};

module.exports =
    messageService;
