const bcrypt =
    require("bcrypt");

const jwt =
    require("jsonwebtoken");

const userRepository =
    require("../repositories/userRepository");

const userService = {

    async register({
        phone,
        email,
        password,
        displayName
    }) {

        if (!displayName) {
            throw new Error(
                "Display name is required"
            );
        }

        if (!phone && !email) {
            throw new Error(
                "Phone or email is required"
            );
        }

        if (!password) {
            throw new Error(
                "Password is required"
            );
        }

        if (password.length < 6) {
            throw new Error(
                "Password must be at least 6 characters"
            );
        }

        if (phone) {

            const existingUser =
                await userRepository.getByPhone(
                    phone
                );

            if (existingUser) {
                throw new Error(
                    "Phone number is already registered"
                );
            }
        }

        if (email) {

            const existingUser =
                await userRepository.getByEmail(
                    email
                );

            if (existingUser) {
                throw new Error(
                    "Email is already registered"
                );
            }
        }

        const passwordHash =
            await bcrypt.hash(
                password,
                12
            );

        return userRepository.createUser({
            phone,
            email,
            passwordHash,
            displayName
        });
    },

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

    async login({
        phone,
        email,
        password
    }) {

        if (!phone && !email) {
            throw new Error(
                "Phone or email is required"
            );
        }

        if (!password) {
            throw new Error(
                "Password is required"
            );
        }

        let user = null;

        if (phone) {

            user =
                await userRepository.getByPhone(
                    phone
                );

        } else {

            user =
                await userRepository.getByEmail(
                    email
                );
        }

        if (!user) {
            throw new Error(
                "Invalid credentials"
            );
        }

        const passwordMatches =
            await bcrypt.compare(
                password,
                user.password_hash
            );

        if (!passwordMatches) {
            throw new Error(
                "Invalid credentials"
            );
        }

        const token =
            jwt.sign(
                {
                    userId: user.id
                },
                process.env.JWT_SECRET,
                {
                    expiresIn: "30d"
                }
            );

        return {
            token,
            user: {
                id: user.id,
                phone: user.phone,
                email: user.email,
                display_name: user.display_name,
                avatar_url: user.avatar_url,
                about: user.about,
                last_seen_at: user.last_seen_at,
                created_at: user.created_at
            }
        };
    },

    async loginWithOtp({
        identifier,
        identifierType
    }) {
        if (!identifier) {
            throw new Error(
                "Identifier is required"
            );
        }

        let user = null;

        if (
            identifierType ===
            "phone"
        ) {
            user =
                await userRepository.getByPhone(
                    identifier
                );
        } else if (
            identifierType ===
            "email"
        ) {
            user =
                await userRepository.getByEmail(
                    identifier
                );
        } else {
            throw new Error(
                "Invalid identifier type"
            );
        }

        if (!user) {
            throw new Error(
                "User not found"
            );
        }

        const token =
            jwt.sign(
                {
                    userId: user.id
                },
                process.env.JWT_SECRET,
                {
                    expiresIn: "30d"
                }
            );

        return {
            token,
            user: {
                id: user.id,
                phone: user.phone,
                email: user.email,
                display_name:
                    user.display_name,
                avatar_url:
                    user.avatar_url,
                about: user.about,
                last_seen_at:
                    user.last_seen_at,
                created_at:
                    user.created_at
            }
        };
    },

    async updateLastSeen(userId) {

        return userRepository.updateLastSeen(
            userId
        );
    },

};

module.exports =
    userService;
