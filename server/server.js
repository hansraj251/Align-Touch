require("dotenv").config({
    path: __dirname + "/.env"
});

const http =
    require("http");

const express =
    require("express");

const cors =
    require("cors");

const {
    Server
} = require("socket.io");

const authRoutes =
    require("./routes/authRoutes");

const userRoutes =
    require("./routes/userRoutes");

const conversationRoutes =
    require("./routes/conversationRoutes");

const messageRoutes =
    require("./routes/messageRoutes");

const messageReactionRoutes =
    require("./routes/messageReactionRoutes");

const contactRoutes =
    require("./routes/contactRoutes");

const conversationRepository =
    require("./repositories/conversationRepository");

const messageService =
    require("./services/messageService");

const messageReactionService =
    require("./services/messageReactionService");

const messageRepository =
    require("./repositories/messageRepository");

const messageReceiptService =
    require("./services/messageReceiptService");

const userRepository =
    require("./repositories/userRepository");

const userPresence =
    require("./sockets/userPresence");

const socketAuthMiddleware =
    require("./sockets/socketAuthMiddleware");

const app =
    express();

const httpServer =
    http.createServer(
        app
    );

const io =
    new Server(
        httpServer,
        {
            cors: {
                origin: "*"
            }
        }
    );

io.use(
    socketAuthMiddleware
);

const PORT =
    process.env.PORT || 3000;

app.use(
    cors()
);

app.use(
    express.json()
);

app.use(
    "/api/auth",
    authRoutes
);

app.use(
    "/api/users",
    userRoutes
);

app.use(
    "/api/conversations",
    conversationRoutes
);

app.use(
    "/api/messages",
    messageRoutes
);

app.use(
    "/api/message-reactions",
    messageReactionRoutes
);

app.use(
    "/api/contacts",
    contactRoutes
);

app.get(
    "/api/health",
    (req, res) => {

        res.json({
            success: true,
            message: "ChatFlow server is running"
        });
    }
);

io.on(
    "connection",
    async (socket) => {

        const userId =
            socket.user.userId;

        socket.on(
            "join_conversation",
            async (conversationId) => {
                try {
                    if (!conversationId) {
                        return;
                    }

                    const isMember =
                        await conversationRepository.isMember(
                            conversationId,
                            userId
                        );

                    if (!isMember) {
                        socket.emit(
                            "socket_error",
                            {
                                message:
                                    "You are not a member of this conversation"
                            }
                        );
                        return;
                    }

                    const conversationRoom =
                        `conversation_${conversationId}`;

                    socket.join(
                        conversationRoom
                    );

                    console.log(
                        "User joined conversation:",
                        userId,
                        conversationId
                    );
                } catch (error) {
                    console.error(
                        "Join conversation error:",
                        error
                    );

                    socket.emit(
                        "socket_error",
                        {
                            message:
                                "Failed to join conversation"
                        }
                    );
                }
            }
        );

        const presenceResult =
            userPresence.addSocket(
                userId,
                socket.id
            );

        console.log(
            "User presence:",
            userId,
            "online:",
            true,
            "socket count:",
            presenceResult.socketCount
        );

        if (
            presenceResult.isFirstSocket
        ) {

            try {

                const conversationIds =
                    await conversationRepository.getConversationIdsForUser(
                        userId
                    );

                for (
                    const conversationId
                    of conversationIds
                ) {

                    const otherUserIds =
                        await conversationRepository.getOtherMemberIds(
                            conversationId,
                            userId
                        );

                    for (
                        const otherUserId
                        of otherUserIds
                    ) {

                        io.to(
                            `user_${otherUserId}`
                        ).emit(
                            "user_online",
                            {
                                userId:
                                    String(userId)
                            }
                        );
                    }
                }

            } catch (error) {

                console.error(
                    "User online event error:",
                    error
                );
            }
        }

        const userRoom =
            `user_${userId}`;

        socket.join(
            userRoom
        );

        console.log(
            "Socket connected:",
            socket.id,
            "user:",
            userId
        );

        socket.on(
            "send_message",
            async (data) => {

                console.log(
                    "Socket send_message received:",
                    data,
                    "user:",
                    userId
                );

                try {

                    const {
                        conversationId,
                        content,
                        replyToMessageId,
                        expiresAt
                    } = data || {};

                    const message =
                        await messageService.sendTextMessage({
                            conversationId,
                            senderId: userId,
                            content,
                            replyToMessageId,
                            expiresAt
                        });

                    const conversationRoom =
                        `conversation_${conversationId}`;

                    console.log(
                        "Broadcasting new_message:",
                        message,
                        "room:",
                        conversationRoom
                    );

                    io.to(
                        conversationRoom
                    ).emit(
                        "new_message",
                        message
                    );

                    console.log(
                        "new_message broadcast completed:",
                        message.id
                    );

                } catch (error) {

                    console.error(
                        "Socket send message error:",
                        error
                    );

                    socket.emit(
                        "socket_error",
                        {
                            message:
                                error.message
                        }
                    );
                }
            }
        );

        socket.on(
            "delete_message",
            async (data) => {
                console.log(
                    "Socket delete_message received:",
                    data,
                    "user:",
                    userId
                );

                try {
                    const {
                        messageId
                    } = data || {};

                    const message =
                        await messageService.deleteMessageForEveryone(
                            messageId,
                            userId
                        );

                    const conversationRoom =
                        `conversation_${message.conversation_id}`;

                    console.log(
                        "Broadcasting message_deleted:",
                        message,
                        "room:",
                        conversationRoom
                    );

                    io.to(
                        conversationRoom
                    ).emit(
                        "message_deleted",
                        message
                    );
                } catch (error) {
                    console.error(
                        "Socket delete message error:",
                        error
                    );

                    socket.emit(
                        "socket_error",
                        {
                            message:
                                error.message
                        }
                    );
                }
            }
        );

        socket.on(
            "react_to_message",
            async (data) => {
                console.log(
                    "Socket react_to_message received:",
                    data,
                    "user:",
                    userId
                );

                try {
                    const {
                        messageId,
                        reaction
                    } = data || {};

                    const result =
                        await messageReactionService.addReaction({
                            messageId,
                            userId,
                            reaction
                        });

                    const message =
                        await messageRepository.getById(
                            messageId
                        );

                    console.log(
                        "Reaction message lookup:",
                        message
                    );

                    const conversationRoom =
                        `conversation_${message.conversation_id}`;

                    console.log(
                        "Broadcasting message_reaction_updated:",
                        result,
                        "room:",
                        conversationRoom
                    );

                    io.to(
                        conversationRoom
                    ).emit(
                        "message_reaction_updated",
                        result
                    );
                } catch (error) {
                    console.error(
                        "Socket react to message error:",
                        error
                    );

                    socket.emit(
                        "socket_error",
                        {
                            message:
                                error.message
                        }
                    );
                }
            }
        );

        socket.on(
            "remove_message_reaction",
            async (messageId) => {
                try {
                    const result =
                        await messageReactionService.removeReaction({
                            messageId,
                            userId
                        });

                    const message =
                        await messageRepository.getById(
                            messageId
                        );

                    const conversationRoom =
                        `conversation_${message.conversation_id}`;

                    io.to(
                        conversationRoom
                    ).emit(
                        "message_reaction_removed",
                        {
                            messageId:
                                String(messageId),
                            userId:
                                String(userId),
                            reaction:
                                result?.reaction || null
                        }
                    );
                } catch (error) {
                    console.error(
                        "Socket remove message reaction error:",
                        error
                    );

                    socket.emit(
                        "socket_error",
                        {
                            message:
                                error.message
                        }
                    );
                }
            }
        );

        socket.on(
            "mark_delivered",
            async (messageId) => {

                try {

                    const receipt =
                        await messageReceiptService.markDelivered(
                            messageId,
                            userId
                        );

                    const message =
                        await messageRepository.getById(
                            messageId
                        );

                    const senderRoom =
                        `user_${message.sender_id}`;

                    io.to(
                        senderRoom
                    ).emit(
                        "message_delivered",
                        receipt
                    );

                } catch (error) {

                    console.error(
                        "Socket mark delivered error:",
                        error
                    );

                    socket.emit(
                        "socket_error",
                        {
                            message:
                                error.message
                        }
                    );
                }
            }
        );

        socket.on(
            "mark_read",
            async (messageId) => {

                try {

                    const receipt =
                        await messageReceiptService.markRead(
                            messageId,
                            userId
                        );

                    const message =
                        await messageRepository.getById(
                            messageId
                        );

                    const senderRoom =
                        `user_${message.sender_id}`;

                    io.to(
                        senderRoom
                    ).emit(
                        "message_read",
                        receipt
                    );

                } catch (error) {

                    console.error(
                        "Socket mark read error:",
                        error
                    );

                    socket.emit(
                        "socket_error",
                        {
                            message:
                                error.message
                        }
                    );
                }
            }
        );

        socket.on(
            "typing_start",
            async (conversationId) => {

                try {

                    if (!conversationId) {
                        return;
                    }

                    const isMember =
                        await conversationRepository.isMember(
                            conversationId,
                            userId
                        );

                    if (!isMember) {
                        return;
                    }

                    const conversationRoom =
                        `conversation_${conversationId}`;

                    socket.to(
                        conversationRoom
                    ).emit(
                        "user_typing",
                        {
                            conversationId:
                                String(conversationId),
                            userId:
                                String(userId)
                        }
                    );

                } catch (error) {

                    console.error(
                        "Socket typing start error:",
                        error
                    );
                }
            }
        );

        socket.on(
            "typing_stop",
            async (conversationId) => {

                try {

                    if (!conversationId) {
                        return;
                    }

                    const isMember =
                        await conversationRepository.isMember(
                            conversationId,
                            userId
                        );

                    if (!isMember) {
                        return;
                    }

                    const conversationRoom =
                        `conversation_${conversationId}`;

                    socket.to(
                        conversationRoom
                    ).emit(
                        "user_stopped_typing",
                        {
                            conversationId:
                                String(conversationId),
                            userId:
                                String(userId)
                        }
                    );

                } catch (error) {

                    console.error(
                        "Socket typing stop error:",
                        error
                    );
                }
            }
        );

        socket.on(
            "disconnect",
            async () => {

                const presenceResult =
                    userPresence.removeSocket(
                        userId,
                        socket.id
                    );

                try {

                    if (
                        presenceResult.isLastSocket
                    ) {

                        await userRepository.updateLastSeen(
                            userId
                        );

                        const conversationIds =
                            await conversationRepository.getConversationIdsForUser(
                                userId
                            );

                        for (
                            const conversationId
                            of conversationIds
                        ) {

                            const otherUserIds =
                                await conversationRepository.getOtherMemberIds(
                                    conversationId,
                                    userId
                                );

                            for (
                                const otherUserId
                                of otherUserIds
                            ) {

                                io.to(
                                    `user_${otherUserId}`
                                ).emit(
                                    "user_offline",
                                    {
                                        userId:
                                            String(userId)
                                    }
                                );
                            }
                        }
                    }

                    console.log(
                        "Socket disconnected:",
                        socket.id,
                        "user:",
                        userId,
                        "last socket:",
                        presenceResult.isLastSocket,
                        "remaining sockets:",
                        presenceResult.socketCount
                    );

                } catch (error) {

                    console.error(
                        "Presence disconnect error:",
                        error
                    );
                }
            }
        );
    }
);

httpServer.listen(
    PORT,
    () => {

        console.log(
            `ChatFlow server running on port ${PORT}`
        );
    }
);
