const test = require("node:test");
const assert = require("node:assert/strict");

const locationAttachmentService =
    require("../services/locationAttachmentService");

test("creates a location message with coordinates", async () => {
    const result =
        await locationAttachmentService.createLocationAttachment({
            conversationId: "1",
            senderId: "1",
            latitude: 30.900965,
            longitude: 75.857277
        });

    assert.equal(result.messageType, "location");
    assert.equal(result.latitude, 30.900965);
    assert.equal(result.longitude, 75.857277);
});

test("rejects latitude outside valid range", async () => {
    await assert.rejects(
        locationAttachmentService.createLocationAttachment({
            conversationId: "1",
            senderId: "1",
            latitude: 91,
            longitude: 75.857277
        }),
        /Latitude must be between -90 and 90/
    );
});

test("rejects longitude outside valid range", async () => {
    await assert.rejects(
        locationAttachmentService.createLocationAttachment({
            conversationId: "1",
            senderId: "1",
            latitude: 30.900965,
            longitude: 181
        }),
        /Longitude must be between -180 and 180/
    );
});
