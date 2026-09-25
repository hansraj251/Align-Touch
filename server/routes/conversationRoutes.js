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

router.get(
    "/:id/group",
    authMiddleware,
    conversationController.getGroupDetails
);

router.put(
    "/:id/group",
    authMiddleware,
    conversationController.updateGroup
);

router.delete(
    "/:id/group/members/:userId",
    authMiddleware,
    conversationController.removeGroupMember
);

router.delete(
    "/:id/group",
    authMiddleware,
    conversationController.deleteGroup
);

router.delete(
    "/:id/clear",
    authMiddleware,
    conversationController.clearChat
);


router.get(
    "/:id/group/members",
    authMiddleware,
    conversationController.getGroupMembers
);

router.post(
    "/group",
    authMiddleware,
    conversationController.createGroupConversation
);

router.post(
    "/direct",
    authMiddleware,
    conversationController.createDirectConversation
);

module.exports =
    router;
