package com.chatflow.app

import android.net.Uri

import android.graphics.BitmapFactory

import com.chatflow.app.data.Message

import androidx.compose.foundation.border

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.clip
import com.chatflow.app.data.Contact
import com.chatflow.app.data.Conversation

import android.util.Log

import android.os.Bundle
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Card

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.sp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Forward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle

import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.Composable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

private fun formatRemainingMessageTime(seconds: Long): String {

    if (seconds < 60L) {
        return "${seconds}s"
    }

    if (seconds < 3600L) {
        val minutes = seconds / 60L
        val remainingSeconds = seconds % 60L

        return if (remainingSeconds > 0L) {
            "${minutes}m ${remainingSeconds}s"
        } else {
            "${minutes}m"
        }
    }

    if (seconds < 86400L) {
        val hours = seconds / 3600L
        val remainingMinutes = (seconds % 3600L) / 60L

        return if (remainingMinutes > 0L) {
            "${hours}h ${remainingMinutes}m"
        } else {
            "${hours}h"
        }
    }

    val days = seconds / 86400L
    val remainingHours = (seconds % 86400L) / 3600L

    return if (remainingHours > 0L) {
        "${days}d ${remainingHours}h"
    } else {
        "${days}d"
    }
}

private fun remainingMessageSeconds(expiresAt: String?): Long {
    if (expiresAt.isNullOrBlank()) {
        return 0L
    }

    return try {
        val remainingMillis =
            java.time.Instant
                .parse(expiresAt)
                .toEpochMilli() -
            System.currentTimeMillis()

        ((remainingMillis + 999L) / 1000L)
            .coerceAtLeast(0L)
    } catch (_: Exception) {
        0L
    }
}

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

                var loggedIn by
                    remember {
                        mutableStateOf(
                            sessionManager.isLoggedIn()
                        )
                    }

                if (loggedIn) {

                    ChatFlowApp()

                } else {

                    LoginScreen(
                        onLoginSuccess = {
                            loggedIn = true
                        }
                    )

                }

            }

        }

    }

}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel =
        viewModel()
) {

    var identifier by remember {
        mutableStateOf("")
    }

    var otp by remember {
        mutableStateOf("")
    }

    val uiState by
        viewModel.uiState.collectAsState()

    androidx.compose.runtime.LaunchedEffect(
        uiState.success
    ) {
        if (uiState.success) {
            onLoginSuccess()
        }
    }

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
            text = "",
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
                Text("Mobile Number")
            },

            singleLine = true,

            enabled =
                !uiState.otpSent &&
                !uiState.loading
        )

        if (!uiState.otpSent) {

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Button(
                onClick = {
                    viewModel.requestOtp(
                        identifier =
                            identifier
                    )
                },

                enabled =
                    !uiState.otpLoading
            ) {

                if (uiState.otpLoading) {

                    CircularProgressIndicator()

                } else {

                    Text("Send OTP")
                }
            }

        } else {

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            uiState.otpResponse?.otp?.let { generatedOtp ->
                Text(
                    text =
                        "Development OTP: $generatedOtp",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )
            }

            OutlinedTextField(
                value = otp,

                onValueChange = {
                    otp = it
                },

                label = {
                    Text("Enter OTP")
                },

                singleLine = true
            )

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Button(
                onClick = {
                    viewModel.verifyOtp(
                        identifier =
                            identifier,
                        otp = otp
                    )
                },

                enabled =
                    !uiState.loading
            ) {

                if (uiState.loading) {

                    CircularProgressIndicator()

                } else {

                    Text("Verify OTP")
                }
            }
        }

        if (uiState.message.isNotBlank()) {

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Text(
                text =
                    uiState.message,

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

    val conversationViewModel: ConversationViewModel =
        viewModel()

    val messageViewModel: MessageViewModel =
        viewModel()

    var selectedConversation by
        remember {
            mutableStateOf<Conversation?>(null)
        }

    var showNewChat by
        remember {
            mutableStateOf(false)
        }

    var selectedTab by
        remember {
            mutableStateOf(0)
        }

    var showProfile by
        remember {
            mutableStateOf(false)
        }

    var showEditContact by
        remember {
            mutableStateOf(false)
        }

    var showAddContact by
        remember {
            mutableStateOf(false)
        }

    var showGroupInfo by
        remember {
            mutableStateOf(false)
        }

    var showForwardPicker by
        remember {
            mutableStateOf(false)
        }

    var messageToForward by
        remember {
            mutableStateOf<Message?>(null)
        }

    val context =
        androidx.compose.ui.platform.LocalContext.current

    androidx.activity.compose.BackHandler(
        enabled =
            showEditContact ||
            showAddContact ||
            showGroupInfo ||
            showProfile ||
            showNewChat ||
            showForwardPicker ||
            selectedConversation != null
    ) {
        when {
            showEditContact -> {
                showEditContact = false
            }

            showAddContact -> {
                showAddContact = false
            }

            showGroupInfo -> {
                showGroupInfo = false
            }

            showProfile -> {
                showProfile = false
            }

            showNewChat -> {
                showNewChat = false
            }

            showForwardPicker -> {
                showForwardPicker = false
                messageToForward = null
            }

            selectedConversation != null -> {
                selectedConversation = null
            }
        }
    }

    Column(
        modifier =
            Modifier.fillMaxSize()
    ) {

            if (selectedConversation == null) {
                Text(
                    text = "",
                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 12.dp
                            )
                )
            }


            androidx.compose.foundation.layout.Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
            ) {

                if (showForwardPicker) {

                    val forwardUiState by
                        conversationViewModel
                            .uiState
                            .collectAsState()

                    LaunchedEffect(showForwardPicker) {
                        conversationViewModel
                            .loadConversations()
                    }

                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                    ) {

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 12.dp,
                                        vertical = 8.dp
                                    ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            TextButton(
                                onClick = {
                                    showForwardPicker = false
                                    messageToForward = null
                                }
                            ) {
                Text("‹")
                            }

                            Text(
                                text = "Forward message",
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleLarge
                            )
                        }

                        if (forwardUiState.loading) {

                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxSize(),
                                contentAlignment =
                                    Alignment.Center
                            ) {
                CircularProgressIndicator()
                            }

                        } else {

                            LazyColumn(
                                modifier =
                                    Modifier
                                        .fillMaxSize()
                            ) {

                                items(
                                    forwardUiState
                                        .conversations
                                        .filter {
                                            it.id !=
                                                selectedConversation?.id
                                        }
                                ) { conversation ->

                                    val displayName =
                                        if (conversation.type == "group") {
                                            conversation.title
                                                ?.takeIf { it.isNotBlank() }
                                                ?: "Group"
                                        } else {
                                            conversation
                                                .other_user_display_name
                                                .ifBlank {
                                                    conversation
                                                        .other_user_phone
                                                        ?: "Unknown"
                                                }
                                        }

                                    Row(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    messageToForward?.let { message ->
                                                        messageViewModel.forwardMessage(
                                                            messageId = message.id,
                                                            targetConversationId = conversation.id
                                                        )
                                                    }

                                                    showForwardPicker = false
                                                    messageToForward = null
                                                }
                                                .padding(
                                                    horizontal = 16.dp,
                                                    vertical = 14.dp
                                                ),
                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {

                                        Text(
                                            text = displayName,
                                            style =
                                                MaterialTheme
                                                    .typography
                                                    .bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                    }

                } else if (showAddContact) {
                    AddContactScreen(
                        contactName =
                            selectedConversation
                                ?.other_user_display_name
                                .orEmpty(),
                        contactPhone =
                            selectedConversation
                                ?.other_user_phone
                                .orEmpty(),
                        onBack = {
                            showAddContact = false
                        },
                        onSaved = { contact ->
                            selectedConversation =
                                selectedConversation?.copy(
                                    contact_id =
                                        contact.id,
                                    other_user_display_name =
                                        listOf(
                                            contact.first_name,
                                            contact.last_name
                                        )
                                            .filter {
                                                !it.isNullOrBlank()
                                            }
                                            .joinToString(" "),
                                    other_user_phone =
                                        listOf(
                                            contact.country_code,
                                            contact.phone
                                        )
                                            .filter {
                                                !it.isNullOrBlank()
                                            }
                                            .joinToString(" ")
                                )

                            showAddContact = false
                        }
                    )
                } else if (showEditContact) {

                    val contactId =
                        selectedConversation
                            ?.contact_id

                    if (!contactId.isNullOrBlank()) {

                        EditContactScreen(
                            contactId = contactId,
                            onBack = {
                                showEditContact = false
                            },
                            onSaved = { updatedContact ->

                                selectedConversation =
                                    selectedConversation?.copy(
                                        other_user_display_name =
                                            listOf(
                                                updatedContact.first_name,
                                                updatedContact.last_name
                                            )
                                                .filter {
                                                    !it.isNullOrBlank()
                                                }
                                                .joinToString(" "),
                                        other_user_phone =
                                            listOf(
                                                updatedContact.country_code,
                                                updatedContact.phone
                                            )
                                                .filter {
                                                    !it.isNullOrBlank()
                                                }
                                                .joinToString(" ")
                                    )

                                showEditContact = false
                            }
                        )

                    } else {

                        showEditContact = false

                    }

                } else if (showGroupInfo) {

                    GroupInfoScreen(
                        conversationId =
                            selectedConversation!!.id,
                        onBack = {
                            showGroupInfo = false
                        }
                    ,
                        onDeleted = {
                            showGroupInfo = false
                            selectedConversation = null
                        }
)

                } else if (showProfile) {

                    ProfileScreen(
                        onBack = {
                            showProfile = false
                        }
                    )

                } else if (selectedTab == 0) {

                    if (showNewChat) {

                        NewChatScreen(
                            onBack = {
                                showNewChat = false
                            },
                            onConversationCreated = {
                                showNewChat = false
                                selectedConversation = it
                            }
                        )

                    } else if (
                        selectedConversation == null
                    ) {

                        ConversationListScreen(
                            viewModel = conversationViewModel,
                            onConversationClick = {
                                selectedConversation = it
                            },
                            onNewChatClick = {
                                showNewChat = true
                            },
                            onProfileClick = {
                                showProfile = true
                            },
                            onMenuClick = {}
                        )

                    } else {

                        ChatScreen(
                            conversationId =
                                selectedConversation!!.id,
                            contactName =
                                selectedConversation!!
                                    .other_user_display_name,
                            contactPhone =
                                selectedConversation!!
                                    .other_user_phone,
                            contactId =
                                selectedConversation!!
                                    .contact_id,
                            otherUserId =
                                selectedConversation!!
                                    .other_user_id,
                            onlineUserIds =
                                conversationViewModel
                                    .uiState
                                    .collectAsState()
                                    .value
                                    .onlineUserIds,
                            lastSeenAt =
                                selectedConversation!!
                                    .other_user_last_seen_at,
                            conversationType =
                                selectedConversation!!
                                    .type,
                            onContactClick = {
                                if (
                                    selectedConversation!!
                                        .type == "group"
                                ) {
                                    showGroupInfo = true
                                } else if (
                                    !selectedConversation!!
                                        .contact_id
                                        .isNullOrBlank()
                                ) {
                                    showEditContact = true
                                } else {
                                    showAddContact = true
                                }
                            },
                            onForwardMessage = { message ->
                                messageToForward = message
                                showForwardPicker = true
                            },

                            onBack = {
                                selectedConversation = null
                            }
                        )
                    }

                } else {

                    val title =
                        when (selectedTab) {
                            1 -> "Updates"
                            2 -> "Communities"
                            else -> "Calls"
                        }

                    androidx.compose.foundation.layout.Box(
                        modifier =
                            Modifier.fillMaxSize(),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = title,
                            style =
                                MaterialTheme
                                    .typography
                                    .headlineMedium
                        )
                    }
                }
            }

            if (
                selectedConversation == null &&
                !showNewChat
            ) {

                androidx.compose.material3.NavigationBar {

                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            selectedConversation = null
                            showNewChat = false
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Chat,
                                contentDescription = "Chats"
                            )
                        },
                        label = {
                            Text(
                                text = "Chats",
                                fontSize = 12.sp
                            )
                        },
                        alwaysShowLabel = true
                    )

                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            selectedConversation = null
                            showNewChat = false
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Circle,
                                contentDescription = "Updates"
                            )
                        },
                        label = {
                            Text(
                                text = "Updates",
                                fontSize = 12.sp
                            )
                        },
                        alwaysShowLabel = true
                    )

                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = {
                            selectedTab = 2
                            selectedConversation = null
                            showNewChat = false
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Group,
                                contentDescription = "Communities"
                            )
                        },
                        label = {
                            Text(
                                text = "Communities",
                                fontSize = 12.sp
                            )
                        },
                        alwaysShowLabel = true
                    )

                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = {
                            selectedTab = 3
                            selectedConversation = null
                            showNewChat = false
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Call,
                                contentDescription = "Calls"
                            )
                        },
                        label = {
                            Text(
                                text = "Calls",
                                fontSize = 12.sp
                            )
                        },
                        alwaysShowLabel = true
                    )
                }
            }
        }
}

@Composable
fun ConversationListScreen(
    onConversationClick: (Conversation) -> Unit,
    onNewChatClick: () -> Unit,
    onProfileClick: () -> Unit,
    onMenuClick: () -> Unit,
    viewModel: ConversationViewModel = viewModel()
) {

    val context =
        androidx.compose.ui.platform.LocalContext.current

    val uiState by
        viewModel.uiState.collectAsState()

    var searchQuery by
        remember {
            mutableStateOf("")
        }

    var menuOpen by
        remember {
            mutableStateOf(false)
        }

    var selectedFilter by
        remember {
            mutableStateOf("All")
        }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadConversations()
    }

    val filteredConversations =
        uiState.conversations.filter { conversation ->

            val query =
                searchQuery.trim()

            query.isBlank() ||
                conversation
                    .other_user_display_name
                    .contains(
                        query,
                        ignoreCase = true
                    ) ||
                (
                    conversation.other_user_phone
                        ?: ""
                ).contains(query)

        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            androidx.compose.foundation.text.BasicTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                },
                modifier =
                    Modifier
                        .weight(1f)
                        .height(48.dp),
                singleLine = true,
                textStyle =
                    androidx.compose.material3.LocalTextStyle.current.copy(
                        fontSize = 14.sp
                    ),
                decorationBox = { innerTextField ->
                    androidx.compose.foundation.layout.Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .clip(
                                    androidx.compose.foundation.shape
                                        .RoundedCornerShape(28.dp)
                                )
                                .background(
                                    androidx.compose.material3.MaterialTheme
                                        .colorScheme.surface
                                )
                                .border(
                                    1.dp,
                                    androidx.compose.material3.MaterialTheme
                                        .colorScheme.outline,
                                    androidx.compose.foundation.shape
                                        .RoundedCornerShape(28.dp)
                                )
                                .padding(
                                    horizontal = 14.dp
                                )
                    ) {
                        androidx.compose.foundation.layout.Row(
                            modifier =
                                Modifier.fillMaxSize(),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⌕",
                                fontSize = 18.sp,
                                modifier =
                                    Modifier.padding(
                                        end = 8.dp
                                    )
                            )

                            androidx.compose.foundation.layout.Box(
                                modifier =
                                    Modifier.weight(1f)
                            ) {
                                if (searchQuery.isBlank()) {
                                    Text(
                                        text = "Search",
                                        fontSize = 14.sp,
                                        color =
                                            androidx.compose.material3.MaterialTheme
                                                .colorScheme.onSurfaceVariant
                                    )
                                }

                                innerTextField()
                            }
                        }
                    }
                }
            )
            androidx.compose.foundation.layout.Box {

                androidx.compose.material3.IconButton(
                    onClick = {
                        menuOpen = true
                    }
                ) {
                    Text(
                        text = "⋮",
                        style =
                            MaterialTheme
                                .typography
                                .headlineSmall
                    )
                }

                DropdownMenu(
                    expanded = menuOpen,
                    onDismissRequest = {
                        menuOpen = false
                    }
                ) {

                    DropdownMenuItem(
                        text = {
                            Text("Profile")
                        },
                        onClick = {
                            menuOpen = false
                            onProfileClick()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Settings")
                        },
                        onClick = {
                            menuOpen = false
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Privacy")
                        },
                        onClick = {
                            menuOpen = false
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Notifications")
                        },
                        onClick = {
                            menuOpen = false
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Help")
                        },
                        onClick = {
                            menuOpen = false
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Logout")
                        },
                        onClick = {
                            menuOpen = false

                            SessionManager(context)
                                .clearSession()

                            (context as? MainActivity)
                                ?.recreate()
                        }
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        androidx.compose.foundation
                            .rememberScrollState()
                    )
                    .padding(
                        horizontal = 16.dp
                    ),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            listOf(
                "All",
                "Unread",
                "Favorites",
                "Groups"
            ).forEach { label ->

                androidx.compose.material3.Surface(
                    modifier =
                        Modifier.clickable {
                            selectedFilter = label
                        },
                    shape =
                        androidx.compose.foundation.shape
                            .RoundedCornerShape(22.dp),
                    tonalElevation =
                        if (label == selectedFilter) 2.dp
                        else 0.dp
                ) {

                    Text(
                        text = label,
                        modifier =
                            Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 9.dp
                            )
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        androidx.compose.material3.HorizontalDivider()

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(
                        horizontal = 20.dp,
                        vertical = 15.dp
                    ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = "▣",
                style =
                    MaterialTheme
                        .typography
                        .titleLarge
            )

            Text(
                text = "Archived",
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                modifier =
                    Modifier.padding(
                        start = 18.dp
                    )
            )
        }

        when {

            uiState.loading -> {

                androidx.compose.foundation.layout.Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    contentAlignment =
                        Alignment.Center
                ) {

                    androidx.compose.material3
                        .CircularProgressIndicator()
                }
            }

            uiState.message.isNotBlank() -> {

                androidx.compose.foundation.layout.Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            uiState.message
                    )
                }
            }

            filteredConversations.isEmpty() -> {

                androidx.compose.foundation.layout.Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            if (searchQuery.isBlank())
                                "No conversations yet"
                            else
                                "No chats found"
                    )
                }
            }

            else -> {

                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                ) {

                    items(
                        filteredConversations
                    ) { conversation ->

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onConversationClick(
                                            conversation
                                        )
                                    }
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 11.dp
                                    ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            if (


                                conversation.type == "direct" &&


                                conversation.other_user_id.isNotBlank()


                            ) {


                            


                                UserAvatar(


                                    userId =


                                        conversation.other_user_id,


                                    displayName =


                                        conversation


                                            .other_user_display_name


                                            .orEmpty(),


                                    size = 54.dp


                                )


                            


                            } else {


                            


                                androidx.compose.material3.Surface(


                                    modifier =


                                        Modifier.size(54.dp),


                                    shape =


                                        androidx.compose.foundation


                                            .shape


                                            .CircleShape


                                ) {


                                    androidx.compose.foundation.layout


                                        .Box(


                                            contentAlignment =


                                                Alignment.Center


                                        ) {


                                        Text(


                                            text =


                                                conversation


                                                    .other_user_display_name


                                                    .orEmpty()


                                                    .take(1)


                                                    .uppercase(),


                                            style =


                                                MaterialTheme


                                                    .typography


                                                    .titleLarge


                                        )


                                    }


                                }


                            }

                            Column(
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .padding(
                                            start = 14.dp
                                        )
                            ) {

                                Text(
                                    text =
                                        conversation
                                            .other_user_display_name
                                            .orEmpty(),
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium
                                )

                                Text(
                                    text =
                                        conversation
                                            .other_user_phone


                                            ?: "",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyMedium,
                                    maxLines = 1
                                )
                            }
                        }

                        androidx.compose.material3.HorizontalDivider(
                            modifier =
                                Modifier.padding(
                                    start = 84.dp
                                )
                        )
                    }
                }
            }
        }

        androidx.compose.foundation.layout.Box(
            modifier =
                Modifier.fillMaxWidth()
        ) {
            androidx.compose.material3.FloatingActionButton(
                onClick = onNewChatClick,
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        )
            ) {
                Text(
                    text = "＋"
                )
            }
        }
    }
}


@Composable
fun UserAvatar(
    userId: String,
    displayName: String,
    size: androidx.compose.ui.unit.Dp
) {

    val context =
        androidx.compose.ui.platform.LocalContext.current

    var avatarBytes by
        remember(userId) {
            mutableStateOf(
                AvatarCache.read(
                    context,
                    userId
                )
            )
        }

    androidx.compose.runtime.LaunchedEffect(
        userId
    ) {

        if (avatarBytes != null) {
            return@LaunchedEffect
        }

        val token =
            SessionManager(context)
                .getToken()

        if (token.isNullOrBlank()) {
            return@LaunchedEffect
        }

        try {

            val response =
                com.chatflow.app.data.UserRepository()
                    .downloadAvatar(
                        userId = userId,
                        token = token
                    )

            val bytes =
                response.bytes()

            AvatarCache.write(
                context = context,
                userId = userId,
                bytes = bytes
            )

            avatarBytes =
                bytes

        } catch (error: Exception) {

            avatarBytes = null
        }
    }

    androidx.compose.material3.Surface(
        modifier =
            Modifier.size(size),
        shape =
            androidx.compose.foundation.shape
                .CircleShape
    ) {

        androidx.compose.foundation.layout.Box(
            modifier =
                Modifier.fillMaxSize(),
            contentAlignment =
                Alignment.Center
        ) {

            val bytes =
                avatarBytes

            val bitmap =
                remember(bytes) {
                    bytes?.let {
                        BitmapFactory.decodeByteArray(
                            it,
                            0,
                            it.size
                        )
                    }
                }

            if (bitmap != null) {

                androidx.compose.foundation.Image(
                    bitmap =
                        bitmap.asImageBitmap(),
                    contentDescription =
                        "Profile photo",
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clip(
                                androidx.compose.foundation
                                    .shape
                                    .CircleShape
                            ),
                    contentScale =
                        ContentScale.Crop
                )

            } else {

                Text(
                    text =
                        displayName
                            .firstOrNull()
                            ?.uppercase()
                            ?: "?"
                )
            }
        }
    }
}

@Composable
fun NewGroupScreen(

    onBack: () -> Unit,

    onConversationCreated:
        (Conversation) -> Unit,

    users:
        List<com.chatflow.app.data.User>,

    viewModel: NewChatViewModel

) {

    val context =
        androidx.compose.ui.platform.LocalContext.current

    val token =
        SessionManager(context).getToken()

    val uiState by
        viewModel.uiState.collectAsState()

    val contactRepository =
        remember {
            com.chatflow.app.data.ContactRepository()
        }

    var contacts by
        remember {
            mutableStateOf<
                List<com.chatflow.app.data.Contact>
            >(emptyList())
        }

    LaunchedEffect(token) {
        if (!token.isNullOrBlank()) {
            try {
                contacts =
                    contactRepository
                        .getContacts(token)
                        .contacts
            } catch (
                error: Exception
            ) {
                contacts =
                    emptyList()
            }
        }
    }

    val contactUserIds =
        contacts
            .mapNotNull {
                it.linked_user_id
            }
            .toSet()

    val contactUsers =
        users.filter { user ->
            contactUserIds.contains(
                user.id
            )
        }

    var groupTitle by
        remember {
            mutableStateOf("")
        }

    var selectedUserIds by
        remember {
            mutableStateOf(
                emptySet<String>()
            )
        }

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 16.dp
                )

    ) {

        Row(

            modifier =
                Modifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            androidx.compose.material3.IconButton(

                onClick = onBack

            ) {

                Icon(

                    imageVector =
                        Icons.Filled.ArrowBack,

                    contentDescription =
                        "Back"

                )

            }

            Text(

                text = "New Group",

                style =
                    MaterialTheme
                        .typography
                        .headlineSmall,

                modifier =
                    Modifier.padding(
                        start = 4.dp
                    )

            )

        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        OutlinedTextField(

            value = groupTitle,

            onValueChange = {
                groupTitle = it
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text("Group name")
            },

            singleLine = true

        )

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        Text(

            text =
                "Select members (${selectedUserIds.size})",

            style =
                MaterialTheme
                    .typography
                    .titleMedium

        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        if (contactUsers.isEmpty()) {

            Box(
                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center

            ) {

                Text(
                    text = "No users available"
                )

            }

        } else {

            LazyColumn(

                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),

                verticalArrangement =
                    Arrangement.spacedBy(
                        4.dp
                    )

            ) {

                items(
                    contactUsers,
                    key = {
                        it.id
                    }
                ) { user ->

                    val selected =
                        selectedUserIds
                            .contains(user.id)

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable {

                                    selectedUserIds =
                                        if (selected) {

                                            selectedUserIds -
                                                user.id

                                        } else {

                                            selectedUserIds +
                                                user.id

                                        }

                                }
                                .padding(
                                    vertical = 12.dp,
                                    horizontal = 8.dp
                                ),

                        verticalAlignment =
                            Alignment.CenterVertically

                    ) {

                        androidx.compose.material3.Checkbox(

                            checked = selected,

                            onCheckedChange = {
                                selectedUserIds =
                                    if (it) {

                                        selectedUserIds +
                                            user.id

                                    } else {

                                        selectedUserIds -
                                            user.id

                                    }
                            }

                        )

                        val contact =
                            contacts.firstOrNull {
                                it.linked_user_id == user.id
                            }

                        val contactName =
                            listOfNotNull(
                                contact?.first_name,
                                contact?.last_name
                            )
                                .joinToString(" ")
                                .ifBlank {
                                    user.display_name
                                }

                        Text(

                            text =
                                contactName,

                            modifier =
                                Modifier.padding(
                                    start = 8.dp
                                )

                        )

                    }

                }

            }

        }

        if (uiState.message.isNotBlank()) {

            Text(

                text = uiState.message,

                modifier =
                    Modifier.padding(
                        vertical = 8.dp
                    )

            )

        }

        Button(

            onClick = {

                if (!token.isNullOrBlank()) {

                    viewModel.createGroupConversation(

                        token = token,

                        title = groupTitle.trim(),

                        memberUserIds =
                            selectedUserIds.toList()

                    ) { conversation ->

                        onConversationCreated(

                            conversation.copy(

                                title =
                                    groupTitle.trim(),

                                other_user_id = "",

                                other_user_display_name =
                                    groupTitle.trim()

                            )

                        )

                    }

                }

            },

            enabled =
                groupTitle.trim().isNotBlank() &&
                    selectedUserIds.isNotEmpty() &&
                    !uiState.creating,

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 12.dp
                    )

        ) {

            if (uiState.creating) {

                CircularProgressIndicator()

            } else {

                Text(
                    text = "Create Group"
                )

            }

        }

    }

}

@Composable
fun NewChatScreen(
    onBack: () -> Unit,
    onConversationCreated: (Conversation) -> Unit,
    viewModel: NewChatViewModel = viewModel()
) {

    val context =
        androidx.compose.ui.platform.LocalContext.current

    val sessionManager =
        SessionManager(context)

    val token =
        sessionManager.getToken()

    val uiState by
        viewModel.uiState.collectAsState()

    var showNewContact by
        remember {
            mutableStateOf(false)
        }

    var showNewGroup by
        remember {
            mutableStateOf(false)
        }

    androidx.compose.runtime.LaunchedEffect(
        token
    ) {

        if (!token.isNullOrBlank()) {
            viewModel.loadUsers(token)
            viewModel.loadContacts(token)
        }
    }

    if (showNewGroup) {

        NewGroupScreen(

            onBack = {
                showNewGroup = false
            },

            onConversationCreated = { conversation ->

                showNewGroup = false

                onConversationCreated(
                    conversation
                )

            },

            users = uiState.users,

            viewModel = viewModel

        )

        return

    }

    if (showNewContact) {
        NewContactScreen(
            onBack = {
                showNewContact = false
            },
            onSave = { firstName, lastName, countryCode, phone ->

                if (!token.isNullOrBlank()) {

                    viewModel.createContact(
                        token = token,
                        request =
                            com.chatflow.app.data.CreateContactRequest(
                                firstName = firstName,
                                lastName =
                                    lastName.ifBlank {
                                        null
                                    },
                                countryCode =
                                    countryCode,
                                phone = phone
                            ),
                        onSuccess = {
                            showNewContact = false
                            viewModel.loadContacts(token)
                        }
                    )
                }
            }
        )
        return
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 16.dp
                )
    ) {

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            androidx.compose.material3.IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector =
                        Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Select contact",
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall,
                modifier =
                    Modifier.padding(
                        start = 4.dp
                    )
            )
        }

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        androidx.compose.material3.Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable {
                    showNewGroup = true
                },
            shape =
                androidx.compose.foundation.shape
                    .RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.GroupAdd,
                    contentDescription = "New Group"
                )

                Text(
                    text = "New Group",
                    modifier =
                        Modifier.padding(
                            start = 16.dp
                        ),
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        androidx.compose.material3.Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable {
                    showNewContact = true
                },
            shape =
                androidx.compose.foundation.shape
                    .RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.PersonAdd,
                    contentDescription = "New Contact"
                )

                Text(
                    text = "New Contact",
                    modifier =
                        Modifier.padding(
                            start = 16.dp
                        ),
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        Text(
            text = "Contacts",
            style =
                MaterialTheme
                    .typography
                    .titleMedium
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        when {

            uiState.loading -> {

                androidx.compose.foundation.layout.Box(
                    modifier =
                        Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
                ) {

                    androidx.compose.material3.CircularProgressIndicator()
                }
            }

            uiState.message.isNotBlank() -> {

                androidx.compose.foundation.layout.Box(
                    modifier =
                        Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = uiState.message
                    )
                }
            }

            uiState.users.isEmpty() -> {

                androidx.compose.foundation.layout.Box(
                    modifier =
                        Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = "No users available"
                    )
                }
            }

            else -> {

                LazyColumn(
                    modifier =
                        Modifier.fillMaxSize()
                ) {

                    items(
                        uiState.contacts
                    ) { contact ->

                        val contactName =
                            listOfNotNull(
                                contact.first_name.takeIf {
                                    it.isNotBlank()
                                },
                                contact.last_name?.takeIf {
                                    it.isNotBlank()
                                }
                            ).joinToString(" ")

                        Column(

                            modifier =

                                Modifier

                                    .fillMaxWidth()

                                    .clickable(
                                        enabled =
                                            !contact.linked_user_id
                                                .isNullOrBlank()
                                    ) {

                                        val linkedUserId =
                                            contact.linked_user_id

                                        if (
                                            !token.isNullOrBlank() &&
                                            !linkedUserId.isNullOrBlank()
                                        ) {

                                            viewModel
                                                .createConversation(

                                                    token = token,

                                                    userId =
                                                        linkedUserId

                                                ) { conversation ->

                                                onConversationCreated(
                                                    conversation.copy(
                                                        title =
                                                            contactName,
                                                        other_user_phone =
                                                            contact.phone,
                                                        other_user_display_name =
                                                            contactName
                                                    )
                                                )
                                            }

                                        }

                                    }

                                    .padding(

                                        vertical = 14.dp

                                    )

                        ) {

                            Text(

                                text =
                                    contactName.ifBlank {
                                        contact.phone
                                    },

                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium

                            )

                            Text(

                                text =
                                    contact.country_code +
                                        " " +
                                        contact.phone,

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium

                            )

                        }

                    }
                    }
                }
            }
        }
    }

private fun formatLastSeen(lastSeenAt: String?): String {
    if (lastSeenAt.isNullOrBlank()) {
        return "Offline"
    }

    return try {
        val instant =
            java.time.Instant.parse(lastSeenAt)

        val zoneId =
            java.time.ZoneId.systemDefault()

        val lastSeen =
            instant.atZone(zoneId)

        val now =
            java.time.ZonedDateTime.now(zoneId)

        if (
            lastSeen.toLocalDate() ==
                now.toLocalDate()
        ) {
            lastSeen.format(
                java.time.format.DateTimeFormatter
                    .ofPattern("HH:mm")
            )
        } else {
            lastSeen.format(
                java.time.format.DateTimeFormatter
                    .ofPattern("dd.MM.yyyy")
            )
        }
    } catch (error: Exception) {
        lastSeenAt
    }
}

@Composable
fun ChatScreen(
    conversationId: String,
    contactName: String,
    contactPhone: String?,
    contactId: String?,
    otherUserId: String,
    onlineUserIds: Set<String>,
    lastSeenAt: String?,
    conversationType: String,
    onContactClick: () -> Unit,
    onBack: () -> Unit,
    onForwardMessage: (Message) -> Unit,
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
    val context = LocalContext.current

    var selectedMessageIds by remember {
        mutableStateOf<Set<String>>(emptySet())
    }
    var selectedMessage by remember {
        mutableStateOf<Message?>(null)
    }
    var selectionMoreExpanded by remember {
        mutableStateOf(false)
    }
    var showMessageInfo by remember {
        mutableStateOf(false)
    }
    var replyingToMessage by remember {
        mutableStateOf<Message?>(null)
    }
    var editingMessage by remember {
        mutableStateOf<Message?>(null)
    }
    var isTyping by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(
        messageText,
        isTyping
    ) {
        if (!isTyping || messageText.isBlank()) {
            return@LaunchedEffect
        }

        delay(1500)

        if (messageText.isNotBlank()) {
            viewModel.stopTyping(
                conversationId
            )
            isTyping = false
        }
    }

    var chatHeaderMenuExpanded by remember {
        mutableStateOf(false)
    }

    var chatNotificationsMuted by remember {
        mutableStateOf(false)
    }

    var expiryMenuExpanded by remember {
        mutableStateOf(false)
    }
    var selectedExpirySeconds by remember {
        mutableStateOf<Long?>(null)
    }

    var attachmentMenuExpanded by remember {
        mutableStateOf(false)
    }

    var pendingAttachment by remember {
        mutableStateOf<PendingAttachment?>(null)
    }

    val galleryPicker =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .GetContent()
        ) { uri ->
            if (uri != null) {
                pendingAttachment =
                    PendingAttachment(
                        uri = uri.toString(),
                        mimeType = "image/*",
                        displayName =
                            uri.lastPathSegment
                                ?: "Image",
                        kind = AttachmentKind.GALLERY
                    )
            }
        }

    val documentPicker =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .GetContent()
        ) { uri ->
            if (uri != null) {
                pendingAttachment =
                    PendingAttachment(
                        uri = uri.toString(),
                        mimeType = "application/octet-stream",
                        displayName =
                            uri.lastPathSegment
                                ?: "Document",
                        kind = AttachmentKind.DOCUMENT
                    )
            }
        }

    val audioPicker =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .GetContent()
        ) { uri ->
            if (uri != null) {
                pendingAttachment =
                    PendingAttachment(
                        uri = uri.toString(),
                        mimeType = "audio/*",
                        displayName =
                            uri.lastPathSegment
                                ?: "Audio",
                        kind = AttachmentKind.AUDIO
                    )
            }
        }

    androidx.compose.runtime.LaunchedEffect(
        conversationId
    ) {
        viewModel.loadMessages(
            conversationId
        )
    }

    androidx.activity.compose.BackHandler(
        enabled = selectedMessageIds.isNotEmpty()
    ) {
        selectedMessageIds = emptySet()
        selectedMessage = null
        selectionMoreExpanded = false
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .imePadding()
    ) {

        if (selectedMessageIds.isNotEmpty()) {
            androidx.compose.foundation.layout.Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                androidx.compose.material3.TextButton(
                    onClick = {
                        selectedMessageIds = emptySet()
                        selectedMessage = null
                        selectionMoreExpanded = false
                    }
                ) {
                    Text(
                        text = "‹",
                        style =
                            MaterialTheme
                                .typography
                                .headlineMedium
                    )
                }

                Text(
                    text = selectedMessageIds.size.toString(),
                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,
                    modifier =
                        Modifier.padding(
                            horizontal = 8.dp
                        )
                )

                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )

                androidx.compose.material3.TextButton(
                    onClick = {
                        selectedMessage?.let {
                            replyingToMessage = it
                        }
                        selectedMessageIds = emptySet()
                        selectedMessage = null
                    },
                    enabled = selectedMessage != null
                ) {
                    Icon(
                        imageVector = Icons.Filled.Reply,
                        contentDescription = "Reply"
                    )
                }

                androidx.compose.material3.TextButton(
                    onClick = {
                        selectedMessage?.let {
                            onForwardMessage(it)
                        }
                        selectedMessageIds = emptySet()
                        selectedMessage = null
                    },
                    enabled = selectedMessage != null
                ) {
                    Icon(
                        imageVector = Icons.Filled.Forward,
                        contentDescription = "Forward"
                    )
                }

                androidx.compose.material3.TextButton(
                    onClick = {
                        selectedMessage?.let {
                            viewModel.deleteMessage(it.id)
                        }
                        selectedMessageIds = emptySet()
                        selectedMessage = null
                    },
                    enabled =
                        selectedMessage != null &&
                            selectedMessage?.sender_id == currentUserId &&
                            selectedMessage?.deleted_at == null
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete"
                    )
                }

                Box {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            selectionMoreExpanded = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More"
                        )
                    }

                    DropdownMenu(
                        expanded = selectionMoreExpanded,
                        onDismissRequest = {
                            selectionMoreExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text("Message info")
                            },
                            onClick = {
                                selectionMoreExpanded = false
                                showMessageInfo = true
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text("Copy")
                            },
                            onClick = {
                                selectedMessage?.content?.let { content ->
                                    val clipboard =
                                        context.getSystemService(
                                            Context.CLIPBOARD_SERVICE
                                        ) as ClipboardManager

                                    clipboard.setPrimaryClip(
                                        android.content.ClipData.newPlainText(
                                            "Message",
                                            content
                                        )
                                    )
                                }

                                selectionMoreExpanded = false
                                selectedMessageIds = emptySet()
                                selectedMessage = null
                            }
                        )

                        if (
                            selectedMessage?.sender_id == currentUserId &&
                            selectedMessage?.deleted_at == null
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text("Edit")
                                },
                                onClick = {
                                    editingMessage =
                                        selectedMessage
                                    messageText =
                                        selectedMessage?.content.orEmpty()
                                    selectionMoreExpanded = false
                                    selectedMessageIds = emptySet()
                                    selectedMessage = null
                                }
                            )
                        }

                        DropdownMenuItem(
                            text = {
                                Text("Pin")
                            },
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Pin support is not connected yet",
                                    Toast.LENGTH_SHORT
                                ).show()
                                selectionMoreExpanded = false
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text("Translate")
                            },
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Translation service is not connected yet",
                                    Toast.LENGTH_SHORT
                                ).show()
                                selectionMoreExpanded = false
                            }
                        )
                    }
                }
            }
        } else {
            androidx.compose.foundation.layout.Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(
                            start = 12.dp,
                            top = 0.dp,
                            end = 12.dp,
                            bottom = 6.dp
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

                UserAvatar(
                    userId = otherUserId,
                    displayName = contactName,
                    size = 40.dp
                )

                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(start = 10.dp)
                            .clickable {
                                onContactClick()
                            }
                ) {
                    Text(
                        text = contactName,
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    if (conversationType == "direct") {
                        Text(
                            text =
                                if (
                                    uiState.typingUserIds.isNotEmpty()
                                ) {
                                    "Typing..."
                                } else if (
                                    onlineUserIds.contains(
                                        otherUserId
                                    )
                                ) {
                                    "Online"
                                } else if (
                                    !lastSeenAt.isNullOrBlank()
                                ) {
                                    "Last seen " +
                                        formatLastSeen(
                                            lastSeenAt
                                        )
                                } else {
                                    "Offline"
                                },
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,
                            color =
                                if (
                                    uiState.typingUserIds.isNotEmpty()
                                ) {
                                    MaterialTheme
                                        .colorScheme
                                        .primary
                                } else {
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant
                                }
                        )
                    } else {
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
                }

                Box {
                    androidx.compose.material3.IconButton(
                        onClick = {
                            chatHeaderMenuExpanded = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Chat menu"
                        )
                    }

                    DropdownMenu(
                        expanded = chatHeaderMenuExpanded,
                        onDismissRequest = {
                            chatHeaderMenuExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (conversationType == "group") {
                                        "Group info"
                                    } else {
                                        "Contact info"
                                    }
                                )
                            },
                            onClick = {
                                chatHeaderMenuExpanded = false
                                onContactClick()
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text("Search")
                            },
                            onClick = {
                                chatHeaderMenuExpanded = false
                                Toast.makeText(
                                    context,
                                    "Message search will be connected next",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (chatNotificationsMuted) {
                                        "Unmute notifications"
                                    } else {
                                        "Mute notifications"
                                    }
                                )
                            },
                            onClick = {
                                chatNotificationsMuted =
                                    !chatNotificationsMuted

                                chatHeaderMenuExpanded = false

                                Toast.makeText(
                                    context,
                                    if (chatNotificationsMuted) {
                                        "Notifications muted"
                                    } else {
                                        "Notifications unmuted"
                                    },
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text("Disappearing messages")
                            },
                            onClick = {
                                chatHeaderMenuExpanded = false
                                expiryMenuExpanded = true
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text("Clear chat")
                            },
                            onClick = {
                                chatHeaderMenuExpanded = false

                                viewModel.clearChat(
                                    conversationId = conversationId,
                                    onSuccess = {
                                        Toast.makeText(
                                            context,
                                            "Chat cleared",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onError = { message ->
                                        Toast.makeText(
                                            context,
                                            message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text("Block")
                            },
                            onClick = {
                                chatHeaderMenuExpanded = false
                                Toast.makeText(
                                    context,
                                    "Block will be connected to server",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text("Report")
                            },
                            onClick = {
                                chatHeaderMenuExpanded = false
                                Toast.makeText(
                                    context,
                                    "Report will be connected to server",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }
        }

        androidx.compose.material3.HorizontalDivider()

        if (showMessageInfo && selectedMessage != null) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = {
                    showMessageInfo = false
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            showMessageInfo = false
                        }
                    ) {
                        Text("Close")
                    }
                },
                title = {
                    Text("Message info")
                },
                text = {
                    Column {
                        Text(
                            text =
                                "Sent: " +
                                    selectedMessage!!
                                        .created_at
                                        .replace("T", " ")
                                        .take(16)
                        )

                        if (
                            selectedMessage!!
                                .sender_id ==
                                currentUserId
                        ) {
                            Text(
                                text = "Status: " +
                                    when {
                                        uiState.messageReceipts[
                                            selectedMessage!!.id
                                        ]?.read_at != null ->
                                            "Read"

                                        uiState.messageReceipts[
                                            selectedMessage!!.id
                                        ]?.delivered_at != null ->
                                            "Delivered"

                                        else ->
                                            "Sent"
                                    }
                            )
                        }
                    }
                }
            )
        }

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

            val messageListState =
                androidx.compose.foundation.lazy
                    .rememberLazyListState()

            var initialMessageCount by
                remember(conversationId) {
                    mutableStateOf<Int?>(null)
                }

            androidx.compose.runtime.LaunchedEffect(
                uiState.loading,
                uiState.messages.size
            ) {
                if (
                    !uiState.loading &&
                    uiState.messages.isNotEmpty()
                ) {
                    if (initialMessageCount == null) {
                        initialMessageCount =
                            uiState.messages.size

                        messageListState.scrollToItem(
                            uiState.messages.lastIndex
                        )
                    } else if (
                        uiState.messages.size >
                            initialMessageCount!!
                    ) {
                        initialMessageCount =
                            uiState.messages.size

                        messageListState.animateScrollToItem(
                            uiState.messages.lastIndex
                        )
                    }
                }
            }

            LazyColumn(
                state = messageListState,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
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

                        Column(
                            horizontalAlignment =
                                if (isMine) {
                                    Alignment.End
                                } else {
                                    Alignment.Start
                                }
                        ) {

                            if (selectedMessage?.id == message.id) {
                                Card(
                                    modifier =
                                        Modifier
                                            .padding(
                                                horizontal = 4.dp,
                                                vertical = 2.dp
                                            )
                                            .offset(
                                                y = (-4).dp
                                            )
                                            .zIndex(2f),
                                    shape =
                                        RoundedCornerShape(24.dp),
                                    colors =
                                        CardDefaults.cardColors(
                                            containerColor =
                                                MaterialTheme
                                                    .colorScheme
                                                    .surface
                                        ),
                                    elevation =
                                        CardDefaults.cardElevation(
                                            defaultElevation = 6.dp
                                        )
                                ) {
                                    Row(
                                        modifier =
                                            Modifier.padding(
                                                horizontal = 6.dp,
                                                vertical = 4.dp
                                            ),
                                        horizontalArrangement =
                                            Arrangement.spacedBy(1.dp),
                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {
                                        listOf(
                                            "❤️",
                                            "😂",
                                            "😮",
                                            "😢",
                                            "🙏",
                                            "👍"
                                        ).forEach { reaction ->
                                            Text(
                                                text = reaction,
                                                fontSize = 24.sp,
                                                modifier =
                                                    Modifier
                                                        .clickable {
                                                            viewModel.reactToMessage(
                                                                message.id,
                                                                reaction
                                                            )
                                                            selectedMessageIds =
                                                                emptySet()
                                                            selectedMessage =
                                                                null
                                                            selectionMoreExpanded =
                                                                false
                                                        }
                                                        .padding(
                                                            horizontal = 4.dp,
                                                            vertical = 2.dp
                                                        )
                                            )
                                        }
                                    }
                                }
                            }

                        Card(
                            modifier =
                                Modifier
                                    .widthIn(
                                        max = 320.dp
                                    )
                                    .pointerInput(message.id) {
                                        var totalDrag = 0f

                                        detectHorizontalDragGestures(
                                            onHorizontalDrag = { _, dragAmount ->
                                                if (dragAmount > 0f) {
                                                    totalDrag += dragAmount
                                                }
                                            },
                                            onDragEnd = {
                                                if (
                                                    totalDrag >= 80f &&
                                                    selectedMessageIds.isEmpty()
                                                ) {
                                                    replyingToMessage =
                                                        message
                                                }
                                            }
                                        )
                                    }
                                    .combinedClickable(
                                        onClick = {
                                            if (
                                                selectedMessageIds.isNotEmpty()
                                            ) {
                                                selectedMessageIds =
                                                    if (
                                                        selectedMessageIds
                                                            .contains(
                                                                message.id
                                                            )
                                                    ) {
                                                        selectedMessageIds -
                                                            message.id
                                                    } else {
                                                        selectedMessageIds +
                                                            message.id
                                                    }

                                                selectedMessage =
                                                    selectedMessageIds
                                                        .firstOrNull()
                                                        ?.let { id ->
                                                            uiState.messages
                                                                .firstOrNull {
                                                                    it.id == id
                                                                }
                                                        }
                                            }
                                        },
                                        onLongClick = {
                                            selectedMessageIds =
                                                selectedMessageIds +
                                                    message.id

                                            selectedMessage =
                                                message
                                        }
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
                                val repliedMessage =
                                    message.reply_to_message_id?.let { replyId ->
                                        uiState.messages.firstOrNull { originalMessage ->
                                            originalMessage.id == replyId
                                        }
                                    }

                                if (repliedMessage != null) {
                                    Card(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(
                                                    bottom = 6.dp
                                                ),
                                        colors =
                                            CardDefaults.cardColors(
                                                containerColor =
                                                    MaterialTheme
                                                        .colorScheme
                                                        .surface
                                            ),
                                        shape =
                                            RoundedCornerShape(
                                                8.dp
                                            )
                                    ) {
                                        Column(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        8.dp
                                                    )
                                        ) {
                                            Text(
                                                text = "↩ Reply",
                                                style =
                                                    MaterialTheme
                                                        .typography
                                                        .labelSmall
                                            )
                                            Text(
                                                text =
                                                    if (
                                                        repliedMessage.deleted_at != null
                                                    ) {
                                                        "This message was deleted"
                                                    } else {
                                                        repliedMessage.content
                                                            ?: ""
                                                    },
                                                maxLines = 2,
                                                overflow =
                                                    androidx.compose.ui.text.style
                                                        .TextOverflow.Ellipsis,
                                                style =
                                                    MaterialTheme
                                                        .typography
                                                        .bodySmall
                                            )
                                        }
                                    }
                                }

                                    if (message.forwarded_from_message_id != null) {
                                        Text(
                                            text = "↗ Forwarded",
                                            style =
                                                MaterialTheme
                                                    .typography
                                                    .labelSmall
                                        )
                                    }

                                Text(
                                    text =
                                        if (
                                            message.deleted_at != null
                                        ) {
                                            "This message was deleted"
                                        } else {
                                            message.content
                                                ?: ""
                                        },
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyLarge
                                )


                                message.attachments.forEach { attachment ->

                                    Card(

                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(
                                                    bottom = 6.dp
                                                )
                                                .clickable {

                                                    viewModel
                                                        .downloadAttachment(
                                                            attachment
                                                        )

                                                },

                                        colors =
                                            CardDefaults.cardColors(

                                                containerColor =
                                                    MaterialTheme
                                                        .colorScheme
                                                        .surface

                                            ),

                                        shape =
                                            RoundedCornerShape(
                                                8.dp
                                            )

                                    ) {

                                        Row(

                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        10.dp
                                                    ),

                                            verticalAlignment =
                                                Alignment.CenterVertically

                                        ) {

                                            Text(
                                                text = "📄",
                                                fontSize = 24.sp
                                            )

                                            Spacer(
                                                modifier =
                                                    Modifier.width(
                                                        10.dp
                                                    )
                                            )

                                            Column(
                                                modifier =
                                                    Modifier.weight(
                                                        1f
                                                    )
                                            ) {

                                                Text(
                                                    text =
                                                        attachment.original_name,
                                                    style =
                                                        MaterialTheme
                                                            .typography
                                                            .bodyMedium
                                                )

                                                Text(
                                                    text =
                                                        "${attachment.file_size} bytes",
                                                    style =
                                                        MaterialTheme
                                                            .typography
                                                            .labelSmall
                                                )

                                            }

                                        }

                                    }

                                }

                                Spacer(
                                    modifier =
                                        Modifier.height(3.dp)
                                )

                                val messageReactions =
                                    uiState.messageReactions[
                                        message.id
                                    ].orEmpty()

                                androidx.compose.foundation.layout.Row(
                                    modifier =
                                        Modifier.fillMaxWidth(),
                                    horizontalArrangement =
                                        Arrangement.End,
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {
                                    messageReactions
                                        .groupBy {
                                            it.reaction
                                        }
                                        .forEach { entry ->
                                            androidx.compose.material3.Text(
                                                text =
                                                    entry.key +
                                                        if (entry.value.size > 1) {
                                                            " ${entry.value.size}"
                                                        } else {
                                                            ""
                                                        },
                                                modifier =
                                                    Modifier
                                                        .padding(
                                                            horizontal = 3.dp
                                                        )
                                                        .clickable {
                                                            val ownReaction =
                                                                entry.value.any {
                                                                    it.user_id ==
                                                                        currentUserId
                                                                }

                                                            if (ownReaction) {
                                                                viewModel.removeMessageReaction(
                                                                    message.id
                                                                )
                                                            } else {
                                                                viewModel.reactToMessage(
                                                                    message.id,
                                                                    entry.key
                                                                )
                                                            }
                                                        },
                                                style =
                                                    MaterialTheme
                                                        .typography
                                                        .labelMedium
                                            )
                                        }
                                }

                                var remainingSeconds by remember(
                                    message.id,
                                    message.expires_at
                                ) {
                                    mutableStateOf(
                                        remainingMessageSeconds(
                                            message.expires_at
                                        )
                                    )
                                }

                                if (
                                    isMine &&
                                    !message.expires_at.isNullOrBlank()
                                ) {
                                    LaunchedEffect(
                                        message.id,
                                        message.expires_at
                                    ) {
                                        while (true) {
                                            val seconds =
                                                remainingMessageSeconds(
                                                    message.expires_at
                                                )

                                            remainingSeconds =
                                                seconds

                                            if (seconds <= 0L) {
                                                break
                                            }

                                            delay(1000L)
                                        }
                                    }
                                } else {
                                    remainingSeconds = 0L
                                }

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

                                    if (
                                        isMine &&
                                        remainingSeconds > 0L
                                    ) {
                                        Spacer(
                                            modifier =
                                                Modifier.width(4.dp)
                                        )

                                        Text(
                                            text =
                                                formatRemainingMessageTime(remainingSeconds),
                                            style =
                                                MaterialTheme
                                                    .typography
                                                    .labelSmall
                                        )
                                    }

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
        }

        androidx.compose.material3.HorizontalDivider()

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 10.dp,
                        top = 8.dp,
                        end = 10.dp,
                        bottom = 16.dp
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

                if (editingMessage != null) {
                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 8.dp,
                                    top = 6.dp,
                                    end = 8.dp
                                ),
                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme
                                        .colorScheme
                                        .surface
                            )
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 10.dp,
                                        top = 6.dp,
                                        end = 4.dp,
                                        bottom = 6.dp
                                    ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Column(
                                modifier =
                                    Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "✏ Edit message",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .labelMedium
                                )

                                Text(
                                    text =
                                        editingMessage
                                            ?.content
                                            ?: "",
                                    maxLines = 2,
                                    overflow =
                                        androidx.compose.ui.text.style
                                            .TextOverflow.Ellipsis,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodySmall
                                )
                            }

                            androidx.compose.material3.TextButton(
                                onClick = {
                                    editingMessage =
                                        null
                                    messageText =
                                        ""
                                }
                            ) {
                                Text("×")
                            }
                        }
                    }
                }

                if (replyingToMessage != null) {
                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 8.dp,
                                    top = 6.dp,
                                    end = 8.dp
                                ),
                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme
                                        .colorScheme
                                        .surface
                            )
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 10.dp,
                                        top = 6.dp,
                                        end = 4.dp,
                                        bottom = 6.dp
                                    ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Column(
                                modifier =
                                    Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "↩ Reply",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .labelMedium
                                )
                                Text(
                                    text =
                                        replyingToMessage
                                            ?.content
                                            ?: "",
                                    maxLines = 2,
                                    overflow =
                                        androidx.compose.ui.text.style
                                            .TextOverflow.Ellipsis,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodySmall
                                )
                            }

                            androidx.compose.material3.TextButton(
                                onClick = {
                                    replyingToMessage =
                                        null
                                }
                            ) {
                                Text("×")
                            }
                        }
                    }
                }

                if (pendingAttachment != null) {
                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 6.dp
                                ),
                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme
                                        .colorScheme
                                        .surface
                            )
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            if (
                                pendingAttachment!!
                                    .isPreviewable
                            ) {
                                AsyncImage(
                                    model =
                                        Uri.parse(
                                            pendingAttachment!!
                                                .uri
                                        ),
                                    contentDescription =
                                        "Attachment preview",
                                    modifier =
                                        Modifier
                                            .size(72.dp),
                                    contentScale =
                                        ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text =
                                        pendingAttachment!!
                                            .displayName,
                                    modifier =
                                        Modifier
                                            .weight(1f),
                                    maxLines = 1,
                                    overflow =
                                        androidx.compose.ui.text.style
                                            .TextOverflow.Ellipsis
                                )
                            }

                            androidx.compose.material3.TextButton(
                                onClick = {
                                    pendingAttachment = null
                                }
                            ) {
                                Text("×")
                            }
                        }
                    }
                }

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
                        contentPadding = PaddingValues(horizontal = 2.dp),
onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SentimentSatisfied,
                            contentDescription = "Emoji",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    androidx.compose.foundation.text.BasicTextField(
                        value = messageText,
                        onValueChange = {
                            messageText = it

                            if (it.isBlank()) {
                                if (isTyping) {
                                    viewModel.stopTyping(
                                        conversationId
                                    )
                                    isTyping = false
                                }
                            } else if (!isTyping) {
                                viewModel.startTyping(
                                    conversationId
                                )
                                isTyping = true
                            }
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
                        contentPadding = PaddingValues(horizontal = 2.dp),
onClick = {
                            expiryMenuExpanded =
                                true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Timer,
                            contentDescription = "Message timer"
                        )
                    }

                    DropdownMenu(
                        expanded = expiryMenuExpanded,
                        onDismissRequest = {
                            expiryMenuExpanded =
                                false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text("Never")
                            },
                            onClick = {
                                selectedExpirySeconds =
                                    null
                                expiryMenuExpanded =
                                    false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text("30 seconds")
                            },
                            onClick = {
                                selectedExpirySeconds =
                                    30L
                                expiryMenuExpanded =
                                    false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text("1 minute")
                            },
                            onClick = {
                                selectedExpirySeconds =
                                    60L
                                expiryMenuExpanded =
                                    false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text("1 hour")
                            },
                            onClick = {
                                selectedExpirySeconds =
                                    3600L
                                expiryMenuExpanded =
                                    false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text("24 hours")
                            },
                            onClick = {
                                selectedExpirySeconds =
                                    86400L
                                expiryMenuExpanded =
                                    false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text("7 days")
                            },
                            onClick = {
                                selectedExpirySeconds =
                                    604800L
                                expiryMenuExpanded =
                                    false
                            }
                        )
                    }

                    androidx.compose.material3.TextButton(
                        contentPadding =
                            PaddingValues(horizontal = 2.dp),
                        onClick = {
                            attachmentMenuExpanded = true
                        }
                    ) {
                        Icon(
                            imageVector =
                                Icons.Filled.AttachFile,
                            contentDescription =
                                "Attachment"
                        )
                    }

                    DropdownMenu(
                        expanded =
                            attachmentMenuExpanded,
                        onDismissRequest = {
                            attachmentMenuExpanded = false
                        }
                    ) {
                        AttachmentKind.menuItems().forEach { kind ->
                            DropdownMenuItem(
                                text = {
                                    Text(kind.label)
                                },
                                onClick = {
                                    attachmentMenuExpanded =
                                        false

                                    when (kind) {
                                        AttachmentKind.GALLERY -> {
                                            galleryPicker.launch(
                                                AttachmentPickerSpec
                                                    .mimeTypes(
                                                        AttachmentKind.GALLERY
                                                    )
                                                    .first()
                                            )
                                        }

                                        AttachmentKind.DOCUMENT -> {
                                            documentPicker.launch(
                                                AttachmentPickerSpec
                                                    .mimeTypes(
                                                        AttachmentKind.DOCUMENT
                                                    )
                                                    .first()
                                            )
                                        }

                                        AttachmentKind.AUDIO -> {
                                            audioPicker.launch(
                                                AttachmentPickerSpec
                                                    .mimeTypes(
                                                        AttachmentKind.AUDIO
                                                    )
                                                    .first()
                                            )
                                        }

                                        AttachmentKind.CAMERA,
                                        AttachmentKind.LOCATION,
                                        AttachmentKind.CONTACT -> {
                                            Toast.makeText(
                                                context,
                                                "${kind.label} will be added next.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            if (uiState.typingUserIds.isNotEmpty()) {
                Text(
                    text = "Typing...",
                    modifier =
                        Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 4.dp
                        ),
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }

            Spacer(
                modifier =
                    Modifier.width(6.dp)
            )

            Button(
                onClick = {

                    if (
                        messageText.isNotBlank() ||
                        pendingAttachment != null
                    ) {

                        if (editingMessage != null) {
                            viewModel.editMessage(
                                messageId =
                                    editingMessage!!.id,
                                content =
                                    messageText
                            )

                            editingMessage =
                                null
                            messageText =
                                ""
                            return@Button
                        }
                        val expiresAt =
                            selectedExpirySeconds?.let { seconds ->
                                java.time.Instant
                                    .ofEpochMilli(
                                        System.currentTimeMillis() +
                                            (seconds * 1000L)
                                    )
                                    .toString()
                            }

                        if (pendingAttachment != null) {
                            val attachment = pendingAttachment!!

                            viewModel.uploadAttachment(
                                conversationId = conversationId,
                                uri = Uri.parse(
                                    attachment.uri
                                ),
                                content = messageText,
                                expiresAt = expiresAt,
                                replyToMessageId =
                                    replyingToMessage?.id
                            )

                            pendingAttachment = null
                        } else {
                            viewModel.sendMessage(
                                conversationId = conversationId,
                                content = messageText,
                                expiresAt = expiresAt,
                                replyToMessageId =
                                    replyingToMessage?.id
                            )
                        }
                        messageText = ""
                        replyingToMessage = null
                    }
                },
                enabled =
                    !uiState.sending &&
                    !uiState.uploadingAttachment &&
                    (
                        messageText.isNotBlank() ||
                        pendingAttachment != null
                    ),
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


@Composable
fun NewContactScreen(
    onBack: () -> Unit,
    onSave: (
        String,
        String,
        String,
        String
    ) -> Unit
) {
    var firstName by
        remember {
            mutableStateOf("")
        }

    var lastName by
        remember {
            mutableStateOf("")
        }

    var countryCode by
        remember {
            mutableStateOf("+91")
        }

    var phone by
        remember {
            mutableStateOf("")
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
    ) {
        Row(
            modifier =
                Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            androidx.compose.material3.IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector =
                        Icons.Filled.ArrowBack,
                    contentDescription =
                        "Back"
                )
            }

            Text(
                text = "New Contact",
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall,
                modifier =
                    Modifier.padding(
                        start = 4.dp
                    )
            )
        }

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        OutlinedTextField(
            value = firstName,
            onValueChange = {
                firstName = it
            },
            modifier =
                Modifier.fillMaxWidth(),
            label = {
                Text("First name")
            },
            singleLine = true
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = {
                lastName = it
            },
            modifier =
                Modifier.fillMaxWidth(),
            label = {
                Text("Last name")
            },
            singleLine = true
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = countryCode,
                onValueChange = {
                    countryCode = it
                },
                modifier =
                    Modifier.width(100.dp),
                label = {
                    Text("Code")
                },
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                },
                modifier =
                    Modifier.weight(1f),
                label = {
                    Text("Phone")
                },
                singleLine = true
            )
        }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        Button(
            onClick = {
                onSave(
                    firstName,
                    lastName,
                    countryCode,
                    phone
                )
            },
            modifier =
                Modifier.fillMaxWidth(),
            enabled =
                firstName.isNotBlank() &&
                phone.isNotBlank()
        ) {
            Text("Save")
        }
    }
}

@Composable
fun AddContactScreen(
    contactName: String,
    contactPhone: String,
    onBack: () -> Unit,
    onSaved: (Contact) -> Unit,
    viewModel: ContactEditViewModel =
        viewModel()
) {
    val context =
        androidx.compose.ui.platform.LocalContext.current

    val sessionManager =
        SessionManager(context)

    val token =
        sessionManager.getToken()

    val uiState by
        viewModel.uiState.collectAsState()

    var firstName by
        remember {
            mutableStateOf("")
        }

    var lastName by
        remember {
            mutableStateOf("")
        }

    val canSave =
        firstName.trim().isNotBlank() &&
        contactPhone.trim().isNotBlank() &&
        !uiState.saving

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
    ) {
        Row(
            modifier =
                Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            androidx.compose.material3.IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector =
                        androidx.compose.material.icons
                            .Icons
                            .Filled
                            .ArrowBack,
                    contentDescription =
                        "Back"
                )
            }

            Text(
                text = "Add to Contact",
                style =
                    MaterialTheme
                        .typography
                        .titleLarge
            )
        }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        Text(
            text =
                contactName.ifBlank {
                    "Chat Contact"
                },
            style =
                MaterialTheme
                    .typography
                    .titleMedium
        )

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = contactPhone,
            onValueChange = {},
            modifier =
                Modifier.fillMaxWidth(),
            label = {
                Text("Mobile Number")
            },
            enabled = false,
            singleLine = true
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = firstName,
            onValueChange = {
                firstName = it
            },
            modifier =
                Modifier.fillMaxWidth(),
            label = {
                Text("First Name")
            },
            singleLine = true
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = {
                lastName = it
            },
            modifier =
                Modifier.fillMaxWidth(),
            label = {
                Text("Last Name")
            },
            singleLine = true
        )

        if (
            uiState.message.isNotBlank()
        ) {
            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Text(
                text = uiState.message,
                color =
                    MaterialTheme
                        .colorScheme
                        .error
            )
        }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        Button(
            onClick = {
                val normalizedPhone =
                    contactPhone.trim()

                val parts =
                    normalizedPhone.split(
                        Regex("\\s+"),
                        limit = 2
                    )

                val hasCountryCode =
                    parts.size == 2 &&
                    parts[0].startsWith("+")

                val countryCode =
                    if (hasCountryCode) {
                        parts[0]
                    } else {
                        "+91"
                    }

                val phone =
                    if (hasCountryCode) {
                        parts[1]
                    } else {
                        normalizedPhone
                    }

                viewModel.createContact(
                    token = token.orEmpty(),
                    request =
                        com.chatflow.app.data
                            .CreateContactRequest(
                                firstName =
                                    firstName.trim(),
                                lastName =
                                    lastName
                                        .trim()
                                        .ifBlank {
                                            null
                                        },
                                countryCode =
                                    countryCode,
                                phone =
                                    phone
                            ),
                    onSuccess = onSaved
                )
            },
            enabled = canSave,
            modifier =
                Modifier.fillMaxWidth()
        ) {
            if (uiState.saving) {
                CircularProgressIndicator(
                    modifier =
                        Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Save Contact")
            }
        }
    }
}

@Composable
fun EditContactScreen(
    contactId: String,
    onBack: () -> Unit,
    onSaved: (Contact) -> Unit,
    viewModel: ContactEditViewModel =
        viewModel()
) {

    val context =
        androidx.compose.ui.platform.LocalContext.current

    val sessionManager =
        SessionManager(context)

    val token =
        sessionManager.getToken()

    val uiState by
        viewModel.uiState.collectAsState()

    var firstName by
        remember {
            mutableStateOf("")
        }

    var lastName by
        remember {
            mutableStateOf("")
        }

    var countryCode by
        remember {
            mutableStateOf("+91")
        }

    var phone by
        remember {
            mutableStateOf("")
        }

    var initialized by
        remember {
            mutableStateOf(false)
        }

    var showDeleteDialog by
        remember {
            mutableStateOf(false)
        }

    androidx.compose.runtime.LaunchedEffect(
        token,
        contactId
    ) {

        if (
            !token.isNullOrBlank() &&
            contactId.isNotBlank()
        ) {

            viewModel.loadContact(
                token = token,
                contactId = contactId
            )
        }
    }

    androidx.compose.runtime.LaunchedEffect(
        uiState.contact
    ) {

        uiState.contact?.let { contact ->

            if (!initialized) {

                firstName =
                    contact.first_name

                lastName =
                    contact.last_name ?: ""

                countryCode =
                    contact.country_code

                phone =
                    contact.phone

                initialized = true
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
    ) {

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            androidx.compose.material3.IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector =
                        Icons.Filled.ArrowBack,
                    contentDescription =
                        "Back"
                )
            }

            Text(
                text = "Edit Contact",
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall,
                modifier =
                    Modifier.padding(
                        start = 4.dp
                    )
            )
        }

        Spacer(
            modifier =
                Modifier.height(20.dp)
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

                androidx.compose.material3.CircularProgressIndicator()
            }

        } else {

            OutlinedTextField(
                value = firstName,
                onValueChange = {
                    firstName = it
                },
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("First name")
                },
                singleLine = true
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = {
                    lastName = it
                },
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("Last name")
                },
                singleLine = true
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = countryCode,
                    onValueChange = {
                        countryCode = it
                    },
                    modifier =
                        Modifier.width(100.dp),
                    label = {
                        Text("Code")
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                    },
                    modifier =
                        Modifier.weight(1f),
                    label = {
                        Text("Phone")
                    },
                    readOnly = true,
                    singleLine = true
                )
            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            if (
                uiState.message.isNotBlank()
            ) {

                Text(
                    text = uiState.message,
                    modifier =
                        Modifier.padding(
                            bottom = 12.dp
                        )
                )
            }

            Button(
                onClick = {

                    if (!token.isNullOrBlank()) {

                        viewModel.updateContact(
                            token = token,
                            contactId = contactId,
                            request =
                                com.chatflow.app.data
                                    .UpdateContactRequest(
                                        firstName =
                                            firstName.trim(),
                                        lastName =
                                            lastName
                                                .trim()
                                                .ifBlank {
                                                    null
                                                },
                                        countryCode =
                                            countryCode.trim(),
                                        phone =
                                            phone.trim()
                                    ),
                            onSuccess = { updatedContact ->
                                onSaved(updatedContact)
                            }
                        )
                    }
                },
                modifier =
                    Modifier.fillMaxWidth(),
                enabled =
                    firstName.isNotBlank() &&
                    phone.isNotBlank() &&
                    !uiState.saving
            ) {

                if (uiState.saving) {

                    androidx.compose.material3.CircularProgressIndicator(
                        modifier =
                            Modifier.size(20.dp)
                    )

                } else {

                    Text("Save")
                }
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            androidx.compose.material3.OutlinedButton(
                onClick = {
                    showDeleteDialog = true
                },
                modifier =
                    Modifier.fillMaxWidth(),
                enabled =
                    !uiState.saving &&
                    !uiState.deleting
            ) {
                if (uiState.deleting) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier =
                            Modifier.size(20.dp)
                    )
                } else {
                    Text("Delete Contact")
                }
            }
        }
    }

    if (showDeleteDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                if (!uiState.deleting) {
                    showDeleteDialog = false
                }
            },
            title = {
                Text("Delete Contact?")
            },
            text = {
                Text(
                    "Are you sure you want to delete this contact?"
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        if (!token.isNullOrBlank()) {
                            viewModel.deleteContact(
                                token = token,
                                contactId = contactId,
                                onSuccess = {
                                    showDeleteDialog = false
                                    onBack()
                                }
                            )
                        }
                    },
                    enabled =
                        !uiState.deleting
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showDeleteDialog = false
                    },
                    enabled =
                        !uiState.deleting
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileScreen(
    onBack: () -> Unit
) {

    val viewModel: ProfileViewModel =
        viewModel()

    val uiState by
        viewModel.uiState.collectAsState()

    val imagePicker =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .GetContent()
        ) { uri ->
            if (uri != null) {
                viewModel.uploadAvatar(uri)
            }
        }

    var displayName by
        remember {
            mutableStateOf("")
        }

    var about by
        remember {
            mutableStateOf("")
        }

    androidx.compose.runtime.LaunchedEffect(
        Unit
    ) {
        viewModel.loadProfile()
    }

    androidx.compose.runtime.LaunchedEffect(
        uiState.user
    ) {

        uiState.user?.let { user ->

            displayName =
                user.display_name

            about =
                user.about ?: ""
            viewModel.loadAvatar(
                user.id
            )
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
    ) {

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            androidx.compose.material3.IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector =
                        Icons.Filled.ArrowBack,
                    contentDescription =
                        "Back"
                )
            }

            Text(
                text = "Profile",
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall,
                modifier =
                    Modifier.padding(
                        start = 4.dp
                    )
            )
        }

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

        } else if (uiState.user != null) {

            val user =
                uiState.user!!

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            androidx.compose.foundation.layout.Box(
                modifier =
                    Modifier
                        .size(88.dp)
                        .clip(
                            androidx.compose.foundation.shape
                                .CircleShape
                        )
                        .background(
                            MaterialTheme
                                .colorScheme
                                .primaryContainer
                        )
                        .clickable(
                            enabled = !uiState.saving
                        ) {
                            imagePicker.launch("image/*")
                        }
                        .align(
                            Alignment.CenterHorizontally
                        ),
                contentAlignment =
                    Alignment.Center
            ) {

                val avatarBytes =
                    uiState.avatarBytes

                if (avatarBytes != null) {

                    val bitmap =
                        BitmapFactory.decodeByteArray(
                            avatarBytes,
                            0,
                            avatarBytes.size
                        )

                    if (bitmap != null) {

                        androidx.compose.foundation.Image(
                            bitmap =
                                bitmap.asImageBitmap(),
                            contentDescription =
                                "Profile photo",
                            modifier =
                                Modifier.fillMaxSize(),
                            contentScale =
                                ContentScale.Crop
                        )

                    } else {

                        Text(
                            text =
                                displayName
                                    .firstOrNull()
                                    ?.uppercase()
                                    ?: "?",
                            style =
                                MaterialTheme
                                    .typography
                                    .headlineMedium
                        )
                    }

                } else {

                    Text(
                        text =
                            displayName
                                .firstOrNull()
                                ?.uppercase()
                                ?: "?",
                        style =
                            MaterialTheme
                                .typography
                                .headlineMedium
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            OutlinedTextField(
                value = displayName,
                onValueChange = {
                    displayName = it
                },
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("Display name")
                },
                singleLine = true,
                enabled =
                    !uiState.saving
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = user.phone ?: "",
                onValueChange = {},
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("Phone")
                },
                singleLine = true,
                enabled = false
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = about,
                onValueChange = {
                    about = it
                },
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("About")
                },
                minLines = 3,
                maxLines = 4,
                enabled =
                    !uiState.saving
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Button(
                onClick = {
                    viewModel.updateProfile(
                        displayName =
                            displayName,
                        about =
                            about
                    )
                },
                modifier =
                    Modifier.fillMaxWidth(),
                enabled =
                    !uiState.saving &&
                    displayName.isNotBlank()
            ) {

                if (uiState.saving) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(20.dp)
                    )

                } else {

                    Text("Save")
                }
            }

            if (
                uiState.message.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text =
                        uiState.message,
                    color =
                        if (
                            uiState.message ==
                            "Profile updated"
                        ) {
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
}
