const messageService =
    require("../services/messageService");

const messageController = {

    async sendMessage(req, res) {

        try {

            const {
                conversationId,
                content,
                replyToMessageId,
                expiresAt
            } = req.body;

            const message =
                await messageService.sendTextMessage({
                    conversationId,
                    senderId: req.user.userId,
                    content,
                    replyToMessageId,
                    expiresAt
                });

            return res.status(201).json({
                success: true,
                message
            });

        } catch (error) {

            console.error(
                "Send message error:",
                error
            );

            return res.status(400).json({
                success: false,
                message: error.message
            });
        }
    },

    async deleteMessageForEveryone(
        req,
        res
    ) {
        try {
            const message =
                await messageService.deleteMessageForEveryone(
                    req.params.messageId,
                    req.user.userId
                );

            return res.status(200).json({
                success: true,
                message
            });
        } catch (error) {
            console.error(
                "Delete message error:",
                error
            );

            return res.status(400).json({
                success: false,
                message: error.message
            });
        }
    },

    async listMessages(req, res) {

        try {

            const messages =
                await messageService.listMessages(
                    req.params.conversationId,
                    req.user.userId
                );

            return res.status(200).json({
                success: true,
                messages
            });

        } catch (error) {

            console.error(
                "List messages error:",
                error
            );

            return res.status(400).json({
                success: false,
                message: error.message
            });
        }
    }
};

module.exports =
    messageController;
