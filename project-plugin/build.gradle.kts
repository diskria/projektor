import io.github.diskria.projektor.gradle.extensions.configureGradlePlugin
import io.github.diskria.projektor.owner.GithubProfile
import io.github.diskria.projektor.projekt.PublishingTarget

plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.projektor)
}

dependencies {
    compileOnly(project(":common"))

    compileOnly(kotlin("gradle-plugin"))
    compileOnly(gradleKotlinDsl())

    compileOnly(libs.build.config.plugin)
    compileOnly(libs.fabric.plugin)
    compileOnly(libs.neoforge.plugin)
    compileOnly(libs.modrinth.minotaur.plugin)

    implementation(libs.ktor.http)
    compileOnly(libs.kotlin.utils)
    implementation(libs.kotlin.serialization)

    constraints {
        // Override vulnerable transitive dependency (Okio < 3.4.0, CVE-2023-3635)
        // com.modrinth.minotaur → Modrinth4J → Okio
        implementation(libs.okio)
    }
}

configureGradlePlugin(GithubProfile, PublishingTarget.GITHUB_PAGES)
