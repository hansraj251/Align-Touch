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

    
async updateGroup(req, res) {
    try {
        const group = await conversationService.updateGroup(
            req.user.userId,
            req.params.id,
            req.body.title,
            req.body.avatarUrl || null
        );

        return res.status(200).json({
            success: true,
            group
        });
    } catch (error) {
        console.error("Update group error:", error);

        if (
            error.message ===
                "Only group admin can edit the group"
        ) {
            return res.status(403).json({
                success: false,
                message: error.message
            });
        }

        if (
            error.message ===
                "Group name is required"
        ) {
            return res.status(400).json({
                success: false,
                message: error.message
            });
        }

        if (
            error.message ===
                "Group not found"
        ) {
            return res.status(404).json({
                success: false,
                message: error.message
            });
        }

        return res.status(500).json({
            success: false,
            message: "Failed to update group"
        });
    }
},

async addGroupMember(req, res) {
    try {
        const member =
            await conversationService.addGroupMember(
                req.user.userId,
                req.params.id,
                req.body.memberUserId
            );

        return res.status(201).json({
            success: true,
            member
        });

    } catch (error) {
        console.error(
            "Add group member error:",
            error
        );

        const status =
            error.message ===
                "Group not found"
                ? 404
                : error.message ===
                    "User not found"
                    ? 404
                    : error.message ===
                        "User is already a group member"
                        ? 409
                        : 400;

        return res.status(status).json({
            success: false,
            message: error.message
        });
    }
},

async removeGroupMember(req, res) {
    try {
        const removed =
            await conversationService.removeGroupMember(
                req.user.userId,
                req.params.id,
                req.params.userId
            );

        return res.status(200).json({
            success: true,
            member: removed
        });
    } catch (error) {
        console.error(
            "Remove group member error:",
            error
        );

        if (
            error.message ===
                "Only group admin can remove members"
        ) {
            return res.status(403).json({
                success: false,
                message: error.message
            });
        }

        if (
            error.message ===
                "Admin cannot remove self"
        ) {
            return res.status(400).json({
                success: false,
                message: error.message
            });
        }

        if (
            error.message ===
                "Group member not found"
        ) {
            return res.status(404).json({
                success: false,
                message: error.message
            });
        }

        if (
            error.message ===
                "Group not found"
        ) {
            return res.status(404).json({
                success: false,
                message: error.message
            });
        }

        return res.status(500).json({
            success: false,
            message: "Failed to remove group member"
        });
    }
},

async deleteGroup(req, res) {
    try {
        const deleted =
            await conversationService.deleteGroup(
                req.user.userId,
                req.params.id
            );

        return res.status(200).json({
            success: true,
            groupId: deleted.id
        });
    } catch (error) {
        console.error(
            "Delete group error:",
            error
        );

        if (
            error.message ===
                "Only group admin can delete the group"
        ) {
            return res.status(403).json({
                success: false,
                message: error.message
            });
        }

        if (
            error.message ===
                "Group not found"
        ) {
            return res.status(404).json({
                success: false,
                message: error.message
            });
        }

        return res.status(500).json({
            success: false,
            message: "Failed to delete group"
        });
    }
},

async getGroupDetails(
        req,
        res
    ) {
        try {
            const group =
                await conversationService
                    .getGroupDetails(
                        req.user.userId,
                        req.params.id
                    );

            return res.status(200).json({
                success: true,
                group
            });
        } catch (error) {
            console.error(
                "Get group details error:",
                error
            );

            if (
                error.message ===
                "Group not found"
            ) {
                return res.status(404).json({
                    success: false,
                    message: error.message
                });
            }

            return res.status(500).json({
                success: false,
                message:
                    "Failed to get group details"
            });
        }
    },

    async getGroupMembers(
        req,
        res
    ) {
        try {
            const members =
                await conversationService
                    .getGroupMembers(
                        req.user.userId,
                        req.params.id
                    );

            return res.status(200).json({
                success: true,
                members
            });
        } catch (error) {
            console.error(
                "Get group members error:",
                error
            );

            if (
                error.message ===
                "Group not found"
            ) {
                return res.status(404).json({
                    success: false,
                    message: error.message
                });
            }

            return res.status(500).json({
                success: false,
                message:
                    "Failed to get group members"
            });
        }
    },

    async createGroupConversation(
        req,
        res
    ) {
        try {
            const conversation =
                await conversationService
                    .createGroupConversation(
                        req.user.userId,
                        req.body.title,
                        req.body.memberUserIds
                    );

            return res.status(201).json({
                success: true,
                conversation
            });
        } catch (error) {
            console.error(
                "Create group conversation error:",
                error
            );

            return res.status(400).json({
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
