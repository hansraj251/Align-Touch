const test = require("node:test");
const assert = require("node:assert/strict");

const messageController =
    require("../controllers/messageController");

const messageService =
    require("../services/messageService");

test("sendMessage creates a location message when coordinates are supplied", async () => {
    const original =
        messageService.createLocationMessage;

    let received = null;

    messageService.createLocationMessage =
        async (payload) => {
            received = payload;

            return {
                id: 123,
                message_type: "location",
                latitude: payload.latitude,
                longitude: payload.longitude
            };
        };

    try {
        const req = {
            body: {
                conversationId: 10,
                latitude: "30.900965",
                longitude: "75.857277"
            },
            user: {
                userId: 20
            }
        };

        let responseBody = null;
        let responseStatus = null;

        const res = {
            status(code) {
                responseStatus = code;
                return this;
            },

            json(body) {
                responseBody = body;
                return this;
            }
        };

        await messageController.sendMessage(
            req,
            res
        );

        assert.equal(
            responseStatus,
            201
        );

        assert.deepEqual(
            received,
            {
                conversationId: 10,
                senderId: 20,
                latitude: 30.900965,
                longitude: 75.857277
            }
        );

        assert.equal(
            responseBody.success,
            true
        );

        assert.equal(
            responseBody.message.message_type,
            "location"
        );
    } finally {
        messageService.createLocationMessage =
            original;
    }
});
