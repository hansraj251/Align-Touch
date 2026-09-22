const crypto =
    require("crypto");

const authOtpRepository =
    require("../repositories/authOtpRepository");

const emailService =
    require("./emailService");

const OTP_EXPIRY_MINUTES = 5;
const MAX_ATTEMPTS = 5;

function generateOtp() {
    return String(
        crypto.randomInt(
            100000,
            1000000
        )
    );
}

function hashOtp(otp) {
    return crypto
        .createHash("sha256")
        .update(otp)
        .digest("hex");
}

const authOtpService = {

    async createOtp({
        identifier,
        identifierType,
        purpose = "login"
    }) {
        if (!identifier) {
            throw new Error(
                "Identifier is required"
            );
        }

        if (
            ![
                "phone",
                "email"
            ].includes(identifierType)
        ) {
            throw new Error(
                "Invalid identifier type"
            );
        }

        if (
            ![
                "login",
                "register"
            ].includes(purpose)
        ) {
            throw new Error(
                "Invalid OTP purpose"
            );
        }

        const otp =
            generateOtp();

        const otpHash =
            hashOtp(otp);

        const expiresAt =
            new Date(
                Date.now() +
                    OTP_EXPIRY_MINUTES *
                        60 *
                        1000
            );

        const record =
            await authOtpRepository.createOtp({
                identifier,
                identifierType,
                otpHash,
                purpose,
                expiresAt
            });

        if (
            identifierType ===
            "email"
        ) {

            await emailService.sendOtpEmail({
                to: identifier,
                otp
            });

        } else {

            console.log(
                "ChatFlow OTP:",
                identifier,
                otp
            );
        }

        return {
            id: record.id,
            identifier: record.identifier,
            identifierType: record.identifier_type,
            purpose: record.purpose,
            expiresAt: record.expires_at,
            otp
        };
    },

    async verifyOtp({
        identifier,
        purpose = "login",
        otp
    }) {
        if (!identifier) {
            throw new Error(
                "Identifier is required"
            );
        }

        if (!otp) {
            throw new Error(
                "OTP is required"
            );
        }

        const record =
            await authOtpRepository.getLatestActiveOtp(
                identifier,
                purpose
            );

        if (!record) {
            throw new Error(
                "OTP expired or not found"
            );
        }

        if (
            record.attempts >=
            MAX_ATTEMPTS
        ) {
            throw new Error(
                "Too many OTP attempts"
            );
        }

        const otpHash =
            hashOtp(otp);

        if (
            otpHash !==
            record.otp_hash
        ) {
            await authOtpRepository.incrementAttempts(
                record.id
            );

            throw new Error(
                "Invalid OTP"
            );
        }

        await authOtpRepository.markUsed(
            record.id
        );

        return {
            verified: true,
            identifier:
                record.identifier,
            identifierType:
                record.identifier_type,
            purpose:
                record.purpose
        };
    }
};

module.exports =
    authOtpService;
