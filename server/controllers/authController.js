const userService =
    require("../services/userService");
const authOtpService =
    require("../services/authOtpService");

const authController = {

    async register(req, res) {

        try {

            const {
                phone,
                email,
                password,
                displayName
            } = req.body;

            const user =
                await userService.register({
                    phone,
                    email,
                    password,
                    displayName
                });

            return res.status(201).json({
                success: true,
                message: "User registered successfully",
                user
            });

        } catch (error) {

            console.error(
                "Register error:",
                error
            );

            return res.status(400).json({
                success: false,
                message: error.message
            });
        }
    },

    async login(req, res) {

        try {

            const {
                phone,
                email,
                password
            } = req.body;

            const result =
                await userService.login({
                    phone,
                    email,
                    password
                });

            return res.status(200).json({
                success: true,
                message: "Login successful",
                token: result.token,
                user: result.user
            });

        } catch (error) {

            console.error(
                "Login error:",
                error
            );

            return res.status(401).json({
                success: false,
                message: error.message
            });
        }
    },

    async requestOtp(req, res) {
        try {
            const {
                identifier,
                identifierType
            } = req.body;

            const result =
                await authOtpService.createOtp({
                    identifier,
                    identifierType,
                    purpose: "login"
                });

            console.log(
                "ChatFlow OTP:",
                result.identifier,
                result.otp
            );

            return res.status(200).json({
                success: true,
                message: "OTP sent successfully",
                expiresAt:
                    result.expiresAt
            });
        } catch (error) {
            console.error(
                "Request OTP error:",
                error
            );

            return res.status(400).json({
                success: false,
                message: error.message
            });
        }
    },

    async verifyOtp(req, res) {
        try {
            const {
                identifier,
                purpose,
                otp
            } = req.body;

            const result =
                await authOtpService.verifyOtp({
                    identifier,
                    purpose:
                        purpose || "login",
                    otp
                });

            const loginResult =
                await userService.loginWithOtp({
                    identifier:
                        result.identifier,
                    identifierType:
                        result.identifierType
                });

            return res.status(200).json({
                success: true,
                message: "OTP verified successfully",
                verified:
                    result.verified,
                token:
                    loginResult.token,
                user:
                    loginResult.user
            });
        } catch (error) {
            console.error(
                "Verify OTP error:",
                error
            );

            return res.status(400).json({
                success: false,
                message: error.message
            });
        }
    },

    async getMe(req, res) {

        try {

            const user =
                await userService.getById(
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
                "Get me error:",
                error
            );

            return res.status(500).json({
                success: false,
                message: "Failed to get user"
            });
        }
    }
};

module.exports =
    authController;
