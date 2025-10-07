import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.runExtension
import io.github.diskria.projektor.publishing.LocalMaven

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
//    publishingTarget = LocalMaven

    gradlePlugin {
        isSettingsPlugin = true
    }
}

runExtension<PublishingExtension> {
    repositories {
        maven(getBuildDirectory("localMaven")) {
            name = "LocalMaven"
        }
    }
}
