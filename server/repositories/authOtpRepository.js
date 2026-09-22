const pool =
    require("../database/db");

const authOtpRepository = {

    async createOtp({
        identifier,
        identifierType,
        otpHash,
        purpose,
        expiresAt
    }) {
        const result =
            await pool.query(
                `
                INSERT INTO auth_otps (
                    identifier,
                    identifier_type,
                    otp_hash,
                    purpose,
                    expires_at
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
                    identifier,
                    identifier_type,
                    otp_hash,
                    purpose,
                    expires_at,
                    attempts,
                    used_at,
                    created_at
                `,
                [
                    identifier,
                    identifierType,
                    otpHash,
                    purpose,
                    expiresAt
                ]
            );

        return result.rows[0];
    },

    async getLatestActiveOtp(
        identifier,
        purpose
    ) {
        const result =
            await pool.query(
                `
                SELECT
                    id,
                    identifier,
                    identifier_type,
                    otp_hash,
                    purpose,
                    expires_at,
                    attempts,
                    used_at,
                    created_at
                FROM auth_otps
                WHERE identifier = $1
                    AND purpose = $2
                    AND used_at IS NULL
                    AND expires_at > NOW()
                ORDER BY created_at DESC
                LIMIT 1
                `,
                [
                    identifier,
                    purpose
                ]
            );

        return result.rows[0] || null;
    },

    async incrementAttempts(
        id
    ) {
        const result =
            await pool.query(
                `
                UPDATE auth_otps
                SET attempts = attempts + 1
                WHERE id = $1
                RETURNING
                    id,
                    attempts
                `,
                [id]
            );

        return result.rows[0] || null;
    },

    async markUsed(
        id
    ) {
        const result =
            await pool.query(
                `
                UPDATE auth_otps
                SET used_at = NOW()
                WHERE id = $1
                RETURNING
                    id,
                    used_at
                `,
                [id]
            );

        return result.rows[0] || null;
    }
};

module.exports =
    authOtpRepository;
