import io.github.diskria.projektor.publishing.maven.GithubPages

plugins {
    `kotlin-dsl`
    alias(libs.plugins.projektor)
}

dependencies {
    implementation(libs.projektor.common)
    implementation(libs.bundles.diskria.utils)

    compileOnly(kotlin("gradle-plugin"))
    compileOnly(libs.fabric.loom.plugin)
    implementation(libs.build.config.plugin)
    implementation(libs.kotlin.serialization.plugin)
    implementation(libs.neoforge.moddev.plugin)
    implementation(libs.modrinth.minotaur.plugin)

    implementation(libs.bundles.ktor.client)

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
