const userService =
    require("../services/userService");

const userController = {

    async getProfile(req, res) {

        try {

            const user =
                await userService.getProfile(
                    req.user.userId
                );

            if (!user) {
                return res.status(404).json({
                    success: false,
                    message: "User not found"
                });
            }

            return res.status(200).json({
                success: true,
                user
            });

        } catch (error) {

            console.error(
                "Get profile error:",
                error
            );

            return res.status(500).json({
                success: false,
                message: "Failed to load profile"
            });
        }
    },

    async updateProfile(req, res) {

        try {

            const {
                displayName,
                about,
                avatarUrl
            } = req.body;

            const user =
                await userService.updateProfile(
                    req.user.userId,
                    {
                        displayName,
                        about,
                        avatarUrl
                    }
                );

            if (!user) {
                return res.status(404).json({
                    success: false,
                    message: "User not found"
                });
            }

            return res.status(200).json({
                success: true,
                user
            });

        } catch (error) {

            console.error(
                "Update profile error:",
                error
            );

            if (
                error.message ===
                    "Display name is required" ||
                error.message ===
                    "Display name must be at most 100 characters" ||
                error.message ===
                    "About must be at most 500 characters"
            ) {
                return res.status(400).json({
                    success: false,
                    message: error.message
                });
            }

            return res.status(500).json({
                success: false,
                message: "Failed to update profile"
            });
        }
    },

    async listUsers(req, res) {

        try {

            const users =
                await userService.listUsers(
                    req.user.userId
                );

            return res.status(200).json({
                success: true,
                users
            });

        } catch (error) {

            console.error(
                "List users error:",
                error
            );

            return res.status(500).json({
                success: false,
                message: "Failed to load users"
            });
        }
    }
};

module.exports =
    userController;
