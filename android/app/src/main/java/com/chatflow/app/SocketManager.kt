package com.chatflow.app

import android.util.Log

import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class SocketManager {

    private var socket: Socket? = null

    fun connect(
        token: String,
        onConnected: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (socket?.connected() == true) {
            onConnected()
            return
        }

        try {
            Log.d(
                "ChatFlowSocket",
                "Connecting socket..."
            )

            val options =
                IO.Options().apply {
                    auth =
                        mapOf(
                            "token" to token
                        )
                    transports =
                        arrayOf(
                            "websocket"
                        )
                }

            socket =
                IO.socket(
                    "http://10.85.201.156:3000",
                    options
                )

            socket?.on(
                Socket.EVENT_CONNECT
            ) {
                Log.d(
                    "ChatFlowSocket",
                    "Socket connected: ${socket?.id()}"
                )
                onConnected()
            }

            socket?.on(
                Socket.EVENT_CONNECT_ERROR
            ) { args ->
                Log.e(
                    "ChatFlowSocket",
                    "Socket connection error: " +
                        args.firstOrNull()
                )

                val message =
                    args.firstOrNull()
                        ?.toString()
                        ?: "Socket connection failed"

                onError(message)
            }

            socket?.connect()
        } catch (error: Exception) {
            onError(
                error.message
                    ?: "Socket connection failed"
            )
        }
    }

    fun listenForUserOnline(
        onUserOnline: (JSONObject) -> Unit
    ) {
        socket?.on(
            "user_online"
        ) { args ->
            val data =
                args.firstOrNull()

            if (data is JSONObject) {
                onUserOnline(data)
            }
        }
    }

    fun listenForUserOffline(
        onUserOffline: (JSONObject) -> Unit
    ) {
        socket?.on(
            "user_offline"
        ) { args ->
            val data =
                args.firstOrNull()

            if (data is JSONObject) {
                onUserOffline(data)
            }
        }
    }

    fun joinConversation(
        conversationId: String
    ) {
        Log.d(
            "ChatFlowSocket",
            "Joining conversation: $conversationId"
        )

        socket?.emit(
            "join_conversation",
            conversationId
        )
    }

    fun listenForNewMessages(
        onMessage: (JSONObject) -> Unit
    ) {
        Log.d(
            "ChatFlowSocket",
            "Registering new_message listener"
        )

        socket?.on(
            "new_message"
        ) { args ->
            val data =
                args.firstOrNull()

            if (data is JSONObject) {
                Log.d(
                    "ChatFlowSocket",
                    "new_message received: $data"
                )
                onMessage(data)
            }
        }
    }

    fun forwardMessage(
        messageId: String,
        targetConversationId: String
    ) {
        Log.d(
            "ChatFlowSocket",
            "forwardMessage called, connected=${socket?.connected()}"
        )

        val data =
            JSONObject().apply {
                put(
                    "messageId",
                    messageId
                )
                put(
                    "targetConversationId",
                    targetConversationId
                )
            }

        if (socket?.connected() != true) {
            Log.e(
                "ChatFlowSocket",
                "Cannot forward message: socket is not connected"
            )
            return
        }

        Log.d(
            "ChatFlowSocket",
            "Emitting forward_message: $data"
        )

        socket?.emit(
            "forward_message",
            data
        )
    }

    fun sendMessage(
        conversationId: String,
        content: String,
        expiresAt: String? = null,
        replyToMessageId: String? = null
    ) {
        Log.d(
            "ChatFlowSocket",
            "sendMessage called, connected=${socket?.connected()}"
        )

        val data =
            JSONObject().apply {
                put(
                    "conversationId",
                    conversationId
                )
                put(
                    "content",
                    content
                )

                if (expiresAt != null) {
                    put(
                        "expiresAt",
                        expiresAt
                    )
                }

                if (replyToMessageId != null) {
                    put(
                        "replyToMessageId",
                        replyToMessageId
                    )
                }
            }

        if (socket?.connected() != true) {
            Log.e(
                "ChatFlowSocket",
                "Cannot send message: socket is not connected"
            )
            return
        }

        Log.d(
            "ChatFlowSocket",
            "Emitting send_message: $data"
        )

        socket?.emit(
            "send_message",
            data
        )

        Log.d(
            "ChatFlowSocket",
            "send_message emit completed"
        )
    }

    fun deleteMessage(
        messageId: String
    ) {
        Log.d(
            "ChatFlowSocket",
            "Deleting message: $messageId"
        )

        if (socket?.connected() != true) {
            Log.e(
                "ChatFlowSocket",
                "Cannot delete message: socket is not connected"
            )
            return
        }

        val data =
            JSONObject().apply {
                put(
                    "messageId",
                    messageId
                )
            }

        socket?.emit(
            "delete_message",
            data
        )

        Log.d(
            "ChatFlowSocket",
            "delete_message emit completed: $data"
        )
    }

    fun listenForMessageDeleted(
        onMessageDeleted: (JSONObject) -> Unit
    ) {
        socket?.on(
            "message_deleted"
        ) { args ->
            val data =
                args.firstOrNull()

            if (data is JSONObject) {
                Log.d(
                    "ChatFlowSocket",
                    "message_deleted received: $data"
                )

                onMessageDeleted(data)
            }
        }
    }

    fun editMessage(
        messageId: String,
        content: String
    ) {
        Log.d(
            "ChatFlowSocket",
            "Editing message: $messageId"
        )

        if (socket?.connected() != true) {
            Log.e(
                "ChatFlowSocket",
                "Cannot edit message: socket is not connected"
            )
            return
        }

        val data =
            JSONObject().apply {
                put(
                    "messageId",
                    messageId
                )
                put(
                    "content",
                    content
                )
            }

        socket?.emit(
            "edit_message",
            data
        )

        Log.d(
            "ChatFlowSocket",
            "edit_message emit completed: $data"
        )
    }

    fun listenForMessageEdited(
        onMessageEdited: (JSONObject) -> Unit
    ) {
        socket?.on(
            "message_edited"
        ) { args ->
            val data =
                args.firstOrNull()

            if (data is JSONObject) {
                Log.d(
                    "ChatFlowSocket",
                    "message_edited received: $data"
                )

                onMessageEdited(data)
            }
        }
    }

    fun reactToMessage(
        messageId: String,
        reaction: String
    ) {
        Log.d(
            "ChatFlowSocket",
            "Reacting to message: $messageId, reaction=$reaction"
        )

        if (socket?.connected() != true) {
            Log.e(
                "ChatFlowSocket",
                "Cannot react to message: socket is not connected"
            )
            return
        }

        val data =
            JSONObject().apply {
                put(
                    "messageId",
                    messageId
                )
                put(
                    "reaction",
                    reaction
                )
            }

        socket?.emit(
            "react_to_message",
            data
        )

        Log.d(
            "ChatFlowSocket",
            "react_to_message emit completed: $data"
        )
    }

    fun removeMessageReaction(
        messageId: String
    ) {
        Log.d(
            "ChatFlowSocket",
            "Removing reaction from message: $messageId"
        )

        if (socket?.connected() != true) {
            Log.e(
                "ChatFlowSocket",
                "Cannot remove reaction: socket is not connected"
            )
            return
        }

        socket?.emit(
            "remove_message_reaction",
            messageId
        )

        Log.d(
            "ChatFlowSocket",
            "remove_message_reaction emit completed"
        )
    }

    fun listenForMessageReactionUpdated(
        onReaction: (JSONObject) -> Unit
    ) {
        socket?.on(
            "message_reaction_updated"
        ) { args ->
            val data =
                args.firstOrNull()

            if (data is JSONObject) {
                Log.d(
                    "ChatFlowSocket",
                    "message_reaction_updated received: $data"
                )

                onReaction(data)
            }
        }
    }

    fun listenForMessageReactionRemoved(
        onReactionRemoved: (JSONObject) -> Unit
    ) {
        socket?.on(
            "message_reaction_removed"
        ) { args ->
            val data =
                args.firstOrNull()

            if (data is JSONObject) {
                Log.d(
                    "ChatFlowSocket",
                    "message_reaction_removed received: $data"
                )

                onReactionRemoved(data)
            }
        }
    }

    fun startTyping(
        conversationId: String
    ) {
        if (socket?.connected() != true) {
            return
        }

        socket?.emit(
            "typing_start",
            conversationId
        )
    }

    fun stopTyping(
        conversationId: String
    ) {
        if (socket?.connected() != true) {
            return
        }

        socket?.emit(
            "typing_stop",
            conversationId
        )
    }

    fun listenForUserTyping(
        onTyping: (JSONObject) -> Unit
    ) {
        socket?.on(
            "user_typing"
        ) { args ->
            val data =
                args.firstOrNull()

            if (data is JSONObject) {
                onTyping(data)
            }
        }
    }

    fun listenForUserStoppedTyping(
        onStoppedTyping: (JSONObject) -> Unit
    ) {
        socket?.on(
            "user_stopped_typing"
        ) { args ->
            val data =
                args.firstOrNull()

            if (data is JSONObject) {
                onStoppedTyping(data)
            }
        }
    }

    fun markDelivered(
        messageId: String
    ) {
        Log.d(
            "ChatFlowSocket",
            "Marking message delivered: $messageId"
        )

        if (socket?.connected() != true) {
            Log.e(
                "ChatFlowSocket",
                "Cannot mark delivered: socket is not connected"
            )
            return
        }

        socket?.emit(
            "mark_delivered",
            messageId
        )
    }

    fun markRead(
        messageId: String
    ) {
        Log.d(
            "ChatFlowSocket",
            "Marking message read: $messageId"
        )

        if (socket?.connected() != true) {
            Log.e(
                "ChatFlowSocket",
                "Cannot mark read: socket is not connected"
            )
            return
        }

        socket?.emit(
            "mark_read",
            messageId
        )
    }

    fun listenForMessageDelivered(
        onReceipt: (JSONObject) -> Unit
    ) {
        socket?.on(
            "message_delivered"
        ) { args ->
            val data =
                args.firstOrNull()

            if (data is JSONObject) {
                onReceipt(data)
            }
        }
    }

    fun listenForMessageRead(
        onReceipt: (JSONObject) -> Unit
    ) {
        socket?.on(
            "message_read"
        ) { args ->
            val data =
                args.firstOrNull()

            if (data is JSONObject) {
                onReceipt(data)
            }
        }
    }

    fun disconnect() {
        socket?.disconnect()
        socket = null
    }
}
