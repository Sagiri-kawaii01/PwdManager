package tech.ibdw.pwdm.mvi

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tech.ibdw.pwdm.cfg.Config
import tech.ibdw.pwdm.cfg.Entry
import tech.ibdw.pwdm.cfg.Page
import tech.ibdw.pwdm.cfg.Profile
import tech.ibdw.pwdm.saveProfile
import tech.ibdw.pwdm.saveConfig

/**
 * @author <a href="https://github.com/Sagiri-kawaii01">lgz</a>
 * @date 2024/8/8 10:23
 * @since
 */
class ProfileViewModel(
    profiles: List<Profile>,
    config: Config
): ViewModel() {
    private var _profiles = MutableStateFlow(profiles.toMutableStateList())
    private var _state = MutableStateFlow(_profiles.value[0])
    private var _config = MutableStateFlow(config)
    private var _pageState = MutableStateFlow(_state.value.pages.toMutableStateList())
    private var _entryState = MutableStateFlow(mutableStateListOf<Entry>())
    private var page = 0

    val profiles: StateFlow<List<Profile>> = _profiles
    val configState: StateFlow<Config> = _config
    val state: StateFlow<Profile> = _state
    val pageState: StateFlow<List<Page>> = _pageState
    val entryState: StateFlow<List<Entry>> = _entryState

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadProfile -> {
                _state.value = _profiles.value[event.index]
                _pageState.value = _profiles.value[event.index].pages.toMutableStateList()
                if (_pageState.value.isNotEmpty()) {
                    _entryState.value = _pageState.value[0].entries.toMutableStateList()
                }
            }
            ProfileEvent.AddPage -> {
                val page = Page()
                _state.value.pages.add(page)
                _pageState.value.add(page)
                saveProfile()
            }
            is ProfileEvent.LoadPage -> {
                page = event.index
                _entryState.value = _pageState.value[event.index].entries.toMutableStateList()
            }
            ProfileEvent.SaveProfile -> {
                saveProfile()
            }
            is ProfileEvent.DeletePage -> {
                _pageState.value.removeAt(event.index)
                _state.value.pages.removeAt(event.index)
                saveProfile()
            }
            is ProfileEvent.EditEntry -> {
                _entryState.value[event.index].apply {
                    this.name = event.entry.name
                    this.account = event.entry.account
                    this.password = event.entry.password
                    this.ip = event.entry.ip
                    this.port = event.entry.port
                }
                _state.value.pages[page].entries = _entryState.value
                saveProfile()
            }
            is ProfileEvent.DeleteEntry -> {
                _entryState.value.removeAt(event.index)
                _state.value.pages[page].entries = _entryState.value
                saveProfile()
            }
            is ProfileEvent.AddEntry -> {
                _entryState.value.add(event.entry)
                _state.value.pages[page].entries.add(event.entry)
                saveProfile()
            }
            is ProfileEvent.EditConfig -> {
                _config.value = Config().apply {
                    this.defaultProfile = event.config.defaultProfile
                    this.showPassword = event.config.showPassword
                }
                saveConfig()
            }
            is ProfileEvent.SetDefault -> {
                _config.value = Config().apply {
                    this.defaultProfile = _profiles.value[event.profileIndex].name
                    this.showPassword = _config.value.showPassword
                }
                saveConfig()
            }
            ProfileEvent.SaveConfig -> {
                saveConfig()
            }
            is ProfileEvent.EditProfile -> {
                _profiles.value[event.index] = Profile().apply {
                    this.pages = event.profile.pages
                    this.name = event.profile.name
                    this.title = event.profile.title
                }
            }
            ProfileEvent.SaveAllProfile -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _profiles.value.forEach {
                        saveProfile(it)
                    }
                }
            }
        }
    }

    private fun saveProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            saveProfile(_state.value)
        }
    }

    private fun saveConfig() {
        viewModelScope.launch(Dispatchers.IO) {
            saveConfig(_config.value)
        }
    }

}