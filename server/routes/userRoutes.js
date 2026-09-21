const express =
    require("express");

const userController =
    require("../controllers/userController");

const authMiddleware =
    require("../middleware/authMiddleware");

const router =
    express.Router();

router.get(
    "/",
    authMiddleware,
    userController.listUsers
);

module.exports =
    router;
