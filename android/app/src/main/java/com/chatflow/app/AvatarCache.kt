package com.chatflow.app

import android.content.Context
import java.io.File

object AvatarCache {

    private const val DIRECTORY = "avatar_cache"

    fun getFile(
        context: Context,
        userId: String
    ): File {

        val directory =
            File(
                context.cacheDir,
                DIRECTORY
            )

        if (!directory.exists()) {
            directory.mkdirs()
        }

        return File(
            directory,
            "$userId.avatar"
        )
    }

    fun read(
        context: Context,
        userId: String
    ): ByteArray? {

        val file =
            getFile(
                context,
                userId
            )

        if (!file.exists()) {
            return null
        }

        return try {
            file.readBytes()
        } catch (error: Exception) {
            null
        }
    }

    fun write(
        context: Context,
        userId: String,
        bytes: ByteArray
    ) {

        try {

            getFile(
                context,
                userId
            ).writeBytes(bytes)

        } catch (error: Exception) {
        }
    }

    fun delete(
        context: Context,
        userId: String
    ) {

        try {

            getFile(
                context,
                userId
            ).delete()

        } catch (error: Exception) {
        }
    }
}
