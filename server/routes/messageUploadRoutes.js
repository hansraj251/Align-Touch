const express =
    require("express");

const messageUploadController =
    require("../controllers/messageUploadController");

const authMiddleware =
    require("../middleware/authMiddleware");

const upload =
    require("../middleware/uploadMiddleware");

module.exports = function createMessageUploadRoutes(io) {

    const router =
        express.Router();

    router.post(
        "/",
        authMiddleware,
        upload.single("file"),
        (req, res) =>
            messageUploadController.uploadAttachment(
                req,
                res,
                io
            )
    );

    router.get(
        "/:attachmentId",
        authMiddleware,
        messageUploadController.downloadAttachment
    );

    return router;
};
