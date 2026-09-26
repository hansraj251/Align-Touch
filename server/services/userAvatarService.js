const path =
    require("path");

const userRepository =
    require("../repositories/userRepository");

const avatarStorageService =
    require("../storage/avatarStorageService");

const MAX_AVATAR_SIZE =
    5 * 1024 * 1024;

const ALLOWED_AVATAR_MIME_TYPES = [
    "image/jpeg",
    "image/png",
    "image/webp"
];

function getExtension(originalName) {

    return path
        .extname(
            String(originalName)
        )
        .replace(
            ".",
            ""
        );

}

const userAvatarService = {

    async getAvatar({
        userId
    }) {

        if (!userId) {

            throw new Error(
                "User is required"
            );

        }

        const user =
            await userRepository.getById(
                userId
            );

        if (!user) {

            throw new Error(
                "User not found"
            );

        }

        console.log(
            "AVATAR DEBUG:",
            JSON.stringify({
                userId,
                repositoryUserId: user.id,
                avatarUrl: user.avatar_url,
                avatarUrlType: typeof user.avatar_url
            })
        );

        if (
            !user.avatar_url ||
            !String(
                user.avatar_url
            ).startsWith(
                "avatars/"
            )
        ) {

            throw new Error(
                "Avatar not found"
            );

        }

        const filePath =
            avatarStorageService.getFilePath(
                user.avatar_url
            );

        return {
            user,
            filePath
        };

    },

    async uploadAvatar({
        userId,
        file
    }) {

        if (!userId) {

            throw new Error(
                "User is required"
            );

        }

        if (!file) {

            throw new Error(
                "Avatar file is required"
            );

        }

        if (
            !Number.isInteger(file.size) ||
            file.size <= 0
        ) {

            throw new Error(
                "Invalid avatar file size"
            );

        }

        if (
            file.size >
            MAX_AVATAR_SIZE
        ) {

            throw new Error(
                "Avatar file cannot exceed 5 MB"
            );

        }

        const mimeType =
            String(
                file.mimetype
            )
                .toLowerCase()
                .trim();

        if (
            !ALLOWED_AVATAR_MIME_TYPES.includes(
                mimeType
            )
        ) {

            throw new Error(
                "Avatar must be a JPEG, PNG, or WebP image"
            );

        }

        const extension =
            getExtension(
                file.originalname
            );

        const existingUser =

            await userRepository.getById(

                userId

            );

        if (!existingUser) {

            throw new Error(

                "User not found"

            );

        }

        const oldAvatarUrl =

            existingUser.avatar_url;

        let storageResult = null;

        try {

            storageResult =

                avatarStorageService.saveFile({

                    buffer:

                        file.buffer,

                    extension

                });

            const user =

                await userRepository.updateAvatar(

                    userId,

                    storageResult.storageKey

                );

            if (!user) {

                throw new Error(

                    "User not found"

                );

            }

            if (

                oldAvatarUrl &&

                String(

                    oldAvatarUrl

                ).startsWith(

                    "avatars/"

                ) &&

                oldAvatarUrl !==

                    storageResult.storageKey

            ) {

                try {

                    avatarStorageService.deleteFile(

                        oldAvatarUrl

                    );

                } catch (cleanupError) {

                    console.error(

                        "Old avatar cleanup error:",

                        cleanupError

                    );

                }

            }

            return user;

        } catch (error) {

            if (storageResult) {

                try {

                    avatarStorageService.deleteFile(
                        storageResult.storageKey
                    );

                } catch (cleanupError) {

                    console.error(
                        "Avatar cleanup error:",
                        cleanupError
                    );

                }

            }

            throw error;

        }

    }

};

module.exports =
    userAvatarService;
