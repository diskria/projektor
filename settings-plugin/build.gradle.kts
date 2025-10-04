import io.github.diskria.projektor.licenses.MitLicense
import io.github.diskria.projektor.owner.GithubProfile
import io.github.diskria.projektor.publishing.GitHubPages

plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.projektor)
    alias(libs.plugins.build.config)
}

dependencies {
    implementation(libs.kotlin.utils)

    implementation(libs.gradle.utils)
}

projekt {
    owner = GithubProfile
    license = MitLicense
    publishingTarget = GitHubPages

    gradlePlugin {
        isSettingsPlugin = true
        tags = setOf("project", "configuration")
    }
}
