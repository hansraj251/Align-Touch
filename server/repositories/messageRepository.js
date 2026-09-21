const pool =
    require("../database/db");

const messageRepository = {

    async createMessage({
        conversationId,
        senderId,
        messageType,
        content,
        replyToMessageId
    }) {

        const result =
            await pool.query(
                `
                INSERT INTO messages (
                    conversation_id,
                    sender_id,
                    message_type,
                    content,
                    reply_to_message_id
                )
                VALUES (
                    $1,
                    $2,
                    $3,
                    $4,
                    $5
                )
                RETURNING
                    id,
                    conversation_id,
                    sender_id,
                    message_type,
                    content,
                    reply_to_message_id,
                    created_at,
                    edited_at,
                    deleted_at
                `,
                [
                    conversationId,
                    senderId,
                    messageType,
                    content,
                    replyToMessageId
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
                    conversation_id,
                    sender_id,
                    message_type,
                    content,
                    reply_to_message_id,
                    created_at,
                    edited_at,
                    deleted_at
                FROM messages
                WHERE id = $1
                `,
                [id]
            );

        return result.rows[0] || null;
    },

    async listByConversation(
        conversationId,
        limit = 50
    ) {

        const result =
            await pool.query(
                `
                SELECT
                    id,
                    conversation_id,
                    sender_id,
                    message_type,
                    content,
                    reply_to_message_id,
                    created_at,
                    edited_at,
                    deleted_at
                FROM messages
                WHERE conversation_id = $1
                ORDER BY id ASC
                LIMIT $2
                `,
                [
                    conversationId,
                    limit
                ]
            );

        return result.rows;
    }
};

module.exports =
    messageRepository;
