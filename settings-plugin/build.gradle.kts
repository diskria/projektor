import io.github.diskria.gradle.utils.extensions.runExtension
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.generics.toNullIfEmpty
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
    publishingTarget = LocalMaven

    val plugin = gradlePlugin {
        isSettingsPlugin = true
    }
    plugin.publishingTarget?.configure(plugin, project)
}
