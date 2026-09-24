const fs =
    require("fs");

const path =
    require("path");

const crypto =
    require("crypto");

const STORAGE_ROOT =
    path.join(
        __dirname,
        "..",
        "uploads",
        "attachments"
    );

function ensureStorageDirectory() {
    fs.mkdirSync(
        STORAGE_ROOT,
        {
            recursive: true
        }
    );
}

const localStorageService = {

    saveFile({
        buffer,
        extension
    }) {
        if (
            !Buffer.isBuffer(buffer) ||
            buffer.length === 0
        ) {
            throw new Error(
                "Invalid file buffer"
            );
        }

        ensureStorageDirectory();

        const randomName =
            crypto.randomBytes(24)
                .toString("hex");

        const safeExtension =
            extension
                ? String(extension)
                    .replace(
                        /[^a-zA-Z0-9]/g,
                        ""
                    )
                    .toLowerCase()
                : "";

        const fileName =
            safeExtension
                ? `${randomName}.${safeExtension}`
                : randomName;

        const storageKey =
            `attachments/${fileName}`;

        const filePath =
            path.join(
                STORAGE_ROOT,
                fileName
            );

        fs.writeFileSync(
            filePath,
            buffer
        );

        return {
            storageKey,
            filePath
        };
    },

    deleteFile(storageKey) {
        if (
            !storageKey ||
            !String(storageKey).startsWith(
                "attachments/"
            )
        ) {
            throw new Error(
                "Invalid storage key"
            );
        }

        const fileName =
            String(storageKey)
                .replace(
                    "attachments/",
                    ""
                );

        const filePath =
            path.join(
                STORAGE_ROOT,
                fileName
            );

        if (fs.existsSync(filePath)) {
            fs.unlinkSync(filePath);
        }

        return true;
    }

    ,

    getFilePath(storageKey) {
        if (
            !storageKey ||
            !String(storageKey).startsWith(
                "attachments/"
            )
        ) {
            throw new Error(
                "Invalid storage key"
            );
        }

        const fileName =
            String(storageKey)
                .replace(
                    "attachments/",
                    ""
                );

        return path.join(
            STORAGE_ROOT,
            fileName
        );
    }

};

module.exports =
    localStorageService;
