const pool =
    require("../database/db");

const userRepository = {

    async createUser({
        phone,
        email,
        passwordHash,
        displayName
    }) {

        const result =
            await pool.query(
                `
                INSERT INTO users (
                    phone,
                    email,
                    password_hash,
                    display_name
                )
                VALUES ($1, $2, $3, $4)
                RETURNING
                    id,
                    phone,
                    email,
                    display_name,
                    avatar_url,
                    about,
                    created_at
                `,
                [
                    phone,
                    email,
                    passwordHash,
                    displayName
                ]
            );

        return result.rows[0];
    },

    async getById(id) {

        const result =
            await pool.query(
                `
                SELECT
                    id,
                    phone,
                    email,
                    display_name,
                    avatar_url,
                    about,
                    last_seen_at,
                    created_at
                FROM users
                WHERE id = $1
                `,
                [id]
            );

        return result.rows[0] || null;
    },

    async listUsers(excludeUserId) {

        const result =
            await pool.query(
                `
                SELECT
                    id,
                    phone,
                    email,
                    display_name,
                    avatar_url,
                    about,
                    last_seen_at,
                    created_at
                FROM users
                WHERE id <> $1
                ORDER BY display_name ASC
                `,
                [excludeUserId]
            );

        return result.rows;
    },

    async getByPhone(phone) {

        const result =
            await pool.query(
                `
                SELECT *
                FROM users
                WHERE phone = $1
                `,
                [phone]
            );

        return result.rows[0] || null;
    },

    async getByEmail(email) {

        const result =
            await pool.query(
                `
                SELECT *
                FROM users
                WHERE email = $1
                `,
                [email]
            );

        return result.rows[0] || null;
    }
,
    async updateLastSeen(userId) {

        const result =
            await pool.query(
                `
                UPDATE users
                SET
                    last_seen_at = NOW(),
                    updated_at = NOW()
                WHERE id = $1
                RETURNING
                    id,
                    last_seen_at
                `,
                [userId]
            );

        return result.rows[0] || null;
    },

};

module.exports =
    userRepository;
