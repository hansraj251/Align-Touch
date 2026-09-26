package com.chatflow.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AttachmentComposerTest {

    @Test
    fun attachmentMenu_containsWhatsAppStyleOptionsInExpectedOrder() {
        assertEquals(
            listOf(
                AttachmentKind.GALLERY,
                AttachmentKind.DOCUMENT,
                AttachmentKind.AUDIO,
                AttachmentKind.CAMERA,
                AttachmentKind.LOCATION,
                AttachmentKind.CONTACT
            ),
            AttachmentKind.menuItems()
        )
    }

    @Test
    fun attachmentKinds_haveUserFacingLabels() {
        assertEquals("Gallery", AttachmentKind.GALLERY.label)
        assertEquals("Document", AttachmentKind.DOCUMENT.label)
        assertEquals("Audio", AttachmentKind.AUDIO.label)
        assertEquals("Camera", AttachmentKind.CAMERA.label)
        assertEquals("Location", AttachmentKind.LOCATION.label)
        assertEquals("Contact", AttachmentKind.CONTACT.label)
    }

    @Test
    fun gallery_acceptsImages() {
        assertTrue(
            AttachmentKind.GALLERY.acceptedMimeTypes.contains("image/*")
        )
    }

    @Test
    fun document_acceptsDocuments() {
        assertTrue(
            AttachmentKind.DOCUMENT.acceptedMimeTypes.contains("*/*")
        )
    }

    @Test
    fun imageAttachment_isPreviewableBeforeUpload() {
        val attachment =
            PendingAttachment(
                uri = "content://image",
                mimeType = "image/jpeg",
                displayName = "photo.jpg",
                kind = AttachmentKind.GALLERY
            )

        assertEquals(true, attachment.isPreviewable)
    }

    @Test
    fun documentAttachment_isNotImagePreview() {
        val attachment =
            PendingAttachment(
                uri = "content://document",
                mimeType = "application/pdf",
                displayName = "document.pdf",
                kind = AttachmentKind.DOCUMENT
            )

        assertEquals(false, attachment.isPreviewable)
    }

    @Test
    fun galleryPicker_usesImageMimeType() {
        assertEquals(
            listOf("image/*"),
            AttachmentPickerSpec.mimeTypes(
                AttachmentKind.GALLERY
            )
        )
    }

    @Test
    fun documentPicker_acceptsAnyDocumentType() {
        assertEquals(
            listOf("*/*"),
            AttachmentPickerSpec.mimeTypes(
                AttachmentKind.DOCUMENT
            )
        )
    }

    @Test
    fun audioPicker_usesAudioMimeType() {
        assertEquals(
            listOf("audio/*"),
            AttachmentPickerSpec.mimeTypes(
                AttachmentKind.AUDIO
            )
        )
    }


    @Test

    fun cameraResult_createsCameraAttachment() {

        val attachment =

            CameraAttachmentFactory.fromUri(

                "content://camera/photo"

            )

        assertEquals(

            PendingAttachment(

                uri = "content://camera/photo",

                mimeType = "image/jpeg",

                displayName = "Camera photo",

                kind = AttachmentKind.CAMERA

            ),

            attachment

        )

    }

    @Test
    fun camera_usesImageMimeType() {
        assertEquals(
            listOf("image/*"),
            AttachmentPickerSpec.mimeTypes(
                AttachmentKind.CAMERA
            )
        )
    }

}
