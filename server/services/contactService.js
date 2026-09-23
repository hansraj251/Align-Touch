const contactRepository =
    require("../repositories/contactRepository");

const userRepository =
    require("../repositories/userRepository");

const contactService = {

    async createContact(
        ownerUserId,
        {
            firstName,
            lastName,
            username,
            countryCode,
            phone
        }
    ) {

        const existingContact =
            await contactRepository
                .getByOwnerAndPhone(
                    ownerUserId,
                    phone
                );

        if (existingContact) {
            throw new Error(
                "Contact already exists"
            );
        }

        let linkedUser =
            await userRepository
                .getByPhone(phone);

        return contactRepository
            .createContact({
                ownerUserId,
                linkedUserId:
                    linkedUser
                        ? linkedUser.id
                        : null,
                firstName,
                lastName,
                username,
                countryCode,
                phone
            });
    },

    async getContact(
        ownerUserId,
        contactId
    ) {

        return contactRepository
            .getById(
                ownerUserId,
                contactId
            );
    },

    async deleteContact(
        ownerUserId,
        contactId
    ) {
        const contact =
            await contactRepository.getById(
                ownerUserId,
                contactId
            );

        if (!contact) {
            throw new Error(
                "Contact not found"
            );
        }

        return contactRepository.deleteContact(
            ownerUserId,
            contactId
        );
    },

    async updateContact(
        ownerUserId,
        contactId,
        {
            firstName,
            lastName,
            username,
            countryCode,
            phone
        }
    ) {

        const contact =
            await contactRepository.getById(
                ownerUserId,
                contactId
            );

        if (!contact) {
            throw new Error(
                "Contact not found"
            );
        }

        if (!firstName || !String(firstName).trim()) {
            throw new Error(
                "First name is required"
            );
        }

        if (!phone || !String(phone).trim()) {
            throw new Error(
                "Phone is required"
            );
        }

        return contactRepository.updateContact({
            ownerUserId,
            contactId,
            firstName: String(firstName).trim(),
            lastName:
                lastName
                    ? String(lastName).trim()
                    : "",
            username:
                username
                    ? String(username).trim()
                    : "",
            countryCode:
                countryCode
                    ? String(countryCode).trim()
                    : "",
            phone: String(phone).trim()
        });
    },

    async listContacts(
        ownerUserId
    ) {

        return contactRepository
            .listByOwner(
                ownerUserId
            );
    }

};

module.exports =
    contactService;
