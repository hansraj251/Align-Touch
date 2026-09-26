package com.chatflow.app

object CameraAttachmentFactory {

    fun fromUri(
        uri: String
    ): PendingAttachment =
        PendingAttachment(
            uri = uri,
            mimeType = "image/jpeg",
            displayName = "Camera photo",
            kind = AttachmentKind.CAMERA
        )
}
