package tech.ibdw.pwdm.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
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
@Composable
fun SideBar(
    modifier: Modifier,
    profiles: List<Profile>,
    config: Config,
    viewModel: ProfileViewModel,
) {

    var defaultIndex = 0
    var selectPageIndex by remember { mutableStateOf(0) }
    for ((index, profile) in profiles.withIndex()) {
        if (profile.name == config.defaultProfile) {
            defaultIndex = index
            break
        }
    }
    viewModel.onEvent(ProfileEvent.LoadProfile(defaultIndex))
    val pageState by viewModel.pageState.collectAsState()

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
                        Row(
                            modifier = Modifier
                                .clickable {
                                    selectPageIndex = index
                                    viewModel.onEvent(ProfileEvent.LoadPage(index))
                                }
                                .pointerHoverIcon(PointerIcon.Hand)

                                .run {
                                    if (selectPageIndex == index) {
                                        this.border(
                                            BorderStroke(1.dp, Color.Black)
                                        )
                                    } else this
                                }
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(page.name)
                        }
                    }
                }
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.onEvent(ProfileEvent.AddPage)
                }
            ) {
                Text("Add")
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
    var selectProfileIndex by remember { mutableStateOf(defaultIndex) }

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