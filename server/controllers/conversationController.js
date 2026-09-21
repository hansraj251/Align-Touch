const conversationService =
    require("../services/conversationService");

const conversationController = {

    async getConversations(
        req,
        res
    ) {

        try {

            const conversations =
                await conversationService
                    .getConversationsForUser(
                        req.user.userId
                    );

            return res.status(200).json({
                success: true,
                conversations
            });

        } catch (error) {

            console.error(
                "Get conversations error:",
                error
            );

            return res.status(500).json({
                success: false,
                message: error.message
            });
        }
    },

    async createDirectConversation(
        req,
        res
    ) {

        try {

            const conversation =
                await conversationService.createDirectConversation(
                    req.user.userId,
                    req.body.otherUserId
                );

            return res.status(201).json({
                success: true,
                conversation
            });

        } catch (error) {

            console.error(
                "Create conversation error:",
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
    conversationController;
