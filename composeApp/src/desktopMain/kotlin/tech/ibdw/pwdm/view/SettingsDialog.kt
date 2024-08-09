package tech.ibdw.pwdm.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import tech.ibdw.pwdm.cfg.Config
import tech.ibdw.pwdm.mvi.ProfileEvent
import tech.ibdw.pwdm.mvi.ProfileViewModel

/**
 * @author <a href="https://github.com/Sagiri-kawaii01">lgz</a>
 * @date 2024/8/8 16:23
 * @since
 */
@Composable
fun SettingsDialog(
    viewModel: ProfileViewModel,
    onDismiss: () -> Unit,
) {
    val config by viewModel.configState.collectAsState()
    val profiles by viewModel.profiles.collectAsState()
    val defaultProfile = profiles.indexOfFirst { config.defaultProfile == it.name }

    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        confirmButton = {},
        title = {
            Text("Settings")
        },
        text = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("隐藏密码")
                    Switch(
                        checked = !config.showPassword,
                        onCheckedChange = {
                            config.showPassword = !config.showPassword
                            viewModel.onEvent(ProfileEvent.EditConfig(config))
                        },
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                    )
                }
                LazyColumn {
                    itemsIndexed(profiles) { index, profile ->
                        Card(
                            modifier = Modifier.padding(vertical = 4.dp),
                            elevation = 8.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(profile.title)
                                    Spacer(Modifier.width(12.dp))
                                    Text("设为默认")
                                    Switch(
                                        checked = defaultProfile == index,
                                        onCheckedChange = {
                                            viewModel.onEvent(ProfileEvent.SetDefault(index))
                                        },
                                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    )
}