import io.github.diskria.projektor.extensions.configureGradlePlugin
import io.github.diskria.projektor.owner.GithubProfile
import io.github.diskria.projektor.projekt.PublishingTarget

plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.projektor)
    alias(libs.plugins.build.config)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    compileOnly(kotlin("gradle-plugin"))

    compileOnly(libs.build.config.plugin)
    compileOnly(libs.fabric.plugin)
    compileOnly(libs.neoforge.plugin)
    compileOnly(libs.modrinth.plugin)

    implementation(libs.ktor.http)
    implementation(libs.kotlin.utils)
    implementation(libs.kotlin.serialization)

    implementation(libs.gradle.utils)

    constraints {
        // Override vulnerable transitive dependency (Okio < 3.4.0, CVE-2023-3635)
        // com.modrinth.minotaur → Modrinth4J → Okio
        implementation(libs.okio)
    }
}

configureGradlePlugin(GithubProfile, PublishingTarget.GITHUB_PAGES)
