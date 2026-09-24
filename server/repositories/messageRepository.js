const pool =
    require("../database/db");

const messageRepository = {

    async createMessage({
        conversationId,
        senderId,
        messageType,
        content,
        replyToMessageId,
        expiresAt,
        forwardedFromMessageId = null
    }) {

        const result =
            await pool.query(
                `
                INSERT INTO messages (
                    conversation_id,
                    sender_id,
                    message_type,
                    content,
                    reply_to_message_id,
                    forwarded_from_message_id,
                    expires_at
                )
                VALUES (
                    $1,
                    $2,
                    $3,
                    $4,
                    $5,
                    $6,
                    $7
                )
                RETURNING
                    id,
                    conversation_id,
                    sender_id,
                    message_type,
                    content,
                    reply_to_message_id,
                    forwarded_from_message_id,
                    created_at,
                    edited_at,
                    deleted_at,
                    expires_at
                `,
                [
                    conversationId,
                    senderId,
                    messageType,
                    content,
                    replyToMessageId,
                    forwardedFromMessageId,
                    expiresAt
                ]
            );

        return result.rows[0];
    },

    async forwardMessage({
        conversationId,
        senderId,
        messageType,
        content,
        forwardedFromMessageId
    }) {
        const result =
            await pool.query(
                `
                INSERT INTO messages (
                    conversation_id,
                    sender_id,
                    message_type,
                    content,
                    forwarded_from_message_id
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
                    forwarded_from_message_id,
                    created_at,
                    edited_at,
                    deleted_at,
                    expires_at
                `,
                [
                    conversationId,
                    senderId,
                    messageType,
                    content,
                    forwardedFromMessageId
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
                    forwarded_from_message_id,
                    created_at,
                    edited_at,
                    deleted_at,
                    expires_at
                FROM messages
                WHERE id = $1
                `,
                [id]
            );

        return result.rows[0] || null;
    },

    async deleteForEveryone(
        messageId,
        senderId
    ) {
        const result =
            await pool.query(
                `
                UPDATE messages
                SET
                    content = NULL,
                    deleted_at = NOW()
                WHERE id = $1
                  AND sender_id = $2
                  AND deleted_at IS NULL
                RETURNING
                    id,
                    conversation_id,
                    sender_id,
                    message_type,
                    content,
                    reply_to_message_id,
                    forwarded_from_message_id,
                    created_at,
                    edited_at,
                    deleted_at,
                    expires_at
                `,
                [
                    messageId,
                    senderId
                ]
            );

        return result.rows[0] || null;
    },

    async editMessage(
        messageId,
        senderId,
        content
    ) {
        const result =
            await pool.query(
                `
                UPDATE messages
                SET
                    content = $3,
                    edited_at = NOW()
                WHERE id = $1
                  AND sender_id = $2
                  AND deleted_at IS NULL
                RETURNING
                    id,
                    conversation_id,
                    sender_id,
                    message_type,
                    content,
                    reply_to_message_id,
                    forwarded_from_message_id,
                    created_at,
                    edited_at,
                    deleted_at,
                    expires_at
                `,
                [
                    messageId,
                    senderId,
                    content
                ]
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
                    forwarded_from_message_id,
                    created_at,
                    edited_at,
                    deleted_at,
                    expires_at
                FROM messages
                WHERE conversation_id = $1
                  AND (
                      expires_at IS NULL
                      OR expires_at > NOW()
                  )
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
