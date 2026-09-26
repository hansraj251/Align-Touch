package com.chatflow.app.data

import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Test

class MessageJsonDeserializerTest {

    private val gson =
        GsonBuilder()
            .registerTypeAdapter(
                Message::class.java,
                MessageJsonDeserializer()
            )
            .create()

    @Test
    fun `null attachments become empty list`() {

        val json =
            """
            {
                "id": "1",
                "conversation_id": "1",
                "sender_id": "1",
                "message_type": "text",
                "content": "hello",
                "reply_to_message_id": null,
                "forwarded_from_message_id": null,
                "created_at": "2026-09-25T10:00:00.000Z",
                "edited_at": null,
                "deleted_at": null,
                "expires_at": null,
                "attachments": null
            }
            """.trimIndent()

        val message =
            gson.fromJson(
                json,
                Message::class.java
            )

        assertEquals(
            emptyList<MessageAttachment>(),
            message.attachments
        )
    }

    @Test
    fun `missing attachments become empty list`() {

        val json =
            """
            {
                "id": "2",
                "conversation_id": "1",
                "sender_id": "1",
                "message_type": "text",
                "content": "hello",
                "reply_to_message_id": null,
                "forwarded_from_message_id": null,
                "created_at": "2026-09-25T10:00:00.000Z",
                "edited_at": null,
                "deleted_at": null,
                "expires_at": null
            }
            """.trimIndent()

        val message =
            gson.fromJson(
                json,
                Message::class.java
            )

        assertEquals(
            emptyList<MessageAttachment>(),
            message.attachments
        )
    }
}
