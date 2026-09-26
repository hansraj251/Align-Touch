package com.chatflow.app

enum class AttachmentKind(
    val label: String,
    val acceptedMimeTypes: List<String>
) {

    GALLERY(
        label = "Gallery",
        acceptedMimeTypes = listOf("image/*")
    ),

    DOCUMENT(
        label = "Document",
        acceptedMimeTypes = listOf("*/*")
    ),

    AUDIO(
        label = "Audio",
        acceptedMimeTypes = listOf("audio/*")
    ),

    CAMERA(
        label = "Camera",
        acceptedMimeTypes = listOf("image/*")
    ),

    LOCATION(
        label = "Location",
        acceptedMimeTypes = emptyList()
    ),

    CONTACT(
        label = "Contact",
        acceptedMimeTypes = emptyList()
    );

    companion object {

        fun menuItems(): List<AttachmentKind> =
            listOf(
                GALLERY,
                DOCUMENT,
                AUDIO,
                CAMERA,
                LOCATION,
                CONTACT
            )
    }
}

data class PendingAttachment(
    val uri: String,
    val mimeType: String,
    val displayName: String,
    val kind: AttachmentKind
) {

    val isPreviewable: Boolean
        get() =
            mimeType.startsWith("image/")
}

object AttachmentPickerSpec {

    fun mimeTypes(
        kind: AttachmentKind
    ): List<String> =
        kind.acceptedMimeTypes

}
