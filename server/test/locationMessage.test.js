const test = require("node:test");

const assert = require("node:assert/strict");

const conversationRepository =
    require("../repositories/conversationRepository");

const messageRepository =
    require("../repositories/messageRepository");

const messageService =
    require("../services/messageService");

test("creates a location message with coordinates", async () => {

    const originalIsMember =
        conversationRepository.isMember;

    const originalCreateMessage =
        messageRepository.createMessage;

    conversationRepository.isMember =
        async () => true;

    messageRepository.createMessage =
        async (data) => ({
            id: "1",
            conversation_id:
                data.conversationId,
            sender_id:
                data.senderId,
            message_type:
                data.messageType,
            content:
                data.content,
            latitude:
                data.latitude,
            longitude:
                data.longitude
        });

    try {

        const result =
            await messageService.createLocationMessage({
                conversationId: "1",
                senderId: "1",
                latitude: 30.900965,
                longitude: 75.857277
            });

        assert.equal(
            result.message_type,
            "location"
        );

        assert.equal(
            result.latitude,
            30.900965
        );

        assert.equal(
            result.longitude,
            75.857277
        );

    } finally {

        conversationRepository.isMember =
            originalIsMember;

        messageRepository.createMessage =
            originalCreateMessage;

    }

});
