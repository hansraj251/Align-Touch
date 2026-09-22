package com.chatflow.app

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle

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
                Text("Email")
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

    val context =
        androidx.compose.ui.platform.LocalContext.current

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

                if (showProfile) {

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
                ).contains(query) ||
                (
                    conversation.other_user_email
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
            androidx.compose.material3.OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                },
                modifier =
                    Modifier.weight(1f),
                singleLine = true,
                placeholder = {
                    Text(
                        "Search"
                    )
                },
                leadingIcon = {
                    Text("⌕")
                },
                shape =
                    androidx.compose.foundation.shape
                        .RoundedCornerShape(28.dp)
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
                        Modifier.clickable { },
                    shape =
                        androidx.compose.foundation.shape
                            .RoundedCornerShape(22.dp),
                    tonalElevation =
                        if (label == "All") 2.dp
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
                                                .take(1)
                                                .uppercase(),
                                        style =
                                            MaterialTheme
                                                .typography
                                                .titleLarge
                                    )
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
                                            .other_user_display_name,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium
                                )

                                Text(
                                    text =
                                        conversation
                                            .other_user_phone
                                            ?: conversation
                                                .other_user_email
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

    androidx.compose.runtime.LaunchedEffect(
        token
    ) {

        if (!token.isNullOrBlank()) {
            viewModel.loadUsers(token)
            viewModel.loadContacts(token)
        }
    }

    if (showNewContact) {
        NewContactScreen(
            onBack = {
                showNewContact = false
            },
            onSave = { firstName, lastName, username, countryCode, phone ->

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
                                username =
                                    username.ifBlank {
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
                    .clickable { },
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

            androidx.compose.foundation.layout.Box(
                modifier =
                    Modifier
                        .padding(horizontal = 4.dp)
                        .size(40.dp)
                        .clip(
                            androidx.compose.foundation.shape
                                .CircleShape
                        )
                        .background(
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .primaryContainer
                        ),
                contentAlignment =
                    Alignment.Center
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

            val messageListState =
                androidx.compose.foundation.lazy
                    .rememberLazyListState()

            androidx.compose.runtime.LaunchedEffect(
                uiState.messages.size
            ) {
                if (uiState.messages.isNotEmpty()) {
                    messageListState.animateScrollToItem(
                        uiState.messages.lastIndex
                    )
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


@Composable
fun NewContactScreen(
    onBack: () -> Unit,
    onSave: (
        String,
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

    var username by
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

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
            },
            modifier =
                Modifier.fillMaxWidth(),
            label = {
                Text("Username")
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
                    username,
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
fun ProfileScreen(
    onBack: () -> Unit
) {

    val viewModel: ProfileViewModel =
        viewModel()

    val uiState by
        viewModel.uiState.collectAsState()

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
                        .align(
                            Alignment.CenterHorizontally
                        ),
                contentAlignment =
                    Alignment.Center
            ) {

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

            if (!user.email.isNullOrBlank()) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value =
                        user.email ?: "",
                    onValueChange = {},
                    modifier =
                        Modifier.fillMaxWidth(),
                    label = {
                        Text("Email")
                    },
                    singleLine = true,
                    enabled = false
                )
            }

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
