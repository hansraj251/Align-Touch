const test = require("node:test");
const assert = require("node:assert/strict");

const messageService =
    require("../services/messageService");

test(
    "socket send_message creates a location message when coordinates are supplied",
    async () => {
        let locationReceived = null;
        let textCalled = false;

        const originalCreateLocationMessage =
            messageService.createLocationMessage;

        const originalSendTextMessage =
            messageService.sendTextMessage;

        messageService.createLocationMessage =
            async (data) => {
                locationReceived = data;

                return {
                    id: 100,
                    conversation_id: 10,
                    sender_id: 20,
                    message_type: "location",
                    latitude: data.latitude,
                    longitude: data.longitude
                };
            };

        messageService.sendTextMessage =
            async () => {
                textCalled = true;

                throw new Error(
                    "Text message path should not be used"
                );
            };

        try {
            const data = {
                conversationId: 10,
                latitude: "30.900965",
                longitude: "75.857277"
            };

            const {
                conversationId,
                latitude,
                longitude
            } = data;

            /*
             * This represents the behavior that
             * server.js socket handler must implement.
             *
             * The current production socket handler
             * still always calls sendTextMessage().
             */
            const message =
                await messageService.sendTextMessage({
                    conversationId,
                    senderId: 20,
                    content: undefined,
                    latitude,
                    longitude
                });

            assert.deepEqual(
                locationReceived,
                {
                    conversationId: 10,
                    senderId: 20,
                    latitude: 30.900965,
                    longitude: 75.857277
                }
            );

            assert.equal(
                message.message_type,
                "location"
            );

            assert.equal(
                textCalled,
                false
            );
        } finally {
            messageService.createLocationMessage =
                originalCreateLocationMessage;

            messageService.sendTextMessage =
                originalSendTextMessage;
        }
    }
);
