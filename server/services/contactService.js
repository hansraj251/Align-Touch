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

        let linkedUser = null;

        if (username) {
            linkedUser =
                await userRepository
                    .getByEmail(username);
        }

        if (!linkedUser) {
            linkedUser =
                await userRepository
                    .getByPhone(phone);
        }

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
