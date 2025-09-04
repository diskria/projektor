import io.github.diskria.projektor.gradle.extensions.configureGradlePlugin
import io.github.diskria.projektor.owner.MainDeveloper
import io.github.diskria.projektor.projekt.PublishingTarget

plugins {
    `kotlin-dsl`
    `maven-publish`
}

dependencies {
    compileOnly(libs.kotlin.plugin)
    compileOnly(libs.build.config.plugin)
    compileOnly(libs.fabric.loom.plugin)
    compileOnly(libs.neoforge.moddevplugin)
    compileOnly(libs.modrinth.minotaur.plugin)

    implementation(libs.ktor.http)
    implementation(libs.kotlin.utils)
    implementation(libs.kotlin.serialization)

    constraints {
        // Override vulnerable transitive dependency (Okio < 3.4.0, CVE-2023-3635)
        // com.modrinth.minotaur → Modrinth4J → Okio
        implementation(libs.okio)
    }
}

configureGradlePlugin(MainDeveloper, PublishingTarget.GITHUB_PACKAGES)
