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
    runExtension<GradlePluginDevelopmentExtension> {
        website.set(plugin.getRepoUrl())
        vcsUrl.set(plugin.getRepoPath(isVcs = true))
        plugins {
            create(plugin.id) {
                id = plugin.id
                implementationClass = plugin.packageName.appendPackageName(plugin.classNameBase + "GradlePlugin")
                displayName = plugin.name
                description = plugin.description
                tags.set(plugin.tags.toNullIfEmpty())
            }
        }
    }
}
