import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.runExtension
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
//    publishingTarget = LocalMaven

    gradlePlugin()

}

runExtension<PublishingExtension> {
    repositories {
        maven(getBuildDirectory("localMaven")) {
            name = "LocalMaven"
        }
    }
}
