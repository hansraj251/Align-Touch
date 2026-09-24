const express =
    require("express");

const messageController =
    require("../controllers/messageController");

const authMiddleware =
    require("../middleware/authMiddleware");

const router =
    express.Router();

router.post(
    "/",
    authMiddleware,
    messageController.sendMessage
);

router.post(
    "/forward",
    authMiddleware,
    messageController.forwardMessage
);

router.delete(
    "/:messageId",
    authMiddleware,
    messageController.deleteMessageForEveryone
);

router.patch(
    "/:messageId",
    authMiddleware,
    messageController.editMessage
);

router.get(
    "/:conversationId",
    authMiddleware,
    messageController.listMessages
);

module.exports =
    router;
