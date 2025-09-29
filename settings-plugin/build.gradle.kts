import io.github.diskria.projektor.extensions.configureGradlePlugin
import io.github.diskria.projektor.owner.GithubProfile
import io.github.diskria.projektor.projekt.PublishingTarget

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

configureGradlePlugin(GithubProfile, PublishingTarget.GITHUB_PAGES, isSettingsPlugin = true)
