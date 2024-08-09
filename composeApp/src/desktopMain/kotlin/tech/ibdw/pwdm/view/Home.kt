package tech.ibdw.pwdm.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.NetworkPing
import androidx.compose.material.icons.outlined.Password
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.dokar.sonner.ToastType
import com.dokar.sonner.Toaster
import com.dokar.sonner.ToasterState
import com.dokar.sonner.rememberToasterState
import tech.ibdw.pwdm.cfg.Entry
import tech.ibdw.pwdm.mvi.ProfileEvent
import tech.ibdw.pwdm.mvi.ProfileViewModel
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection

/**
 * @author <a href="https://github.com/Sagiri-kawaii01">lgz</a>
 * @date 2024/8/7 16:07
 * @since
 */
@Composable
fun Home(
    modifier: Modifier,
    viewModel: ProfileViewModel,
) {
    val entries by viewModel.entryState.collectAsState()
    val toaster = rememberToasterState()
    val config by viewModel.configState.collectAsState()

    var onEditEntry: (Entry) -> Unit by remember { mutableStateOf({}) }
    var editEntry by remember { mutableStateOf(Entry()) }
    var showEdit by remember { mutableStateOf(false) }

    var onDeleteEntry: () -> Unit by remember { mutableStateOf({}) }
    var deleteEntry by remember { mutableStateOf(Entry()) }
    var showDelete by remember { mutableStateOf(false) }

    Toaster(
        state = toaster
    )

    if (showEdit) {
        EntryEditDialog(
            name = editEntry.name,
            account = editEntry.account,
            password = editEntry.password,
            ip = editEntry.ip,
            port = editEntry.port,
            onCancel = {
                showEdit = false
            }
        ) {
            onEditEntry(it)
            showEdit = false
        }
    }

    if (showDelete) {
        EntryDeleteDialog(
            deleteEntry.name,
            onCancel = {
                showDelete = false
            }
        ) {
            onDeleteEntry()
            showDelete = false
        }
    }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn {
            itemsIndexed(entries) { index, entry ->
                EntryCard(entry, toaster,
                    showPassword = config.showPassword,
                    onEdit = {
                        editEntry = entry
                        onEditEntry = { newEntry ->
                            viewModel.onEvent(ProfileEvent.EditEntry(newEntry, index))
                        }
                        showEdit = true
                    }
                ) {
                    deleteEntry = entry
                    onDeleteEntry = {
                        viewModel.onEvent(ProfileEvent.DeleteEntry(index))
                    }
                    showDelete = true
                }
            }
        }
        Row(
            modifier = Modifier.padding(bottom = 8.dp, end = 10.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = {
                    editEntry = Entry()
                    onEditEntry = {
                        viewModel.onEvent(ProfileEvent.AddEntry(it))
                    }
                    showEdit = true
                },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            ) {
                Icon(Icons.Default.Add, "")
            }
        }
    }
}

@Composable
fun EntryCard(
    entry: Entry,
    toaster: ToasterState,
    showPassword: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        elevation = 4.dp
    ) {
        Column {
            Row (
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Spacer(Modifier.width(12.dp))
                    Text(entry.name)
                }
                Row {
                    IconButton(
                        onClick = onEdit
                    ) {
                        Icon(Icons.Default.Edit,
                            "",
                            tint = Color(0, 164, 255),
                            modifier = Modifier.size(24.dp)
                                .pointerHoverIcon(PointerIcon.Hand)
                        )
                    }

                    IconButton(
                        onClick = onDelete
                    ) {
                        Icon(Icons.Default.Delete,
                            "",
                            tint = Color(255, 83, 112),
                            modifier = Modifier.size(24.dp)
                                .pointerHoverIcon(PointerIcon.Hand)
                        )
                    }
                }
            }

            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                CopyText(
                    title = "账号：",
                    text = entry.account,
                    value = entry.account,
                    modifier = Modifier
                        .padding(
                            horizontal = 16.dp,
                            vertical = 2.dp
                        )
                        .height(24.dp),
                    toaster
                ) {
                    Icon(
                        Icons.Outlined.AccountBox,
                        "",
                        modifier = Modifier.size(20.dp)
                    )
                }
                if (!entry.ip.isNullOrBlank()) {
                    CopyText(
                        title = "IP：",
                        text = entry.ip!!,
                        value = entry.ip!!,
                        modifier = Modifier
                            .padding(
                                horizontal = 16.dp,
                                vertical = 2.dp
                            )
                            .height(24.dp),
                        toaster
                    ) {
                        Icon(
                            Icons.Outlined.NetworkPing,
                            "",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                CopyText(
                    title = "密码：",
                    text = if (showPassword) {
                        entry.password
                    } else {
                        "*" * entry.password.length
                    },
                    value = entry.password,
                    modifier = Modifier
                        .padding(
                            horizontal = 16.dp,
                            vertical = 2.dp
                        )
                        .height(24.dp),
                    toaster
                ) {
                    Icon(
                        Icons.Outlined.Password,
                        "",
                        modifier = Modifier.size(20.dp)
                    )
                }
                if (!entry.port.isNullOrBlank()) {
                    CopyText(
                        title = "Port：",
                        text = entry.port!!,
                        value = entry.port!!,
                        modifier = Modifier
                            .padding(
                                horizontal = 16.dp,
                                vertical = 2.dp
                            )
                            .height(24.dp),
                        toaster
                    ) {
                        Icon(
                            Icons.Outlined.NetworkPing,
                            "",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

private operator fun String.times(i: Int): String {
    var s = ""
    for (i1 in 0 until i) {
        s += this
    }
    return s
}

@Composable
fun CopyText(
    title: String,
    text: String,
    value: String,
    modifier: Modifier = Modifier,
    toaster: ToasterState,
    icon: @Composable () -> Unit
) {
    Row (
        modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
        Text(title)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            Icons.Default.ContentCopy,
            "",
            modifier = Modifier.size(12.dp)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    writeToClipboard(value)
                    toaster.show(
                        message = "$title 复制成功",
                        type = ToastType.Success
                    )
                }
                .pointerHoverIcon(PointerIcon.Hand)
        )
    }
}

@Composable
fun EntryEditDialog(
    name: String,
    account: String,
    password: String,
    ip: String?,
    port: String?,
    onCancel: () -> Unit,
    onSave: (Entry) -> Unit,
) {
    var eName by remember { mutableStateOf(name) }
    var eAccount by remember { mutableStateOf(account) }
    var ePassword by remember { mutableStateOf(password) }
    var eIp by remember { mutableStateOf(ip ?: "") }
    var ePort by remember { mutableStateOf(port ?: "") }

    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    Entry().apply {
                        this.name = eName
                        this.account = eAccount
                        this.password = ePassword
                        this.ip = eIp.ifBlank { null }
                        this.port = ePort.ifBlank { null }
                    }
                )
            }) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onCancel()
            }) {
                Text("取消")
            }
        },
        title = {
            Text("编辑【$eName】账号信息")
        },
        text = {
            Column {
                val focusRequester1 = remember { FocusRequester() }
                val focusRequester2 = remember { FocusRequester() }
                val focusRequester3 = remember { FocusRequester() }
                val focusRequester4 = remember { FocusRequester() }
                val focusRequester5 = remember { FocusRequester() }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = eName,
                    onValueChange = {
                        eName = it
                    },
                    label = {
                        Text("名称")
                    },
                    modifier = Modifier.nextFocus(focusRequester1, focusRequester2)
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = eAccount,
                    onValueChange = {
                        eAccount = it
                    },
                    label = {
                        Text("账号")
                    },
                    modifier = Modifier.nextFocus(focusRequester2, focusRequester3)
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = ePassword,
                    onValueChange = {
                        ePassword = it
                    },
                    label = {
                        Text("密码")
                    },
                    modifier = Modifier.nextFocus(focusRequester3, focusRequester4)
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = eIp,
                    onValueChange = {
                        eIp = it
                    },
                    label = {
                        Text("IP")
                    },
                    modifier = Modifier.nextFocus(focusRequester4, focusRequester5)
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = ePort,
                    onValueChange = {
                        ePort = it
                    },
                    label = {
                        Text("Port")
                    },
                    modifier = Modifier.nextFocus(focusRequester5, focusRequester1)
                )
            }
        }
    )
}

@Composable
fun EntryDeleteDialog(
    value: String,
    onCancel: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(onClick = {
                onDelete()
            }) {
                Text("删除")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onCancel()
            }) {
                Text("取消")
            }
        },
        text = {
            Text("是否删除【$value】")
        }
    )
}

fun writeToClipboard(text: String) {
    val stringSelection = StringSelection(text)
    val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(stringSelection, null)
}

private fun Modifier.nextFocus(focus1: FocusRequester, focus2: FocusRequester): Modifier {
    return this.focusRequester(focus1)
        .onPreviewKeyEvent { event ->
            if (event.type == KeyEventType.KeyDown) {
                when (event.key) {
                    Key.Tab -> {
                        focus2.requestFocus()
                        true
                    }
                    Key.Enter -> true
                    else -> false
                }
            } else {
                false
            }
        }
}