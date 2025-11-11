import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    alias(libs.plugins.projektor)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.bundles.diskria.utils)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.implementation.settings.plugins)
    implementation(libs.jsoup)
}

projekt {
    gradlePlugin {
        isSettingsPlugin = true
        packageNameSuffix = "settings"
        jvmTarget = JvmTarget.JVM_21
    }
}
