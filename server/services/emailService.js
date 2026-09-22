const https = require("https");

const emailService = {

    async sendOtpEmail({
        to,
        otp
    }) {

        if (!process.env.BREVO_API_KEY) {
            throw new Error(
                "BREVO_API_KEY is not configured"
            );
        }

        const payload =
            JSON.stringify({
                sender: {
                    name: "Align",
                    email:
                        process.env.BREVO_SENDER_EMAIL
                },
                to: [
                    {
                        email: to
                    }
                ],
                subject:
                    "Your Align verification code",
                htmlContent:
                    `
                    <p>Your Align verification code is:</p>
                    <h2>${otp}</h2>
                    <p>This code will expire in 5 minutes.</p>
                    `
            });

        return new Promise(
            (resolve, reject) => {

                const request =
                    https.request(
                        {
                            hostname:
                                "api.brevo.com",
                            path:
                                "/v3/smtp/email",
                            method:
                                "POST",
                            headers: {
                                "accept":
                                    "application/json",
                                "api-key":
                                    process.env
                                        .BREVO_API_KEY,
                                "content-type":
                                    "application/json",
                                "content-length":
                                    Buffer.byteLength(
                                        payload
                                    )
                            }
                        },
                        (response) => {

                            let body = "";

                            response.on(
                                "data",
                                (chunk) => {
                                    body += chunk;
                                }
                            );

                            response.on(
                                "end",
                                () => {

                                    if (
                                        response.statusCode >=
                                            200 &&
                                        response.statusCode <
                                            300
                                    ) {
                                        resolve(
                                            body
                                                ? JSON.parse(
                                                      body
                                                  )
                                                : {}
                                        );
                                        return;
                                    }

                                    reject(
                                        new Error(
                                            `Brevo email failed: ${response.statusCode} ${body}`
                                        )
                                    );
                                }
                            );
                        }
                    );

                request.on(
                    "error",
                    reject
                );

                request.write(payload);

                request.end();
            }
        );
    }

};

module.exports =
    emailService;
