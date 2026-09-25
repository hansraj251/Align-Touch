package com.chatflow.app

import com.chatflow.app.data.Message
import org.junit.Assert.assertEquals
import org.junit.Test

class MessageMergeTest {

    @Test
    fun mergeMessages_keepsSocketMessageWhenRestDoesNotContainIt() {
        val restMessage =
            message(
                id = "1",
                createdAt = "2026-09-22T10:00:00Z"
            )

        val socketMessage =
            message(
                id = "2",
                createdAt = "2026-09-22T10:01:00Z"
            )

        val result =
            MessageMerge.mergeMessages(
                restMessages = listOf(restMessage),
                socketMessages = listOf(socketMessage)
            )

        assertEquals(
            listOf("1", "2"),
            result.map { it.id }
        )
    }

    @Test
    fun mergeMessages_removesDuplicateMessageIds() {
        val restMessage =
            message(
                id = "1",
                createdAt = "2026-09-22T10:00:00Z"
            )

        val duplicateSocketMessage =
            message(
                id = "1",
                createdAt = "2026-09-22T10:00:00Z"
            )

        val result =
            MessageMerge.mergeMessages(
                restMessages = listOf(restMessage),
                socketMessages = listOf(
                    duplicateSocketMessage
                )
            )

        assertEquals(
            1,
            result.size
        )

        assertEquals(
            "1",
            result.first().id
        )
    }


    @Test
    fun mergeMessages_sortsMessagesByCreatedAt() {
        val newerMessage =
            message(
                id = "2",
                createdAt = "2026-09-22T10:02:00Z"
            )

        val olderMessage =
            message(
                id = "1",
                createdAt = "2026-09-22T10:01:00Z"
            )

        val result =
            MessageMerge.mergeMessages(
                restMessages = listOf(newerMessage),
                socketMessages = listOf(olderMessage)
            )

        assertEquals(
            listOf("1", "2"),
            result.map { it.id }
        )
    }

    private fun message(
        id: String,
        createdAt: String
    ) =
        Message(
            id = id,
            conversation_id = "100",
            sender_id = "10",
            message_type = "text",
            content = "Hello",
            reply_to_message_id = null,
            forwarded_from_message_id = null,
            created_at = createdAt,
            edited_at = null,
            deleted_at = null,
            expires_at = null
        )
}
