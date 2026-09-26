const test = require("node:test");

const assert = require("node:assert/strict");

const jwt = require("jsonwebtoken");

const {

    io,

    httpServer

} = require("../server");

const {

    io: clientIo

} = require("socket.io-client");

test(

    "Socket.IO send_message creates and broadcasts a location message",

    async () => {

        const token =

            jwt.sign(

                {

                    userId: 1

                },

                process.env.JWT_SECRET,

                {

                    expiresIn: "1h"

                }

            );

        const socket =

            clientIo(

                `http://localhost:${process.env.PORT || 3000}`,

                {

                    auth: {

                        token

                    }

                }

            );

        try {

            await new Promise(

                (

                    resolve,

                    reject

                ) => {

                    const timer =

                        setTimeout(

                            () => {

                                reject(

                                    new Error(

                                        "Socket connection timeout"

                                    )

                                );

                            },

                            3000

                        );

                    socket.on(

                        "connect",

                        () => {

                            clearTimeout(

                                timer

                            );

                            resolve();

                        }

                    );

                    socket.on(

                        "connect_error",

                        (error) => {

                            clearTimeout(

                                timer

                            );

                            reject(

                                error

                            );

                        }

                    );

                }

            );

            socket.emit(
                "join_conversation",
                1
            );

            await new Promise(
                (resolve) => {
                    setTimeout(
                        resolve,
                        300
                    );
                }
            );

            socket.emit(
                "send_message",
                {
                    conversationId: 1,
                    latitude: "30.900965",
                    longitude: "75.857277"
                }
            );

            const message =

                await new Promise(

                    (

                        resolve,

                        reject

                    ) => {

                        const timer =

                            setTimeout(

                                () => {

                                    reject(

                                        new Error(

                                            "new_message timeout"

                                        )

                                    );

                                },

                                3000

                            );

                        socket.on(

                            "new_message",

                            (data) => {

                                clearTimeout(

                                    timer

                                );

                                resolve(

                                    data

                                );

                            }

                        );

                    }

                );

            assert.equal(
                message.conversation_id,
                "1"
            );

            assert.equal(
                message.sender_id,
                "1"
            );

            assert.equal(

                message.message_type,

                "location"

            );

            assert.equal(

                Number(

                    message.latitude

                ),

                30.900965

            );

            assert.equal(

                Number(

                    message.longitude

                ),

                75.857277

            );

        } finally {

            socket.close();

        }

    }

);

