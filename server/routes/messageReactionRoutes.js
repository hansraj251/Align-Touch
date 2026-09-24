const express =
    require("express");

const messageReactionController =
    require("../controllers/messageReactionController");

const authMiddleware =
    require("../middleware/authMiddleware");

const router =
    express.Router();

router.post(
    "/:messageId/reaction",
    authMiddleware,
    messageReactionController.addReaction
);

router.delete(
    "/:messageId/reaction",
    authMiddleware,
    messageReactionController.removeReaction
);

router.get(
    "/:messageId/reaction",
    authMiddleware,
    messageReactionController.listReactions
);

module.exports =
    router;
