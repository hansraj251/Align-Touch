const userSockets =
    new Map();

const userPresence = {

    addSocket(
        userId,
        socketId
    ) {

        const key =
            String(userId);

        let sockets =
            userSockets.get(key);

        if (!sockets) {

            sockets =
                new Set();

            userSockets.set(
                key,
                sockets
            );
        }

        const wasOffline =
            sockets.size === 0;

        sockets.add(
            socketId
        );

        return {
            isFirstSocket:
                wasOffline,
            socketCount:
                sockets.size
        };
    },

    removeSocket(
        userId,
        socketId
    ) {

        const key =
            String(userId);

        const sockets =
            userSockets.get(key);

        if (!sockets) {

            return {
                isLastSocket:
                    false,
                socketCount:
                    0
            };
        }

        sockets.delete(
            socketId
        );

        const isLastSocket =
            sockets.size === 0;

        if (isLastSocket) {

            userSockets.delete(
                key
            );
        }

        return {
            isLastSocket,
            socketCount:
                sockets.size
        };
    },

    isOnline(userId) {

        const sockets =
            userSockets.get(
                String(userId)
            );

        return Boolean(
            sockets &&
            sockets.size > 0
        );
    }
};

module.exports =
    userPresence;
