const pool =
    require("../database/db");

const conversationRepository = {

    async findDirectConversation(
        userId,
        otherUserId
    ) {

        const result =
            await pool.query(
                `
                SELECT
                    c.id,
                    c.type,
                    c.title,
                    c.avatar_url,
                    c.created_by,
                    c.created_at,
                    c.updated_at,
                    u.id AS other_user_id,
                    u.phone AS other_user_phone,
                    u.email AS other_user_email,
                    u.display_name AS other_user_display_name,
                    u.avatar_url AS other_user_avatar_url,
                    u.about AS other_user_about,
                    u.last_seen_at AS other_user_last_seen_at
                FROM conversations c
                INNER JOIN conversation_members cm1
                    ON cm1.conversation_id = c.id
                    AND cm1.user_id = $1
                INNER JOIN conversation_members cm2
                    ON cm2.conversation_id = c.id
                    AND cm2.user_id = $2
                INNER JOIN users u
                    ON u.id = cm2.user_id
                WHERE c.type = 'direct'
                LIMIT 1
                `,
                [
                    userId,
                    otherUserId
                ]
            );

        return result.rows[0] || null;
    },

    async isMember(
        conversationId,
        userId
    ) {

        const result =
            await pool.query(
                `
                SELECT
                    1
                FROM conversation_members
                WHERE conversation_id = $1
                    AND user_id = $2
                LIMIT 1
                `,
                [
                    conversationId,
                    userId
                ]
            );

        return result.rowCount > 0;
    },

    async createDirectConversation(
        userId,
        otherUserId
    ) {

        const client =
            await pool.connect();

        try {

            await client.query(
                "BEGIN"
            );

            const conversationResult =
                await client.query(
                    `
                    INSERT INTO conversations (
                        type,
                        created_by
                    )
                    VALUES (
                        'direct',
                        $1
                    )
                    RETURNING
                        id,
                        type,
                        title,
                        avatar_url,
                        created_by,
                        created_at,
                        updated_at
                    `,
                    [userId]
                );

            const conversation =
                conversationResult.rows[0];

            await client.query(
                `
                INSERT INTO conversation_members (
                    conversation_id,
                    user_id,
                    role
                )
                VALUES
                    ($1, $2, 'member'),
                    ($1, $3, 'member')
                `,
                [
                    conversation.id,
                    userId,
                    otherUserId
                ]
            );

            await client.query(
                "COMMIT"
            );

            const result =
                await client.query(
                    `
                    SELECT
                        c.id,
                        c.type,
                        c.title,
                        c.avatar_url,
                        c.created_by,
                        c.created_at,
                        c.updated_at,
                        u.id AS other_user_id,
                        u.phone AS other_user_phone,
                        u.email AS other_user_email,
                        u.display_name AS other_user_display_name,
                        u.avatar_url AS other_user_avatar_url,
                        u.about AS other_user_about,
                        u.last_seen_at AS other_user_last_seen_at
                    FROM conversations c
                    INNER JOIN conversation_members cm
                        ON cm.conversation_id = c.id
                        AND cm.user_id = $1
                    INNER JOIN conversation_members other_cm
                        ON other_cm.conversation_id = c.id
                        AND other_cm.user_id <> $1
                    INNER JOIN users u
                        ON u.id = other_cm.user_id
                    WHERE c.id = $2
                    LIMIT 1
                    `,
                    [
                        userId,
                        conversation.id
                    ]
                );

            return result.rows[0] || null;

        } catch (error) {

            await client.query(
                "ROLLBACK"
            );

            throw error;

        } finally {

            client.release();
        }
    },

    async getOtherMemberIds(
        conversationId,
        userId
    ) {

        const result =
            await pool.query(
                `
                SELECT
                    user_id
                FROM conversation_members
                WHERE conversation_id = $1
                    AND user_id <> $2
                `,
                [
                    conversationId,
                    userId
                ]
            );

        return result.rows.map(
            (row) => row.user_id
        );
    },

    async getConversationsForUser(
        userId
    ) {

        const result =
            await pool.query(
                `
                SELECT
                    c.id,
                    c.type,
                    c.title,
                    c.avatar_url,
                    c.created_by,
                    c.created_at,
                    c.updated_at,
                    u.id AS other_user_id,
                    COALESCE(
                        contact.country_code || contact.phone,
                        u.phone
                    ) AS other_user_phone,
                    u.email AS other_user_email,
                    COALESCE(
                        NULLIF(
                            TRIM(
                                contact.first_name || ' ' ||
                                COALESCE(contact.last_name, '')
                            ),
                            ''
                        ),
                        u.display_name
                    ) AS other_user_display_name,
                    u.avatar_url AS other_user_avatar_url,
                    u.about AS other_user_about,
                    u.last_seen_at AS other_user_last_seen_at
                FROM conversations c
                INNER JOIN conversation_members cm
                    ON cm.conversation_id = c.id
                    AND cm.user_id = $1
                INNER JOIN conversation_members other_cm
                    ON other_cm.conversation_id = c.id
                    AND other_cm.user_id <> $1
                INNER JOIN users u
                    ON u.id = other_cm.user_id
                LEFT JOIN contacts contact
                    ON contact.owner_user_id = $1
                    AND contact.linked_user_id = other_cm.user_id
                WHERE c.type = 'direct'
                ORDER BY c.updated_at DESC
                `,
                [
                    userId
                ]
            );

        return result.rows;
    },

    async getConversationIdsForUser(
        userId
    ) {

        const result =
            await pool.query(
                `
                SELECT
                    conversation_id
                FROM conversation_members
                WHERE user_id = $1
                `,
                [userId]
            );

        return result.rows.map(
            (row) => row.conversation_id
        );
    },

};

module.exports =
    conversationRepository;
