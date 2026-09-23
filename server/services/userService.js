const jwt =
    require("jsonwebtoken");

const userRepository =
    require("../repositories/userRepository");

const userService = {

    async getById(id) {

        return userRepository.getById(
            id
        );
    },

    async listUsers(currentUserId) {

        return userRepository.listUsers(
            currentUserId
        );
    },

    async loginWithOtp({
        identifier
    }) {
        if (!identifier) {
            throw new Error(
                "Mobile number is required"
            );
        }

        const user =
            await userRepository.getByPhone(
                identifier
            );

        let loginUser = user;

        if (!loginUser) {
            loginUser =
                await userRepository.createOtpUser({
                    phone: identifier,
                    email: null,
                    displayName:
                        ""
                });

            await userRepository
                .linkUserToPhoneContacts(
                    loginUser.id,
                    identifier
                );
        }

        const token =
            jwt.sign(
                {
                    userId: loginUser.id
                },
                process.env.JWT_SECRET,
                {
                    expiresIn: "30d"
                }
            );

        return {
            token,
            user: {
                id: loginUser.id,
                phone: loginUser.phone,
                display_name:
                    loginUser.display_name,
                avatar_url:
                    loginUser.avatar_url,
                about: loginUser.about,
                last_seen_at:
                    loginUser.last_seen_at,
                created_at:
                    loginUser.created_at
            }
        };
    },

    async getProfile(userId) {

        return userRepository.getById(
            userId
        );
    },

    async updateProfile(
        userId,
        {
            displayName,
            about,
            avatarUrl
        }
    ) {

        const normalizedDisplayName =
            displayName?.trim();

        if (!normalizedDisplayName) {
            throw new Error(
                "Display name is required"
            );
        }

        if (
            normalizedDisplayName.length >
            100
        ) {
            throw new Error(
                "Display name must be at most 100 characters"
            );
        }

        const normalizedAbout =
            about?.trim() || null;

        if (
            normalizedAbout &&
            normalizedAbout.length >
            500
        ) {
            throw new Error(
                "About must be at most 500 characters"
            );
        }

        return userRepository.updateProfile(
            userId,
            {
                displayName:
                    normalizedDisplayName,
                about:
                    normalizedAbout,
                avatarUrl:
                    avatarUrl?.trim() || null
            }
        );
    },

    async updateLastSeen(userId) {

        return userRepository.updateLastSeen(
            userId
        );
    },

};

module.exports =
    userService;
