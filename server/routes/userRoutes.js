const express =
    require("express");

const userController =
    require("../controllers/userController");

const authMiddleware =
    require("../middleware/authMiddleware");

const upload =
    require("../middleware/uploadMiddleware");

const router =
    express.Router();

router.get(
    "/me",
    authMiddleware,
    userController.getProfile
);

router.post(
    "/me/avatar",
    authMiddleware,
    upload.single("file"),
    userController.uploadAvatar
);

router.patch(
    "/me",
    authMiddleware,
    userController.updateProfile
);

router.get(
    "/:userId/avatar",
    authMiddleware,
    userController.getAvatar
);

router.get(
    "/",
    authMiddleware,
    userController.listUsers
);

module.exports =
    router;
