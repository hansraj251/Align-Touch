const locationAttachmentService = {

    async createLocationAttachment({
        conversationId,
        senderId,
        latitude,
        longitude
    }) {

        if (!conversationId) {
            throw new Error(
                "Conversation is required"
            );
        }

        if (!senderId) {
            throw new Error(
                "Sender is required"
            );
        }

        if (
            !Number.isFinite(latitude) ||
            latitude < -90 ||
            latitude > 90
        ) {
            throw new Error(
                "Latitude must be between -90 and 90"
            );
        }

        if (
            !Number.isFinite(longitude) ||
            longitude < -180 ||
            longitude > 180
        ) {
            throw new Error(
                "Longitude must be between -180 and 180"
            );
        }

        return {
            messageType: "location",
            latitude,
            longitude
        };
    }

};

module.exports =
    locationAttachmentService;
