import io.github.diskria.projektor.publishing.maven.GithubPages

plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.projektor)
}

dependencies {
    implementation(libs.bundles.diskria.utils)
    implementation(libs.bundles.ktor.client)
}

projekt {
    publishingTarget = GithubPages

    kotlinLibrary()
}
