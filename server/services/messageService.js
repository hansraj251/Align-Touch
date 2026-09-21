const conversationRepository =
    require("../repositories/conversationRepository");

const messageRepository =
    require("../repositories/messageRepository");

const messageService = {

    async sendTextMessage({
        conversationId,
        senderId,
        content,
        replyToMessageId = null
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
            replyToMessageId
        });
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
