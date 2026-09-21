const express =
    require("express");

const conversationController =
    require("../controllers/conversationController");

const authMiddleware =
    require("../middleware/authMiddleware");

const router =
    express.Router();

router.get(
    "/",
    authMiddleware,
    conversationController.getConversations
);

router.post(
    "/direct",
    authMiddleware,
    conversationController.createDirectConversation
);

module.exports =
    router;
