package com.chatflow.app

import com.chatflow.app.data.Conversation

import android.util.Log

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Card

import androidx.compose.material3.CardDefaults

import androidx.compose.ui.graphics.Color

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        actionBar?.hide()

        val sessionManager =
            SessionManager(this)

        setContent {

            MaterialTheme {

                if (sessionManager.isLoggedIn()) {

                    ChatFlowApp()

                } else {

                    LoginScreen()
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    viewModel: LoginViewModel =
        viewModel()
) {

    var identifier by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val uiState by
        viewModel.uiState.collectAsState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),

        verticalArrangement =
            Arrangement.Center,

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = "ChatFlow",
            style =
                MaterialTheme
                    .typography
                    .headlineLarge
        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        OutlinedTextField(
            value = identifier,

            onValueChange = {
                identifier = it
            },

            label = {
                Text("Phone or Email")
            },

            singleLine = true
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = password,

            onValueChange = {
                password = it
            },

            label = {
                Text("Password")
            },

            visualTransformation =
                PasswordVisualTransformation(),

            singleLine = true
        )

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        Button(
            onClick = {
                viewModel.login(
                    identifier = identifier,
                    password = password
                )
            },

            enabled =
                !uiState.loading
        ) {

            if (uiState.loading) {

                CircularProgressIndicator()

            } else {

                Text("Login")
            }
        }

        if (uiState.message.isNotBlank()) {

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Text(
                text = uiState.message,
                color =
                    if (uiState.success) {
                        MaterialTheme
                            .colorScheme
                            .primary
                    } else {
                        MaterialTheme
                            .colorScheme
                            .error
                    }
            )
        }
    }
}


@Composable
fun ChatFlowApp() {

    var selectedConversation by
        remember {
            mutableStateOf<Conversation?>(null)
        }

    if (
        selectedConversation == null
    ) {

        ConversationListScreen(
            onConversationClick = {
                selectedConversation =
                    it
            }
        )

    } else {

        ChatScreen(
            conversationId =
                selectedConversation!!.id,
            contactName =
                selectedConversation!!.other_user_display_name,
            contactPhone =
                selectedConversation!!.other_user_phone,
            onBack = {
                selectedConversation = null
            }
        )
    }
}

@Composable
fun ConversationListScreen(
    onConversationClick: (Conversation) -> Unit,
    viewModel: ConversationViewModel =
        viewModel()
) {
    val uiState by
        viewModel.uiState.collectAsState()

    val context =
        androidx.compose.ui.platform.LocalContext.current

    androidx.compose.runtime.LaunchedEffect(
        Unit
    ) {
        viewModel.loadConversations()
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Text(
                text = "Chats",
                style =
                    MaterialTheme
                        .typography
                        .headlineLarge
            )

            androidx.compose.material3.TextButton(
                onClick = {
                    SessionManager(context)
                        .clearSession()

                    (context as? MainActivity)
                        ?.recreate()
                }
            ) {
                Text("Logout")
            }
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        if (uiState.loading) {

            androidx.compose.foundation.layout.Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                contentAlignment =
                    Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else if (
            uiState.message.isNotBlank()
        ) {

            Text(
                text = uiState.message,
                color =
                    MaterialTheme
                        .colorScheme
                        .error
            )

        } else if (
            uiState.conversations.isEmpty()
        ) {

            androidx.compose.foundation.layout.Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                contentAlignment =
                    Alignment.Center
            ) {
                Text(
                    text = "No conversations yet"
                )
            }

        } else {

            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
            ) {
                items(
                    uiState.conversations,
                    key = {
                        it.id
                    }
                ) { conversation ->

                    androidx.compose.material3.Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 4.dp
                                ),
                        onClick = {
                            onConversationClick(
                                conversation
                            )
                        }
                    ) {
                        Column(
                            modifier =
                                Modifier.padding(16.dp)
                        ) {
                            Text(
                                text =
                                    conversation
                                        .other_user_display_name,
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(
                                text =
                                    conversation
                                        .other_user_phone
                                        ?: conversation
                                            .other_user_email
                                        ?: "No contact"
                            )

                            if (
                                !conversation
                                    .other_user_about
                                    .isNullOrBlank()
                            ) {
                                Spacer(
                                    modifier =
                                        Modifier.height(4.dp)
                                )

                                Text(
                                    text =
                                        conversation
                                            .other_user_about
                                            ?: ""
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatScreen(
    conversationId: String,
    contactName: String,
    contactPhone: String?,
    onBack: () -> Unit,
    viewModel: MessageViewModel =
        viewModel()
) {
    Log.d(
        "ChatFlowScreen",
        "ChatScreen composed: $conversationId"
    )

    val uiState by
        viewModel.uiState.collectAsState()

    val sessionManager =
        SessionManager(
            androidx.compose.ui.platform.LocalContext.current
        )

    val currentUserId =
        sessionManager.getUserId()

    var messageText by remember {
        mutableStateOf("")
    }

    androidx.compose.runtime.LaunchedEffect(
        conversationId
    ) {
        viewModel.loadMessages(
            conversationId
        )
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .imePadding()
    ) {

        androidx.compose.foundation.layout.Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        horizontal = 8.dp,
                        vertical = 8.dp
                    ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            androidx.compose.material3.TextButton(
                onClick = onBack
            ) {
                Text(
                    text = "‹",
                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium
                )
            }

            androidx.compose.foundation.layout.Box(
                modifier =
                    Modifier
                        .padding(horizontal = 4.dp)
                        .background(
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .primaryContainer,
                            shape =
                                RoundedCornerShape(50)
                        )
                        .padding(10.dp)
            ) {
                Text(
                    text =
                        contactName
                            .firstOrNull()
                            ?.uppercase()
                            ?: "?",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium
                )
            }

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
            ) {
                Text(
                    text = contactName,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium
                )

                Text(
                    text =
                        contactPhone
                            ?: "No phone number",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall
                )
            }

            androidx.compose.material3.TextButton(
                onClick = {}
            ) {
                Text("⋮")
            }
        }

        androidx.compose.material3.HorizontalDivider()

        if (uiState.loading) {

            androidx.compose.foundation.layout.Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                contentAlignment =
                    Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else {

            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(
                            horizontal = 12.dp
                        ),
                verticalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {

                items(
                    uiState.messages,
                    key = {
                        it.id
                    }
                ) { message ->

                    val isMine =
                        message.sender_id ==
                            currentUserId

                    if (!isMine) {
                        viewModel.markMessageRead(
                            message.id
                        )
                    }

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            if (isMine) {
                                Arrangement.End
                            } else {
                                Arrangement.Start
                            }
                    ) {

                        Card(
                            modifier =
                                Modifier
                                    .widthIn(
                                        max = 320.dp
                                    ),
                            shape =
                                RoundedCornerShape(
                                    topStart = 18.dp,
                                    topEnd = 18.dp,
                                    bottomStart =
                                        if (isMine) {
                                            18.dp
                                        } else {
                                            4.dp
                                        },
                                    bottomEnd =
                                        if (isMine) {
                                            4.dp
                                        } else {
                                            18.dp
                                        }
                                ),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        if (isMine) {
                                            MaterialTheme
                                                .colorScheme
                                                .primaryContainer
                                        } else {
                                            MaterialTheme
                                                .colorScheme
                                                .surfaceVariant
                                        }
                                )
                        ) {
                            Column(
                                modifier =
                                    Modifier.padding(
                                        start = 12.dp,
                                        top = 8.dp,
                                        end = 12.dp,
                                        bottom = 7.dp
                                    )
                            ) {
                                Text(
                                    text =
                                        message.content
                                            ?: "",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyLarge
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(3.dp)
                                )

                                Row(
                                    modifier =
                                        Modifier.fillMaxWidth(),
                                    horizontalArrangement =
                                        Arrangement.End,
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {
                                    Text(
                                        text =
                                            message.created_at
                                                .replace(
                                                    "T",
                                                    " "
                                                )
                                                .take(16),
                                        style =
                                            MaterialTheme
                                                .typography
                                                .labelSmall
                                    )

                                    if (isMine) {
                                        Spacer(
                                            modifier =
                                                Modifier.width(
                                                    4.dp
                                                )
                                        )

                                        val receipt =
                                            uiState
                                                .messageReceipts[
                                                    message.id
                                                ]

                                        Text(
                                            text =
                                                when {
                                                    receipt
                                                        ?.read_at !=
                                                        null ->
                                                        "✓✓"

                                                    receipt
                                                        ?.delivered_at !=
                                                        null ->
                                                        "✓"

                                                    else ->
                                                        "○"
                                                },
                                            style =
                                                MaterialTheme
                                                    .typography
                                                    .labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        androidx.compose.material3.HorizontalDivider()

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 10.dp,
                        vertical = 8.dp
                    ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Card(
                modifier =
                    Modifier.weight(1f),
                shape =
                    RoundedCornerShape(24.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .surfaceVariant
                    )
            ) {

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 4.dp,
                                end = 6.dp
                            ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    androidx.compose.material3.TextButton(
                        onClick = {}
                    ) {
                        Text("😊")
                    }

                    androidx.compose.foundation.text.BasicTextField(
                        value = messageText,
                        onValueChange = {
                            messageText = it
                        },
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(
                                    vertical = 12.dp
                                ),
                        maxLines = 4,
                        decorationBox = { innerTextField ->

                            if (messageText.isBlank()) {
                                Text(
                                    text = "Message...",
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                )
                            }

                            innerTextField()
                        }
                    )

                    androidx.compose.material3.TextButton(
                        onClick = {}
                    ) {
                        Text("📎")
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.width(6.dp)
            )

            Button(
                onClick = {

                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(
                            conversationId =
                                conversationId,
                            content =
                                messageText
                        )

                        messageText = ""
                    }
                },
                enabled =
                    !uiState.sending &&
                    messageText.isNotBlank(),
                shape =
                    RoundedCornerShape(50.dp),
                modifier =
                    Modifier.height(52.dp)
            ) {
                Text(
                    text =
                        if (uiState.sending) {
                            "..."
                        } else {
                            "➤"
                        }
                )
            }
        }


        if (uiState.message.isNotBlank()) {

            Text(
                text = uiState.message,
                modifier =
                    Modifier.padding(
                        horizontal = 12.dp
                    ),
                color =
                    MaterialTheme
                        .colorScheme
                        .error
            )
        }
    }
}

