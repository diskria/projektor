import io.github.diskria.projektor.publishing.GithubPages

plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.projektor)
    alias(libs.plugins.build.config)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.projektor.common)
    implementation(libs.bundles.diskria.utils)
    implementation(libs.bundles.ktor.client)
    implementation(libs.kotlin.serialization)
}

projekt {
    publishingTarget = GithubPages

    gradlePlugin {
        isSettingsPlugin = true
    }
}
