const pool =
    require("../database/db");

const messageAttachmentRepository = {

    async createAttachment({
        messageId,
        storageKey,
        originalName,
        mimeType,
        fileSize
    }) {
        const result =
            await pool.query(
                `
                INSERT INTO message_attachments (
                    message_id,
                    storage_key,
                    original_name,
                    mime_type,
                    file_size
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
                    message_id,
                    storage_key,
                    original_name,
                    mime_type,
                    file_size,
                    created_at
                `,
                [
                    messageId,
                    storageKey,
                    originalName,
                    mimeType,
                    fileSize
                ]
            );

        return result.rows[0];
    },

    async getByMessageId(messageId) {
        const result =
            await pool.query(
                `
                SELECT
                    id,
                    message_id,
                    storage_key,
                    original_name,
                    mime_type,
                    file_size,
                    created_at
                FROM message_attachments
                WHERE message_id = $1
                ORDER BY id ASC
                `,
                [
                    messageId
                ]
            );

        return result.rows;
    },

    async getById(id) {
        const result =
            await pool.query(
                `
                SELECT
                    ma.id,
                    ma.message_id,
                    m.conversation_id,
                    ma.storage_key,
                    ma.original_name,
                    ma.mime_type,
                    ma.file_size,
                    ma.created_at
                FROM message_attachments ma
                INNER JOIN messages m
                    ON m.id = ma.message_id
                WHERE ma.id = $1
                `,
                [
                    id
                ]
            );

        return result.rows[0] || null;
    },

    async deleteById(id) {
        const result =
            await pool.query(
                `
                DELETE FROM message_attachments
                WHERE id = $1
                RETURNING id
                `,
                [
                    id
                ]
            );

        return result.rows[0] || null;
    }

};

module.exports =
    messageAttachmentRepository;
