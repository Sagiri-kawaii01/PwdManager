package tech.ibdw.pwdm

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

import tech.ibdw.pwdm.cfg.Config
import tech.ibdw.pwdm.cfg.Profile
import tech.ibdw.pwdm.mvi.ProfileEvent
import tech.ibdw.pwdm.mvi.ProfileViewModel
import tech.ibdw.pwdm.view.Home
import tech.ibdw.pwdm.view.SettingsDialog
import tech.ibdw.pwdm.view.SideBar

@Composable
@Preview
fun App(config: Config, profiles: MutableList<Profile>) {
    val viewModel = ProfileViewModel(profiles, config)
    var showSettings by remember { mutableStateOf(false) }
    MaterialTheme {
        if (showSettings) {
            SettingsDialog(viewModel) {
                showSettings = false
                viewModel.onEvent(ProfileEvent.SaveAllProfile)
            }
        }
        Row (
            modifier = Modifier.fillMaxSize()
        ) {
            SideBar(
                modifier = Modifier.fillMaxHeight(),
                profiles = profiles,
                viewModel = viewModel
            ) {
                showSettings = true
            }
            Home(
                modifier = Modifier.fillMaxHeight(),
                viewModel = viewModel
            )
        }
    }
}