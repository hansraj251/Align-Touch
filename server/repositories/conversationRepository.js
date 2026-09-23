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

    async createGroupConversation(
        userId,
        title,
        memberUserIds
    ) {
        const client =
            await pool.connect();

        try {
            await client.query(
                "BEGIN"
            );

            const uniqueMemberIds = [
                ...new Set(
                    [
                        userId,
                        ...memberUserIds
                    ].map(
                        (id) =>
                            String(id)
                    )
                )
            ];

            const conversationResult =
                await client.query(
                    `
                    INSERT INTO conversations (
                        type,
                        title,
                        created_by
                    )
                    VALUES (
                        'group',
                        $1,
                        $2
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
                    [
                        title,
                        userId
                    ]
                );

            const conversation =
                conversationResult.rows[0];

            for (
                const memberId
                of uniqueMemberIds
            ) {
                await client.query(
                    `
                    INSERT INTO conversation_members (
                        conversation_id,
                        user_id,
                        role
                    )
                    VALUES (
                        $1,
                        $2,
                        $3
                    )
                    `,
                    [
                        conversation.id,
                        memberId,
                        String(memberId) ===
                            String(userId)
                            ? "admin"
                            : "member"
                    ]
                );
            }

            await client.query(
                "COMMIT"
            );

            return {
                ...conversation,
                other_user_id: null,
                other_user_phone: null,
                other_user_email: null,
                other_user_display_name: null,
                other_user_avatar_url: null,
                other_user_about: null,
                other_user_last_seen_at: null
            };
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

    async getGroupDetails(
        conversationId,
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
                    cm.role AS current_user_role
                FROM conversations c
                INNER JOIN conversation_members cm
                    ON cm.conversation_id = c.id
                    AND cm.user_id = $2
                WHERE c.id = $1
                    AND c.type = 'group'
                LIMIT 1
                `,
                [
                    conversationId,
                    userId
                ]
            );

        return result.rows[0] || null;
    },

    async getGroupMembers(
        conversationId
    ) {

        const result =
            await pool.query(
                `
                SELECT
                    cm.id,
                    cm.user_id,
                    cm.role,
                    cm.joined_at,
                    u.phone,
                    u.display_name,
                    u.avatar_url,
                    u.about
                FROM conversation_members cm
                INNER JOIN users u
                    ON u.id = cm.user_id
                WHERE cm.conversation_id = $1
                ORDER BY
                    CASE
                        WHEN cm.role = 'admin'
                            THEN 0
                        ELSE 1
                    END,
                    cm.id
                `,
                [
                    conversationId
                ]
            );

        return result.rows;
    },

    
async getGroupMemberRole(conversationId, userId) {
    const result = await pool.query(
        `
        SELECT
            cm.role
        FROM conversation_members cm
        INNER JOIN conversations c
            ON c.id = cm.conversation_id
        WHERE cm.conversation_id = $1
            AND cm.user_id = $2
            AND c.type = 'group'
        LIMIT 1
        `,
        [conversationId, userId]
    );

    return result.rows[0] || null;
},

async updateGroup(
    conversationId,
    title,
    avatarUrl = null
) {
    const result = await pool.query(
        `
        UPDATE conversations
        SET
            title = $2,
            avatar_url = $3,
            updated_at = NOW()
        WHERE id = $1
            AND type = 'group'
        RETURNING
            id,
            type,
            title,
            avatar_url,
            created_by,
            created_at,
            updated_at
        `,
        [
            conversationId,
            title,
            avatarUrl
        ]
    );

    return result.rows[0] || null;
},

async addGroupMember(
    conversationId,
    memberUserId
) {
    const result = await pool.query(
        `
        INSERT INTO conversation_members (
            conversation_id,
            user_id,
            role
        )
        VALUES (
            $1,
            $2,
            'member'
        )
        ON CONFLICT (
            conversation_id,
            user_id
        )
        DO NOTHING
        RETURNING
            id,
            conversation_id,
            user_id,
            role,
            joined_at
        `,
        [
            conversationId,
            memberUserId
        ]
    );

    return result.rows[0] || null;
},

async removeGroupMember(
    conversationId,
    memberUserId
) {
    const result = await pool.query(
        `
        DELETE FROM conversation_members
        WHERE conversation_id = $1
            AND user_id = $2
        RETURNING
            id,
            conversation_id,
            user_id,
            role
        `,
        [
            conversationId,
            memberUserId
        ]
    );

    return result.rows[0] || null;
},

async deleteGroup(conversationId) {
    const result = await pool.query(
        `
        DELETE FROM conversations
        WHERE id = $1
            AND type = 'group'
        RETURNING id
        `,
        [conversationId]
    );

    return result.rows[0] || null;
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

                    CASE
                        WHEN c.type = 'group'
                            THEN ''
                        ELSE
                            COALESCE(
                                direct_user.id::text,
                                ''
                            )
                    END AS other_user_id,
                    CASE
                        WHEN c.type = 'group'
                            THEN NULL
                        ELSE
                            contact.id
                    END AS contact_id,

                    CASE
                        WHEN c.type = 'group'
                            THEN NULL
                        ELSE
                            COALESCE(
                                contact.country_code ||
                                contact.phone,
                                direct_user.phone
                            )
                    END AS other_user_phone,

                    CASE
                        WHEN c.type = 'group'
                            THEN NULL
                        ELSE
                            direct_user.email
                    END AS other_user_email,

                    CASE
                        WHEN c.type = 'group'
                            THEN c.title
                        ELSE
                            COALESCE(
                                NULLIF(
                                    TRIM(
                                        contact.first_name ||
                                        ' ' ||
                                        COALESCE(
                                            contact.last_name,
                                            ''
                                        )
                                    ),
                                    ''
                                ),
                                direct_user.display_name
                            )
                    END AS other_user_display_name,

                    CASE
                        WHEN c.type = 'group'
                            THEN NULL
                        ELSE
                            direct_user.avatar_url
                    END AS other_user_avatar_url,

                    CASE
                        WHEN c.type = 'group'
                            THEN NULL
                        ELSE
                            direct_user.about
                    END AS other_user_about,

                    CASE
                        WHEN c.type = 'group'
                            THEN NULL
                        ELSE
                            direct_user.last_seen_at
                    END AS other_user_last_seen_at

                FROM conversations c

                INNER JOIN conversation_members cm
                    ON cm.conversation_id = c.id
                    AND cm.user_id = $1

                LEFT JOIN LATERAL (
                    SELECT
                        u.id,
                        u.phone,
                        u.email,
                        u.display_name,
                        u.avatar_url,
                        u.about,
                        u.last_seen_at
                    FROM conversation_members other_cm
                    INNER JOIN users u
                        ON u.id = other_cm.user_id
                    WHERE
                        other_cm.conversation_id = c.id
                        AND other_cm.user_id <> $1
                    ORDER BY
                        other_cm.id
                    LIMIT 1
                ) direct_user
                    ON c.type = 'direct'

                LEFT JOIN contacts contact
                    ON c.type = 'direct'
                    AND contact.owner_user_id = $1
                    AND contact.linked_user_id =
                        direct_user.id

                WHERE c.type IN (
                    'direct',
                    'group'
                )

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
