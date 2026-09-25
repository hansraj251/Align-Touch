package com.chatflow.app

import com.chatflow.app.data.Message

object MessageMerge {

    fun mergeMessages(
        restMessages: List<Message>,
        socketMessages: List<Message>
    ): List<Message> {

        val messagesById =

            linkedMapOf<String, Message>()

        restMessages.forEach { message ->

            messagesById[message.id] =

                message

        }

        socketMessages.forEach { socketMessage ->

            val existingMessage =

                messagesById[socketMessage.id]

            messagesById[socketMessage.id] =

                if (
                    existingMessage != null &&
                    socketMessage.attachments.isEmpty() &&
                    existingMessage.attachments.isNotEmpty()
                ) {

                    socketMessage.copy(

                        attachments =
                            existingMessage.attachments

                    )

                } else {

                    socketMessage

                }

        }

        return messagesById
            .values
            .sortedWith(
                compareBy<Message> {
                    it.created_at
                }.thenBy {
                    it.id.toLongOrNull()
                        ?: Long.MAX_VALUE
                }
            )
    }
}
