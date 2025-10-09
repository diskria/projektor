import io.github.diskria.projektor.publishing.LocalMaven

plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.projektor)
    alias(libs.plugins.build.config)
}

dependencies {
    implementation(libs.projektor.common)
    implementation(libs.bundles.diskria.utils)
    implementation(libs.bundles.ktor.client)
}

projekt {
    publishingTarget = LocalMaven

    gradlePlugin {
        isSettingsPlugin = true
    }
}
