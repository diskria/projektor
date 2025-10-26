plugins {
    `kotlin-dsl`
    alias(libs.plugins.projektor) version "4.+"
}

projekt {
    gradlePlugin {
        isSettingsPlugin = true
    }
}
