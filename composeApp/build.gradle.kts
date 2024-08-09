import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.utils.nativeMemoryAllocator

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.toml)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.toast)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}



compose.desktop {
    application {
        mainClass = "tech.ibdw.pwdm.MainKt"

        jvmArgs("-Xmx32m", "-Xms32m", "-XX:MaxMetaspaceSize=32m", "-Xss512k")

        val version = "1.0.0"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Exe)
            packageName = "PwdManager"
            packageVersion = version
            windows {
//                includeAllModules = true
                packageVersion = version
                msiPackageVersion = version
                exePackageVersion = version
                modules("java.instrument", "java.sql", "jdk.unsupported")
                iconFile.set(project.file("src/desktopMain/resources/icon.ico"))
            }
            macOS {
                packageVersion = version
                dmgPackageVersion = version
                pkgPackageVersion = version
                dockName = "PwdManager"

                packageBuildVersion = version
                dmgPackageBuildVersion = version
                pkgPackageBuildVersion = version
                // 设置图标
                iconFile.set(project.file("src/desktopMain/resources/icon.icns"))
            }

        }

    }
}
