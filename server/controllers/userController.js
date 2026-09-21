const userService =
    require("../services/userService");

const userController = {

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
