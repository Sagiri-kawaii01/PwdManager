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

/**
 * @author <a href="https://github.com/Sagiri-kawaii01">lgz</a>
 * @date 2024/8/8 10:23
 * @since
 */
class ProfileViewModel(
    val profiles: List<Profile>
): ViewModel() {
    private var _state = MutableStateFlow(profiles[0])
    private var _pageState = MutableStateFlow(_state.value.pages.toMutableStateList())
    private var _entryState = MutableStateFlow(mutableStateListOf<Entry>())
    val state: StateFlow<Profile> = _state
    val pageState: StateFlow<List<Page>> = _pageState
    val entryState: StateFlow<List<Entry>> = _entryState

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadProfile -> {
                _state.value = profiles[event.index]
                _pageState.value = profiles[event.index].pages.toMutableStateList()
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
        }
    }

    private fun saveProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            saveProfile(_state.value)
        }
    }

}