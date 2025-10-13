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

    compileOnly(kotlin("gradle-plugin"))
    compileOnly(libs.fabric.plugin)
    implementation(libs.build.config.plugin)
    implementation(libs.kotlin.serialization.plugin)
    implementation(libs.neoforge.plugin)
    implementation(libs.modrinth.plugin)

    implementation(libs.bundles.ktor.client)
    implementation(libs.kotlin.serialization)

    constraints {
        // Override vulnerable transitive dependency (Okio < 3.4.0, CVE-2023-3635)
        // com.modrinth.minotaur → Modrinth4J → Okio
        implementation(libs.okio)
    }
}

projekt {
    publishingTarget = GithubPages

    gradlePlugin()
}
