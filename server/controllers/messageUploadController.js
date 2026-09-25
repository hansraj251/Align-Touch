const fs =
    require("fs");

const messageUploadService =
    require("../services/messageUploadService");

const messageAttachmentService =
    require("../services/messageAttachmentService");

const localStorageService =
    require("../storage/localStorageService");

const messageUploadController = {

    async uploadAttachment(req, res, io) {
        try {
            const {
                conversationId
            } = req.body;

            const result =
                await messageUploadService
                    .uploadAttachment({
                        conversationId,
                        senderId:
                            req.user.userId,
                        file:
                            req.file
                    });

            const conversationRoom =
                `conversation_${conversationId}`;

            io.to(conversationRoom).emit(
                "new_message",
                result.message
            );

            return res.status(201).json({
                success: true,
                message:
                    result.message,
                attachment:
                    result.attachment
            });
        } catch (error) {
            console.error(
                "Upload attachment error:",
                error
            );

            return res.status(400).json({
                success: false,
                message:
                    error.message
            });
        }
    },

    async downloadAttachment(req, res) {

        try {

            const {
                attachmentId
            } = req.params;

            const attachment =
                await messageAttachmentService
                    .getAttachmentForUser(
                        attachmentId,
                        req.user.userId
                    );

            const filePath =
                localStorageService
                    .getFilePath(
                        attachment.storage_key
                    );

            if (!fs.existsSync(filePath)) {

                return res.status(404).json({
                    success: false,
                    message:
                        "Attachment file not found"
                });

            }

            res.setHeader(
                "Content-Type",
                attachment.mime_type
            );

            res.setHeader(
                "Content-Length",
                attachment.file_size
            );

            res.setHeader(
                "Content-Disposition",
                `inline; filename="${String(
                    attachment.original_name
                ).replace(/"/g, "")}"`
            );
            const fileStream =
                fs.createReadStream(
                    filePath
                );

            fileStream.on(
                "error",
                (error) => {
                    console.error(
                        "Attachment stream error:",
                        error
                    );

                    if (!res.headersSent) {
                        res.status(404).json({
                            success: false,
                            message:
                                "Attachment file not found"
                        });
                    } else {
                        res.destroy(error);
                    }
                }
            );

            return fileStream.pipe(res);


        } catch (error) {

            console.error(
                "Download attachment error:",
                error
            );

            return res.status(400).json({
                success: false,
                message:
                    error.message
            });

        }

    }

};

module.exports =
    messageUploadController;
