const pool =
    require("../database/db");

const messageReactionRepository = {

    async createReaction({
        messageId,
        userId,
        reaction
    }) {
        const result =
            await pool.query(
                `
                INSERT INTO message_reactions (
                    message_id,
                    user_id,
                    reaction
                )
                VALUES (
                    $1,
                    $2,
                    $3
                )
                RETURNING
                    id,
                    message_id,
                    user_id,
                    reaction,
                    created_at
                `,
                [
                    messageId,
                    userId,
                    reaction
                ]
            );
        return result.rows[0];
    },

    async getByMessageAndUser(
        messageId,
        userId
    ) {
        const result =
            await pool.query(
                `
                SELECT
                    id,
                    message_id,
                    user_id,
                    reaction,
                    created_at
                FROM message_reactions
                WHERE message_id = $1
                    AND user_id = $2
                `,
                [
                    messageId,
                    userId
                ]
            );
        return result.rows[0] || null;
    },

    async deleteReaction(
        messageId,
        userId
    ) {
        const result =
            await pool.query(
                `
                DELETE FROM message_reactions
                WHERE message_id = $1
                    AND user_id = $2
                RETURNING
                    id,
                    message_id,
                    user_id,
                    reaction,
                    created_at
                `,
                [
                    messageId,
                    userId
                ]
            );
        return result.rows[0] || null;
    },

    async listByMessage(
        messageId
    ) {
        const result =
            await pool.query(
                `
                SELECT
                    id,
                    message_id,
                    user_id,
                    reaction,
                    created_at
                FROM message_reactions
                WHERE message_id = $1
                ORDER BY id ASC
                `,
                [
                    messageId
                ]
            );
        return result.rows;
    }

};

module.exports =
    messageReactionRepository;
