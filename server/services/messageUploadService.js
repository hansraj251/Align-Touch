const path =
    require("path");

const conversationRepository =
    require("../repositories/conversationRepository");

const messageRepository =
    require("../repositories/messageRepository");

const messageAttachmentService =
    require("./messageAttachmentService");

const messageAttachmentRepository =
    require("../repositories/messageAttachmentRepository");

const localStorageService =
    require("../storage/localStorageService");

const MEDIA_MIME_PREFIXES = [
    "image/",
    "video/",
    "audio/"
];

function getMessageType(mimeType) {
    const normalizedMimeType =
        String(mimeType)
            .toLowerCase()
            .trim();

    if (
        normalizedMimeType.startsWith(
            "image/"
        )
    ) {
        return "image";
    }

    if (
        normalizedMimeType.startsWith(
            "video/"
        )
    ) {
        return "video";
    }

    if (
        normalizedMimeType.startsWith(
            "audio/"
        )
    ) {
        return "audio";
    }

    return "document";
}

function getExtension(originalName) {
    return path.extname(
        String(originalName)
    ).replace(
        ".",
        ""
    );
}

const messageUploadService = {

    async uploadAttachment({
        conversationId,
        senderId,
        file,
        content = null,
        replyToMessageId = null,
        expiresAt = null
    }) {
        if (!conversationId) {
            throw new Error(
                "Conversation is required"
            );
        }

        if (!file) {
            throw new Error(
                "File is required"
            );
        }

        const isMember =
            await conversationRepository.isMember(
                conversationId,
                senderId
            );

        if (!isMember) {
            throw new Error(
                "You are not a member of this conversation"
            );
        }

        messageAttachmentService.validateAttachment({
            originalName:
                file.originalname,
            mimeType:
                file.mimetype,
            fileSize:
                file.size
        });

        const messageType =
            getMessageType(
                file.mimetype
            );

        const extension =
            getExtension(
                file.originalname
            );

        let storageResult = null;

        try {
            storageResult =
                localStorageService.saveFile({
                    buffer:
                        file.buffer,
                    extension
                });

            const message =
                await messageRepository.createMessage({
                    conversationId,
                    senderId,
                    messageType,
                    content:
                        content && String(content).trim()
                            ? String(content).trim()
                            : null,
                    replyToMessageId,
                    expiresAt
                });

            const attachment =
                await messageAttachmentRepository
                    .createAttachment({
                        messageId:
                            message.id,
                        storageKey:
                            storageResult.storageKey,
                        originalName:
                            file.originalname,
                        mimeType:
                            file.mimetype,
                        fileSize:
                            file.size
                    });

            return {
                message,
                attachment
            };
        } catch (error) {
            if (storageResult) {
                try {
                    localStorageService.deleteFile(
                        storageResult.storageKey
                    );
                } catch (cleanupError) {
                    console.error(
                        "Attachment cleanup error:",
                        cleanupError
                    );
                }
            }

            throw error;
        }
    }

};

module.exports =
    messageUploadService;
