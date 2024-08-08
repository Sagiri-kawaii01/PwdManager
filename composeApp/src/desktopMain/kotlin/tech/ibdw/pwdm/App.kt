package tech.ibdw.pwdm

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

import tech.ibdw.pwdm.cfg.Config
import tech.ibdw.pwdm.cfg.Profile
import tech.ibdw.pwdm.mvi.ProfileViewModel
import tech.ibdw.pwdm.view.Home
import tech.ibdw.pwdm.view.SideBar

@Composable
@Preview
fun App(config: Config, profiles: MutableList<Profile>) {
    val viewModel = ProfileViewModel(profiles)
    MaterialTheme {
        Row (
            modifier = Modifier.fillMaxSize()
        ) {
            SideBar(
                modifier = Modifier.fillMaxHeight(),
                profiles = profiles,
                config = config,
                viewModel = viewModel
            )
            Home(
                modifier = Modifier.fillMaxHeight(),
                viewModel = viewModel
            )
        }
    }
}