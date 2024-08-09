package tech.ibdw.pwdm.view

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import tech.ibdw.pwdm.cfg.Config
import tech.ibdw.pwdm.cfg.Page
import tech.ibdw.pwdm.cfg.Profile
import tech.ibdw.pwdm.mvi.ProfileEvent
import tech.ibdw.pwdm.mvi.ProfileViewModel
import tech.ibdw.pwdm.saveProfile

/**
 * @author <a href="https://github.com/Sagiri-kawaii01">lgz</a>
 * @date 2024/8/7 16:06
 * @since
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SideBar(
    modifier: Modifier,
    profiles: List<Profile>,
    viewModel: ProfileViewModel,
    onOpenSettings: () -> Unit,
) {

    var defaultIndex by remember { mutableStateOf(0) }
    var selectPageIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        for ((index, profile) in profiles.withIndex()) {
            if (profile.name == viewModel.configState.value.defaultProfile) {
                defaultIndex = index
                break
            }
        }
        viewModel.onEvent(ProfileEvent.LoadProfile(defaultIndex))
    }

    val pageState by viewModel.pageState.collectAsState()

    var pageEditName by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var onEditPageName: (String) -> Unit by remember { mutableStateOf({}) }

    if (showEditDialog) {
        PageNameEditDialog(pageEditName, {
            showEditDialog = false
        }) {
            onEditPageName(it)
            showEditDialog = false
        }
    }

    var pageDeleteName by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var onDeletePage: () -> Unit by remember { mutableStateOf({}) }

    if (showDeleteDialog) {
        PageDeleteDialog(pageDeleteName, {
            showDeleteDialog = false
        }) {
            onDeletePage()
            showDeleteDialog = false
        }
    }

    Card (
        modifier
            .widthIn(min = 100.dp, max = 200.dp)
            .padding(10.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                DropProfile(profiles, defaultIndex) {
                    viewModel.onEvent(ProfileEvent.LoadProfile(it))
                }
                Spacer(Modifier.height(8.dp))
                LazyColumn{
                    itemsIndexed(pageState) { index, page ->
                        var showDropMenu by remember { mutableStateOf(false) }
                        Row(
                            modifier = Modifier
                                .onClick(matcher = PointerMatcher.mouse(PointerButton.Primary)) {
                                    selectPageIndex = index
                                    viewModel.onEvent(ProfileEvent.LoadPage(index))
                                }
                                .onClick(matcher = PointerMatcher.mouse(PointerButton.Secondary)) {
                                    showDropMenu = true
                                }
                                .pointerHoverIcon(PointerIcon.Hand)
                                .run {
                                    if (selectPageIndex == index) {
                                        this.border(
                                            BorderStroke(1.dp, Color.Black)
                                        ).background(Color(244, 244, 244))
                                    } else this
                                }
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(page.name)
                            if (showDropMenu) {
                                DropdownMenu(
                                    expanded = showDropMenu,
                                    onDismissRequest = { showDropMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        onClick = {
                                            pageEditName = page.name
                                            onEditPageName = {
                                                page.name = it
                                                viewModel.onEvent(ProfileEvent.SaveProfile)
                                            }
                                            showEditDialog = true
                                            showDropMenu = false
                                        }
                                    ) {
                                        Text("重命名")
                                    }
                                    DropdownMenuItem(
                                        onClick = {
                                            pageDeleteName = page.name
                                            onDeletePage = {
                                                viewModel.onEvent(ProfileEvent.DeletePage(index))
                                            }
                                            showDeleteDialog = true
                                            showDropMenu = false
                                        }
                                    ) {
                                        Text("删除")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Column {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth()
                        .pointerHoverIcon(PointerIcon.Hand),
                    onClick = {
                        onOpenSettings()
                    }
                ) {
                    Text("Settings")
                }
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth()
                        .pointerHoverIcon(PointerIcon.Hand),
                    onClick = {
                        viewModel.onEvent(ProfileEvent.AddPage)
                    }
                ) {
                    Text("Add")
                }
            }
        }

    }
}

@Composable
fun DropProfile(
    profiles: List<Profile>,
    defaultIndex: Int,
    modifier: Modifier = Modifier,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectProfileIndex by mutableStateOf(defaultIndex)

    Box(modifier) {
        OutlinedTextField(
            value = profiles[selectProfileIndex].title,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            trailingIcon = {
                IconButton(
                    onClick = {
                        expanded = true
                    },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                ) {
                    Icon(Icons.Default.ArrowDropDown, "")
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.widthIn(max = 200.dp)
        ) {
            profiles.forEachIndexed { index, profile ->
                DropdownMenuItem(
                    onClick = {
                        onSelect(index)
                        selectProfileIndex = index
                        expanded = false
                    },
                    enabled = selectProfileIndex != index
                ) {
                    Text(profile.title)
                }
            }
        }
    }
}

@Composable
fun PageDeleteDialog(
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

@Composable
fun PageNameEditDialog(
    value: String,
    onCancel: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(value) }
    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(onClick = {
                onSave(text)
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
            Text("编辑【$value】标题")
        },
        text = {
            Column {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    label = {
                        Text("标题")
                    }
                )
            }
        }
    )
}