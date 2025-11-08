plugins {
    `kotlin-dsl`
    alias(libs.plugins.projektor)
}

dependencies {
    compileOnly(project(":common"))

    implementation(libs.bundles.diskria.utils)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.implementation.settings.plugins)
    implementation(libs.jsoup)
}

projekt {
    gradlePlugin {
        isSettingsPlugin = true
    }
}
