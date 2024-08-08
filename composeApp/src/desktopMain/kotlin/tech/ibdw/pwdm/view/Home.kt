package tech.ibdw.pwdm.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.NetworkPing
import androidx.compose.material.icons.outlined.Password
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.dokar.sonner.ToastType
import com.dokar.sonner.Toaster
import com.dokar.sonner.ToasterState
import com.dokar.sonner.rememberToasterState
import tech.ibdw.pwdm.cfg.Entry
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
    val entries = viewModel.entryState.collectAsState()
    val toaster = rememberToasterState()

    Toaster(
        state = toaster
    )
    LazyColumn {
        items(entries.value) {
            EntryCard(it, toaster)
        }
    }
}

@Composable
fun EntryCard(
    entry: Entry,
    toaster: ToasterState
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
                        onClick = {

                        }
                    ) {
                        Icon(Icons.Default.Edit,
                            "",
                            tint = Color(0, 164, 255),
                            modifier = Modifier.size(24.dp)
                                .pointerHoverIcon(PointerIcon.Hand)
                        )
                    }

                    IconButton(
                        onClick = {

                        }
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

@Composable
fun CopyText(
    title: String,
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
        Text(value)
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

fun writeToClipboard(text: String) {
    val stringSelection = StringSelection(text)
    val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(stringSelection, null)
}