import io.github.diskria.projektor.publishing.maven.GithubPages

plugins {
    `kotlin-dsl`
    alias(libs.plugins.projektor)
}

dependencies {
    implementation(libs.foojay.resolver.plugin)
    implementation(libs.projektor.common)
    implementation(libs.bundles.diskria.utils)
    implementation(libs.bundles.ktor.client)
}

projekt {
    publishingTarget = GithubPages

    gradlePlugin {
        isSettingsPlugin = true
    }
}
