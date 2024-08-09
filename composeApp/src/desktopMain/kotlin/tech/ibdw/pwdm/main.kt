package tech.ibdw.pwdm

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import tech.ibdw.pwdm.cfg.Config
import tech.ibdw.pwdm.cfg.Profile
import java.io.File
import java.io.FileFilter
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "密码管理器",
        alwaysOnTop = true,
        icon = painterResource("icon.icns")
    ) {
        App(loadConfig(), loadProfiles())
    }
}

fun loadProfiles(): MutableList<Profile> {
    val dict = File("${System.getProperty("user.home")}/pwdm/profile")
    val profiles = mutableListOf<Profile>()
    dict.listFiles(FileFilter {
        it.name.endsWith(".toml")
    })?.forEach {
        profiles.add(Toml().read(it).to(Profile::class.java))
    }
    return profiles
}

fun loadConfig(): Config {
    val cfg = File("${System.getProperty("user.home")}/pwdm/conf/conf.toml")
    if (!cfg.exists()) {
        createDefaultConfig(cfg)
    }
    return Toml().read(cfg).to(Config::class.java)
}

fun createDefaultConfig(file: File) {
    file.parentFile.mkdirs()
    TomlWriter().write(Config(), file)
    createDefaultProfile()
}

fun createDefaultProfile() {
    val profile = File("${System.getProperty("user.home")}/pwdm/profile/1.toml")
    profile.parentFile.mkdirs()
    TomlWriter().write(Profile(), profile)
}

fun saveProfile(profile: Profile) {
    TomlWriter().write(profile, File("${System.getProperty("user.home")}/pwdm/profile/${profile.name}.toml"))
}

fun saveConfig(config: Config) {
    TomlWriter().write(config, File("${System.getProperty("user.home")}/pwdm/conf/conf.toml"))
}


