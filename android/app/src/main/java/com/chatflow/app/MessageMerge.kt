package com.chatflow.app

import com.chatflow.app.data.Message

object MessageMerge {

    fun mergeMessages(
        restMessages: List<Message>,
        socketMessages: List<Message>
    ): List<Message> {
        return (
            restMessages + socketMessages
        )
            .associateBy {
                it.id
            }
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
