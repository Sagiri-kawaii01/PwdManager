package tech.ibdw.pwdm.cfg

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * @author <a href="https://github.com/Sagiri-kawaii01">lgz</a>
 * @date 2024/8/7 16:18
 * @since
 */
class Profile {
    var title = "本地配置"
    var remoteAddr: String? = null
    var secretKey: String? = null
    var name = 1
    var pages: MutableList<Page> = mutableListOf()
}

class Page {
    var name = "未命名"
    var entries: MutableList<Entry> = mutableListOf()
}

class Entry {
    var name = "未命名"
    var account = "account"
    var password = "password"
    var ip: String? = null
    var port: String? = null
}