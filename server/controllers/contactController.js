const contactService =
    require("../services/contactService");

const contactController = {

    async createContact(req, res) {

        try {

            const contact =
                await contactService
                    .createContact(
                        req.user.userId,
                        req.body
                    );

            return res.status(201).json({
                success: true,
                contact
            });

        } catch (error) {

            console.error(
                "Create contact error:",
                error
            );

            if (
                error.message ===
                "Contact already exists"
            ) {

                return res.status(409).json({
                    success: false,
                    message:
                        error.message
                });
            }

            return res.status(500).json({
                success: false,
                message:
                    "Failed to create contact"
            });
        }
    },

    async updateContact(req, res) {

        try {

            const contact =
                await contactService
                    .updateContact(
                        req.user.userId,
                        req.params.id,
                        req.body
                    );

            if (!contact) {

                return res.status(404).json({

                    success: false,

                    message:
                        "Contact not found"

                });

            }

            return res.status(200).json({

                success: true,

                contact

            });

        } catch (error) {

            console.error(

                "Update contact error:",

                error

            );

            if (

                error.message ===

                "Contact not found"

            ) {

                return res.status(404).json({

                    success: false,

                    message:
                        error.message

                });

            }

            if (

                error.message ===

                "First name is required"

            ) {

                return res.status(400).json({

                    success: false,

                    message:
                        error.message

                });

            }

            if (

                error.message ===

                "Phone is required"

            ) {

                return res.status(400).json({

                    success: false,

                    message:
                        error.message

                });

            }

            return res.status(500).json({

                success: false,

                message:
                    "Failed to update contact"

            });

        }

    },

    async deleteContact(req, res) {
        try {
            const contact =
                await contactService
                    .deleteContact(
                        req.user.userId,
                        req.params.id
                    );

            return res.status(200).json({
                success: true,
                contact
            });
        } catch (error) {
            console.error(
                "Delete contact error:",
                error
            );

            if (
                error.message ===
                "Contact not found"
            ) {
                return res.status(404).json({
                    success: false,
                    message:
                        error.message
                });
            }

            return res.status(500).json({
                success: false,
                message:
                    "Failed to delete contact"
            });
        }
    },

    async listContacts(req, res) {

        try {

            const contacts =
                await contactService
                    .listContacts(
                        req.user.userId
                    );

            return res.status(200).json({
                success: true,
                contacts
            });

        } catch (error) {

            console.error(
                "List contacts error:",
                error
            );

            return res.status(500).json({
                success: false,
                message:
                    "Failed to load contacts"
            });
        }
    },

    async getContact(req, res) {

        try {

            const contact =
                await contactService
                    .getContact(
                        req.user.userId,
                        req.params.id
                    );

            if (!contact) {

                return res.status(404).json({
                    success: false,
                    message:
                        "Contact not found"
                });
            }

            return res.status(200).json({
                success: true,
                contact
            });

        } catch (error) {

            console.error(
                "Get contact error:",
                error
            );

            return res.status(500).json({
                success: false,
                message:
                    "Failed to load contact"
            });
        }
    }

};

module.exports =
    contactController;
