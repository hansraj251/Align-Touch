const express =
    require("express");

const contactController =
    require("../controllers/contactController");

const authMiddleware =
    require("../middleware/authMiddleware");

const router =
    express.Router();

router.post(
    "/",
    authMiddleware,
    contactController.createContact
);

router.get(
    "/",
    authMiddleware,
    contactController.listContacts
);

router.get(
    "/:id",
    authMiddleware,
    contactController.getContact
);

module.exports =
    router;
