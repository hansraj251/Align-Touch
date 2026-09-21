const jwt =
    require("jsonwebtoken");

const socketAuthMiddleware =
    (socket, next) => {

        try {

            const token =
                socket.handshake.auth &&
                socket.handshake.auth.token;

            if (!token) {
                return next(
                    new Error(
                        "Authentication required"
                    )
                );
            }

            const decoded =
                jwt.verify(
                    token,
                    process.env.JWT_SECRET
                );

            socket.user =
                decoded;

            return next();

        } catch (error) {

            return next(
                new Error(
                    "Invalid or expired token"
                )
            );
        }
    };

module.exports =
    socketAuthMiddleware;
