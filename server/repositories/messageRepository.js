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
                    m.id,
                    m.conversation_id,
                    m.sender_id,
                    m.message_type,
                    m.content,
                    m.reply_to_message_id,
                    m.forwarded_from_message_id,
                    m.created_at,
                    m.edited_at,
                    m.deleted_at,
                    m.expires_at
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
        userId,
        limit = 50
    ) {

        const result =
            await pool.query(
                `
                SELECT
                    m.id,
                    m.conversation_id,
                    m.sender_id,
                    m.message_type,
                    m.content,
                    m.reply_to_message_id,
                    m.forwarded_from_message_id,
                    m.created_at,
                    m.edited_at,
                    m.deleted_at,
                    m.expires_at

                FROM messages m
                INNER JOIN conversation_members cm
                    ON cm.conversation_id = m.conversation_id
                    AND cm.user_id = $2
                WHERE m.conversation_id = $1
                  AND (
                      cm.cleared_at IS NULL
                      OR m.created_at > cm.cleared_at
                  )
                  AND (
                      m.expires_at IS NULL
                      OR m.expires_at > NOW()
                  )
                ORDER BY m.id ASC
                LIMIT $3
                `,
                [
                    conversationId,
                    userId,
                    limit
                ]
            );

        return result.rows;
    }
};

module.exports =
    messageRepository;
