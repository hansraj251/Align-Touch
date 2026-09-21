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
                    "http://localhost:3000",
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
                onMessage(data)
            }
        }
    }

    fun sendMessage(
        conversationId: String,
        content: String
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
