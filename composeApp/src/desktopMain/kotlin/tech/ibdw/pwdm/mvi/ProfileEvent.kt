package tech.ibdw.pwdm.mvi

/**
 * @author <a href="https://github.com/Sagiri-kawaii01">lgz</a>
 * @date 2024/8/8 10:24
 * @since
 */
sealed interface ProfileEvent {
    data class LoadProfile(val index: Int): ProfileEvent
    data object AddPage: ProfileEvent
    data class LoadPage(val index: Int): ProfileEvent
}