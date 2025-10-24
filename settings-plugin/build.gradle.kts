plugins {
    `kotlin-dsl`
    alias(libs.plugins.projektor)
}

dependencies {
    implementation(libs.projektor.common)
    implementation(libs.bundles.diskria.utils)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.implementation.settings.plugins)
}

projekt {
    gradlePlugin {
        isSettingsPlugin = true
    }
}
