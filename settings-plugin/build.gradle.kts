import io.github.diskria.projektor.licenses.MIT
import io.github.diskria.projektor.publishing.GitHubPages

plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.projektor)
    alias(libs.plugins.build.config)
}

dependencies {
    implementation(libs.bundles.diskria.utils)
    implementation(libs.bundles.ktor.client)
}

projekt {
    license = MIT
    publishingTarget = GitHubPages

    gradlePlugin {
        isSettingsPlugin = true
        tags = setOf("settings", "configuration")
    }
}
