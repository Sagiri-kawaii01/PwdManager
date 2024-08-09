package tech.ibdw.pwdm.mvi

import tech.ibdw.pwdm.cfg.Config
import tech.ibdw.pwdm.cfg.Entry
import tech.ibdw.pwdm.cfg.Profile

/**
 * @author <a href="https://github.com/Sagiri-kawaii01">lgz</a>
 * @date 2024/8/8 10:24
 * @since
 */
sealed interface ProfileEvent {
    data class LoadProfile(val index: Int): ProfileEvent
    data object AddPage: ProfileEvent
    data class LoadPage(val index: Int): ProfileEvent
    data class DeletePage(val index: Int): ProfileEvent
    data class EditEntry(val entry: Entry, val index: Int): ProfileEvent
    data class EditProfile(val profile: Profile, val index: Int): ProfileEvent
    data object SaveAllProfile: ProfileEvent
    data class DeleteEntry(val index: Int): ProfileEvent
    data class AddEntry(val entry: Entry): ProfileEvent
    data object SaveProfile: ProfileEvent
    data object SaveConfig: ProfileEvent
    data class EditConfig(val config: Config): ProfileEvent
    data class SetDefault(val profileIndex: Int): ProfileEvent
}