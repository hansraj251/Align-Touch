const pool =
    require("../database/db");

const messageReceiptRepository = {

    async createReceipt(
        messageId,
        userId
    ) {
        const result =
            await pool.query(
                `
                INSERT INTO message_receipts (
                    message_id,
                    user_id
                )
                VALUES ($1, $2)
                ON CONFLICT (
                    message_id,
                    user_id
                )
                DO NOTHING
                RETURNING
                    id,
                    message_id,
                    user_id,
                    delivered_at,
                    read_at,
                    created_at
                `,
                [
                    messageId,
                    userId
                ]
            );

        return result.rows[0] || null;
    },

    async markDelivered(
        messageId,
        userId
    ) {
        const result =
            await pool.query(
                `
                INSERT INTO message_receipts (
                    message_id,
                    user_id,
                    delivered_at
                )
                VALUES (
                    $1,
                    $2,
                    NOW()
                )
                ON CONFLICT (
                    message_id,
                    user_id
                )
                DO UPDATE SET
                    delivered_at =
                        COALESCE(
                            message_receipts.delivered_at,
                            NOW()
                        )
                RETURNING
                    id,
                    message_id,
                    user_id,
                    delivered_at,
                    read_at,
                    created_at
                `,
                [
                    messageId,
                    userId
                ]
            );

        return result.rows[0] || null;
    },

    async markRead(
        messageId,
        userId
    ) {
        const result =
            await pool.query(
                `
                INSERT INTO message_receipts (
                    message_id,
                    user_id,
                    delivered_at,
                    read_at
                )
                VALUES (
                    $1,
                    $2,
                    NOW(),
                    NOW()
                )
                ON CONFLICT (
                    message_id,
                    user_id
                )
                DO UPDATE SET
                    delivered_at =
                        COALESCE(
                            message_receipts.delivered_at,
                            NOW()
                        ),
                    read_at =
                        COALESCE(
                            message_receipts.read_at,
                            NOW()
                        )
                RETURNING
                    id,
                    message_id,
                    user_id,
                    delivered_at,
                    read_at,
                    created_at
                `,
                [
                    messageId,
                    userId
                ]
            );

        return result.rows[0] || null;
    },

    async getReceipt(
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
                    delivered_at,
                    read_at,
                    created_at
                FROM message_receipts
                WHERE message_id = $1
                    AND user_id = $2
                `,
                [
                    messageId,
                    userId
                ]
            );

        return result.rows[0] || null;
    }
};

module.exports =
    messageReceiptRepository;
