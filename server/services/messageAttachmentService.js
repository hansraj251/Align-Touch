const messageAttachmentRepository =
    require("../repositories/messageAttachmentRepository");

const conversationRepository =
    require("../repositories/conversationRepository");


const MAX_FILE_SIZE =
    50 * 1024 * 1024;

const ALLOWED_MIME_PREFIXES = [
    "image/",
    "video/",
    "audio/"
];

const ALLOWED_DOCUMENT_MIME_TYPES = [
    "application/pdf",
    "application/msword",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    "application/vnd.ms-excel",
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    "application/vnd.ms-powerpoint",
    "application/vnd.openxmlformats-officedocument.presentationml.presentation",
    "text/plain",
    "application/zip"
];

const messageAttachmentService = {

    validateAttachment({
        originalName,
        mimeType,
        fileSize
    }) {
        if (
            !originalName ||
            !String(originalName).trim()
        ) {
            throw new Error(
                "Original file name is required"
            );
        }

        if (
            !mimeType ||
            !String(mimeType).trim()
        ) {
            throw new Error(
                "MIME type is required"
            );
        }

        if (
            !Number.isInteger(fileSize) ||
            fileSize <= 0
        ) {
            throw new Error(
                "Invalid file size"
            );
        }

        if (fileSize > MAX_FILE_SIZE) {
            throw new Error(
                "File size cannot exceed 50 MB"
            );
        }

        const normalizedMimeType =
            String(mimeType)
                .toLowerCase()
                .trim();

        const isMedia =
            ALLOWED_MIME_PREFIXES.some(
                (prefix) =>
                    normalizedMimeType.startsWith(
                        prefix
                    )
            );

        const isDocument =
            ALLOWED_DOCUMENT_MIME_TYPES.includes(
                normalizedMimeType
            );

        if (!isMedia && !isDocument) {
            throw new Error(
                "File type is not supported"
            );
        }

        return true;
    },

    async createAttachment({
        messageId,
        storageKey,
        originalName,
        mimeType,
        fileSize
    }) {
        this.validateAttachment({
            originalName,
            mimeType,
            fileSize
        });

        if (!messageId) {
            throw new Error(
                "Message is required"
            );
        }

        if (
            !storageKey ||
            !String(storageKey).trim()
        ) {
            throw new Error(
                "Storage key is required"
            );
        }

        return messageAttachmentRepository
            .createAttachment({
                messageId,
                storageKey,
                originalName:
                    String(originalName).trim(),
                mimeType:
                    String(mimeType)
                        .toLowerCase()
                        .trim(),
                fileSize
            });
    },

    async getAttachmentsByMessageId(
        messageId
    ) {
        if (!messageId) {
            throw new Error(
                "Message is required"
            );
        }

        return messageAttachmentRepository
            .getByMessageId(messageId);
    },

    async getAttachmentById(id) {
        if (!id) {
            throw new Error(
                "Attachment is required"
            );
        }

        return messageAttachmentRepository
            .getById(id);
    },

    async getAttachmentForUser(
        id,
        userId
    ) {

        if (!id) {
            throw new Error(
                "Attachment is required"
            );
        }

        if (!userId) {
            throw new Error(
                "User is required"
            );
        }

        const attachment =
            await messageAttachmentRepository
                .getById(id);

        if (!attachment) {
            throw new Error(
                "Attachment not found"
            );
        }

        const isMember =
            await conversationRepository
                .isMember(
                    attachment.conversation_id,
                    userId
                );

        if (!isMember) {
            throw new Error(
                "You are not a member of this conversation"
            );
        }

        return attachment;
    },

};

module.exports =
    messageAttachmentService;
