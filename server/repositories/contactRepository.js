const pool =
    require("../database/db");

const contactRepository = {

    async createContact({
        ownerUserId,
        linkedUserId,
        firstName,
        lastName,
        username,
        countryCode,
        phone
    }) {

        const result =
            await pool.query(
                `
                INSERT INTO contacts (
                    owner_user_id,
                    linked_user_id,
                    first_name,
                    last_name,
                    username,
                    country_code,
                    phone
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
                    owner_user_id,
                    linked_user_id,
                    first_name,
                    last_name,
                    username,
                    country_code,
                    phone,
                    created_at,
                    updated_at
                `,
                [
                    ownerUserId,
                    linkedUserId,
                    firstName,
                    lastName,
                    username,
                    countryCode,
                    phone
                ]
            );

        return result.rows[0];
    },

    async getById(
        ownerUserId,
        contactId
    ) {

        const result =
            await pool.query(
                `
                SELECT
                    id,
                    owner_user_id,
                    linked_user_id,
                    first_name,
                    last_name,
                    username,
                    country_code,
                    phone,
                    created_at,
                    updated_at
                FROM contacts
                WHERE id = $1
                  AND owner_user_id = $2
                `,
                [
                    contactId,
                    ownerUserId
                ]
            );

        return result.rows[0] || null;
    },

    async listByOwner(
        ownerUserId
    ) {

        const result =
            await pool.query(
                `
                SELECT
                    id,
                    owner_user_id,
                    linked_user_id,
                    first_name,
                    last_name,
                    username,
                    country_code,
                    phone,
                    created_at,
                    updated_at
                FROM contacts
                WHERE owner_user_id = $1
                ORDER BY
                    first_name ASC,
                    last_name ASC,
                    id ASC
                `,
                [
                    ownerUserId
                ]
            );

        return result.rows;
    },

    async getByOwnerAndPhone(
        ownerUserId,
        phone
    ) {

        const result =
            await pool.query(
                `
                SELECT
                    id,
                    owner_user_id,
                    linked_user_id,
                    first_name,
                    last_name,
                    username,
                    country_code,
                    phone,
                    created_at,
                    updated_at
                FROM contacts
                WHERE owner_user_id = $1
                  AND phone = $2
                `,
                [
                    ownerUserId,
                    phone
                ]
            );

        return result.rows[0] || null;
    }

};

module.exports =
    contactRepository;
