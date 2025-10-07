import io.github.diskria.gradle.utils.extensions.runExtension
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.generics.toNullIfEmpty
import io.github.diskria.projektor.publishing.LocalMaven

plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.projektor)
    alias(libs.plugins.build.config)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.bundles.diskria.utils)

    compileOnly(kotlin("gradle-plugin"))
    compileOnly(libs.build.config.plugin)
    compileOnly(libs.fabric.plugin)
    compileOnly(libs.neoforge.plugin)
    compileOnly(libs.modrinth.plugin)

    implementation(libs.bundles.ktor.client)
    implementation(libs.kotlin.serialization)

    constraints {
        // Override vulnerable transitive dependency (Okio < 3.4.0, CVE-2023-3635)
        // com.modrinth.minotaur → Modrinth4J → Okio
        implementation(libs.okio)
    }
}

projekt {
    publishingTarget = LocalMaven

    val plugin = gradlePlugin()
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
