package com.chatflow.app.data

import com.google.gson.Gson
import com.google.gson.JsonDeserializer

class MessageJsonDeserializer : JsonDeserializer<Message> {

    override fun deserialize(
        json: com.google.gson.JsonElement,
        typeOfT: java.lang.reflect.Type,
        context: com.google.gson.JsonDeserializationContext
    ): Message {

        val obj =
            json.asJsonObject

        val attachments =
            if (
                obj.has("attachments") &&
                !obj["attachments"].isJsonNull
            ) {
                context.deserialize<List<MessageAttachment>>(
                    obj["attachments"],
                    object :
                        com.google.gson.reflect.TypeToken<
                            List<MessageAttachment>
                        >() {}.type
                )
            } else {
                emptyList()
            }

        return Message(
            id = obj.get("id").asString,
            conversation_id =
                obj.get("conversation_id").asString,
            sender_id =
                obj.get("sender_id").asString,
            message_type =
                obj.get("message_type").asString,
            content =
                if (
                    obj.has("content") &&
                    !obj.get("content").isJsonNull
                ) {
                    obj.get("content").asString
                } else {
                    null
                },
            reply_to_message_id =
                if (
                    obj.has("reply_to_message_id") &&
                    !obj.get("reply_to_message_id").isJsonNull
                ) {
                    obj.get("reply_to_message_id").asString
                } else {
                    null
                },
            forwarded_from_message_id =
                if (
                    obj.has("forwarded_from_message_id") &&
                    !obj.get("forwarded_from_message_id").isJsonNull
                ) {
                    obj.get("forwarded_from_message_id").asString
                } else {
                    null
                },
            created_at =
                obj.get("created_at").asString,
            edited_at =
                if (
                    obj.has("edited_at") &&
                    !obj.get("edited_at").isJsonNull
                ) {
                    obj.get("edited_at").asString
                } else {
                    null
                },
            deleted_at =
                if (
                    obj.has("deleted_at") &&
                    !obj.get("deleted_at").isJsonNull
                ) {
                    obj.get("deleted_at").asString
                } else {
                    null
                },
            expires_at =
                if (
                    obj.has("expires_at") &&
                    !obj.get("expires_at").isJsonNull
                ) {
                    obj.get("expires_at").asString
                } else {
                    null
                },
            attachments = attachments
        )
    }
}
