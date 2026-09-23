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

router.put(
    "/:id",
    authMiddleware,
    contactController.updateContact
);

router.delete(
    "/:id",
    authMiddleware,
    contactController.deleteContact
);

router.get(
    "/:id",
    authMiddleware,
    contactController.getContact
);

module.exports =
    router;
