const test = require("node:test");
const assert = require("node:assert/strict");

const messageRepository =
    require("../repositories/messageRepository");

const db =
    require("../database/db");

test("repository inserts latitude and longitude for a location message", async () => {

    const originalQuery =
        db.query;

    let capturedSql = "";
    let capturedValues = null;

    db.query = async (
        sql,
        values
    ) => {

        capturedSql = sql;
        capturedValues = values;

        return {
            rows: [
                {
                    id: "1",
                    conversation_id: "1",
                    sender_id: "1",
                    message_type: "location",
                    content: null,
                    latitude: 30.900965,
                    longitude: 75.857277
                }
            ]
        };
    };

    try {

        await messageRepository.createMessage({
            conversationId: "1",
            senderId: "1",
            messageType: "location",
            content: null,
            latitude: 30.900965,
            longitude: 75.857277,
            replyToMessageId: null,
            expiresAt: null
        });

        assert.match(
            capturedSql,
            /latitude/i
        );

        assert.match(
            capturedSql,
            /longitude/i
        );

        assert.ok(
            capturedValues.includes(
                30.900965
            )
        );

        assert.ok(
            capturedValues.includes(
                75.857277
            )
        );

    } finally {

        db.query =
            originalQuery;
    }
});
