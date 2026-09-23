package com.chatflow.app


import androidx.compose.foundation.layout.height
import androidx.compose.runtime.collectAsState

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chatflow.app.data.GroupMember
import com.chatflow.app.data.Contact
import com.chatflow.app.data.ContactRepository

@Composable
fun GroupInfoScreen(
    conversationId: String,
    onBack: () -> Unit,
    onDeleted: () -> Unit,

viewModel: GroupInfoViewModel =
        viewModel()
) {

    val context =
        androidx.compose.ui.platform.LocalContext.current

    val sessionManager =
        SessionManager(context)

    val token =
        sessionManager.getToken()

    val contactRepository =
        remember {
            ContactRepository()
        }

    val uiState by
        viewModel.uiState.collectAsState()

    var showEditDialog by
        remember {
            mutableStateOf(false)
        }

    var editTitle by
        remember {
            mutableStateOf("")
        }

    var showGroupMenu by
        remember {
            mutableStateOf(false)
        }

    var showDeleteDialog by
        remember {
            mutableStateOf(false)
        }

    var memberToRemove by
        remember {
            mutableStateOf<GroupMember?>(null)
        }

    var showRemoveMemberDialog by
        remember {
            mutableStateOf(false)
        }

    var showAddMemberDialog by
        remember {
            mutableStateOf(false)
        }

    var contactsLoading by
        remember {
            mutableStateOf(false)
        }

    var contacts by
        remember {
            mutableStateOf<List<Contact>>(
                emptyList()
            )
        }

    var contactSearch by
        remember {
            mutableStateOf("")
        }

    LaunchedEffect(
        conversationId
    ) {
        if (!token.isNullOrBlank()) {
            viewModel.loadGroupInfo(
                token = token,
                conversationId =
                    conversationId
            )
        }
    }

    LaunchedEffect(
        showAddMemberDialog
    ) {
        if (
            showAddMemberDialog &&
            !token.isNullOrBlank()
        ) {
            contactsLoading = true

            try {
                contacts =
                    contactRepository
                        .getContacts(
                            token
                        )
                        .contacts
            } catch (
                error: Exception
            ) {
                contacts =
                    emptyList()
            } finally {
                contactsLoading =
                    false
            }
        }
    }

    Column(
        modifier =
            Modifier.fillMaxSize()
    ) {

        Row(
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

            TextButton(
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

            Text(
                text = "Group Info",
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
                modifier =
                    Modifier.weight(1f)
            )

            androidx.compose.foundation.layout.Box {

                TextButton(
                    onClick = {
                        showGroupMenu = true
                    }
                ) {
                    Text(
                        text = "⋮",
                        style =
                            MaterialTheme
                                .typography
                                .headlineMedium
                    )
                }

                DropdownMenu(
                    expanded = showGroupMenu,
                    onDismissRequest = {
                        showGroupMenu = false
                    }
                ) {

                    if (
                        uiState.group
                            ?.current_user_role ==
                            "admin"
                    ) {

                        DropdownMenuItem(
                            text = {
                                Text("Edit Group")
                            },
                            onClick = {
                                val currentGroup =
                                    uiState.group

                                if (
                                    currentGroup != null
                                ) {
                                    editTitle =
                                        currentGroup
                                            .title
                                            .orEmpty()

                                    showGroupMenu =
                                        false

                                    showEditDialog =
                                        true
                                }
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text("Delete Group")
                            },
                            onClick = {
                                showGroupMenu =
                                    false

                                showDeleteDialog =
                                    true
                            }
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        if (uiState.loading) {

            Column(
                modifier =
                    Modifier
                        .fillMaxSize(),
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.Center
            ) {

                CircularProgressIndicator()

            }

        } else if (
            uiState.message.isNotBlank()
        ) {

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(
                    text = uiState.message,
                    color =
                        MaterialTheme
                            .colorScheme
                            .error
                )

            }

        } else {

            uiState.group?.let { group ->

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 24.dp
                            ),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    androidx.compose.foundation.layout.Box(
                        modifier =
                            Modifier
                                .size(96.dp)
                                .clip(
                                    CircleShape
                                ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                group.title
                                    .orEmpty()
                                    .firstOrNull()
                                    ?.uppercase()
                                    ?: "?",
                            style =
                                MaterialTheme
                                    .typography
                                    .headlineLarge
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Text(
                        text =
                            group.title
                                .orEmpty(),
                        style =
                            MaterialTheme
                                .typography
                                .headlineSmall
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "${uiState.members.size} members",
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(20.dp)
                    )

                    if (
                        group.current_user_role ==
                            "admin"
                    ) {
                        Text(
                            text = "You are admin",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium
                        )

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )
                    }
                }

                if (
                    group.current_user_role ==
                        "admin"
                ) {
                    TextButton(
                        onClick = {
                            if (
                                !uiState.actionLoading &&
                                !contactsLoading
                            ) {
                                showAddMemberDialog =
                                    true
                                contactSearch = ""
                            }
                        },
                        enabled =
                            !uiState.actionLoading &&
                                !contactsLoading,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 20.dp
                                )
                    ) {
                        Text("Add Member")
                    }
                }

                HorizontalDivider()

                LazyColumn(
                    modifier =
                        Modifier.fillMaxSize()
                ) {

                    item {

                        Text(
                            text =
                                "Members",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            modifier =
                                Modifier.padding(
                                    horizontal = 20.dp,
                                    vertical = 16.dp
                                )
                        )
                    }

                    items(
                        uiState.members,
                        key = {
                            it.id
                        }
                    ) { member ->

                        GroupMemberRow(
                            member = member,
                            canRemove =
                                group.current_user_role ==
                                    "admin" &&
                                    member.role != "admin",
                            onRemove = {
                                memberToRemove =
                                    member
                                showRemoveMemberDialog =
                                    true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddMemberDialog) {

        val existingMemberIds =
            uiState.members
                .map { it.user_id }
                .toSet()

        val filteredContacts =
            contacts.filter { contact ->
                contact.linked_user_id != null &&
                    !existingMemberIds.contains(
                        contact.linked_user_id
                    ) &&
                    (
                        contact.first_name
                            .contains(
                                contactSearch,
                                ignoreCase = true
                            ) ||
                        contact.last_name
                            .orEmpty()
                            .contains(
                                contactSearch,
                                ignoreCase = true
                            ) ||
                        contact.phone.contains(
                            contactSearch,
                            ignoreCase = true
                        )
                    )
            }

        var selectedUserId by
            remember(
                showAddMemberDialog
            ) {
                mutableStateOf<String?>(null)
            }

        AlertDialog(
            onDismissRequest = {
                if (
                    !uiState.actionLoading
                ) {
                    showAddMemberDialog =
                        false
                }
            },
            title = {
                Text("Add Member")
            },
            text = {

                Column {

                    OutlinedTextField(
                        value = contactSearch,
                        onValueChange = {
                            contactSearch = it
                        },
                        label = {
                            Text("Search contact")
                        },
                        modifier =
                            Modifier.fillMaxWidth()
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    if (contactsLoading) {

                        CircularProgressIndicator()

                    } else if (
                        filteredContacts.isEmpty()
                    ) {

                        Text(
                            text =
                                "No contacts available"
                        )

                    } else {

                        LazyColumn(
                            modifier =
                                Modifier.height(
                                    280.dp
                                )
                        ) {

                            items(
                                filteredContacts,
                                key = {
                                    it.id
                                }
                            ) { contact ->

                                Row(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                vertical =
                                                    6.dp
                                            ),
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    RadioButton(
                                        selected =
                                            selectedUserId ==
                                                contact
                                                    .linked_user_id,
                                        onClick = {
                                            selectedUserId =
                                                contact
                                                    .linked_user_id
                                        }
                                    )

                                    Column {

                                        Text(
                                            text =
                                                listOfNotNull(
                                                    contact.first_name,
                                                    contact.last_name
                                                ).joinToString(
                                                    " "
                                                )
                                        )

                                        Text(
                                            text =
                                                "${contact.country_code}${contact.phone}",
                                            style =
                                                MaterialTheme
                                                    .typography
                                                    .bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {

                TextButton(
                    onClick = {

                        val userId =
                            selectedUserId

                        if (
                            !userId.isNullOrBlank() &&
                            !token.isNullOrBlank() &&
                            !uiState.actionLoading
                        ) {
                            viewModel.addGroupMember(
                                token = token,
                                conversationId =
                                    conversationId,
                                userId = userId
                            )

                            showAddMemberDialog =
                                false
                        }
                    },
                    enabled =
                        selectedUserId != null &&
                            !uiState.actionLoading &&
                            !contactsLoading
                ) {
                    Text("Add")
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {
                        if (
                            !uiState.actionLoading
                        ) {
                            showAddMemberDialog =
                                false
                        }
                    },
                    enabled =
                        !uiState.actionLoading
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showRemoveMemberDialog) {

        val selectedMember =
            memberToRemove

        AlertDialog(
            onDismissRequest = {
                if (!uiState.actionLoading) {
                    showRemoveMemberDialog =
                        false
                    memberToRemove = null
                }
            },
            title = {
                Text("Remove Member?")
            },
            text = {
                Text(
                    "Remove " +
                        (
                            selectedMember
                                ?.display_name
                                .orEmpty()
                                .ifBlank {
                                    selectedMember
                                        ?.phone
                                        .orEmpty()
                                }
                        ) +
                        " from this group?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {

                        val userId =
                            selectedMember
                                ?.user_id

                        if (
                            !uiState.actionLoading &&
                            !token.isNullOrBlank() &&
                            !userId.isNullOrBlank()
                        ) {
                            showRemoveMemberDialog =
                                false

                            memberToRemove = null

                            viewModel.removeGroupMember(
                                token = token,
                                conversationId =
                                    conversationId,
                                userId = userId
                            )
                        }
                    },
                    enabled =
                        !uiState.actionLoading
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        if (
                            !uiState.actionLoading
                        ) {
                            showRemoveMemberDialog =
                                false
                            memberToRemove = null
                        }
                    },
                    enabled =
                        !uiState.actionLoading
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteDialog) {

        AlertDialog(
            onDismissRequest = {
                if (!uiState.actionLoading) {
                    showDeleteDialog = false
                }
            },
            title = {
                Text("Delete Group?")
            },
            text = {
                Text(
                    "This will delete the group for everyone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (
                            !uiState.actionLoading &&
                            !token.isNullOrBlank()
                        ) {
                            showDeleteDialog =
                                false

                            viewModel.deleteGroup(
                                token = token,
                                conversationId =
                                    conversationId
                            )
                        }
                    },
                    enabled =
                        !uiState.actionLoading
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    },
                    enabled =
                        !uiState.actionLoading
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditDialog) {

        AlertDialog(
            onDismissRequest = {
                if (!uiState.actionLoading) {
                    showEditDialog = false
                }
            },
            title = {
                Text("Edit Group")
            },
            text = {
                OutlinedTextField(
                    value = editTitle,
                    onValueChange = {
                        editTitle = it
                    },
                    label = {
                        Text("Group name")
                    },
                    singleLine = true,
                    enabled =
                        !uiState.actionLoading
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val cleanTitle =
                            editTitle.trim()
                        if (
                            cleanTitle.isNotBlank() &&
                            !token.isNullOrBlank()
                        ) {
                            showEditDialog = false
                            viewModel.updateGroup(
                                token = token,
                                conversationId =
                                    conversationId,
                                title = cleanTitle
                            )
                        }
                    },
                    enabled =
                        editTitle.trim().isNotBlank() &&
                        !uiState.actionLoading
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showEditDialog = false
                    },
                    enabled =
                        !uiState.actionLoading
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun GroupMemberRow(
    member: GroupMember,
    canRemove: Boolean,
    onRemove: () -> Unit
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 10.dp
                ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        androidx.compose.foundation.layout.Box(
            modifier =
                Modifier
                    .size(44.dp)
                    .clip(
                        CircleShape
                    ),
            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    member.display_name
                        .orEmpty()
                        .firstOrNull()
                        ?.uppercase()
                        ?: member.phone
                            .orEmpty()
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
                    .padding(
                        start = 12.dp
                    )
        ) {

            Text(
                text =
                    member.display_name
                        .orEmpty()
                        .ifBlank {
                            member.phone
                                .orEmpty()
                        },
                style =
                    MaterialTheme
                        .typography
                        .bodyLarge
            )

            member.phone
                ?.takeIf {
                    it.isNotBlank()
                }
                ?.let { phone ->

                    Text(
                        text = phone,
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall
                    )
                }
        }

        if (
            member.role == "admin"
        ) {

            Text(
                text = "Admin",
                style =
                    MaterialTheme
                        .typography
                        .labelMedium
            )

        } else if (canRemove) {

            TextButton(
                onClick = onRemove
            ) {
                Text("Remove")
            }
        }
    }
}

