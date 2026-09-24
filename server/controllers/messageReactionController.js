const messageReactionService =
    require("../services/messageReactionService");

const messageReactionController = {

    async addReaction(req, res) {

        try {

            const {
                reaction
            } = req.body;

            const messageReaction =
                await messageReactionService
                    .addReaction({
                        messageId:
                            req.params.messageId,
                        userId:
                            req.user.userId,
                        reaction
                    });

            return res.status(201).json({
                success: true,
                reaction: messageReaction
            });

        } catch (error) {

            console.error(
                "Add reaction error:",
                error
            );

            return res.status(400).json({
                success: false,
                message: error.message
            });

        }

    },

    async removeReaction(req, res) {

        try {

            const messageReaction =
                await messageReactionService
                    .removeReaction({
                        messageId:
                            req.params.messageId,
                        userId:
                            req.user.userId
                    });

            return res.status(200).json({
                success: true,
                reaction: messageReaction
            });

        } catch (error) {

            console.error(
                "Remove reaction error:",
                error
            );

            return res.status(400).json({
                success: false,
                message: error.message
            });

        }

    },

    async listReactions(req, res) {

        try {

            const reactions =
                await messageReactionService
                    .listReactions({
                        messageId:
                            req.params.messageId,
                        userId:
                            req.user.userId
                    });

            return res.status(200).json({
                success: true,
                reactions
            });

        } catch (error) {

            console.error(
                "List reactions error:",
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
    messageReactionController;
