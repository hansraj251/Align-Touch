const express =
    require("express");

const userController =
    require("../controllers/userController");

const authMiddleware =
    require("../middleware/authMiddleware");

const router =
    express.Router();

router.get(
    "/me",
    authMiddleware,
    userController.getProfile
);

router.patch(
    "/me",
    authMiddleware,
    userController.updateProfile
);

router.get(
    "/",
    authMiddleware,
    userController.listUsers
);

module.exports =
    router;
